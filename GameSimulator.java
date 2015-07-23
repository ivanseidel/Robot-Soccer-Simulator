import processing.core.*;
import processing.*;
import java.util.*;

public class GameSimulator implements Drawable{

	private static float FIELD_W = 2.44f;
	private static float FIELD_H = 1.82f;
	private static final float WALL_TICK = 0.10f;

	public Vector<Simulatable> simulatables = new Vector<Simulatable>();
	private Vector<Drawable> drawables = new Vector<Drawable>();

	private Block[] walls = new Block[4];

	public Field field;
	public Ball ball;

	// Blocks used to define neutral spots and see if something is hitting it
	public Block[] neutralSpots = new Block[4];
	// This block is not simulated, but exists to define the inner field area
	public Block fieldArea;

	public Goal goalLeft, goalRight;
	public GoalWall[] goalWalls = new GoalWall[4];

	public PVector fieldCenter;

	GameSimulator(){
		reset();
	}

	public void setFieldSize(float w, float h){
		FIELD_W = w;
		FIELD_H = h;
		reset();
	}

	public float getFieldWidth(){
		return FIELD_W - field.space*2;
	}

	public float getFieldHeight(){
		return FIELD_H - field.space*2;
	}

	public void reset(){
		// Erase everything
		simulatables.clear();
		drawables.clear();

		// Create Field
		field = new Field(FIELD_W, FIELD_H);
		drawables.add(field);

		// Create inner field area
		fieldArea = new Block(FIELD_W/2, FIELD_H/2, FIELD_W - field.space*2, FIELD_H - field.space * 2);
		// drawables.add(fieldArea);

		// Save field center
		fieldCenter = new PVector(field.width / 2, field.height / 2);

		// Create Ball
		ball = new Ball();
		ball.position = fieldCenter.get();
		simulatables.add(ball);
		drawables.add(ball);

		// 
		// Make Walls
		walls[0] = new Block(FIELD_W / 2,       0, FIELD_W, WALL_TICK); // TOP
		walls[1] = new Block(FIELD_W / 2, FIELD_H, FIELD_W, WALL_TICK); // BOTTOM
		walls[2] = new Block(0,       FIELD_H / 2, WALL_TICK, FIELD_H + WALL_TICK); // LEFT
		walls[3] = new Block(FIELD_W, FIELD_H / 2, WALL_TICK, FIELD_H + WALL_TICK); // RIGHT

		for(int i = 0; i < 4; i ++){
			simulatables.add(walls[i]);
			drawables.add(walls[i]);
		}

		// Make Neutral Spots
		float xZero = FIELD_W/2;
		float yZero = FIELD_H/2;
		float xDelta = (FIELD_W - field.space * 2 - field.neutral_spot_dist * 2) / 2;
		float yDelta = field.y_goal/2;
		neutralSpots[0] = new Block(xZero - xDelta, yZero + yDelta, 0.05f, 0.05f);
		neutralSpots[1] = new Block(xZero - xDelta, yZero - yDelta, 0.05f, 0.05f);
		neutralSpots[2] = new Block(xZero + xDelta, yZero + yDelta, 0.05f, 0.05f);
		neutralSpots[3] = new Block(xZero + xDelta, yZero - yDelta, 0.05f, 0.05f);
		// for(int i = 0; i < 4; i ++){
		// 	drawables.add(neutralSpots[i]);
		// }

		// Make Goals
		xDelta = (FIELD_W - field.space*2 - field.line_width*2 + field.x_goal) / 2;
		goalLeft = new Goal(xZero - xDelta, yZero, field.x_goal, field.y_goal);
		goalRight = new Goal(xZero + xDelta, yZero, field.x_goal, field.y_goal);
		simulatables.add(goalLeft);
		// drawables.add(goalLeft);
		simulatables.add(goalRight);
		// drawables.add(goalRight);

		// Make Goal Walls
		float tickness = field.line_width * 2;
		yDelta = (field.y_goal + tickness)/2;
		xDelta = (FIELD_W - field.space*2 - field.line_width*2 + field.space) / 2;
		goalWalls[0] = new GoalWall(xZero - xDelta, yZero - yDelta, field.space, tickness);
		goalWalls[1] = new GoalWall(xZero - xDelta, yZero + yDelta, field.space, tickness);
		goalWalls[2] = new GoalWall(xZero + xDelta, yZero - yDelta, field.space, tickness);
		goalWalls[3] = new GoalWall(xZero + xDelta, yZero + yDelta, field.space, tickness);
		for(int i = 0; i < 4; i ++){
			// drawables.add(goalWalls[i]);
			simulatables.add(goalWalls[i]);
		}
	}

	float lastT = 0;
	public float getTime(){
		return lastT;
	}

	public void simulate(float t){
		float dt = t - lastT;
		lastT = t;
		if(dt > 0.5 || dt <= 0f){
			return;
		}

		// Simulate Physics
		for(Simulatable s:simulatables){
			s.simulate(dt);
			
			// Simulate Collisions
			for(Simulatable b:simulatables){
				if(b == s || !s.canCollide(b))
					continue;

				if(s.colliding(b))
					s.resolveCollision(b);
			}
		}

	}

	/*
		Returns a list of NeutralSpots for the Side given
	*/
	public PVector[] getNeutralSpots(TeamSide side){
		if(side == TeamSide.LEFT){
			return new PVector[]{
				neutralSpots[0].getRealPosition(),
				neutralSpots[1].getRealPosition()
			};
		}else{
			return new PVector[]{
				neutralSpots[2].getRealPosition(),
				neutralSpots[3].getRealPosition()
			};
		}
	}

	/*
		Return neares neutral spot to this position
	*/
	public PVector getNearestNeutralSpot(PVector point){
		PVector nearest = null;
		float nearesDist = 0;
		for(int i = 0; i < neutralSpots.length; i++){
			PVector thisPos = neutralSpots[i].getRealPosition();
			float thisDist = PVector.sub(point, thisPos).mag();

			if(nearest == null || thisDist < nearesDist){
				nearest = thisPos;
				nearesDist = thisDist;
			}
		}
		return nearest;
	}

	/*
		Returns if an Simulatable will hit another simulatable
	*/
	public boolean isColliding(Simulatable test){
		for(Simulatable s:simulatables){
			if(test == s) continue;
			if(s.colliding(test))
				return true;
		}
		return false;
	}

	public float closestSimulatableInRay(Robot robot, PVector origin, float direction) {
		float dist = Float.POSITIVE_INFINITY;
		synchronized(simulatables) {
			Iterator i = simulatables.iterator();
			while(i.hasNext()){
				Simulatable sim = (Simulatable)i.next();
				
				if (sim == robot || sim instanceof Ball || sim instanceof GoalWall)
					continue;
				
				dist = Math.min(dist, MathUtil.rayDistance(origin, direction, sim));
			}
		}

		return dist;
	}

	public void addToSimulation(Robot s){
		if(simulatables.contains(s))
			return;

		simulatables.add(s);
		drawables.add(s);
	}

	public boolean inSimulation(Simulatable s){
		return simulatables.contains(s);
	}


	public void removeFromSimulation(Robot s){
		simulatables.remove(s);
		drawables.remove(s);
	}

	public void draw(PApplet canvas, float scale){
		for(Drawable d:drawables)
			d.draw(canvas, scale);
	}

}