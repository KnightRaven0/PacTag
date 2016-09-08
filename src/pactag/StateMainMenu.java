package pactag;

import com.sun.glass.events.KeyEvent;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Josh
 */
public class StateMainMenu extends GameState {
	
	private String[] Options = {"StartGame", "Controls", "Settings", "Quit"};
	private int Selection = 0;
	
	public StateMainMenu(GameStateManager StateManager, Renderer Render) {
		super(StateManager, Render);
		GameRender.RepackFrame();
	}
	
	@Override
	public void Update(){
		if (Input.IsKeyPressed(KeyEvent.VK_ESCAPE)){
			StateController.Quit();
		}
		
		if (Input.IsKeyPressed(KeyEvent.VK_DOWN)){
			Selection = (Selection + 1) % Options.length;
		}
		if (Input.IsKeyPressed(KeyEvent.VK_UP)){
			Selection = Selection - 1;
			if (Selection == -1){
				Selection = Options.length;
			}
		}
		if (Input.IsKeyPressed(KeyEvent.VK_ENTER)){
			switch (Selection){
				case 0:
					StateController.Push(new StateGame(StateController, GameRender));
					break;
				case 1:
					break;
				case 2:
					break;
				case 3:
					StateController.Quit();
					break;
			}
		}
	}
	@Override
	public void Pause(){}
	@Override
	public void Resume(){}

	@Override
	public void Draw(Graphics g) {
		g.setColor(new Color(79, 102, 34));
		g.fillRect(0, 0, 512, 512);
		for (int i = 0; i < Options.length; i++) {
			if (i == Selection) {
				g.setColor(new Color(195, 231, 77));
				g.drawString("~ " + Options[i], 512 / 2 - 200, 150 + i * 70);
			} else {
				g.setColor(new Color(142, 183, 61));
				g.drawString("   " + Options[i], 512 / 2 - 200, 150 + i * 70);
			}
		}
	}
}
