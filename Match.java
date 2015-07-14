/**
 * Defines the parameters of a match - both teams and the number of players
 */
public class Match {
	public final Class<? extends Team> TeamAClass;
	public final Class<? extends Team> TeamBClass;
	public final int teamAPlayers;
	public final int teamBPlayers;

	Match(
		Class<? extends Team> TeamAClass,
		Class<? extends Team> TeamBClass,
		int teamPlayers) {

		this.TeamAClass = TeamAClass;
		this.TeamBClass = TeamBClass;
		this.teamAPlayers = this.teamBPlayers = Math.max(teamPlayers, 0);
	}

	Match(
		Class<? extends Team> TeamAClass,
		Class<? extends Team> TeamBClass,
		int teamAPlayers, int teamBPlayers) {

		this.TeamAClass = TeamAClass;
		this.TeamBClass = TeamBClass;
		this.teamAPlayers = Math.max(teamAPlayers, 0);
		this.teamBPlayers = Math.max(teamBPlayers, 0);
	}
}