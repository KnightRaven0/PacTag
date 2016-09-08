package pactag;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Game for Club Rush, yay!
 * @author Joshua
 */
public class PacTag {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args){
		GameStateManager Game = new GameStateManager();
		
		Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!Game.IsRunning()){
					System.exit(0);
				}else{
					Game.Update();
				}
            }
        }, 0, 16);
	}
}