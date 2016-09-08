package pactag;

import java.awt.Image;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 *
 * @author Josh
 */
public class Renderer {

	private final JFrame Window;

	public Renderer() {
		Window = new JFrame("PacTag");
		Window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Window.setUndecorated(true);
		Window.setResizable(false);
		
	}
	public void RepackFrame(){
		//Window.pack();
		Window.setBounds(0, 0, 512, 512);
		Window.setVisible(true);
	}
	public void AddPanel(GamePanel p){
		Window.getContentPane().add(p);
	}
	public Image GetImage(String Name){
		try{
			return new ImageIcon (ImageIO.read(this.getClass().getResource("/Resources/" + Name))).getImage();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
