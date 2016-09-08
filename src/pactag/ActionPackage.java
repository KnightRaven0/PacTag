package pactag;

public class ActionPackage {
	
	private final int playerID;
	private final int x, y;
	private final int relX, relY;
	private final int action;
	
	public ActionPackage(int playerID, int x, int y, int relX, int relY, int action) {
		this.playerID = playerID;
		this.x = x;
		this.y = y;
		this.relX = relX;
		this.relY = relY;
		this.action = action;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getRelativeX() {
		return relX;
	}
	
	public int getRelativeY() {
		return relY;
	}
	
	public int getActionID() {
		return action;
	}
	
	public int getPlayerID() {
		return playerID;
	}
	
}
