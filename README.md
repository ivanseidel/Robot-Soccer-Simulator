# Open-Source Soccer Simulation League
This is an ongoing project, to create a tournament of Simulated robots lying over Processing.

*Credits: Felipe Nascimento Martins and Ivan Seidel Gomes*

## Usage
1. Download and unzip
2. Rename the folder to "SoccerSimulator"
3. Open the SoccerSimulator.pde with Processing
4. Have fun.

## Documentation
*This is under development.*

The simulator was developed thinking about compatibility with major operating systems, and facility to develop and test, without further deep knowledge about "terminal" commands or deep understanding about compilation and building code from scretch.

Because of that, the solution that seemed wise was to use the Processing enviroment. It's structure is very similar to Arduino Boards that teams already use, and it's written in Java, witch is not so different from C++ also used in the Arduino.

The simulator had two main purposes, that we can also call "challenges" during development:

1) Be really easy to code, in the same time that it can't have enormous changes from the code written on a real robot.

2) Not so hard to hack in, and make changes to the game itself, allowing teams to change the Physics, and the Organization to propose Super Team challenges during the competition, that are challenging to teams.



For those purposes whe thought about, the code was written targeting an reliable and generic interface, that can be used to create Simulatable objects and Sensors in a pretty straight forward way.

The simulator also considers the possibility of different types of "judges". Different kinds of sensors and robot setups. A wide range of Motor Controllers, used to controll the speed of the robot in the simulation.



We are now going to explain the main parts of the puzzle, and also the reason why they are detached as it is.

The core starts with the global object called "GameSimulator". It is responsible for creating physical elements such as walls, goals, Ball and all field-related things. It is also responsible for handling simulation time, and passing it forward to the objects.

By "physical elements", we call anything that can be simulated physically. For that purpose, we created the main class called "Simulatable", witch contains baisc methods for checking collisions, and simulating with discrete time steps. This Class, is extended by all other objects such as: Ball, Block, GoalWall and every Robot.

Simulatables can also be "Drawable", whitch means that it can be drawn on the screen. Most of them have this ability, but a few others doesn't because they don't need to appear in the simulation (such as the Goal Wall, used only to collide with the ball). "Drawable" is a simple interface with the method "draw".

Simulatables can also be modified to collide with just a few types of objects, and also be changed to be physically static (such as walls). There is also a way to modify the collision type of each object, in order to have different types of collision going from fully ellastic to fully non-ellastic.

The physics lying with Simulatables are really simple. It simulates only the position in a 2 dimensional space, acordingly to the Force, Acceleration, Speed and Position applyied to every simulation step. Here is the part of the code where the simulation takes place: 

[IMAGE: Simulation in Simulatable]

The iteration of each simulation iteration is as follows:

1. Find out the dt (how long since last simulation)
2. For every simulatable in this simulation:
  a) simulate it's new position with dt
  b) For every simulatable in this simulation, that isn't it's self:
    i: If it is colliding with this simulatable, then resolve the collision of them

Here is the part of the code responsible for handling all of the simulation controll:

[IMAGE: Simulation controll in GameSimulator]

This approach seemed very simple, and reliable. It has proven to work great under a small simulation step. In cases where the speed of the ball or robots were really high, and dt was also big, it could couse collisions to not occur.



Here we will describe the main part of the code: The Robot.

For a matter of simplicity, Robot also extends a "Simulatable", witch means that it has Force, Acceleration, Speed and Position. And extends "Drawable", allowing even the possibility of teams to change it's appearence. The robot is tought as an basic omni-directional robot, witch means that it can go around in any direction, and rotation, with hard limits on speed.

Robots also have orientation, and that is an extension from the Simulatable objects. It is simulated inside the Robot, before the Simulatable methods handle that. 

Seeking an approach to the reality, where Robots tend to take a while to act after a command is sent, we encapsulated the speed and orientation, as "target Speed" and "target Orientation".

In this way, the robot has a maximum angular and linear acceleration, and this value can vary from robot to robot. 

The way we choosed to give controll of the robots to teams, is extending every Robot from a Runnable, meaning it can be run inside a Thread.

Running Robot's in Threads is essential, because it cannot stops the simulator from simulating, and we wanted teams to be able to use long duration loops and delays inside the controller. Threads are just perfect for this.

The Robot class Application provides a very simple way to controll the robot:
1) setTargetSpeed(float xSpeed, float ySpeed)
	Used to set the target linear speed
2) setTargetAngularSpeed(float speed)
	Used to set the target angular speed
3) stopMotors()
	Simple way to stop the robot. Just set the targed linear and angular speeds to 0
4) run()
	Where your code goes.

Another important part of the Robot Puzzle, are the Sensors.

Targeting an generic way of interfacing with sensors, whe thought in sensors as a basic unit that can do measurements, and return from 1 to N different measurements. In this way, a class called "RobotSensor" is implemented, constructing it from the GameSimulator, and the Robot. the public method "readValues" is then implemented, where it should return an array of float values.

A few of the sensors that could be implemented in this way are:

1) Ball Direction/Distance Sensor
2) Compass Sensor
3) Distance Sensors
4) IR Beacons around the field
5) Light sensors (to read the Field)

To show how simple it is to create a sensor, here is the part of the "BallLocator" code, responsible for measuring the direction and the distance of the Ball. Notice that a few Limits are implemented, to approach the simulation to the reality:

[IMAGE: BallLocator]

Sensors are Registered inside each robot, allowing the use of different sensors inside every single robot in the simulation. Teams can make use of those Sensors, using the method "getSensor(String ID)", and casting the sensor to the expected type of sensor.

Now that both the Controll part and Sensors are explained, we can go forward to explain about how the competition process would occur, and what teams should give as "code" to the Judges.

Every Team shall code a class extending "Team". This class is responsible for "spawning" Robots given an index. Each Robot spawned can be anything, inheriting from "BasicRobot" or even "Robot". Each Team object, will get notified of Side Changes, Robot Removals and etc.

The Team class must include all classes required by itself to run, and that, will compose a single file with the name of the Team. When the simulation is about to start, the judge selects whitch classes to "use", and the rest is handled by the class "GameController".

Here is the final piece of the puzzle, containing the Final class of a simple team, that uses both the BallLocator and CompassSensor to locomove and try to score goals:

[IMAGE: Team]