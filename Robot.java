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
	private HashMap<String, RobotSensor> mappedSensors = new HashMap<String, RobotSensor>();
	private ArrayList<RobotSensor> sensors = new ArrayList<RobotSensor>();

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
	protected void registerSensor(RobotSensor sensor, String ID){
		mappedSensors.put(ID, sensor);
		sensors.add(sensor);
	}

	/*
		Returns the Sensor with the specified ID
	*/
	public RobotSensor getSensor(String ID){
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
		Methods publicly available
	*/
	public void setTargetSpeed(PVector speed){
		targetSpeed = speed.get();
		targetSpeed.limit(maxSpeed);
	}

	public void setTargetSpeed(float x, float y){
		targetSpeed.set(x, y);
		targetSpeed.limit(maxSpeed);
	}

	public void setTargetAngularSpeed(float speed){
		targetAngularSpeed = speed;
	}

	public void stopMotors(){
		setTargetSpeed(0,0);
		setTargetAngularSpeed(0);
	}

	public float getOrientation(){
		return orientation;
	}

	public void run(){
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
		// System.out.println("State changed: "+state+" ["+this+"]");
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
	}

};