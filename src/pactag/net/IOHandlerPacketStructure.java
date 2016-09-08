package pactag.net;


public class IOHandlerPacketStructure {
	private final char[] entries;
	private final int[] length;
	public IOHandlerPacketStructure(char[] entries, int[] length) {
		if (entries.length != length.length)
			throw new IllegalArgumentException("Arrays must be same sizes");
		this.entries = entries;
		this.length = length;
	}
	
	public char getEntryType(int index) {
		return entries[index];
	}
	
	public int getEntryLength(int index) {
		return length[index];
	}
	
	public int getOffset(int index) {
		int offset = 0;
		for (int i = 0; i < index; i++)
			offset += length[i];
		return offset;
	}
	
	public int getSize() {
		int size = 0;
		for (int entry : length)
			size += entry;
		return size;
	}
	
	public int getCount() {
		return entries.length;
	}
	
}
