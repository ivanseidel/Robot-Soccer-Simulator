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

	// Match configuration for this Game
	private Match match;

	// Instantied teams
	Team a, b;

	GameController(Match match){
		this.match = match;

		// Create Judge
		judge = new Judge(this);

		// Create game Simulator
		simulator = new GameSimulator();

		// Reset everything
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

	private boolean lastside = true;
	private void setSideInvertion(boolean inverted){
		if(lastside != inverted){
			int lastGoalsLeft = goalsLeft;
			int lastGoalsRight = goalsRight;
			goalsLeft = lastGoalsRight;
			goalsRight = lastGoalsLeft;
		}

		simulator.field.setColorInvertion(inverted);
	}

	/*
		Called every time to update Game and simulate it	
	*/
	private float time;
	public void run(){
		// Skip simulation if not started
		if(!hasStarted())
			return;

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

		// Reset game Simulator
		simulator.reset();

		// Setup Team Sides and instantiate robots
		initTeamSides(true);

		// Restart positions
		restartPositions(TeamSide.LEFT);

		// Delegate reset to judge
		judge.onGameControllerReseted();

		started = false;
	}

	public void initTeamSides(boolean invertSide){
		
		// Remove Robots
		while(robots.size() > 0){
			unRegisterRobot(robots.get(0));
		}
		
		try{
			a = (Team)(invertSide ? match.TeamBClass : match.TeamAClass).newInstance();
			b = (Team)(invertSide ? match.TeamAClass : match.TeamBClass).newInstance();
		}catch(Exception e){
			System.out.println(e.toString());
			System.exit(1);
			return;
		}

		a.setTeamSide(TeamSide.LEFT);
		b.setTeamSide(TeamSide.RIGHT);

		// Build Robots for Team A
		for(int i = 0; i < (invertSide ? match.teamBPlayers : match.teamAPlayers); i++){
			Robot ar = a.buildRobot(simulator, i);
			if(ar != null){
				ar.setTeamColor(invertSide ? 0xFFFFFF00 : 0xFF0000FF);
				registerRobot(ar, TeamSide.LEFT);
			}
		}

		// Build Robots for Team B
		for(int i = 0; i < (invertSide ? match.teamAPlayers : match.teamBPlayers); i++){
			Robot br = b.buildRobot(simulator, i);
			if(br != null){
				br.setTeamColor(invertSide ? 0xFF0000FF : 0xFFFFFF00);
				registerRobot(br, TeamSide.RIGHT);
			}
		}

		// Fix points, by inverting them if needed
		setSideInvertion(invertSide);
	}

	// If simulation started, or not
	private boolean started = false;
	public boolean hasStarted(){
		return started;
	}

	// If game is running or not
	private boolean running = false;
	public boolean isRunning(){
		return running;
	}

	/*
		Restart position of robots and ball
	*/
	public void restartPositions(TeamSide vantage){
		moveBallToSpot(simulator.fieldCenter);

		restartTeamPosition(TeamSide.LEFT, vantage == TeamSide.LEFT);
		restartTeamPosition(TeamSide.RIGHT, vantage == TeamSide.RIGHT);
	}

	/*
		Restart position of robots and ball for a single team,
		but gives vantage to the TeamSide, by placing it near the ball.
		Example: Left team scored goal, so Right team starts with ball.

		Basically, we try to position robots in a vertical line, centered
		in the side of the team. That means: 1/4 of the field in X.
		If the team has vantage, then it will start a little closer.
	*/
	public void restartTeamPosition(TeamSide side, boolean vantage){

		// Count robots
		int count = 0;
		for(Robot r:robots){
			// Skip robots that are not from this team side
			if(robotSides.get(r) == side)
				count++;
		}

		// Get center of the field
		PVector start = simulator.fieldCenter.get();

		float fieldW = getSimulator().getFieldWidth();
		float fieldH = getSimulator().getFieldHeight();

		float offsetX = fieldW / (vantage ? 8 : 4);
		float offsetY = fieldH / (count + 1);

		start.x += (side == TeamSide.LEFT ? -1 : 1) * offsetX;
		start.y -= fieldH / 2;

		for(Robot r:robots){
			if(robotSides.get(r) != side)
				continue;

			start.y += offsetY;
			placeRobot(r, start);
		}
	}

	public void resumeGame(){
		// First place robots and add them to simulatables, then start threads,
		// otherwise we can get concurrency exceptions when reading the
		// collection of simulatables
		// for(Robot r:robots)
			// placeRobot(r);

		for(Robot r:robots)
			startRobot(r);

		simulator.ball.setOn(true);

		running = true;
		started = true;
	}

	public void pauseGame(){
		for(Robot r:robots){
			pauseRobot(r);

			try{
				Thread.sleep(2);
			}catch(Exception e){
				System.out.println(e);
			}
		}

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
	PVector simulatorPos = new PVector(0, 150);
	public void draw(PApplet canvas, float scale){


		canvas.translate(simulatorPos.x, simulatorPos.y);
		simulator.draw(canvas, scale);
		canvas.translate(-simulatorPos.x, -simulatorPos.y);

		canvas.fill(255);

		// Draw Score
		canvas.textSize(48);
		canvas.textAlign(PApplet.CENTER);
		String text = getPointsFor(TeamSide.LEFT)+" x "+getPointsFor(TeamSide.RIGHT);
		canvas.text(text, getWidth(scale)/2, 50);

		// Draw Time
		canvas.textSize(34);
		canvas.fill(0);
		canvas.textAlign(PApplet.CENTER);
		float time = judge.getCurrentTime();
		String mins = (int)(time / 60) + "";
		String secs = ((int)time) % 60 + "";
		if(secs.length() < 2)
			secs = "0"+secs;
		canvas.text(mins+":"+secs, getWidth(scale)/2, 90);

		// Draw State
		canvas.textSize(24);
		canvas.fill(200);
		canvas.textAlign(PApplet.CENTER);
		text = judge.getCurrentState();
		canvas.text(text, getWidth(scale)/2, 120);


		// Print Team Name on both sides
		canvas.fill(255);
		if(a != null){
			canvas.textSize(28);
			canvas.textAlign(PApplet.LEFT);
			canvas.text(a.getTeamName(), 20, 50);
			canvas.textSize(16);
			canvas.text(a.getClass().getSimpleName(), 20, 70);
		}   

		if(b != null){
			canvas.textSize(28);
			canvas.textAlign(PApplet.RIGHT);
			canvas.text(b.getTeamName(), getWidth(scale) - 20, 50);
			canvas.textSize(16);
			canvas.text(b.getClass().getSimpleName(), getWidth(scale) - 20, 70);
		}
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
		r.setState(position.get(), orientation, true);
		judge.onRobotPlaced(r);
		r.onStateChanged("PLACED");
	}

	/*
		Only Places robot in the field, but don't run it
	*/
	protected void placeRobot(Robot r, PVector position){
		TeamSide side = robotSides.get(r);
		float orientation = (float)(side == TeamSide.LEFT ? 0 : Math.PI);

		placeRobot(r, position, orientation);
	}

	/*
		Same as placeRobot, except that it finds a neutral spot for it
		
		If none found, will return in the middle of the field
	*/
	protected void placeRobot(Robot r){
		TeamSide side = robotSides.get(r);
		PVector spot = new PVector(simulator.field.width/2, simulator.field.height/2);
		PVector[] spots = simulator.getNeutralSpots(side);
		
		for(int i = 0; i < spots.length; i++){
			r.setState(spots[i], 0, false);
			if(simulator.isColliding(r)) continue;
			spot = spots[i];
		}

		placeRobot(r, spot);
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