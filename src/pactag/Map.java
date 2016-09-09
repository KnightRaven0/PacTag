package pactag;

import java.util.Scanner;


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
	
	private String fileName = "/Resources/pacMap.txt";

	public  Map(int SizeX, int SizeY) {
		makeMap();
		
	}
	
	private void makeMap() {
		try(Scanner reader = new Scanner(GamePanel.class.getResourceAsStream(fileName))) {

			while(reader.hasNextLine()) {
				String[] dimensions = reader.nextLine().split(",");
				GridSizeX = Integer.parseInt(dimensions[0]);
				GridSizeY = Integer.parseInt(dimensions[1]);
				Grid = new Tile[GridSizeX][GridSizeY];
				
				for(int i = 0; i < GridSizeY; i++) {
					char[] nextLine = reader.nextLine().toCharArray();
					for(int j = 0; j < GridSizeX; j++) {
						if(nextLine[j] == '#') {
							Grid[j][i] = Tile.WALL;
						}
						else {
							Grid[j][i] = Tile.OPEN;
						}
					}
				}

			}
		}
		catch(NumberFormatException e) {
			System.err.println("Number could not be parsed, check the formatting of the map text file.");
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
