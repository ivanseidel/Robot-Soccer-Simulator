---
layout: default
title: "Basics"
category: doc
date: 2015-07-13 18:17:38 
order: 1
---
### How the Simulator handles Teams

Every Robot created and programmed inside the Simulator, must be declared and coded inside the
Team class.

The Team class is a representation of your own team, and consists of a few basic methods needed
to `build` robots, be notified of `side changes` on the field, define the `team name` and so on...

The main reason why each Team is a `class`, is because you can easily customise your `Match` on the
Simulator, by passing the `class` name to it.

### A few words about Measurement Units

We are using [SI](https://en.wikipedia.org/wiki/International_System_of_Units). That means:

* Compriments are in *meters*
* Mass is in *kilograms*
* Time is measured in *seconds*

When using floating point numbers, most of times you will have to specify it's type as being
a `float`, not `double`. We did that in order to speed up a little bit Math calculations,
because `float` is less "complex" than `double`. What that means? This:

```java
// OK, Java interprets as integer and casts to float (not a problem)
float myFloat = 10;

// NOT OK, Java interprets it as a double, and cannot cast to float
float myOtherFloat = 10.1;

// OK, Java interprets it as a float, not a double
float myOtherOtherFloat = 10.1f;
```

So, be aware of this, because you might need it in many parts of the simulator.

### Defining a Match

Match represents a single `Match` containing two `Team` classes for each side, and also a
number of robots to be build/spawned in field.

To initialize a `Match` setup do like this:

```java
Match myMatch = new Match(
	// Team A Class
	CustomTeamA.class,
	
	// Team B Class
	CustomTeamB.class,

	// Number of robots on each side
	2
);

// If you want a different number of robots on each side:
Match myMatch = new Match(
	CustomTeamA.class,
	CustomTeamB.class,

	// Number of robots on side A
	2,
	// Number of robots on side B
	3
);
```

If you have created a `Team` class, your Class name should go in one side or another in the Match instantiation, with the `.class`.

### Initializing the GameController

`GameController` is the one who deals with the `Field` creation, `Robot` creation, and everything related to the Simulator. It is basically, a Class that wraps everything nicelly inside it, and provides public methods for controlling the state of the Game.

In order to initialize a Game, you must pass in the `match` you created, so that the Game knows who will play egains who, and other configurations.

```java
GameController myController = new GameController(myMatch);

// However, to simplify the code, we have done both instantiations together:
GameController myController = new GameController(new Match(
	// Team A Class
	CustomTeamA.class,
	// Team B Class
	CustomTeamB.class,
	// Number of robots on each side
	2
));
```

### The GameSimulator
`GameSimulator` is the part where Simulation Happens. It is agnostic about controlling the match, and 
the only thing it does, is to continuously update and handle colisions between `Simulatables` objects.

### Configuring the Field Size

The `Field` is part of the `GameSimulator`, that resides inside the `GameController`.
However, there is a public method available inside the `GameSimulator` that allows you to
dinamically change the field size.

```java
GameSimulator simulator = myController.getSimulator();

// Where the parameters are: Width and Height, both in meters
simulator.setFieldSize(2.44f, 1.82f);

// Or just...

myController.getSimulator().setFieldSize(2.44f, 1.82f);
```

### Controlling the game

If you notice, in the bottom of the `SoccerSimulator.pde` class, we have given you a few
key shortcuts. That can be done by controlling the `gameController` class.

A few usefull shortcuts while running the Simulator:

```
space: Resume/Pause Game
i: initialize the entire game, and start.
r: restart positions, without restarting the game
d: debug. Should write a lot of crazy things on the console...
```