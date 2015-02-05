package fouronarow;

public class SmartStrategy extends NaiveStrategy {
	/**
	 * Gives the name of the strategy.
	 * 
	 * @return name
	 * 		- String with name of strategy
	 */
	public String getName() {
		return "Smart";
	}

	/**
	 * Determines a new move according to the strategy.
	 * @param b
	 * 		Instance of Board
	 * @param m
	 * 		Mark (YELLOW, RED or EMPTY)
	 * @return	index
	 * 		index of the column the Mark is supposed to be placed
	 */
	public int determineMove(Board board, Mark mark) {
		if (winField(board, mark) != -1) {
			return winField(board, mark);
		} else if (winField(board, mark.other()) != -1) {
			return winField(board, mark.other());
		} else {
			int counter = 0;
			int put = board.getLength() / 2;
			Board dc;
			while (put >= 0 && put < board.getLength()) {
				dc = board.deepCopy();
				if (!board.getColumn(put).isFull()) {
					dc.setColumn(put, mark);
					if (winField(dc, mark.other()) == -1) {
						return put;
					}
				}
				counter++;
				if ((counter % 2) == 0) {
					put += counter;
				} else {
					put -= counter;
				}
			}
			return super.determineMove(board, mark);
		}
	}

	/**
	 * Looks if there is a field what makes you win.
	 * 
	 * @param b
	 *            - The present state board
	 * @param mark
	 *            - XX or OO
	 * @return -1 - there is no field with wich you can win, otherwise the
	 *         number of the win field
	 */
	private int winField(Board b, Mark mark) {
		Board board;
		for (int i = 0; i < b.getLength(); i++) {
			if (!b.getColumn(i).isFull()) {
				board = b.deepCopy();
				board.setColumn(i, mark);
				if (board.hasWinner()) {
					return i;
				}
			}
		}
		return -1;
	}
}
