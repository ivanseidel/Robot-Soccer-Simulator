import processing.core.*;

/**
 * Simulates an ultrasonic distance sensor.
 * Readings are synchronous and blocking.
 */

class UsDistanceSensor extends RobotSensor{

	// Random noise applied to the final reading
	private static final float NOISE_AMOUNT = .01f;

	// Angle in degrees between robot's heading and sensor heading
	public float localOrientation = 0f;

	UsDistanceSensor(GameSimulator g, Robot r, float localOrientation) {
		super(g, r);
		this.localOrientation = localOrientation;
	}

	/**
	 * @return float array of size 1 with the current distance
	 */
	// TODO this should fail to read based on the angle between ray and surface
	public float[] readValues() {
		float dist = game.closestSimulatableInRay(robot, this);
		dist = getReadingAfterNoise(dist, NOISE_AMOUNT);

		float[] values = new float[1];
		values[0] = dist;
		return values;
	}

}