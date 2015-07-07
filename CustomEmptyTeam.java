import processing.core.*;
import java.util.*;

public class CustomEmptyTeam implements Team{
	
	public String getTeamName(){
		return "Dumb team";
	}

	public void setTeamSide(TeamSide side){

	}

	public Robot buildRobot(GameSimulator s, int index){
		return new EmptyRobot(s);
	}
 
	class EmptyRobot extends RobotBasic{
		EmptyRobot(GameSimulator s){
			super(s);
		}

		/*
			Movement methods:
				void setSpeed(FORWARD_SPEED) in m/s
				void setSpeed(FORWARD_SPEED, SIDE_SPEED) in m/s

				void setRotation(ANGULAR_SPEED) in degrees/s

			Extra methods:
				void delay(MS) in miliseconds
				long millis() returns current time in miliseconds since simulation started
		*/


		public void setup(){
			/*
				You should use this method to initialize your code,
				setup Sensors and variables.

				It will be runned once.
			*/
		}

		public void loop(){
			/*
				This is the place where you should place the control
				code for your robot. It is called everytime it returns,
				unlimited times.
			*/
		}

		/*
			If you want to code the thread method yourself, instead of
			using the already made `setup` and `loop` methods, you can
			override the method `run`. Uncomment those lines to use.
		*/
		// public void run(){
			
		// }

		/*
			You can use this method to decorate your robot.
			use Processing methods from the `canvas` object.

			The center of the robot is at [0,0], and the limits
			are 100px x 100px.
		*/
		public void decorateRobot(PApplet canvas){
			
		}

		/*
			Called whenever a robot is:
				PAUSED
				REMOVED from field
				PLACED in field
				STARTED
		*/
		public void onStateChanged(String state){
		}
	}
 
}