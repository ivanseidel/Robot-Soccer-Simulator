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
 
        float speedMultiplier = (float)Math.random() * 5 + 5;
 
        Sensor locator;

        public void setup(){
            System.out.println("Running!");
            locator = getSensor("BALL");
        }

        public void loop(){
            float[] vals = locator.readValues();
            float angle = vals[0];

            setRotation(angle * speedMultiplier);
            setSpeed(0.5f,0);
            delay(100);
        }
    }
 
    class Goalier extends RobotBasic{
        Goalier(GameSimulator s){
            super(s);
        }

        float divisor = (float)Math.random() * 150 + 70;
 
        SensorBall locator;
        // Front, left, back, right
        SensorDistance[] ultrasonic_sensors = new SensorDistance[4];
        
        public void run(){
            locator = (SensorBall)getSensor("BALL");

            ultrasonic_sensors[0] = (SensorDistance)getSensor("ULTRASONIC_FRONT");
            ultrasonic_sensors[1] = (SensorDistance)getSensor("ULTRASONIC_LEFT");
            ultrasonic_sensors[2] = (SensorDistance)getSensor("ULTRASONIC_BACK");
            ultrasonic_sensors[3] = (SensorDistance)getSensor("ULTRASONIC_RIGHT");
 
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