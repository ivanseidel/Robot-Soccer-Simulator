---
layout: page
title: "Movimentation"
category: doc
date: 2015-07-13 18:48:23
order: 3
---

### Movimenting your robot

Forward, Backward, Left, Right and Turn. Those are the basic movements needed to
create something that at least, change pixels in the screen.

ROSOS robot's are Omnidirectional, meaning that they can go in any direction,
and also rotate arount it's axis. Here is the plot of it's directions:

![Movimentation](/Robot-Soccer-Simulator/images/movimentation.png "Movimentation")

Notice that, the arrows indicate where the `positive` value will go. And here is the 
available methods for movimenting the robot:

#### setSpeed(float x [, float y])
When setting speed, you can either specify just the forward/backward speed,
or specify both forward/backwad and right/left speed (Omni). Both are ok:

```java
// Go to front at 1m/s
setSpeed(1f);
setSpeed(1f, 0f);

// Go to the Right, at 0.5m/s
setSpeed(0, 0.5f);

// Go diagonal
setSpeed(0.5f, 0.5f);
```

**Notice that: Speeds are limited by the Robot, so putting `999999.312f` will not
make it faster than it's limit. Also, speeds applyed to the robot don't occur
imediately. There is an `Force` limit, witch means that your robot will take some
time to achieve that speed. You thought it would be that easy hun?**

#### setRotation(float theta)
The other necessary moviment is to rotate arount it's own axis. When you set your 
rotation to some value, the robot will start to spin.

The method accepts a single parameter: the angular speed in Degrees/second:

```java
// Rotate clockwise at 90 degrees/s
setRotation(90);

// Rotate anti-clockwise at 200.2 degrees/s
setRotation(-200.2f);
```

**Notice egain: Angular speeds are limited by the robot as well.**

### Joining setSpeed and setRotation

When moving, you can easily use both methods by calling one after the other:

```java
// Cause the robot to move like an "arc"
setSpeed(1, 0);
setRotation(10);
```