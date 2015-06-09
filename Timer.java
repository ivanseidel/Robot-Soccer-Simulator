public class Timer{
	
	private float limit = 0f;
	private float lastT = 0f;
	private boolean autoReset = false;

	Timer(float limit, boolean autoReset){
		this.limit = limit;
		this.autoReset = autoReset;
	}

	Timer(float limit){
		this.limit = limit;
	}

	public void reset(float t){
		lastT = t;
	}

	public boolean triggered(float t){
		boolean trig = false;
		if(t > lastT + limit)
			trig = true;

		// Autoreset timer
		if(trig && autoReset)
			reset(t);

		return trig;
	}
}