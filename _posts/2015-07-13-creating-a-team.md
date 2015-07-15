---
layout: page
title: "Creating a Team"
category: doc
date: 2015-07-13 19:55:10
order: 2
---
### How to create a team in a few steps

#### Step 1: Create a new File.
Create a new file inside the folder with the name you want to give and the `.java` extension.

In this example we will use `MyCustomTeam.java` as the class. Note that name in the following
steps because it might change for your custom class.

Here is the how-to doit inside the Processing environment:

![Creating file in Processing](/Robot-Soccer-Simulator/images/create-tab.jpg "Creating file in Processing")

#### Step 2: Write the class and implement the Interface

The first step after creating your class file, is to write the base for your class. 
Also, you should import the Processing library manually, and Java Math library:

```java
// Import usefull stuff
import processing.core.*;
import java.util.*;

// Declare your class
public class MyCustomTeam implements Team{

}
```

However, if you try to compile this you will get an error: `The type MyCustomTeam must implement the inherited abstract method Team.[...]`.

This basically means that: You haven't implemented the methods described in the Interface `Team` (go to Team.java to see all the required methods). Here is the complete Team class implemented:

```java
public class MyCustomTeam implements Team{
	public String getTeamName(){
		return "Your team name";
	}

	public void setTeamSide(TeamSide side){

	}

	public Robot buildRobot(GameSimulator s, int index){
		return new RobotBasic(s);
	}
}
```

#### Step 3: Create a Robot class inside the Team

Your team actually uses the Default robot `RobotBasic`, but it doesn't do anything, yet.

In order to make things happen, you need to create your Robot's class, with it's own
program that will run on a separate Thread.

You can either implement your robot from "scratch" and insert sensors, but that's not
necessary, since there is a robot with all the available sensors just ready to be 
extended, and it's called `RobotBasic`.

The `RobotBasic` class provides `Distance Sensors`, `Compass Sensor`, and the `Ball Sensor`.
To use that Robot as default, declare a class INSIDE your current Team class, here is
where you should place your code:

```java
public class MyCustomTeam implements Team{
	/*
	  The methods we already talked about...
	 */

	// Put your robot classes here, always inside the Team class brackets
}
```

And here comes the declaration of the Class, extending the class `RobotBasic` that as said,
should be inside the Team class:

```java
public class MyRobot extends RobotBasic{
	// This constructor method should be exactly like this,
	// just with it's name matching your robot class name.
	MyRobot(GameSimulator g){
		super(g);
	}

	// Called once in the beggining
	public void setup(){

	}

	// Loop is called until program crashes, robot is stopped
	// or earth is destroyed.
	public void loop(){

	}
}
```

#### Step 4: Configure the building process

Going back to your Team class, there is a method named `buildRobot`. This method
is the responsible for creating a team each time it is called. It MUST be a new
instantied object each time it's called, and it doesn't matter what class it was
instantiated, it just need to extend `Robot`, at least. 

In our case, `RobotBasic` extends `Robot`, so we are ok.

But what about the parameters? Well, we pass in a `GameSimulator` object and a `index`:

* `GameSimulator` is used by the robot in order to instantiate `Sensors`.
* `index` can be used by you to build different robots with different behaviors.

Let's suppose this desired setting: You want a Attacker robot, and a Defense robot. But
how to let the Simulator "agnostic" about that, and put everything in your hand?

Well, that's the `index`. Each time the Simulator is initialized, it Instantiate your Team
class, then instantiate `n` robots using the `buildRobot` method, and passing in a `index` 
that starts at 0, and goes until the number of robots is reached.

If you set the Simulation to run 2 robots on each side, the method `buildRobot` will be
called 2 times, one with `index=0` and then `index=1`. And here is what you can do with it:

```java
public Robot buildRobot(GameSimulator s, int index){
	
	if(index == 0)
		return new MyRobot(s);

	if(index == 1)
		return new MyAttackerRobot(s);

	return new RobotBasic(s);
}
```

The code above, will return 2 different robots for index 0 and 1, and a default one for
any other index. Notice that it is important to have a "default" one, even thought it 
doesn't do anything, because it might crash the simulator if returning `null`.

#### Step 5: Configure the Match

The last step is to set your Match team to use your newly created class. In the 
`SoccerSimulator.pde` file, find where the `Match` is instantied, and place
your Team class name there, like this:

```java
new Match(
	// Team A Class
	MyCustomTeam.class,
	
	// Team B Class
	CustomTeamB.class,

	// Number of robots on each side
	2
);
```