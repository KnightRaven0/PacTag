package pactag;

import java.awt.Graphics;
import javax.swing.JPanel;

/**
 *
 * @author Joshua
 */
public class GamePanel extends JPanel implements Runnable{
	public static final int HEIGHT = 512;
	public static final int WIDTH = 512;

	private int FPS = 60;
	private boolean isRunning = false;
	private long targetTime = 1000 / FPS;
	
	private GameStateManager StateController;
	
	public GamePanel(GameStateManager Manager){
		StateController = Manager;
	}
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.clearRect(0, 0, WIDTH, HEIGHT);
		StateController.Draw(g);
	}
	
	public void run() {
		long start, elapsed, wait;

		while (StateController.IsRunning()) {
			start = System.nanoTime();

			repaint();

			elapsed = System.nanoTime() - start;
			wait = targetTime - elapsed / 1000000;

			if (wait <= 0) {
				wait = 5;
			}

			try {
				Thread.sleep(wait);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
