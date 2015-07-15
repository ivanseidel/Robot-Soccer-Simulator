---
layout: page
title: "Distance Sensor"
category: sensor
date: 2015-07-15 00:01:02
order: 2
---
The Distance sensor provides your robot with information about proximity to objects in the
simulation.

The value returns is from 0 (meaning exactly in front of the sensor), to it's limit (meaning far away).
Measurements are in `meters`.

### RobotBasic IDs

`RobotBasic` class provides you with 4 already installed `SensorDistance`, one for each direction.
The sensor `ID's` acordingly to it's direction are:

* Front: `ULTRASONIC_FRONT`
* Right: `ULTRASONIC_RIGHT`
* Back: `ULTRASONIC_BACK`
* Left: `ULTRASONIC_LEFT`

### Values Returned

Distance sensors returns only one measurement, therefor, you can only read the index `0`:

```java
Sensor someDistanceSensor = getSensor("ULTRASONIC_FRONT");
float distance = someDistanceSensor.readValue(0);
```

### Limits and Noise

Distance sensors can be limited to some maximum value, that depends on the internal configuration
of the sensor. And also, it's value are noise-applied, meaning that it's not 100% correct;