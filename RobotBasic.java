import processing.core.*;

class RobotBasic extends Robot{

	RobotBasic(GameSimulator g){
		super(g);
	}

	protected void initializeSensors(GameSimulator game){

		SensorBall locator = new SensorBall(game, this);
		registerSensor(locator, "BALL");

		SensorCompass compass = new SensorCompass(game, this);
		registerSensor(compass, "COMPASS");

		SensorDistance ultrasonic_left = new SensorDistance(game, this, 270f);
		registerSensor(ultrasonic_left, "ULTRASONIC_LEFT");

		SensorDistance ultrasonic_right = new SensorDistance(game, this, 90f);
		registerSensor(ultrasonic_right, "ULTRASONIC_RIGHT");

		SensorDistance ultrasonic_front = new SensorDistance(game, this, 0f);
		registerSensor(ultrasonic_front, "ULTRASONIC_FRONT");

		SensorDistance ultrasonic_back = new SensorDistance(game, this, 180f);
		registerSensor(ultrasonic_back, "ULTRASONIC_BACK");

	}

	/*
		Arduino-like flux
	*/
	public void run(){
		setup();

		while(true){
			loop();
			delay(1);
		}
	}

	public void setup(){};
	public void loop(){};

}