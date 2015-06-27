import processing.core.*;

class RobotBasic extends Robot{

	RobotBasic(GameSimulator g){
		super(g);
	}

	protected void initializeSensors(GameSimulator game){

		BallLocator locator = new BallLocator(game, this);
		registerSensor(locator, "BALL");

		CompassSensor compass = new CompassSensor(game, this);
		registerSensor(compass, "COMPASS");

		UsDistanceSensor ultrasonic_left = new UsDistanceSensor(game, this, 270f);
		registerSensor(ultrasonic_left, "ULTRASONIC_LEFT");

		UsDistanceSensor ultrasonic_right = new UsDistanceSensor(game, this, 90f);
		registerSensor(ultrasonic_right, "ULTRASONIC_RIGHT");

		UsDistanceSensor ultrasonic_front = new UsDistanceSensor(game, this, 0f);
		registerSensor(ultrasonic_front, "ULTRASONIC_FRONT");

		UsDistanceSensor ultrasonic_back = new UsDistanceSensor(game, this, 180f);
		registerSensor(ultrasonic_back, "ULTRASONIC_BACK");

	}


	public void run(){
		System.out.println("I'm running!");
	}

}