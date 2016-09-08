package pactag.net;

public abstract class IOHandlerPacketFilter {
	protected IOHandlerPacketFilter() {
		
	}
	
	public abstract boolean filter(IOHandlerPacket packet);
}
