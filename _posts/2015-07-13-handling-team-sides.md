---
layout: page
title: "Handling team Sides"
category: doc
date: 2015-07-13 18:54:21
order: 10
---
### Why is it important?
Really straight forward: to score goals in the right goal.

Imagine if your robot doesn't know what direction is the opposite side? And then imagine
your robot doing a goal on yourself...

### We don't want that, right?

Not at all. And the way we can solve this, is by making use of the `SensorCompass`, to read
the absolute angle, just like a real Compass. However, the value returned from the Compass 
sensor is not relative to the oposite team...

### Where is the North?

North is to the `RIGHT` side of the field, and if you try to score goals there. In order to
deal with the change in sides, you need to make use of the `setTeamSide(TeamSide)` callback
inside your `Team` class. 

### setTeamSide(TeamSide)

This method is called from the simulator everytime the side changes and in the beggining of
the game. It passes in as parameter, the Side at witch your team is. There are two possible
sides: `TeamSide.LEFT` and `TeamSide.RIGHT`.

Because this method is called once, you need some way to store the "last side" assigned, like this:

```java
public class MyTeam implements Team{
	[... Other implementations ...]

	TeamSide currentSide;
    public void setTeamSide(TeamSide side){
        currentSide = side;
    }
}
```

With this, you can save the current Side of your team, and inside your robots, access this attribute
to check and see what's the correct goal direction, like this:

```java
class MyRobot extends RobotBasic{
	[... Other implementations ...]

	float oppositeGoalDir;
	public void loop(){

		// Find Opposite Goal Direction and saves
		if(currentSide == TeamSide.RIGHT)
		    oppositeGoalDir = 180f;
		else
			oppositeGoalDir = 0f;
	}
}
```

#### [Video: Compass Sensor + Handeling Team Sides]
<iframe width="420" height="315" src="https://www.youtube.com/embed/UBOavC5zXds" frameborder="0" allowfullscreen></iframe>