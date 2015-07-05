public class CustomTeamA implements Team{
     
    public String getTeamName(){
        return "Emerotecos";
    }
 
    public void setTeamSide(TeamSide side){
 
    }
 
    public Robot buildRobot(GameSimulator s, int index){
        if(index == 0)
            return new Attacker(s);
        if(index == 1)
            return new Goalier(s);

        // By default, return a new attacker
        return new Attacker(s);
    }
 
    class Attacker extends RobotBasic{
        Attacker(GameSimulator s){
            super(s);
        }
 
        float divisor = (float)Math.random() * 5 + 5;
 
        BallLocator locator;
        public void run(){
            locator = (BallLocator)getSensor("BALL");
 
            System.out.println("Running!");
            while(true){
                float[] vals = locator.readValues();
                float angle = vals[0];
 
                setRotation(angle / divisor);
                setSpeed(0.5f,0);
                delay(100);
            }
        }
    }
 
    class Goalier extends RobotBasic{
        Goalier(GameSimulator s){
            super(s);
        }

        float divisor = (float)Math.random() * 150 + 70;
 
        BallLocator locator;
        // Front, left, back, right
        UsDistanceSensor[] ultrasonic_sensors = new UsDistanceSensor[4];
        public void run(){
            locator = (BallLocator)getSensor("BALL");

            ultrasonic_sensors[0] = (UsDistanceSensor)getSensor("ULTRASONIC_FRONT");
            ultrasonic_sensors[1] = (UsDistanceSensor)getSensor("ULTRASONIC_LEFT");
            ultrasonic_sensors[2] = (UsDistanceSensor)getSensor("ULTRASONIC_BACK");
            ultrasonic_sensors[3] = (UsDistanceSensor)getSensor("ULTRASONIC_RIGHT");
 
            System.out.println("Running!");
            while(true){
                float[] vals = locator.readValues();
                float angle = vals[0];
 
                if(Math.abs(angle) < 90)
                    setSpeed(0f, angle / divisor);
                else
                    setSpeed(0f, 0f);
 
                delay(100);
            }
        }
    }
 
}