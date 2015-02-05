package fouronarow;

import java.util.Observable;

/**
 * Class for maintaining the Four on a Row game.
 * 
 * @author Jorn Boksem and Ingrid Maas
 * @version $Revision: 1.0 $
 */
public class Game extends Observable {

	// -- Instance variables -----------------------------------------

	public static final int NUMBER_PLAYERS = 2;

	/*
	 * @ private invariant board != null;
	 */
	/**
	 * The board.
	 */
	private Board board;

	/*
	 * @ private invariant players.length == NUMBER_PLAYERS; private invariant
	 * (\forall int i; 0 <= i && i < NUMBER_PLAYERS; players[i] != null);
	 */
	/**
	 * The 2 players of the game.
	 */
	private Player[] players;

	/*
	 * @ private invariant 0 <= current && current < NUMBER_PLAYERS;
	 */
	/**
	 * Index of the current player.
	 */
	private int current = 0;

	private TUI view;

	// -- Constructors -----------------------------------------------
	/*
	 * public Game() { TUI view = new TUI(); view.start(); }
	 */
	/*
	 * @ requires s0 != null; requires s1 != null;
	 */
	/**
	 * Creates new Game instance.
	 * 
	 * @param s0
	 * - the first player
	 * @param s1
	 * - the second player
	 * @param viewA
	 * - TUI 
	 */
	public Game(Player s0, Player s1, TUI viewA) {
		board = new Board();
		this.view = viewA;
		board.addObserver(viewA);
		addObserver(viewA);
		players = new Player[NUMBER_PLAYERS];
		players[0] = s0;
		players[1] = s1;
		current = 0;
	}

	// -- Commands ---------------------------------------------------

	/**
	 * Returns TUI of this game.
	 * 
	 * @return view
	 */
	private TUI getView() {
		return view;
	}

	/**
	 * Returns Board of this game.
	 * 
	 * @return board
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * This method compares answer with the other parameters and
	 * returns a boolean as a result. If answer and another are equal,
	 * the method "another" in class TUI will be called. 
	 * 
	 * @param another 
	 * 			  - the String corresponding to a another answer
	 * @param yes
	 *            - the String corresponding to a yes answer
	 * @param no
	 *            - the String corresponding to a no answer
	 * @param answer
	 * 			  - the String that will be compared with the other parameters
	 * @return true is the yes answer is typed, false if the no answer is typed
	 */
	public boolean readBoolean(String yes, String no, String another, String answer) {
		if (answer == null) {
			return false;
		}
		if (!answer.equals(yes) && !answer.equals(no) && !answer.equals(another)) {
			return false;
		}
		if (answer.equals(another)) {
			getView().another();
		}
		return answer.equals(yes);
	}

	/**
	 * Resets the game.
	 * 
	 * Current will be set as the default value 0.
	 * The board is Emptied, and there will be a move or
	 * a serie of moves if there is one ComputerPlayers,
	 * respectively two ComputerPlayers.
	 */
	public void reset() {
		current = 0;
		board.reset();
		if (getCurrentPlayer() instanceof ComputerPlayer) {
			doMove();
		}
		if (getCurrentPlayer() instanceof ComputerPlayer) {
			cpuS();
		}
	}

	
	/**
	 * This method first checks if the slot the HumanPlayer
	 * wants to do a move in, is actually a slot. When it is an actual slot
	 * the method makemove from class Player will be called. After this it will 
	 * check if the game is finshed. If it is finished or the slot is not an actual slot
	 * there will be proper messages printed on the System.out, if it is not finished the
	 * current player will determined and checked if it is a ComputerPlayer. If it is the method 
	 * doMove will be called and there will be checked again if the game is over.
	 * 
	 * @param slot
	 * 	- The move the HumanPlayer wants to do
	 */
	public void doMove(int slot) {
		if (board.getColumn(slot) != null) {
			players[current].makeMove(board, slot);
			if (board.gameOver()) {
				printResult();
			} else {
				determineCurrent();
			}
			if (players[current] instanceof ComputerPlayer) {
				doMove();
				if (board.gameOver()) {
					printResult();
				}
			}
		} else {
			System.out.println("ERROR: field " + slot + " is no valid choice.");
		}
	}

	/**
	 * Plays move for the ComputerPlayer, by calling the makeMove method with
	 * one parameter from class Player. After this the current player will be determined.
	 */
	private void doMove() {
		players[current].makeMove(board);
		determineCurrent();
	}

	/**
	 * Checks if the game is over, while it is not over, the method
	 * doMove will be called. When the game is over there will be a proper
	 * printed message by the method printResult and the Player will be 
	 * asked to startOver by the method startOver in class TUI
	 */
	private void cpuS() {
		while (!board.gameOver()) {
			doMove();
		}
		if (board.gameOver()) {
			printResult();
			getView().startOver();
		}
	}

	/*
	 * @ requires this.board.gameOver();
	 */

	/**
	 * Prints the result of the last game. <br>
	 */
	private void printResult() {
		if (board.hasWinner()) {
			Player winner = board.isWinner(players[0].getMark()) ? players[0] : players[1];
			String win = "Speler " + winner.getName();
			System.out.println(win + " (" + winner.getMark().toString() + ") has won!");
		} else {
			System.out.println("Draw. There is no winner!");
		}
	}

	/**
	 * Replaces the local variable board with the instance of Board that has been given as 
	 * an argument. The observers of this class will be notified.
	 * 
	 * @param boardA
	 * 	- board to replace the local board with
	 */
	public void replaceBoard(Board boardA) {
		this.board = boardA;
		setChanged();
		notifyObservers();
	}

	/**
	 * Replaces the local variable current with the integer that has been given as 
	 * an argument.
	 * 
	 * @param currentA
	 */
	public void setCurrent(int currentA) {
		this.current = currentA;
	}

	/**
	 * Returns current Player.
	 * 
	 * @return players[current]
	 */
	public Player getCurrentPlayer() {
		return players[current];
	}

	
	/**
	 * Counts the marks and determines on the base of that what the integer of the current variable
	 * will be.
	 */
	public void determineCurrent() {
		int startMark = board.countMark(Mark.STARTMARK);
		int otherMark = board.countMark(Mark.STARTMARK.other());
		if (startMark > otherMark) {
			current = 1;
		} else {
			current = 0;
		}
	}
}
