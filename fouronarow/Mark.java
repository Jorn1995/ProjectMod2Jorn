package fouronarow;

public enum Mark implements protocol.ProtocolControl, protocol.ProtocolConstants {

	EMPTY, YELLOW, RED;
	public static final Mark STARTMARK = Mark.YELLOW;

	/*
	 * @ ensures this == Mark.XX ==> \result == Mark.OO; ensures this == Mark.OO
	 * ==> \result == Mark.XX; ensures this == Mark.EMPTY ==> \result ==
	 * Mark.EMPTY;
	 */
	/**
	 * Returns the other mark.
	 * 
	 * @return the other mark is this mark is not EMPTY or EMPTY
	 */
	public Mark other() {
		if (this == YELLOW) {
			return RED;
		} else if (this == RED) {
			return YELLOW;
		} else {
			return EMPTY;
		}
	}

	/**
	 * Gives a String of the mark.
	 * 
	 * @return string of mark
	 */
	public String toString() {
		if (this.equals(YELLOW)) {
			return "X";
		} else if (this.equals(RED)) {
			return "O";
		} else {
			return " ";
		}
	}

	/**
	 * Gives the mark as a string.
	 * 
	 * @return mark as String
	 */
	public String markString() {
		if (this.equals(YELLOW)) {
			return yellow;
		} else if (this.equals(RED)) {
			return red;
		} else {
			return empty;
		}
	}

	/**
	 * Gives the string as a mark.
	 * 
	 * @param mark
	 * 		-	String of a Mark
	 * 
	 * @return Mark of the String as parameter
	 */
	public static Mark stringToMark(String mark) {
		if (mark.equals(yellow)) {
			return Mark.YELLOW;
		} else if (mark.equals(red)) {
			return Mark.RED;
		} else {
			return Mark.EMPTY;
		}
	}
}
