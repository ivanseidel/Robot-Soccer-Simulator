import processing.core.*;

public class Field implements Drawable{
	// Size is in cm
	float width = 2.44f;
	float height = 1.82f;

	// [m]
	float space = 0.30f;
	// [m]
	float line_width = 0.02f;
	// center circle diammeter [m] 
	float center_circle_diam = 0.60f; 
	// x penalty area size [m]
	float x_pen_area = 0.30f;
	// y penalty area size [m]
	float y_pen_area = 0.90f;
	// x goal size [m]
	float x_goal = 0.15f;
	// y goal size [m]
	float y_goal = 0.60f;
	// [m]
	float neutral_spot_dist = 0.45f;

	// white
	int white = 255;
	// black
	int black = 0; 

	int leftSideColor = 0xFF1010FF;
	int rightSideColor = 0xFFEFEF10;
	
	Field(float w, float h){
		width = w;
		height = h;
	};


	boolean invertColors = false;
	public void setColorInvertion(boolean invert){
		invertColors = invert;
	}

	public void draw(PApplet canvas, float scale){

		canvas.background(32,137,4); //green

		// playing field x_size [m]
		float x_pf = width - 2 * space - line_width;
		// playing field x_size [m]
		float y_pf = height - 2 * space - line_width;
		
		// center circle
		canvas.stroke(black);  
		canvas.noFill();
		canvas.ellipse(width*scale/2, height*scale/2, center_circle_diam*scale, center_circle_diam*scale);

		// left penalty area
		canvas.stroke(black);  
		canvas.fill(black);
		canvas.rect((space+line_width)*scale, (height/2-y_pen_area/2)*scale, x_pen_area*scale, line_width*scale); 
		canvas.rect((space+line_width)*scale, (height/2+y_pen_area/2)*scale, x_pen_area*scale, line_width*scale); 
		canvas.rect((space+x_pen_area)*scale, (height/2-y_pen_area/2)*scale, line_width*scale, y_pen_area*scale); 

		// right penalty area
		canvas.stroke(black);  
		canvas.fill(black);
		canvas.rect((width-x_pen_area-space-line_width)*scale, (height/2-y_pen_area/2)*scale, x_pen_area*scale, line_width*scale); 
		canvas.rect((width-x_pen_area-space-line_width)*scale, (height/2+y_pen_area/2)*scale, x_pen_area*scale, line_width*scale); 
		canvas.rect((width-x_pen_area-space-line_width)*scale, (height/2-y_pen_area/2)*scale, line_width*scale, y_pen_area*scale); 

		// field limits
		canvas.fill(white);  
		canvas.stroke(white);  
		canvas.rect(space*scale, space*scale, x_pf*scale, line_width*scale);  //top
		canvas.rect(space*scale, (y_pf+space)*scale, (x_pf+line_width)*scale, line_width*scale);  //bottom
		canvas.rect(space*scale, space*scale, line_width*scale, y_pf*scale);  //left
		canvas.rect((x_pf+space)*scale, space*scale, line_width*scale, y_pf*scale);  //right

		// neutral spots
		float neutral_diam = 0.01f * scale;
		canvas.stroke(black);  
		canvas.fill(black);
		canvas.ellipse(width/2*scale, height/2*scale, neutral_diam, neutral_diam);  // center
		canvas.ellipse((space+neutral_spot_dist)*scale, (height/2-y_goal/2)*scale, neutral_diam, neutral_diam);  // center
		canvas.ellipse((space+neutral_spot_dist)*scale, (height/2+y_goal/2)*scale, neutral_diam, neutral_diam);  // center
		canvas.ellipse((width-space-neutral_spot_dist)*scale, (height/2-y_goal/2)*scale, neutral_diam, neutral_diam);  // center
		canvas.ellipse((width-space-neutral_spot_dist)*scale, (height/2+y_goal/2)*scale, neutral_diam, neutral_diam);  // center

		// left goal
		canvas.noStroke();
		canvas.stroke((invertColors ? rightSideColor : leftSideColor)); // blue  
		canvas.fill((invertColors ? rightSideColor : leftSideColor));  // blue
		canvas.rect((space-x_goal + line_width)*scale, (height/2-y_goal/2)*scale, x_goal*scale, y_goal*scale);  // center

		// right goal
		canvas.stroke((invertColors ? leftSideColor : rightSideColor)); // yellow  
		canvas.fill((invertColors ? leftSideColor : rightSideColor));  // yellow
		canvas.rect((width-space-line_width)*scale, (height/2-y_goal/2)*scale, x_goal*scale, y_goal*scale);  // center

	}
}