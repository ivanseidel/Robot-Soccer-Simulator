import processing.core.*;
import java.util.*;

public class Robot extends Simulatable implements ShapeCircle, Drawable, Runnable{

	/*
		Saves the GameSimulator in order to access global stuff such as objects from field
	*/
	private GameSimulator game;

	/*
		Robot Sensors ArrayList, where each of them can be read.
	*/
	private HashMap<String, Sensor> mappedSensors = new HashMap<String, Sensor>();
	private ArrayList<Sensor> sensors = new ArrayList<Sensor>();

	/*
		Physics attributes used to simulate orientation and position of robot
	*/
	// Speed state attributes (N/m and m/s)
	private float motorForce = 5f;
	private float maxSpeed = 0.5f;
	private PVector targetSpeed = new PVector(0,0);

	// Orientation attributes
	private float maxAngularAccel = (float)Math.PI * 10;
	private float maxAngularSpeed = (float)Math.PI * 10;
	private float targetAngularSpeed = 0f;
	private float angularSpeed = 0f;

	public float orientation = 0f;


	Robot(GameSimulator g){
		game = g;

		// Initialize sensors
		initializeSensors(game);
	}

	public void setState(PVector position, float orientation, boolean resetAll){
		this.position = position.get();
		this.orientation = orientation;
		
		if(resetAll){
			this.force = new PVector();
			this.accel = new PVector();
			this.speed = new PVector();
		}
	}

	/*
		This is where Sensor instantiation and setup should occur.
	*/
	protected void initializeSensors(GameSimulator game){}

	/*
		Register a sensor inside this robot, with the given ID
	*/
	protected void registerSensor(Sensor sensor, String ID){
		mappedSensors.put(ID, sensor);
		sensors.add(sensor);
	}

	/*
		Returns the Sensor with the specified ID
	*/
	public Sensor getSensor(String ID){
		return mappedSensors.get(ID);
	}

	/*
		Physical properties
	*/
	public float getRadius(){
		return 0.11f;
	}

	public float getMass(){
		return 2.2f;
	}

	public float getKFactor(){
		return 1f;
	}

	public boolean canCollide(Simulatable s){
		if(s instanceof GoalWall)
			return false;

		return true;
	}

	/*
		This is the method used by the Thread.
	*/
	public void run(){
		
	}

	/*
		Methods publicly available
	*/
	// public void setSpeed(PVector speed){
	// 	targetSpeed = speed.get();
	// 	targetSpeed.limit(maxSpeed);
	// }

	// Change robot forward/backward speed only
	public void setSpeed(float x){
		targetSpeed.set(x, 0);
		targetSpeed.limit(maxSpeed);
	}

	// Change robot's front/back left/right speed
	public void setSpeed(float x, float y){
		if(Float.isNaN(x) ||  Float.isNaN(y)){
			// System.out.println("Excep: setSpeed "+x+" "+y);
			return;
		}
		targetSpeed.set(x, y);
		targetSpeed.limit(maxSpeed);
	}

	// Set the target angular speed
	public void setRotation(float speed){
		if(speed == Float.NaN){
			System.out.println("Excep: setRotation");
			return;
		}
		targetAngularSpeed = (float)Math.toRadians(speed);
	}

	public void stopMotors(){
		setSpeed(0,0);
		setRotation(0);
	}

	public float getOrientation(){
		return orientation;
	}

	public long millis(){
		return (long) (game.getTime() * 1000);
	}

	public boolean delay(int time){
		try{
			Thread.sleep(time);
		}catch(Exception e){
			return false;
		}
		return true;
	}

	public void onStateChanged(String state){
	}

	/*
		Perform Simulation
	*/
	public void simulate(float dt){
		// Accelerate angular speed
		float requiredSpeed = targetAngularSpeed - angularSpeed;
		float angularAccel = requiredSpeed / dt;
		angularAccel = PApplet.constrain(angularAccel, -maxAngularAccel, maxAngularAccel);

		// Limit Angular speed
		angularSpeed += angularAccel * dt;
		angularSpeed = PApplet.constrain(angularSpeed, -maxAngularSpeed, maxAngularSpeed);
		
		// Orientation Simulation
		orientation += angularSpeed * dt;

		// Position simulation
		PVector worldRequiredSpeed = targetSpeed.get();
		worldRequiredSpeed.rotate(orientation);
		worldRequiredSpeed.sub(speed);

		// PVector worldRequiredSpeed = worldTargetSpeed.get();
		float dSpeed = worldRequiredSpeed.mag();
		float dAcell = dSpeed / dt;
		float dForce = Math.min(dAcell * getMass(), motorForce);

		worldRequiredSpeed.normalize();
		worldRequiredSpeed.mult(dForce);
		force.add(worldRequiredSpeed);

		super.simulate(dt);
	}

	int teamColor = 0x000000;
	public void setTeamColor(int color){
		teamColor = color;
	}

	public void draw(PApplet canvas, float scale){
		PVector orient = PVector.fromAngle(orientation);
		orient.mult(scale * getRadius());
		float x = (float) position.x * scale;
		float y = (float) position.y * scale;
		float diameter = getRadius() * 2 * scale;

		canvas.fill(teamColor);
		canvas.stroke(0);
		canvas.ellipse(x, y, diameter, diameter);
		canvas.line(x, y, x + (float)orient.x, y + (float)orient.y);

		// Delegate Decoration to Robot
		float heading = orient.heading();
		float drawScale = 100f / scale * getRadius();

		canvas.translate(x, y);
		canvas.rotate(heading);
		canvas.scale(drawScale);
		// TODO: How to resolve scale, so that teams don't have to mind it also...
		decorateRobot(canvas);
		canvas.scale(1f/drawScale);
		canvas.rotate(-heading);
		canvas.translate(-x, -y);
	}

	/*
	 * This method can be used to decorate the robot appearence.
	 * It is called everytime after rendering itself
	 * The area that is drawable, is a 100px x 100px, sacaled to
	 * the robot. The center is at [0,0], and Y axis indicate the
	 * Robot's Front
	 */
	public void decorateRobot(PApplet canvas){

	}

};