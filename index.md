---
layout: default
title: "Home"
---
### *RObot SOccer Simulator* (ROSOS)
This is an ongoing project, to create a tournament of Simulated robots lying over Processing.

### Where can I learn more about (ROSOS)?
1. In the [GitHub Page](https://github.com/ivanseidel/Robot-Soccer-Simulator/wiki) of this Repository (where you are right now)
2. In the [ROSOS Youtube channel](https://www.youtube.com/channel/UCZekRTPIwhe56lbicQpO-vg)
3. If you have any material that you want to share, open an issue in the [Issues](https://github.com/ivanseidel/Robot-Soccer-Simulator/issues) page.

### What is ROSOS?

![ROSOS](/Robot-Soccer-Simulator/images/window.png "ROSOS")

The simulator was developed thinking about compatibility with major operating systems, and facility to develop and test, without further deep knowledge about "terminal" commands or deep understanding about compilation and building code from scratch.

Because of that, the solution that seemed wise was to use the Processing enviroment. It's structure is very similar to Arduino Boards that teams already use, and it's written in Java, witch is not so different from C++ also used in the Arduino.

The simulator had two main purposes, that we can also call "challenges" during development:

Be really easy to code, in the same time that it can't have enormous changes from the code written on a real robot.

Not so hard to hack in, and make changes to the game itself, allowing teams to change the Physics, and the Organization to propose Super Team challenges during the competition, that are challenging to teams.

For those purposes we thought about, the code was written targeting an reliable and generic interface, that can be used to create "Simulatables" objects and Sensors in a pretty straight forward way.

The simulator also considers the possibility of different types of "Judges". Different kinds of sensors and robot setups. A wide range of Motor Controllers, used to control the speed of the robot in the simulation.