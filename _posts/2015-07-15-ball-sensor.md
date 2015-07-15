---
layout: page
title: "Ball Sensor"
category: sensor
date: 2015-07-15 00:00:52
order: 0
---
## Ball Sensor

Ball sensor is the most imoportant sensor to your robot. It provides you with information
about the relative angle from your robot to the ball, as well as it's distance.

The angle goes from -180 to 180, relative to your robot's orientation.

The distance, goes from 0 to the limit of the sensor (around 1 meter). If this limit is reached,
both `angle` and `distance` returns `0`.

Angle measurements are in `degrees` and distance measurements are in `meters`.

### RobotBasic ID

`RobotBasic` class provides you with one `SensorBall` installed, its `ID` is `BALL`.

### Value Returned

Ball Sensor returns two values, the first being the ball angle, and the seccond with the
ball distance.

```java
Sensor ballFinder = getSensor("BALL");

float angleToBall = ballFinder.readValue(0);
float distToBall = ballFinder.readValue(1);
```

### Limits and Noise

In case the ball is too far from the robot (usually > 1 meter), the output will be 0 for both
`angle` and `distance`.

Those values are also subjected to random noises.

#### [Video: Ball Sensor]
<iframe width="560" height="315" src="https://www.youtube.com/embed/BjIsj9V_vrg" frameborder="0" allowfullscreen></iframe>