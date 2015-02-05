package fouronarow;

public interface Strategy {
	/**
	 * Gives the name of the strategy.
	 * 
	 * @return name
	 * 		- String with name of strategy
	 */
	public String getName();

	/**
	 * Determines a new move according to the strategy.
	 * @param b
	 * 		Instance of Board
	 * @param m
	 * 		Mark (YELLOW, RED or EMPTY)
	 * @return	index
	 * 		index of the column the Mark is supposed to be placed
	 */
	public int determineMove(Board b, Mark m);
}
