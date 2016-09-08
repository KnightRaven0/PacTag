package pactag;

/**
 *
 * @author Joshua
 */
public class Vector2D {

	private float x, y;
	public Vector2D(){
		x = 0f;
		y = 0f;
	}
	public Vector2D(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float GetX() {
		return x;
	}

	public float GetY() {
		return y;
	}

	public void SetX(float NewX) {
		x = NewX;
	}

	public void SetY(float NewY) {
		y = NewY;
	}

	public void Multiply(Vector2D Vec) {
		x *= Vec.x;
		y *= Vec.y;
	}

	public void add(Vector2D vec) {
		x += vec.x;
		y += vec.y;
	}

	public void Multiply(int Multiplier) {
		x *= Multiplier;
		y *= Multiplier;
	}
}
