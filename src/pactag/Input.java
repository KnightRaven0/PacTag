package pactag;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 *
 * @author Josh
 */
public class Input {
    private static ArrayList<Boolean> KeyPressed;
	private static ArrayList<Integer> Keys;
	private static ArrayList<Boolean> KeysLastState;	//true = pressed, false = notpressed
    public static boolean IsKeyDown(int Key) {
		if (Keys.contains(Key)){
			int Position = Keys.indexOf(Key);
			return KeyPressed.get(Position);
		}
		return false;
    }
	public static boolean IsKeyReleased(int Key){
		if (Keys.contains(Key)){
			int Position = Keys.indexOf(Key);
			boolean CurrentState = IsKeyDown(Key);
			if (KeysLastState.get(Position) == true && CurrentState == false){
				KeysLastState.set(Position, false);
				return true;
			}
		}else{
			AddKeyToList(Key);
		}
		return false;
	}
	public static boolean IsKeyPressed(int Key){
		if (Keys.contains(Key)){
			int Position = Keys.indexOf(Key);
			if (KeysLastState.get(Position) == false && KeyPressed.get(Position) == true){
				KeysLastState.set(Position, true);
				return true;
			}
		}
		return false;
	}
	private static void AddKeyToList(int Key){
		if (!Keys.contains(Key)){
			Keys.add(Key);
			KeysLastState.add(false);
			KeyPressed.add(true);
		}
	}
	public static void SetupInput(){
		Keys = new ArrayList<Integer>(32);
		KeysLastState = new ArrayList<Boolean>(32);
		KeyPressed = new ArrayList<Boolean>(32);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent ke) {
				synchronized (Input.class) {
					switch (ke.getID()) {
						case KeyEvent.KEY_PRESSED:
							if (Keys.contains(ke.getKeyCode())){
								int Position = Keys.indexOf(ke.getKeyCode());
								KeysLastState.set(Position, KeyPressed.get(Position));
								KeyPressed.set(Position, true);
							}else{
								AddKeyToList(ke.getKeyCode());
							}
							break;
							
						case KeyEvent.KEY_RELEASED:
							int Position = Keys.indexOf(ke.getKeyCode());
								KeysLastState.set(Position, KeyPressed.get(Position));
							KeyPressed.set(Position, false);
							break;
					}
					return false;
				}
			}
		});
	}
}
