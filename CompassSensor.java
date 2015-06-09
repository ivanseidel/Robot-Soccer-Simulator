import processing.core.*;

class CompassSensor extends RobotSensor{

	private static final int NOISE_AMMOUNT = 3;
	float[] values = new float[1];

	CompassSensor(GameSimulator g, Robot r){
		super(g, r);
	}

	float lastRead = 0;
	public float[] readValues(){
		if(game.getTime() >= lastRead + 0.1f)
			doReading();

		return values;
	}

	private void doReading(){
		Robot thisRobot = getRobot();
		// FFFFFFFFFFFFFFFFFFFFFFF - Acrescentei o (float) nas linhas seguintes
		float orientation = (float)Math.toDegrees(thisRobot.orientation);
		float noise = (float)Math.random() * NOISE_AMMOUNT;

		values[0] = orientation + noise;
	}

}
