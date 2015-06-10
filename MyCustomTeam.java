public class MyCustomTeam implements Team{
     
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
 
        float divisor = (float)Math.random() * 5 + 5;
 
        BallLocator locator;
        CompassSensor compass;
        public void run(){
            locator = (BallLocator)getSensor("BALL");
            compass = (CompassSensor)getSensor("COMPASS");

            float goalDir = 0f;
            // Find Goal Direction
            if(s == TeamSide.RIGHT)
                goalDir = 180f;

 
            System.out.println("Running!");
            while(true){
                float ballAngle = locator.readValues()[0];
                float ballDist = locator.readValues()[1];
                float comp = compass.readValues()[0];

                // Correct Angle with compass
                setTargetAngularSpeed((goalDir - comp) * 0.01f);

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


                setTargetSpeed(vY, vX);
                
                delay(50);
            }
        }
    }
 
}