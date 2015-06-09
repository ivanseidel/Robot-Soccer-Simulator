import processing.core.*;
import processing.*;
import java.util.*;

public class GameController implements Drawable, Runnable{

	private GameSimulator simulator;
	private Judge judge;

	// Array of Robots currently registered in this game
	public ArrayList<Robot> robots = new ArrayList<Robot>();

	// Threads of the Robots, mapped to each Robot
	private HashMap<Robot, Thread> robotThreads = new HashMap<Robot, Thread>();

	// Side of the robots, mapped to each Robot
	private HashMap<Robot, TeamSide> robotSides = new HashMap<Robot, TeamSide>();

	// Global start time of simulation (to know relative time from beginning)
	private long startTime;

	// Number of desired players for each side
	private int teamPlayers;
	GameController(int teamPlayers){
		this.teamPlayers = Math.max(teamPlayers, 1);

		judge = new Judge(this);

		resetGame();
	}

	public GameSimulator getSimulator(){
		return simulator;
	}

	/*
		Stores Points for both Sides
	*/
	private int goalsLeft = 0;
	private int goalsRight = 0;

	/*
		Get points for one side
	*/
	public int getPointsFor(TeamSide side){
		if(side == TeamSide.LEFT)
			return goalsLeft;
		else
			return goalsRight;
	}

	/*
		Add points to one side
	*/
	public void addPointsFor(TeamSide side, int points){
		System.out.println("Add points for "+side+":"+points);
		if(side == TeamSide.LEFT)
			goalsLeft += points;
		else
			goalsRight += points;
	}

	/*
		Called every time to update Game and simulate it	
	*/
	private float time;
	public void run(){
		time = (System.currentTimeMillis() - startTime) / 1000f;
		simulator.simulate(time);

		judge.judge(time);
		
	}

	/*
		***** State controllers *****
	*/
	public void resetGame(){

		// Restart start time
		startTime = System.currentTimeMillis();

		// Reset Points
		goalsLeft = goalsRight = 0;

		// Create game Simulator
		simulator = new GameSimulator();

		// Delegate reset to judge
		judge.onGameControllerReseted();
		
		// Remove Robots
		while(robots.size() > 0){
			unRegisterRobot(robots.get(0));
		}

		Team a = new BasicTeam();
		Team b = new BasicTeam();

		for(int i = 0; i < teamPlayers; i++){
			a.setTeamSide(TeamSide.LEFT);
			Robot ar = a.buildRobot(simulator, i);
			ar.setTeamColor(0xFF0000FF);
			registerRobot(ar, TeamSide.LEFT);

			b.setTeamSide(TeamSide.RIGHT);
			Robot br = b.buildRobot(simulator, i);
			br.setTeamColor(0xFFFFFF00);
			registerRobot(br, TeamSide.RIGHT);
		}

	}

	// If game is running or not
	private boolean running = false;
	public boolean isRunning(){
		return running;
	}

	/*
		Restart position of robots and ball
	*/
	public void restartPositions(){
		for(Robot r:robots)
			placeRobot(r);

		moveBallToSpot(simulator.fieldCenter);
	}

	public void startGame(){
		for(Robot r:robots)
			startRobot(r);

		simulator.ball.setOn(true);

		running = true;
	}

	public void pauseGame(){
		for(Robot r:robots)
			pauseRobot(r);

		simulator.ball.setOn(false);

		running = false;
	}

	protected void moveBallToSpot(PVector newPosition){
		if(newPosition == null){
			// Finds a neutral spot near it
			PVector ballPos = simulator.ball.getRealPosition();
			newPosition = simulator.getNearestNeutralSpot(ballPos);
		}
		simulator.ball.setPosition(newPosition);
	}

	/*
		Render UI and simulated Game
	*/
	PVector simulatorPos = new PVector(0, 100);
	public void draw(PApplet canvas, float scale){


		canvas.translate(simulatorPos.x, simulatorPos.y);
		simulator.draw(canvas, scale);
		canvas.translate(-simulatorPos.x, -simulatorPos.y);

		canvas.textSize(48);
		canvas.fill(255);
		canvas.textAlign(PApplet.CENTER);
		String text = getPointsFor(TeamSide.LEFT)+" x "+getPointsFor(TeamSide.RIGHT);
		canvas.text(text, getWidth(scale)/2, 50);
	}

	/*
		Returns robot list
	*/
	public ArrayList<Robot> getRobots(){
		return robots;
	}

	/*
		Only Places robot in the field, but don't run it
	*/
	protected void placeRobot(Robot r, PVector position, float orientation){
		simulator.addToSimulation(r);
		r.setState(position, orientation, true);
		judge.onRobotPlaced(r);
		r.onStateChanged("PLACED");
	}

	/*
		Same as placeRobot, except that it finds a neutral spot for it
		
		If none found, will return in the middle of the field
	*/
	protected void placeRobot(Robot r){
		TeamSide side = robotSides.get(r);
		PVector spot = new PVector(simulator.field.width/2, simulator.field.height/2);
		PVector[] spots = simulator.getNeutralSpots(side);

		float orientation = (float)(side == TeamSide.LEFT ? 0 : Math.PI);
		
		for(int i = 0; i < spots.length; i++){
			r.setState(spots[i], orientation, false);
			if(simulator.isColliding(r)) continue;
			spot = spots[i];
		}
		placeRobot(r, spot, orientation);
	}

	/*
		Start robot Thread
	*/
	protected void startRobot(Robot r){
		// If robot is not in simulator, we place it
		if(!simulator.inSimulation(r))
			placeRobot(r);

		Thread t = robotThreads.get(r);
		if(!t.isAlive())
			t.start();
		else
			t.resume();

		r.onStateChanged("STARTED");
	}

	/*
		Pauses a robot, but it remains in the field
		What happens:
			Thread is paused
			Motors are stopped
	*/
	protected void pauseRobot(Robot r){
		Thread t = robotThreads.get(r);
		t.suspend();
		r.stopMotors();
		r.onStateChanged("PAUSED");
	}

	/*
		Stop robot Thread
		What happens:
			Robot is Paused
			Robot is removed from simulation
	*/
	protected void removeRobot(Robot r){
		pauseRobot(r);
		simulator.removeFromSimulation(r);
		judge.onRobotRemoved(r);
		r.onStateChanged("REMOVED");
	}

	/*
		Add a new robot
	*/
	protected void registerRobot(Robot r, TeamSide side){
		robots.add(r);

		// Creates a thread for this robot
		Thread robotThread = new Thread(r);
		robotThreads.put(r, robotThread);
		robotSides.put(r, side);

		judge.onRobotRegistered(r, side);

		r.onStateChanged("ADDED");
	}

	/*
		Stops robot and removes it
	*/
	protected void unRegisterRobot(Robot r){
		System.out.println("unRegisterRobot: "+this);
		// Remove robot from simulation
		removeRobot(r);

		// Remove robot from everywhere
		robotThreads.remove(r);
		robotSides.remove(r);
		robots.remove(r);

		judge.onRobotUnegistered(r);
	}

	/*
		Calculate width of view
	*/
	public float getWidth(float scale){
		return simulatorPos.x + simulator.field.width * scale;
	}

	/*
		Calculate height of view
	*/
	public float getHeight(float scale){
		return simulatorPos.y + simulator.field.height * scale;
	}
}