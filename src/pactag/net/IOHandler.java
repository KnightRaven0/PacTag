package pactag.net;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.concurrent.ConcurrentHashMap;

public final class IOHandler {

	protected class IReaderThread extends Thread {

		public ByteBuffer buffer;

		private IReaderThread() {
			buffer = ByteBuffer.allocate(BUFFER_SIZE); // 4 KB buffer
			buffer.flip();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			int len = 0;
			int off = 0;
			int read = 0;
			synchronized (buffer) {
				while (true) {
					if (Thread.interrupted())
						try {
							buffer.wait(10); // Introduce timeout for workaround
												// of getting randomly stuck
						} catch (InterruptedException e) {
						}
					try {
						off = buffer.limit(); // Get reading limit
						len = buffer.capacity() - off; // Get available bytes to
														// read
						read = in.read(buffer.array(), off, len); // Direct
																	// write
																	// to
																	// buffer
						if (read == -1)
							break;
						buffer.limit(off + read); // Set reading limit
						if (read == 0)
							interrupt();
					} catch (SocketTimeoutException e) {
						interrupt();
					} catch (IOException e) {
						// e.printStackTrace();
						break;
					}
				}
			}
		}
	}

	private static final int BUFFER_SIZE = 4194304; // 4 MB buffer
	private final CharsetDecoder decoder;
	private final CharsetEncoder encoder;

	private InputStream in;
	protected IReaderThread ireader;
	private final ConcurrentHashMap<Thread, Object> locks;
	private OutputStream out;
	private long receiveID;
	private long sendID;

	private Socket source;

	public IOHandler(Socket source) throws IOException {
		if (!Charset.isSupported("UTF-8"))
			throw new RuntimeException("UTF-8 is unsupported, can not decode!");
		this.source = source;
		this.in = source.getInputStream();
		this.out = source.getOutputStream();
		source.setSoTimeout(1000);

		decoder = Charset.forName("UTF-8").newDecoder();
		encoder = Charset.forName("UTF-8").newEncoder();
		decoder.onMalformedInput(CodingErrorAction.IGNORE);
		encoder.onMalformedInput(CodingErrorAction.IGNORE);

		receiveID = 0;
		sendID = 0;
		locks = new ConcurrentHashMap<>();

		ireader = new IReaderThread();
		ireader.setDaemon(true);
		ireader.setName("IOHandler Buffer Thread");
		ireader.start();
	}

	public synchronized void close() throws IOException {
		source.shutdownInput();
		while (ireader.isAlive())
			try {
				ireader.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		source.shutdownOutput();
		out.flush();
		source.close();
	}
	
	/**
	 * Do Not Access this method directly! Doing so will throw an exception, use getID() instead
	 * 
	 * @return The ID to assign to the calling packet
	 * @throws IllegalAccessException
	 *             If the Thread calling it isn't authorized
	 */
	public long getPacketID() throws IllegalAccessException {
		if (!locks.containsKey(Thread.currentThread()))
			throw new IllegalAccessException("This method is for assigning IOHandlerPacket IDs only!");
		if (!Thread.holdsLock(locks.get(Thread.currentThread())))
			throw new IllegalStateException("The thread doesn't hold it's lock! How is this possible?");
		if (!Thread.holdsLock(this))
			throw new IllegalMonitorStateException("The Instance lock is not held!");
		locks.remove(Thread.currentThread());
		return receiveID;
	}
	
	public synchronized long getReceiveID() {
		return receiveID;
	}

	public synchronized boolean hasMessages() {
		ireader.interrupt();
		synchronized (ireader.buffer) {
			ireader.buffer.notify();
			return new String(ireader.buffer.array()).split("\n", 2).length == 2;
		}
	}

	public synchronized boolean isClosed() {
		return source.isClosed();
	}

	public synchronized boolean isEmpty() {
		ireader.interrupt();
		synchronized (ireader.buffer) {
			ireader.buffer.notify();
			return ireader.buffer.position() == 0; // Position will only be zero
													// if
			// there
			// are no bytes, or if being read
			// from,
			// which won't happen during calls
			// to
			// this
		}
	}

	public synchronized boolean isReading() {
		synchronized (ireader) {
			return ireader.isAlive();
		}
	}

	public synchronized IOHandlerPacket receivePacket() throws IOException {
		int wait = 10;
		while (true) {
			ireader.interrupt();
			try {
				synchronized (ireader.buffer) {
					String[] check = decoder.decode(ireader.buffer).toString().split("\n", 2);
					ireader.buffer.rewind();
					if (check.length != 2)
						throw new BufferUnderflowException();
					ireader.buffer.position(encoder.encode(CharBuffer.wrap((check[0] + "\n"))).remaining());
					String[] head = check[0].split(":", 2);
					if (head.length != 2)
						throw new BufferUnderflowException();
					long id;
					try {
						id = Long.parseLong(head[0]);
					} catch (NumberFormatException e) {
						throw new IOException("IOHandler can't interpret the header!");
					}
					String[] meta = head[1].split(" ");
					char[] types = new char[meta.length];
					int[] length = new int[meta.length];
					byte[][] data = new byte[meta.length][];
					try {
						for (int i = 0; i < meta.length; i++) {
							types[i] = meta[i].charAt(0);
							length[i] = Integer.parseInt(meta[i].substring(1));
							data[i] = new byte[length[i]];
							ireader.buffer.get(data[i]);
						}
					} catch (NumberFormatException e) {
						throw new IOException("IOHandler can't interpret the header!");
					}
					IOHandlerPacket packet;
					IOHandlerPacketStructure struct = new IOHandlerPacketStructure(types, length);
					Object lock = new Object();
					locks.put(Thread.currentThread(), lock);
					try {
						if (id == receiveID)
							synchronized (lock) {
								packet = new IOHandlerPacket(this, struct, data);
								receiveID += packet.count();
							}
						else
							packet = new IOHandlerPacket(struct, data, id);
					} catch (IllegalAccessException | IllegalStateException | IllegalMonitorStateException e) {
						packet = new IOHandlerPacket(struct, data, id);
						//e.printStackTrace();
						System.err.println("Unable to assign id, an error occured!");
					}
					byte[] remaining = new byte[ireader.buffer.remaining()];
					ireader.buffer.get(remaining);
					ireader.buffer.clear();
					ireader.buffer.put(remaining);
					ireader.buffer.flip();
					return packet;
				}
			} catch (BufferUnderflowException e) {
				ireader.buffer.rewind();
				if (ireader.buffer.limit() == ireader.buffer.capacity()) {
					throw new IOException(e);
				}
				if (!ireader.isAlive())
					throw new IOException(e);
				/*
				 * if (ireader.getStackTrace().length == 0) {
				 * System.err.println(
				 * "WARNING: Thread inactiveness detected by stacktrace check!"
				 * ); throw new IOException(e); }
				 */
				try {
					wait(wait);
					wait += 10;
					if (wait > 200)
						wait = 200;
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public void sendPacket(IOHandlerPacket p) throws IOException {
		int restore = p.index();
		p.rewind();
		StringBuilder head = new StringBuilder();
		while (p.next()) {
			head.append(p.type()).append(p.dataLength()).append(' ');
		}
		head.deleteCharAt(head.length() - 1).append('\n');
		synchronized (out) {
			head.insert(0, String.valueOf(sendID) + ":");
			ByteBuffer buf = encoder.encode(CharBuffer.wrap(head.toString().toCharArray()));
			byte[] data = new byte[buf.remaining()];
			buf.get(data);
			out.write(data);
			p.rewind();
			while (p.next()) {
				out.write(p.data());
			}
			out.flush();
			sendID += p.count();
		}
		p.get(restore);
	}

}
