ArrayList<Drawable> uiElements = new ArrayList<Drawable>();

GameController controller;
GameSimulator simulator;

float gameScale = 300f;
void setup(){

	/*
		This is where you setup both Teams and also the number of players

		By default, we have to Team Classes implemented:
			BasicTeam: A functional robot that follows the ball blindly
			MyCustomTeam: A debugger robot, used to read sensors and output to the console
	*/

	controller = new GameController(
		// Team A Class
		CustomTeamA.class,
		// Team B Class
		CustomTeamB.class,
		// Number of robots on each side
		2
	);

	controller.getSimulator().setFieldSize(2.44f, 1.82f);

	uiElements.add(controller);
	size((int)controller.getWidth(gameScale) + 200, (int)controller.getHeight(gameScale)+100);
}

void draw(){
	
	background(255);

	controller.run();

	translate(100, 0);
	for(Drawable d:uiElements)
		d.draw(this, gameScale);

	translate(-100, 0);
}

public void mouseDragged(){
	controller.moveBallToSpot(new PVector((mouseX - 100) / gameScale, (mouseY - 100) / gameScale));
}

public void keyPressed(){

	if(key == ' '){
		if(controller.isRunning()){
			System.out.println("Pausing game...");
			controller.pauseGame();
		}else{
			System.out.println("Starting game...");
			controller.resumeGame();
		}
	}else if(key == 'i'){
		controller.resetGame();
		controller.resumeGame();
	}else if(key == 'r'){
		controller.restartPositions();
	}else if(key == 'd'){
		String debug = "DEBUG:";
		debug += "\nisRunning:"+controller.isRunning();
		debug += "\nController Robots:"+controller.robots.size();
		for(Robot r:controller.robots)
			debug += "\n\t"+r+" ["+r.position.x+","+r.position.y+"]";

		debug += "\nSimulatables:"+controller.getSimulator().simulatables.size();
		for(Simulatable r:controller.getSimulator().simulatables)
			debug += "\n\t"+r;

		System.out.println(debug);
	}

}

