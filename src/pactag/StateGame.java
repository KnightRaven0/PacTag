package pactag;

import com.sun.glass.events.KeyEvent;
import java.awt.Graphics;
import java.awt.Image;
import pactag.Player.Direction;

/**
 *
 * @author Josh
 */
public class StateGame extends GameState{
	Map CurrentMap;
	Player P1, P2;
	public StateGame(GameStateManager StateManager, Renderer Render) {
		super(StateManager, Render);
		CurrentMap = new Map(16, 16);
		P1 = new Player(true);
		P2 = new Player(false);
	}
	@Override
	public void Update(){
		if (P1.GetPosition().GetX() == P2.GetPosition().GetX() && P1.GetPosition().GetY() == P2.GetPosition().GetY()){
			//Tag code
		}
		P1.Update();
		P2.Update();
		HandleInput();
	}
	private void HandleInput(){
		if (Input.IsKeyPressed(KeyEvent.VK_ESCAPE)){
			StateController.Quit();
		}
		if (Input.IsKeyDown(KeyEvent.VK_UP)){
			P1.MoveDirection(Direction.UP);
		}
		if (Input.IsKeyDown(KeyEvent.VK_W)){
			P2.MoveDirection(Direction.UP);
		}
		if (Input.IsKeyDown(KeyEvent.VK_DOWN)){
			P1.MoveDirection(Direction.DOWN);
		}
		if (Input.IsKeyDown(KeyEvent.VK_S)){
			P2.MoveDirection(Direction.DOWN);
		}
		if (Input.IsKeyDown(KeyEvent.VK_LEFT)){
			P1.MoveDirection(Direction.LEFT);
		}
		if (Input.IsKeyDown(KeyEvent.VK_A)){
			P2.MoveDirection(Direction.LEFT);
		}
		if (Input.IsKeyDown(KeyEvent.VK_RIGHT)){
			P1.MoveDirection(Direction.RIGHT);
		}
		if (Input.IsKeyDown(KeyEvent.VK_D)){
			P2.MoveDirection(Direction.RIGHT);
		}
	}
	@Override
	public void Pause(){}
	@Override
	public void Resume(){}

	@Override
	public void Draw(Graphics g) {
		Image ImageWall = GameRender.GetImage("tempWall.png");
		Image Player1 = GameRender.GetImage("tempChar1.png");
		Image Player2 = GameRender.GetImage("tempChar2.png");
		int X = CurrentMap.GetSizeX();	//Get how big the map is in tiles
		int Y = CurrentMap.GetSizeY();
		for (int i = 0; i < Y; i++){	//Loop through each of the FIRST LAYER which represents the Y Axis
			for (int j = 0; j < X; j++){	//Same thing but for the X Axis
				if (CurrentMap.GetCellTile(j, i) == Map.Tile.WALL){	//If the tile to draw is a Wall, add a Wall Label to the Grid else put empty Label
					g.drawImage(ImageWall, 32 * j, 32 * i, 32, 32, null);
				}
			}
		}
		
		g.drawImage(Player1, (int)P1.GetPosition().GetX() * 32 + P1.GetShiftX(), (int)P1.GetPosition().GetY() * 32 + P1.GetShiftY(), 32, 32, null);
		g.drawImage(Player2, (int)P2.GetPosition().GetX() * 32 + P2.GetShiftX(), (int)P2.GetPosition().GetY() * 32 + P2.GetShiftY(), 32, 32, null);
	}
}
