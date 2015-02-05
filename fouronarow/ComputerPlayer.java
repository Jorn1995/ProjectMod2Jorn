package fouronarow;

public class ComputerPlayer extends Player {
	private Strategy strategy;

	// -- Constructors ----------------------------------
	/**
	 * Makes a new ComputerPlayer with the given mark and strategy.
	 * 
	 * @param mark
	 *            - XX or OO
	 * @param strategyA
	 *            - The strategy for the computerplayer
	 */
	public ComputerPlayer(Mark mark, Strategy strategyA) {
		super(strategyA.getName() + "-" + mark.toString(), mark);
		this.strategy = strategyA;
	}

	/**
	 * Makes a new computerplayer with the given mark and a NaiveStrategy.
	 * 
	 * @param mark
	 *            - XX or OO
	 */
	public ComputerPlayer(Mark mark) {
		this(mark, new NaiveStrategy());
	}

	/**
	 * Determines move based on its strategy.
	 * 
	 * @return strategy.determineMove(board, super.getMark())
	 *  - Int that's shows index of the column
	 * @param board
	 *            - Board that the move has to be determined on
	 */
	public int determineMove(Board board) {
		return strategy.determineMove(board, super.getMark());
	}
}
