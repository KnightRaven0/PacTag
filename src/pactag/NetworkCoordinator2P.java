package pactag;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import pactag.net.IOHandler;
import pactag.net.IOHandlerPacket;
import pactag.net.IOHandlerPacketBuilder;
import pactag.net.IOHandlerPacketFilter;

public class NetworkCoordinator2P {
	private static final IOHandlerPacketFilter filter = new IOHandlerPacketFilter() {

		@Override
		public boolean filter(IOHandlerPacket packet) {
			int mark = packet.index();
			try {
				packet.reset();
				if (packet.count() != 6)
					return false;
				while (packet.next()) {
					if (packet.type() != 'I')
						return false;
				}
				return true;
			} finally {
				packet.get(mark);
			}
		}

	};
	public static final NetworkCoordinator2P connect(InetAddress addr, int port) throws IOException {
		return new NetworkCoordinator2P(new IOHandler(new Socket(addr, port)));
	}
	
	public static final ServerSocket makeListener(int port) throws IOException {
		return new ServerSocket(port);
	}
	
	private final IOHandler io;
	
	public NetworkCoordinator2P(IOHandler io) {
		this.io = io;
	}
	
	public NetworkCoordinator2P(Socket sock) throws IOException {
		io = new IOHandler(sock);
	}
	
	public ActionPackage getNextAction() throws IOException {
		IOHandlerPacket packet;
		while (!filter.filter(packet = io.receivePacket()) && !packet.idValid())
			;
		packet.rewind();
		int params[] = new int[6];
		while (packet.next())
			params[packet.index()] = packet.dataAsInt();
		return new ActionPackage(params[0], params[1], params[2], params[3], params[4], params[5]);
	}
	
	public void sendNextAction(ActionPackage pac) throws IOException {
		IOHandlerPacketBuilder builder = new IOHandlerPacketBuilder();
		builder.addInt(pac.getPlayerID());
		builder.addInt(pac.getX());
		builder.addInt(pac.getY());
		builder.addInt(pac.getRelativeX());
		builder.addInt(pac.getRelativeY());
		builder.addInt(pac.getActionID());
		io.sendPacket(builder.build());
		builder.clear();
	}
}
