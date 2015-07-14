import processing.core.*;
import java.util.*;

public class CustomTeamB implements Team{
    private static class UsSensors {
        public static final int FRONT = 0;
        public static final int LEFT = 1;
        public static final int BACK = 2;
        public static final int RIGHT = 3;
    }

    public String getTeamName(){
        return "Loko's Team";
    }
 
    TeamSide s;
    public void setTeamSide(TeamSide side){
        s = side;
    }
 
    public Robot buildRobot(GameSimulator s, int index){
        return new DebuggerRobot(s);
    }
 
    class DebuggerRobot extends RobotBasic{
        DebuggerRobot(GameSimulator s){
            super(s);
        }
 
        Sensor locator;
        Sensor compass;
        Sensor[] ultrasonic_sensors = new Sensor[4];
        float goalDir;

        public void setup(){
            System.out.println("Running!");

            locator = getSensor("BALL");
            compass = getSensor("COMPASS");

            ultrasonic_sensors[UsSensors.FRONT] = getSensor("ULTRASONIC_FRONT");
            ultrasonic_sensors[UsSensors.LEFT] = getSensor("ULTRASONIC_LEFT");
            ultrasonic_sensors[UsSensors.BACK] = getSensor("ULTRASONIC_BACK");
            ultrasonic_sensors[UsSensors.RIGHT] = getSensor("ULTRASONIC_RIGHT");

            goalDir = 0f;
            // Find Goal Direction
            if(s == TeamSide.RIGHT)
                goalDir = 180f;
        }

        public void loop(){
            float ballAngle = locator.readValue(0);
            float ballDist = locator.readValue(1);
            float comp = compass.readValue(0);

            // Correct Angle with compass
            setRotation(MathUtil.relativeAngle(goalDir - comp) * 1f);

            float vX = 0f, vY = 0f;

            float rads = (float)Math.toRadians(ballAngle);
            float ballX = (float)Math.sin(rads);
            float ballY = (float)Math.cos(rads);

            if(ballAngle < 45 && ballAngle > -45){
                vX = ballX * 5;
                vY = 2f;
            }else if(ballAngle > 135 * (1/ (ballDist + 0.1)) || ballAngle < -135 * (1/ (ballDist + 0.1))){
                vX = -ballX * 5;
            }else{
                vY = -2;
            }

            float left = ultrasonic_sensors[UsSensors.LEFT].readValue(0);
            float right = ultrasonic_sensors[UsSensors.RIGHT].readValue(0);
            float front = ultrasonic_sensors[UsSensors.FRONT].readValue(0);
            float back = ultrasonic_sensors[UsSensors.BACK].readValue(0);

            // Avoid contact with other objects and robots
            float threshold = .1f;
            if (left < threshold / 2)
                vX = .5f;
            else if (right < threshold / 2)
                vX = -.5f;
            if (front < threshold)
                vY = -.5f;
            else if (back < threshold)
                vY = .5f;

            setSpeed(vY, vX);
            
            delay(50);
        }
    }
 
}