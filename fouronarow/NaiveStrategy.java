package fouronarow;

import java.util.HashMap;
import java.util.Map;

public class NaiveStrategy implements Strategy {
	public Map<Integer, Integer> empty;

	/**
	 * Gives the name of the strategy.
	 * 
	 * @return "Naive"
	 *  - Name of Strategy	
	 */
	public String getName() {
		return "Naive";
	}

	/*
	 * @ requires board != null && mark != EMPTY; ensures
	 * !\result.getColumn().isFull();
	 */
	/**
	 * Determines a new move according to the NaiveStrategy.
	 * 
	 * @param board
	 * - Instance of Board
	 * @param mark
	 *  - Mark(YELLOW, RED or EMPTY.
	 *  
	 * @return index
	 *  - column that the strategy chose to put a Mark.
	 */
	public int determineMove(Board board, Mark mark) {
		empty = new HashMap<Integer, Integer>();
		int j = 0;
		for (int i = 0; i < board.getLength(); i++) {
			if (!board.getColumn(i).isFull()) {
				empty.put(j, i);
				j++;
			}
		}
		if (j == 0) {
			return -1;
		}
		return empty.get((int) (j * Math.random()));
	}
}
