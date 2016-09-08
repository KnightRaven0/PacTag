package pactag.net;


import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class IOHandlerPacket {

	private WeakReference<ByteBuffer> converter;
	private final byte[][] data;
	private final CharsetDecoder decoder;
	private final long id;
	private int index;
	private WeakReference<String> line;
	private int mark;
	private final IOHandlerPacketStructure structure;
	private final boolean validID;

	public IOHandlerPacket(IOHandler creator, IOHandlerPacketStructure structure, byte[][] data) throws IllegalAccessException {
		int size = 0;
		for (byte[] section : data)
			size += section.length;
		if (size != structure.getSize())
			throw new IllegalArgumentException(
					"Structure's data map doesn't match the provided data! (Sizes don't match!)");
		if (!Charset.isSupported("UTF-8"))
			throw new RuntimeException("UTF-8 Character Set Unavailable!");
		decoder = Charset.forName("UTF-8").newDecoder();
		this.structure = structure;
		this.data = data.clone();
		index = -1;
		mark = -1;
		converter = null;
		line = null;
		id = creator.getPacketID();
		validID = true;
	}
	
	public IOHandlerPacket(IOHandlerPacketStructure structure, byte[][] data, long id) {
		int size = 0;
		for (byte[] section : data)
			size += section.length;
		if (size != structure.getSize())
			throw new IllegalArgumentException(
					"Structure's data map doesn't match the provided data! (Sizes don't match!)");
		if (!Charset.isSupported("UTF-8"))
			throw new RuntimeException("UTF-8 Character Set Unavailable!");
		decoder = Charset.forName("UTF-8").newDecoder();
		this.structure = structure;
		this.data = data.clone();
		index = -1;
		mark = -1;
		converter = null;
		line = null;
		this.id = id;
		validID = false;
	}

	public void clearMark() {
		mark = -1;
	}

	public int count() {
		return data.length;
	}

	public byte[] data() {
		if (index == -1)
			throw new IllegalStateException("You need to pull data with the next() function");
		else if (index >= data.length)
			throw new IndexOutOfBoundsException("Calling reset() is recommended");
		return data[index].clone();
	}

	public byte[] dataAsBytes() {
		if (structure.getEntryType(index) != 'B')
			throw new IllegalStateException("Entry is not raw bytes");
		return data();
	}

	public double dataAsDouble() {
		if (index == -1)
			throw new IllegalStateException("You need to pull data with the next() function");
		else if (index >= data.length)
			throw new IndexOutOfBoundsException("Calling reset() is recommended");
		if (structure.getEntryType(index) != 'D')
			throw new IllegalStateException("Entry is not a Double!");
		ByteBuffer helper = getConverter();
		return helper.getDouble(0);
	}

	public float dataAsFloat() {
		if (index == -1)
			throw new IllegalStateException("You need to pull data with the next() function");
		else if (index >= data.length)
			throw new IndexOutOfBoundsException("Calling reset() is recommended");
		if (structure.getEntryType(index) != 'F')
			throw new IllegalStateException("Entry is not a Float!");
		ByteBuffer helper = getConverter();
		return helper.getFloat(0);
	}

	public int dataAsInt() {
		if (index == -1)
			throw new IllegalStateException("You need to pull data with the next() function");
		else if (index >= data.length)
			throw new IndexOutOfBoundsException("Calling reset() is recommended");
		if (structure.getEntryType(index) != 'I')
			throw new IllegalStateException("Entry is not an Int!");
		ByteBuffer helper = getConverter();
		return helper.getInt(0);
	}

	public long dataAsLong() {
		if (index == -1)
			throw new IllegalStateException("You need to pull data with the next() function");
		else if (index >= data.length)
			throw new IndexOutOfBoundsException("Calling reset() is recommended");
		if (structure.getEntryType(index) != 'L')
			throw new IllegalStateException("Entry is not a Long!");
		ByteBuffer helper = getConverter();
		return helper.getLong(0);
	}

	public short dataAsShort() {
		if (index == -1)
			throw new IllegalStateException("You need to pull data with the next() function");
		else if (index >= data.length)
			throw new IndexOutOfBoundsException("Calling reset() is recommended");
		if (structure.getEntryType(index) != 'S')
			throw new IllegalStateException("Entry is not a Short!");
		ByteBuffer helper = getConverter();
		return helper.getShort(0);
	}

	public String dataAsString() throws CharacterCodingException {
		if (index == -1)
			throw new IllegalStateException("You need to pull data with the next() function");
		else if (index >= data.length)
			throw new IndexOutOfBoundsException("Calling reset() is recommended");
		String ret = null;
		if (structure.getEntryType(index) != 'C')
			throw new IllegalStateException("Entry is not a String!");
		if (line == null || line.get() == null) {
			CharBuffer characters = decoder.decode(ByteBuffer.wrap(data[index]));
			line = new WeakReference<>(characters.toString());
			return characters.toString();
		} else
			ret = line.get();
		return ret;
	}

	public int dataLength() {
		return data[index].length;
	}

	public boolean get(int i) {
		if (i >= data.length)
			return false;
		index = i;
		return true;
	}

	private ByteBuffer getConverter() {
		ByteBuffer ret;
		if (converter != null && (ret = converter.get()) != null)
			return ret;
		ret = ByteBuffer.wrap(data[index]);
		converter = new WeakReference<>(ret);
		return ret;
	}
	
	public boolean hasNext() {
		return index + 1 < data.length;
	}

	public long id() {
		return id;
	}

	public boolean idValid() {
		return validID;
	}

	public int index() {
		return index;
	}

	public void mark() {
		mark = index;
	}

	public boolean next() {
		if (line != null)
			line.clear();
		if (converter != null)
			converter.clear();
		if (data.length != index)
			index++;
		if (data.length == index)
			return false;
		return true;
	}

	public int remaining() {
		return data.length - (index + 1);
	}

	public void reset() {
		rewind();
		index = mark;
	}

	public void rewind() {
		index = -1;
		if (line != null)
			line.clear();
		if (converter != null)
			converter.clear();
	}
	
	public char type() {
		if (index == -1)
			throw new IllegalStateException("You need to pull data with the next() function");
		else if (index >= data.length)
			throw new IndexOutOfBoundsException("Calling reset() is recommended");
		return structure.getEntryType(index);
	}
}
