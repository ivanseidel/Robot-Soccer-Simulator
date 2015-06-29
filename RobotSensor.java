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

	/**
	 * Applies random noise to a given value
	 *
	 * @return The new computed reading
	 */
	protected float getReadingAfterNoise(float reading, float noise) {
		float addedNoise = (float) Math.random() * noise - noise / 2f;
		return reading + addedNoise;
	}
}