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

	}


	public void run(){
		System.out.println("I'm running!");
	}

}
