import processing.core.*;

class SensorBall extends Sensor{

	float[] values = new float[2];
	float sensorLimit = 1f;

	SensorBall(GameSimulator g, Robot r){
		super(g, r);
	}

	float lastRead = 0;
	public float[] readValues(){
		// Avoid multiple readings within 100ms
		if(game.getTime() >= lastRead + 0.1f)
			doReading();

		return values;
	}

	private void doReading(){
		Robot thisRobot = getRobot();
		Ball ball = getGameSimulator().ball;

		// Check if ball is turned off
		if(!ball.isOn()){
			values[0] = 0;
			values[1] = 0;
			return;
		}

		// Find relative distance from Ball to Robot
		PVector dist = PVector.sub(ball.position, thisRobot.position);
		dist.rotate(-thisRobot.getOrientation());

		// index 0 contains the Angle of the ball
		values[0] = (float)Math.toDegrees(dist.heading());
		// index 1 contains the distance to the ball
		values[1] = (float)Math.min(dist.mag(), sensorLimit);
	}

}