public class CustomForwardTeam implements Team{

	public String getTeamName(){
		return "Forward";
	}

	public void setTeamSide(TeamSide side){

	}

	public Robot buildRobot(GameSimulator s, int index){
		return new Forward(s);
	}

	class Forward extends RobotBasic{
		Forward(GameSimulator s){
			super(s);
		}

		public void setup(){
			System.out.println("Running!");
		}

		public void loop(){
			setSpeed(0.5f,0);
			delay(100);
		}
	}
}