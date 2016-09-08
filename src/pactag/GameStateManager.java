package pactag;

import java.awt.Graphics;
import java.util.Stack;

/**
 *
 * @author Josh
 */
public class GameStateManager {
	private final Stack<GameState> StateStack;
	private boolean PopNext = false;
	private boolean Running = true;
	private final Renderer Render;
	private final GamePanel Panel;

	public GameStateManager(){
		Input.SetupInput();
		Render = new Renderer();
		Panel = new GamePanel(this);
		Render.AddPanel(Panel);
		Render.RepackFrame();
		StateStack = new Stack<GameState>();
		StateStack.push(new StateMainMenu(this, Render));
	}
	public void Push(GameState NewState){
		StateStack.lastElement().Pause();
		StateStack.push(NewState);
	}
	public void Pop(){
		this.PopNext = true;
	}
	public void Quit(){
		Running = false;
	}
	public boolean IsRunning(){
		return Running;
	}
	public void Draw(Graphics g){	//TODO implement a class that extends JPanel and Graphics and completely rehaul the achitecture of it calling you becuase that is like REAL dumb
		if (!StateStack.empty() && Running){
			StateStack.lastElement().Draw(g);
			Panel.repaint();
		}
	}
	public void Update(){
		if (!StateStack.empty() && Running){
			if (PopNext){
				PopNext = false;
				StateStack.pop();
				if (!StateStack.empty()){
					StateStack.lastElement().Resume();
					StateStack.lastElement().Update();
				}else{
					Running = false;
				}
			}else{
				StateStack.lastElement().Update();
			}
		}else{
			Running = false;
		}
	}
}
