import processing.core.*;

class CompassSensor extends RobotSensor{

	// Random noise applyed to the final reading
	private static final int NOISE_AMMOUNT = 3;

	// Interval to read the sensor in seconds
	private static final float READ_INTERVAL = 0.01f;

	float[] values = new float[1];

	CompassSensor(GameSimulator g, Robot r){
		super(g, r);
	}

	float lastRead = 0;
	public float[] readValues(){
		if(game.getTime() >= lastRead + 0.01f)
			doReading();

		return values;
	}

	private void doReading(){
		Robot thisRobot = getRobot();
		// FFFFFFFFFFFFFFFFFFFFFFF - Acrescentei o (float) nas linhas seguintes
		float orientation = (float)Math.toDegrees(thisRobot.orientation);
		float noise = (float)Math.random() * NOISE_AMMOUNT - NOISE_AMMOUNT / 2f;

		values[0] = orientation + noise;
	}

}
