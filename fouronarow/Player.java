package fouronarow;

public abstract class Player {

	// -- Instance variables -----------------------------------------

	private String name;
	private Mark mark;
	protected Game game;

	// -- Constructors -----------------------------------------------

	/*
	 * @ requires theName != null; requires theMark == theMark.XX || theMark ==
	 * theMark.OO; ensures this.getName() == theName; ensures this.getMark() ==
	 * theMark;
	 */
	/**
	 * Creates a new Player object.
	 * 
	 * @param theName
	 * - Name of player that is being created
	 * @param theMark
	 * - Mark of player that is being created
	 */
	public Player(String theName, Mark theMark) {
		this.name = theName;
		this.mark = theMark;
	}

	// -- Queries ----------------------------------------------------

	/**
	 * Returns the name of the player.
	 * 
	 * @return name
	 * - String with name of Player
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the mark of the player.
	 * 
	 * @return mark
	 *  - Mark (YELLOW, RED or EMPTY)
	 */
	public Mark getMark() {
		return mark;
	}

	/*
	 * @ requires board != null & !board.isFull(); ensures
	 * board.isField(\result) & board.isEmptyField(\result);
	 */
	/**
	 * Determines the field for the next move.
	 * 
	 * @param bord
	 *            the current game board
	 * @return the player's choice
	 */
	public abstract int determineMove(Board board);

	// -- Commands ---------------------------------------------------

	/*
	 * @ requires board != null & !board.isFull();
	 */
	/**
	 * Makes a move on the board. <br>
	 * 
	 * @param bord
	 *  -          the current board
	 */
	public void makeMove(Board board) {
		int keuze = determineMove(board);
		board.setColumn(keuze, getMark());
	}

	
	/**
	 * Makes a move on the board. <br>
	 * 
	 * @param bord
	 *      	- the current board
	 *            
	 * @param slot
	 *  		- the index of the column a Mark must be placed
	 */
	public void makeMove(Board board, int slot) {
		int keuze = slot;
		boolean valid = board.isColumn(keuze) && !board.getColumn(keuze).isFull();
		if (valid) {
			board.setColumn(keuze, getMark());
		} else {
			System.out.println("ERROR: field " + keuze + " is no valid choice.");
		}
	}

}
