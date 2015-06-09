import processing.core.*;
import processing.*;
import java.util.*;

public class Judge{

	private float BALL_OUTSIDE_TIME_LIMIT = 0.5f;
	private float ROBOT_TAKEN_OUTSIDE_TIME = 5f;
	private float BALL_STOPPED_TIME_LIMIT = 5f;

	private GameController controller;
	private GameSimulator simulator;

	// Robots that have been taken out of field for some reason
	private HashSet<Robot> takenOutRobots = new HashSet<Robot>();
	private HashMap<Robot, Timer> takenOutTimers = new HashMap<Robot, Timer>();

	Judge(GameController controller){
		this.controller = controller;
	}

	float time;
	public void judge(float time){
		this.time = time;

		// Skip Judging if is paused
		if(!controller.isRunning())
			return;

		// Check if Ball is outside field for a long time
		checkBallOutside();

		// Check if Ball is stopped for a long time
		checkBallStopped();

		// Remove robots from game, if they are outside
		checkRobotsOutside();

		// Put back robots that have been outside for longer time
		putBackRobotsOutside();

		// Check if scored goals
		checkGoal();
	}

	private Timer ballOutsideTimer = new Timer(BALL_OUTSIDE_TIME_LIMIT, true);
	private boolean isOut = false;
	private void checkBallOutside(){
		Ball b = simulator.ball;

		if(b.colliding(simulator.fieldArea)){
			ballOutsideTimer.reset(time);
		}else{
			if(ballOutsideTimer.triggered(time)){
				controller.moveBallToSpot(null);
			}
		}
	}

	private Timer ballStoppedTimer = new Timer(BALL_STOPPED_TIME_LIMIT, true);
	private void checkBallStopped(){
		Ball b = simulator.ball;

		if(b.getRealSpeed().magSq() == 0){
			if(ballStoppedTimer.triggered(time)){
				controller.moveBallToSpot(null);
			}
		}else{
			ballStoppedTimer.reset(time);
		}
	}

	private void checkRobotsOutside(){
		for(Robot r:controller.getRobots()){
			if(takenOutRobots.contains(r))
				continue;

			if(!simulator.fieldArea.colliding(r)){
				controller.removeRobot(r);
			}
		}
	}

	private void putBackRobotsOutside(){
		for(Robot r:controller.getRobots()){
			if(!takenOutRobots.contains(r))
				continue;

			Timer outTimer = takenOutTimers.get(r);
			if(outTimer.triggered(time)){
				controller.startRobot(r);
			}
		}
	}

	private void checkGoal(){
		Ball b = simulator.ball;

		if(b.colliding(simulator.goalLeft) && !b.colliding(simulator.fieldArea)){
			controller.addPointsFor(TeamSide.RIGHT, 1);
			controller.restartPositions();
		}else if(b.colliding(simulator.goalRight) && !b.colliding(simulator.fieldArea)){
			controller.addPointsFor(TeamSide.LEFT, 1);
			controller.restartPositions();
			controller.startGame();
		}
	}

	/*
		***** Callbacks from Controller
	*/

	/*
		Did reset everything
	*/
	public void onGameControllerReseted(){
		simulator = controller.getSimulator();
		takenOutRobots.clear();
	}

	public void onRobotRegistered(Robot r, TeamSide side){
		takenOutRobots.add(r);
		takenOutTimers.put(r, new Timer(ROBOT_TAKEN_OUTSIDE_TIME));
	}

	public void onRobotUnegistered(Robot r){
		takenOutRobots.remove(r);
	}

	public void onRobotPlaced(Robot r){
		takenOutRobots.remove(r);
	}

	public void onRobotRemoved(Robot r){
		takenOutRobots.add(r);
		takenOutTimers.get(r).reset(time);
	}


		

}