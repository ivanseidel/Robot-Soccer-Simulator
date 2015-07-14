class Sensor{
	protected GameSimulator game;
	protected Robot robot;

	Sensor(GameSimulator g, Robot r){
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

	/*
		Performs readings and returns the value at index
	*/
	public float readValue(int index){
		return readValues()[index];
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