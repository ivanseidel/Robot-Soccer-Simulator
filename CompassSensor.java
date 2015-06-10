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

		float orientation = (float)Math.toDegrees(thisRobot.orientation);
		float noise = (float)Math.random() * NOISE_AMMOUNT - NOISE_AMMOUNT / 2f;

		// Fix orientation (from 0-359)
		int multiples = (int)(orientation / 360);
		orientation = orientation - multiples * 360;
		while(orientation < 0)
			orientation += 360;

		values[0] = orientation + noise;
	}

}
