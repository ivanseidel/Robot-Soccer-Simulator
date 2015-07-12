/**
 * Use this class to define a set of matches and to choose
 * which one will be used by the SoccerSimulator
 */
public class MatchConfig {
	public static final Match getMatch() {
		return standardMatch;
	}

	private static final Match testCollisionMatch = new Match(
		ForwardTeam.class,
		CustomTeamB.class,
		1
	);

	private static final Match standardMatch = new Match(
		CustomTeamA.class,
		CustomTeamB.class,
		2
	);
}