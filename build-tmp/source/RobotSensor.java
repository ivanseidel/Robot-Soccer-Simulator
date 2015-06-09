class RobotSensor{
	protected GameSimulator game;
	protected Robot robot;

	RobotSensor(GameSimulator g, Robot r){
		game = g;
		robot = r;
	}

	protected GameSimulator getGameSimulator(){
		return game;
	}

	protected Robot getRobot(){
		return robot;
	}

	/*
		Performs readings
	*/
	public float[] readValues(){
		return null;
	}
}
