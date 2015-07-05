import processing.core.*;

public class Block extends Simulatable implements ShapeRect, Drawable{

	float width, height;

	Block(float x, float y, float w, float h){
		position.x = x;
		position.y = y;
		width = w;
		height = h;
	}

	public float getMass(){
		return 100f;
	}

	public float getKFactor(){
		return 1f;
	}

	public float getWidth(){
		return width;
	}

	public float getHeight(){
		return height;
	}

	public boolean canCollide(Simulatable s){
		return false;
	}

	public void draw(PApplet canvas, float scale){
		canvas.fill(255,255,0);
		canvas.stroke(0);
		float x = (position.x - getWidth() / 2) * scale;
		float y = (position.y - getHeight() / 2) * scale;
		canvas.rect(x, y, getWidth() * scale, getHeight() * scale);
	}

}