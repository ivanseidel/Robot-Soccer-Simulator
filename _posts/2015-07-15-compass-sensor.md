---
layout: page
title: "Compass Sensor"
category: sensor
date: 2015-07-15 00:00:58
order: 1
---
Compass Sensor provides your robot with information about it's absolute angle. It's value goes
from 0-360 degrees, and it **always** points in the same direction, regardless of your Team Side.

![Compass Sensor](/Robot-Soccer-Simulator/images/sensor-compass.png "Compass Sensor")

### RobotBasic ID

`RobotBasic` class provides you with one `SensorCompass` installed, its `ID` is `COMPASS`.

### Value Returned

Compass Sensors returns only one measurement, therefor, you can only read the index `0`:

```java
Sensor someCompass = getSensor("COMPASS");
float angle = someCompass.readValue(0);
```

### Where is the North

North is on a point infinitelly far to the **RIGHT**  of the Field (The "Yellow" side). That's
where the Compass will read a `0`.

### Tip: MathUtil.relativeAngle(delta)
This method helps you transforming absolute values (0-360) to something relative (-180 to 180);

See the example:

```java
float absoluteValue = someCompass.readValue();
float targetAngle = 90f;

float relative = absoluteValue - targetAngle;
// Notice that, relative can be between -90 and 290, and that's bad.
// In order to make it relative, going from -180 to 180, use relativeAngle:
relative = MathUtil.relativeAngle(delta);

// Now, relative is between -180 and 180 degrees
```

### Limits and Noise

The value returned is from 0-360, 0 meaning that the robot is pointed exactly to the
`RIGHT` side of the field.

Compass sensors have some noise applyed to it's final output value as well.

#### [Video: Compass Sensor + Handeling Team Sides]
<iframe width="420" height="315" src="https://www.youtube.com/embed/UBOavC5zXds" frameborder="0" allowfullscreen></iframe>