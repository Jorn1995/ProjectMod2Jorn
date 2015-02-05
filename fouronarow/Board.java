package fouronarow;

import java.util.Observable;
import protocol.*;

/**
 * @author Ingrid
 *
 */
public class Board extends Observable implements ProtocolControl, ProtocolConstants {

	// -- Constants --------------------------------------------------
	private static final int CONNECT = 4;
	public static final int LENGTH = 7;
	public static final int HEIGHT = 6;

	// -- Instance variables -----------------------------------------

	/*
	 * @ private invariant columns.length == LENGTH; invariant (\forall int i; 0
	 * <= i & i < LENGTH; \forall int j; 0 <= j & j < HEIGHT; getField(j,i) ==
	 * Mark.EMPTY || getField(j,i) == Mark.XX || getField(j,i) == Mark.OO);
	 */
	/**
	 * An array of length <code>LENGTH</code> of the columns of the Four on a
	 * Row game.
	 */
	private Column[] columns;

	// -- Constructors -----------------------------------------------

	/*
	 * @ ensures (\forall int i; 0 <= i & i < LENGTH; \forall int j; 0 <= j & j
	 * < HEIGHT; getField(j,i) == Mark.EMPTY);
	 */
	/**
	 * Creates an empty board.
	 */
	public Board() {
		columns = new Column[LENGTH];
		for (int i = 0; i < LENGTH; i++) {
			columns[i] = new Column(HEIGHT, CONNECT);
		}
	}

	// -- Queries ----------------------------------------------------

	/*
	 * @ ensures \result != this; ensures (\forall int i; 0 <= i & i < LENGTH;
	 * \result.getColumn(i) == this.getColumn(i));
	 */
	/**
	 * Creates a deep copy of this board.
	 */
	public Board deepCopy() {
		Board copy = new Board();
		for (int i = 0; i < LENGTH; i++) {
			copy.columns[i] = columns[i].deepCopy();
		}
		return copy;
	}

	/*
	 * @ensures \result == true || \result == false;
	 */
	/**
	 * Checks if the given object equals this board.
	 */
	public boolean equals(Object equal) {
		boolean result = false;
		if (equal instanceof Board) {
			result = true;
			for (int j = 0; j < LENGTH; j++) {
				for (int i = 0; i < HEIGHT; i++) {
					if (!(((Board) equal).getField(i, j).equals(this.getField(i, j)))) {
						result = false;
					}
				}
			}
		}
		return result;
	}

	/*
	 * @ ensures \result == (0 <= ix && ix < DIM * DIM); pure;
	 */
	/**
	 * Returns true if <code>ix</code> is a valid index for a column.
	 * 
	 * @return <code>true</code> if <code>0 <= ix < LENGHT</code>
	 */
	public boolean isColumn(int ix) {
		return 0 <= ix && ix < LENGTH;
	}

	/*
	 * @ requires this.isColumn(i);
	 */
	/**
	 * Returns column <code>i</code>.
	 * 
	 * @param i
	 *            - the number of the column
	 * @return the column
	 */
	public Column getColumn(int i) {
		if (isColumn(i)) {
			return columns[i];
		} else {
			return null;
		}
	}

	/*
	 * @ requires this.isColumn(j); ensures \result == Mark.EMPTY || \result ==
	 * Mark.XX || \result == Mark.OO;
	 */
	/**
	 * Returns the content of the field referred to by the (row,col) pair.
	 * 
	 * @param i
	 *            - the row of the field
	 * @param j
	 *            - the column of the field
	 * @return the mark on the field
	 */
	public Mark getField(int i, int j) {
		Mark result = null;
		if (isColumn(j)) {
			result = getColumn(j).getField(i);
		}
		return result;
	}

	/*
	 * @ ensures \result == (\forall int i; i <= 0 & i < HEIGHT; \forall int j;
	 * j <= 0 & j < LENGTH; this.getField(i,j) != Mark.EMPTY); pure;
	 */
	/**
	 * Tests if the whole board is full.
	 * 
	 * @return true if all fields are occupied
	 */
	public boolean isFull() {
		boolean answer = true;
		for (int i = 0; i < LENGTH; i++) {
			answer = answer && getColumn(i).isFull();
		}
		return answer;
	}

	/*
	 * @ ensures \result == this.isFull() || this.hasWinner(); pure;
	 */
	/**
	 * Returns true if the game is over. The game is over when there is a winner
	 * or the whole board is full.
	 * 
	 * @return true if the game is over
	 */
	public boolean gameOver() {
		return this.isFull() || this.hasWinner();
	}

	/* @ pure; */
	/**
	 * Checks whether there is a row which has <code>CONNECT</code> consecutive
	 * fields contains the mark <code>m</code>.
	 * 
	 * @param m
	 *            - the mark of interest
	 * @return true if there is a row which has <code>CONNECT</code> consecutive
	 *         fields contains the mark <code>m</code>
	 */
	public boolean hasRow(Mark m) {
		boolean totalAnswer = false;
		for (int i = 0; i < HEIGHT; i++) {
			totalAnswer = totalAnswer || hasRow(i, m);
		}
		return totalAnswer;
	}

	/* @ pure; */
	/**
	 * Checks if row <code>i</code> has <code>CONNECT</code> consecutive fields
	 * contains the mark <code>m</code>.
	 * 
	 * @param i
	 *            - the row of interest
	 * @param m
	 *            - the mark of interest
	 * @return true if row <code>i</code> has <code>CONNECT</code> consecutive
	 *         fields contains the mark <code>m</code>
	 */
	public boolean hasRow(int i, Mark m) {
		int counter = 0;
		int j = 0;
		while (j < LENGTH && counter < CONNECT) {
			if (getColumn(j).getField(i).equals(m)) {
				counter++;
			} else {
				counter = 0;
			}
			j++;
		}
		return counter == CONNECT;
	}

	/* @ pure; */
	/**
	 * Checks whether there is a column which has <code>CONNECT</code>
	 * consecutive fields contains the mark <code>m</code>.
	 * 
	 * @param m
	 *            - the mark of interest
	 * @return true if there is a column which has <code>CONNECT</code>
	 *         consecutive fields contains the mark <code>m</code>
	 */
	public boolean hasColumn(Mark m) {
		boolean totalAnswer = false;
		for (int i = 0; i < LENGTH; i++) {
			totalAnswer = totalAnswer || getColumn(i).hasColumn(m);
		}
		return totalAnswer;
	}

	/* @ pure; */
	/**
	 * Checks whether there is a diagonal which has <code>CONNECT</code>
	 * consecutive fields contains the mark <code>m</code>.
	 * 
	 * @param m
	 *            - the mark of interest
	 * @return true if there is a diagonal which has <code>CONNECT</code>
	 *         consecutive fields contains the mark <code>m</code>
	 */
	public boolean hasDiagonal(Mark m) {
		return hasDiagonalLR(m) || hasDiagonalRL(m);
	}

	/* @ pure; */
	/**
	 * Checks whether there is a diagonal with left high and right low, which
	 * has <code>CONNECT</code> consecutive fields contains the mark
	 * <code>m</code>.
	 * 
	 * @param m
	 *            - the mark of interest
	 * @return true if there is a diagonal with left high and right low, which
	 *         has <code>CONNECT</code> consecutive fields contains the mark
	 *         <code>m</code>
	 */
	public boolean hasDiagonalLR(Mark m) {
		int iStart = 0;
		int iStop = HEIGHT - CONNECT;
		int jStart = LENGTH - CONNECT;
		int jStop = 0;
		boolean connected = false;
		while (iStart <= iStop && jStart >= jStop) {
			int i = iStart;
			int j = jStart;
			int counter = 0;
			while (i < HEIGHT && j < LENGTH && counter < CONNECT) {
				if (getField(i, j).equals(m)) {
					counter++;
				} else {
					counter = 0;
				}
				i++;
				j++;
			}
			connected = connected || (counter == CONNECT);
			if (jStart > jStop) {
				jStart--;
			} else {
				iStart++;
			}
		}
		return connected;
	}

	/* @ pure; */
	/**
	 * Checks whether there is a diagonal with right high and left low, which
	 * has <code>CONNECT</code> consecutive fields contains the mark
	 * <code>m</code>.
	 * 
	 * @param m
	 *            - the mark of interest
	 * @return true if there is a diagonal with right high and left low, which
	 *         has <code>CONNECT</code> consecutive fields contains the mark
	 *         <code>m</code>
	 */
	public boolean hasDiagonalRL(Mark m) {
		int iStart = 0;
		int iStop = HEIGHT - CONNECT;
		int jStart = LENGTH - CONNECT;
		int jStop = LENGTH - 1;
		boolean connected = false;
		while (iStart <= iStop && jStart <= jStop) {
			int i = iStart;
			int j = jStart;
			int counter = 0;
			while (i < HEIGHT && j >= 0 && counter < CONNECT) {
				if (getField(i, j).equals(m)) {
					counter++;
				} else {
					counter = 0;
				}
				i++;
				j--;
			}
			connected = connected || (counter == CONNECT);
			if (jStart < jStop) {
				jStart++;
			} else {
				iStart++;
			}
		}
		return connected;
	}

	/*
	 * @ requires m == Mark.XX | m == Mark.OO; ensures \result == this.hasRow(m)
	 * | this.hasColumn(m) | this.hasDiagonal(m); pure;
	 */
	/**
	 * Checks if the mark <code>m</code> has won. A mark wins if it has at least
	 * <code>CONNECT</code> on a row, column or diagonal.
	 * 
	 * @param m
	 *            - the mark of interest
	 * @return true if the mark has won
	 */
	public boolean isWinner(Mark m) {
		assert m.equals(Mark.YELLOW) || m.equals(Mark.RED);
		return hasRow(m) || hasColumn(m) || hasDiagonal(m);
	}

	/*
	 * @ ensures \result == isWinner(Mark.XX) | \result == isWinner(Mark.OO);
	 * pure;
	 */
	/**
	 * Returns true if the game has a winner. This is the case when one of the
	 * marks has <code>CONNECT</code> on a row, column or diagonal.
	 * 
	 * @return true if the student has a winner.
	 */
	public boolean hasWinner() {
		return isWinner(Mark.YELLOW) || isWinner(Mark.RED);
	}

	/* @pure; */
	/**
	 * Returns a String representation of the board. In addition to the current
	 * situation.
	 * 
	 * @return the game situation as String
	 */
	public String toString() {
		String s = " ";
		for (int j = 0; j < LENGTH; j++) {
			s = s + " " + j + "  ";
		}
		s = s + "\n";
		for (int j = 0; j < LENGTH; j++) {
			s = s + "+---";
		}
		s = s + "+ \n";
		for (int i = 0; i < HEIGHT; i++) {
			String row = "|";
			for (int j = 0; j < LENGTH; j++) {
				row = row + " " + getColumn(j).toString(i) + " |";

			}
			s = s + row + "\n";
			for (int j = 0; j < LENGTH; j++) {
				s = s + "+---";
			}
			s = s + "+ \n";
		}
		return s;
	}

	// -- Commands ---------------------------------------------------

	/*
	 * @ ensures (\forall int i; 0 <= i & i < HEIGHT; \forall int j; 0 <= j & j
	 * < LENGTH; this.getField(i,j) == Mark.EMPTY);
	 */
	/**
	 * Empties all fields of the board (i.e., let them refer to the value
	 * Mark.EMPTY).
	 */
	public void reset() {
		for (int i = 0; i < LENGTH; i++) {
			columns[i].reset();
		}
		setChanged();
		notifyObservers();
	}

	/*
	 * @ requires this.isColumn(i); ensures this.getField(i) == m;
	 */
	/**
	 * Sets the content of column <code>i</code> to the mark <code>m</code>.
	 * 
	 * @param i
	 *            - the column number
	 * @param m
	 *            - the mark to be placed
	 */
	public void setColumn(int i, Mark m) {
		getColumn(i).setColumn(m);
		setChanged();
		notifyObservers();
	}

	/* @ pure; */
	/**
	 * Gives the length of the board.
	 * 
	 * @return LENGTH
	 */
	public int getLength() {
		return LENGTH;
	}

	/* @ pure; */
	/**
	 * Gives the height of the board.
	 * 
	 * @return HEIGHT
	 */
	public int getHeight() {
		return HEIGHT;
	}

	/*
	 * @pure;
	 * 
	 * @ensures \result >= 0;
	 */
	/**
	 * Counts the mark on the board.
	 * 
	 * @param m
	 *            - The mark to be counted.
	 * @return The amount of this mark in the board.
	 */
	public int countMark(Mark m) {
		int result = 0;
		for (int i = 0; i < LENGTH; i++) {
			result += getColumn(i).countMark(m);
		}
		return result;
	}

	/*
	 * @pure;
	 */
	/**
	 * Gives the board as a String according to the protocol.
	 * 
	 * @return the board as a String
	 */
	public String getBoardString() {
		String board = "";
		for (int i = 0; i < HEIGHT; i++) {
			for (int j = 0; j < LENGTH; j++) {
				board = board + msgSeperator + getColumn(j).getField(i).markString();
			}
		}
		return board;
	}

	/*
	 * @pure;
	 * 
	 * @ensures \result == true || \result == false;
	 */
	/**
	 * Determines if the move is legal.
	 * 
	 * @param move
	 *            - the move you want
	 * @return true if the move is legal, false if the move isn't legal
	 */
	public boolean isLegalMove(int move) {
		boolean result = false;
		if (move >= 0 && move < HEIGHT * LENGTH) {
			int column = move % LENGTH;
			int row = (move - column) / LENGTH;
			if (!getColumn(column).isFull()) {
				result = getColumn(column).getField(row).equals(Mark.EMPTY);
				if (row < HEIGHT) {
					result = result || (!getColumn(column).getField(row).equals(Mark.EMPTY));
				}
			}
		}
		return result;
	}
}
