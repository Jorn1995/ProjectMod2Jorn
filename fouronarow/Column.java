package fouronarow;

public class Column {
	// -- Constants --------------------------------------------------
	/**
	 * How much marks must be connected to win the game.
	 */
	private int connect;

	/**
	 * The height of the column.
	 */
	private int height;
	// -- Instance variables -----------------------------------------

	/*
	 * @ private invariant fields.length == length; invariant (\forall int i; 0
	 * <= i & i < length; getField(i) == Mark.EMPTY || getField(i) == Mark.XX ||
	 * getField(i) == Mark.OO);
	 */
	/**
	 * The column fields of the Four on a Row game.
	 */
	private Mark[] fields;

	// -- Constructors -----------------------------------------------
	/*
	 * @ requires height > 0 && connect > 0; ensures (\forall int i; 0 <= i & i
	 * < height; this.getField(i) == Mark.EMPTY);
	 */
	/**
	 * 
	 * @param height
	 *            - the height of the column
	 * @param connect
	 *            -
	 */
	public Column(int heightA, int connectA) {
		this.height = heightA;
		this.fields = new Mark[height];
		this.connect = connectA;
		reset();
	}

	// -- Queries ----------------------------------------------------

	/**
	 * Sets the content of column, drops a mark (<code>m</code>) in the column.
	 * 
	 * @param m
	 *            - the mark to be placed
	 */
	public boolean setColumn(Mark m) {
		if (isFull()) {
			return false;
		} else {
			setField(getEmptyField(), m);
			return true;
		}
	}

	/*
	 * @ ensures this.getField(i) == m;
	 */
	/**
	 * Sets the content of field <code>i</code> to the mark <code>m</code>.
	 * 
	 * @param i
	 *            - the field number (see NUMBERING)
	 * @param m
	 *            - the mark to be placed
	 */
	public void setField(int i, Mark m) {
		this.fields[i] = m;
	}

	/*
	 * @ ensures (\forall int i; 0 <= i & i < height; this.getField(i) ==
	 * Mark.EMPTY);
	 */
	/**
	 * Empties all fields of this column (i.e., let them refer to the value
	 * Mark.EMPTY).
	 */
	public void reset() {
		for (int i = 0; i < height; i++) {
			setField(i, Mark.EMPTY);
		}
	}

	/*
	 * @ ensures \result != this; ensures (\forall int i; 0 <= i & i < height;
	 * \result.getField(i) == this.getField(i));
	 */
	/**
	 * Creates a deep copy of this column.
	 */
	public Column deepCopy() {
		Column copy = new Column(height, connect);
		for (int i = 0; i < height; i++) {
			copy.setField(i, getField(i));

		}
		return copy;
	}

	// -- Commands ---------------------------------------------------

	/*
	 * @ requires this.isField(i); ensures \result == Mark.EMPTY || \result ==
	 * Mark.XX || \result == Mark.OO; pure;
	 */
	/**
	 * Returns the content of the field <code>i</code>.
	 * 
	 * @param i
	 *            - the number of the field in the column
	 * @return the mark on the field
	 */
	public Mark getField(int i) {
		if (i >= 0 && i < height) {
			return fields[i];
		} else {
			return null;
		}
	}

	/*
	 * @ requires this.isField(i); ensures \result == (this.getField(i) ==
	 * Mark.EMPTY); pure;
	 */
	/**
	 * Returns true if the field <code>i</code> is empty.
	 * 
	 * @param i
	 *            - the index of the field
	 * @return true if the field is empty
	 */
	public boolean isEmptyField(int i) {
		return getField(i).equals(Mark.EMPTY);
	}

	/*
	 * @ ensures \result == (this.getField(0) != Mark.EMPTY); pure;
	 */
	/**
	 * Tests if the whole column is full.
	 * 
	 * @return true if fields[0] is occupied
	 */
	public boolean isFull() {
		return !fields[0].equals(Mark.EMPTY);
	}

	/*
	 * @ pure;
	 */
	/**
	 * Returns a String representation of a field in this column. In addition to
	 * the current situation.
	 * 
	 * @return the game situation as String
	 */
	public String toString(int i) {
		return getField(i).toString();
	}

	/*
	 * @ pure;
	 */
	/**
	 * Checks whether there is <code>connect</code> in this column of the mark
	 * <code>m</code>.
	 * 
	 * @param m
	 *            - the mark of interest
	 * @return true if this column has <code>connect</code> marks <code>m</code>
	 *         in a row.
	 */
	public boolean hasColumn(Mark m) {
		int counter = 0;
		int i = 0;
		while (i < height && counter < connect) {
			if (getField(i).equals(m)) {
				counter++;
			} else {
				counter = 0;
			}
			i++;
		}
		return counter == connect;
	}

	/**
	 * Counts how often a particular mark has been spotted.
	 * 
	 * @param m
	 * 	- Mark of interest
	 * @return int that shows how often a mark is counted
	 */
	public int countMark(Mark m) {
		int result = 0;
		for (int i = 0; i < height; i++) {
			if (getField(i).equals(m)) {
				result++;
			}
		}
		return result;
	}

	
	
	/**
	 * Detects a free field and its index in this column.
	 * 
	 * @return int that represents an index free field.
	 */
	public int getEmptyField() {
		if (isFull()) {
			return height;
		} else {
			int i = height - 1;
			while (!fields[i].equals(Mark.EMPTY)) {
				i--;
			}
			return i;
		}
	}


	/**
	 * Checks if object given as parameter is the same (this).
	 * 
	 * @param column
	 * 	- object
	 * @return true if object given as parameter is the same as (this). false if not.
	 */
	public boolean equals(Object column) {
		boolean result = false;
		if (column instanceof Column) {
			result = true;
			for (int i = 0; i < height; i++) {
				if (!((Column) column).getField(i).equals(getField(i))) {
					result = false;
				}
			}
		}
		return result;
	}
}
