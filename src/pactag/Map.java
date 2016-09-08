package pactag;

/**
 *
 * @author Josh
 */
public class Map {

	enum Tile {
		OPEN, WALL
	}

	private static Tile[][] Grid;
	private static int GridSizeX = 0;
	private static int GridSizeY = 0;

	public  Map(int SizeX, int SizeY) {
		Grid = new Tile[SizeX][SizeY];
		for (int i = 0; i < SizeX; i++) {
			for (int j = 0; j < SizeY; j++) {
				if (i == 0 || i == SizeX - 1 || j == 0 || j == SizeY - 1) {
					Grid[i][j] = Tile.WALL;
				} else {
					Grid[i][j] = Tile.OPEN;
				}
			}
		}
		GridSizeY = Grid.length;
		if (GridSizeY >= 0){
			GridSizeX = Grid[0].length;
		}
	}

	public int GetSizeX() {
		return GridSizeX;
	}
	
	public int GetSizeY() {
		return GridSizeY;
	}
	
	public Tile GetCellTile(int x, int y){
		return Grid[x][y];
	}

	public static boolean CheckCellMoveable(Vector2D Coord) {
		if (Coord.GetX() < 0 || Coord.GetX() > GridSizeX - 1 || Coord.GetY() < 0 || Coord.GetY() > GridSizeY - 1){
			return false;
		}
		if (Grid[(int)Coord.GetX()][(int)Coord.GetY()] == Tile.WALL)
			return false;
		return true;
	}
}
