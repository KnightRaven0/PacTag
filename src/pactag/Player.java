package pactag;

/**
 *
 * @author Joshua
 */
public class Player {

	public enum Direction {

		LEFT(new Vector2D(-1, 0)), RIGHT(new Vector2D(1, 0)), UP(new Vector2D(0, -1)), DOWN(new Vector2D(0, 1));
		private final Vector2D vector;
		private Direction(Vector2D vec) {
			vector = vec;
		}
		public final Vector2D getVector() {
			return vector;
		}
	}
	
	private final Vector2D Position;
	private final boolean Chaser;
	private final int ID;
	private Direction LastDirection;
	private boolean Moving;
	private static int nextID = 0;
	private int MovePercent = 100;

	static int NextID() {
		return nextID++;
	}
	public void Update(){
		if (MovePercent == 100 && Moving){
			Position.add(LastDirection.getVector());
			Moving = false;
		}
		if (MovePercent < 100)
			MovePercent += 10;
	}
	public Player(boolean DefaultChaser) {
		Moving = false;
		Position = new Vector2D(0,0);
		ID = NextID();
		Chaser = DefaultChaser;
		LastDirection = Direction.UP;
		if (Chaser) {
			Position.SetX(10);  //TODO find starting positions
			Position.SetY(10);
		} else {
			Position.SetX(2);
			Position.SetY(2);
		}
	}
	public Vector2D GetPosition(){
		return Position;
	}
	public int GetShiftX(){
		if (MovePercent == 100){
			return 0;
		}
		if (LastDirection == Direction.LEFT){
			return (int)(MovePercent * -.3);
		}else if(LastDirection == Direction.RIGHT){
			return (int)(MovePercent * .3);
		}
		return 0;
	}
	public int GetShiftY(){
		if (LastDirection == Direction.UP){
			return (int)(MovePercent * -.3);
		}else if(LastDirection == Direction.DOWN){
			return (int)(MovePercent * .3);
		}
		return 0;
	}
	public int GetMovePercent(){
		return MovePercent;
	}
	public void MoveDirection(Direction Direct) {
		if (Direct == LastDirection && MovePercent != 100){
			return;
		}
		if (MovePercent == 100 && Moving){
			return;
		}
		Vector2D TestVector = new Vector2D();
		TestVector.SetX(Position.GetX());
		TestVector.SetY(Position.GetY());
		TestVector.add(Direct.getVector());
		if (Map.CheckCellMoveable(TestVector)){
			if (MovePercent == 100){
				MovePercent = 0;
				LastDirection = Direct;
				Moving = true;
			}else if((LastDirection == Direction.LEFT  && Direct == Direction.RIGHT)||
					 (LastDirection == Direction.RIGHT && Direct == Direction.LEFT) ||
					 (LastDirection == Direction.UP    && Direct == Direction.DOWN) ||
					 (LastDirection == Direction.DOWN  && Direct == Direction.UP)){
				Position.add(LastDirection.getVector());
				MovePercent = 100 - MovePercent;
				LastDirection = Direct;
				Moving = true;
			}
		}
	}

}
