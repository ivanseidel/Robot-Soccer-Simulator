public class MyCustomTeam implements Team{
     
    public String getTeamName(){
        return "Loko's Team";
    }
 
    public void setTeamSide(TeamSide side){
 
    }
 
    public Robot buildRobot(GameSimulator s, int index){
        if(index == 0)
            return new DebuggerRobot(s);
        if(index == 1)
            return new DebuggerRobot(s);
        return null;
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
 
            System.out.println("Running!");
            while(true){
                float[] ball = locator.readValues();
                float[] comp = compass.readValues();

                System.out.println("Ball: ["+ball[0]+","+ball[1]+"]");
                System.out.println("Compass: ["+comp[0]+"]");
                
                delay(500);
            }
        }
    }
 
}