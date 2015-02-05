package fouronarow;



public class HumanPlayer extends Player {

	// -- Constructors -----------------------------------------------
	/*
	 * @ requires name != null; requires mark == Mark.XX || mark == Mark.OO;
	 * ensures this.getName() == name; ensures this.getMark() == mark;
	 */
	/**
	 * Creates a new human player object.
	 * 
	 * @param name
	 *           - String
	 *   
	 * @param mark
	 *            - XX or OO
	 */
	public HumanPlayer(String name, Mark mark) {
		super(name, mark);
	}

	/**
	 * Totally unused. Because the human decides his own move.
	 * 
	 * @param board
	 *           - instance of Board
	 *   
	 * @return 0
	 */
	@Override
	public int determineMove(Board board) {
		return 0;
	}
}
