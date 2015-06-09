public class BasicTeam implements Team{
     
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
        return null;
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
 
                setTargetAngularSpeed(angle / divisor);
                setTargetSpeed(0.5f,0);
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
        public void run(){
            locator = (BallLocator)getSensor("BALL");
 
            System.out.println("Running!");
            while(true){
                float[] vals = locator.readValues();
                float angle = vals[0];
 
                if(Math.abs(angle) < 90)
                    setTargetSpeed(0f, angle / divisor);
                else
                    setTargetSpeed(0f, 0f);
 
                delay(100);
            }
        }
    }
 
}
