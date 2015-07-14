public class CustomTeamA implements Team{
	
	public String getTeamName(){
		return "Emerotecos";
	}
	
	public void setTeamSide(TeamSide side){
		
	}
	
	public Robot buildRobot(GameSimulator s, int index){
		if(index == 0)
			return new Attacker(s);
		if(index == 1)
			return new Goalier(s);

		// By default, return a new attacker
		return new Attacker(s);
	}
	
	class Attacker extends RobotBasic{
		Attacker(GameSimulator s){
			super(s);
		}
		
		float speedMultiplier = (float)Math.random() * 5 + 5;
		
		Sensor locator;

		public void setup(){
			System.out.println("Running!");
			locator = getSensor("BALL");
		}

		public void loop(){
			float angle = locator.readValue(0);

			setRotation(angle * speedMultiplier);
			setSpeed(0.5f,0);
			delay(100);
		}
	}
	
	class Goalier extends RobotBasic{
		Goalier(GameSimulator s){
			super(s);
		}

		float divisor = (float)Math.random() * 150 + 70;
		
		Sensor locator;
		// Front, left, back, right
		Sensor[] ultrasonic_sensors = new Sensor[4];
		
		public void run(){
			locator = getSensor("BALL");

			ultrasonic_sensors[0] = getSensor("ULTRASONIC_FRONT");
			ultrasonic_sensors[1] = getSensor("ULTRASONIC_LEFT");
			ultrasonic_sensors[2] = getSensor("ULTRASONIC_BACK");
			ultrasonic_sensors[3] = getSensor("ULTRASONIC_RIGHT");
			
			System.out.println("Running!");
			while(true){
				float angle = locator.readValue(0);
				
				if(Math.abs(angle) < 90)
					setSpeed(0f, angle / divisor);
				else
					setSpeed(0f, 0f);
				
				delay(100);
			}
		}
	}
	
}