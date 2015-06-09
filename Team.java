public interface Team{

	public String getTeamName();
	public void setTeamSide(TeamSide side);
	public Robot buildRobot(GameSimulator simulator, int index);

}