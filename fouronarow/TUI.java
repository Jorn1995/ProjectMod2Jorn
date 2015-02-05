package fouronarow;

import java.util.Observable;
import protocol.*;
import java.util.Observer;
import java.util.Scanner;

import protocol.Client;

/**
 * @author Jorn and Ingrid
 *
 */
public class TUI extends Thread implements Observer, ProtocolControl, ProtocolConstants {
	private Scanner scanner;
	private Scanner scan;
	private boolean started = false;
	private Client client;
	private boolean local = true;
	private boolean rematcher = false;
	private int sleep = 1000;

	private static final String HELPSTRING = "Write 'START' to start a game \n"
											+ "Write 'HELP' to get help \n" 
											+ "Write 'EXIT' to leave the game";
	private Scanner scanner2;

	/*
	 * @requires clientA != null;
	 */
	/**
	 * Makes a new TUI.
	 * 
	 * @param clientA
	 *            - the client for this TUI
	 */
	public TUI(Client clientA) {
		this.client = clientA;
		scanner = new Scanner(System.in);
	}

	/**
	 * prints an error message.
	 * 
	 * @param error
	 *            - the error message to be printed
	 */
	public void showError(String error) {
		print(error);
	}

	/*
	 * @requires client != null;
	 */
	/**
	 * Starts the TUI.
	 */
	public void start() {
		print("FOUR ON A ROW");
		print(HELPSTRING);
		while (scanner.hasNextLine()) {
			scan = new Scanner(scanner.nextLine());
			if (scan.hasNext()) {
				String temp = scan.next();
				scanner2 = new Scanner(temp);
				if (scanner2.hasNextInt() && started) {
					int value = scanner2.nextInt();
					if (local) {
						client.getGame().doMove(value);
						startOver();
					} else {
						sendMove(value);
					}
				}
				if (scanner2.hasNextInt() && !started) {
					print("To do a move, start a Game!!");
				}
				if (temp.equals("START") && !started) {
					started = true;
					boolean door = true;
					while (door) {
						print("Do you want to play on the network? (y/n)");
						scan = new Scanner(scanner.nextLine());
						if (scan.hasNext()) {
							temp = scan.next();
							if (temp.equals("y")) {
								local = false;
								door = false;
								startGameNetwork();
							} else if (temp.equals("n")) {
								door = false;
								startGameLocal();
							}
						}
					}
				}
				if (rematcher && !started) {
					started = true;
					if (readBoolean("y", "n", temp)) {
						client.toServer(rematch);
					} else {
						print("Thanks for playing!");
						System.exit(0);
					}
				}

				if (temp.equals("HELP")) {
					print(HELPSTRING);
				}
				if (temp.equals("EXIT")) {
					System.exit(0);
				}
				if (temp.equals("HINT")) {
					client.getGame().determineCurrent();
					Mark mark;
					if (local) {
						mark = client.getGame().getCurrentPlayer().getMark();
					} else {
						mark = client.getMark();
					}
					SmartStrategy smart = new SmartStrategy();
					Board board = client.getGame().getBoard();
					int move = smart.determineMove(board, mark);
					print("Maybe is column " + move + " a good idea...");
				}
				if (temp.equalsIgnoreCase("SetTimer")) {
					if (scan.hasNextInt()) {
						int sleepA = scan.nextInt();
						setSleep(sleepA);
					}
				}
			}
		}

	}

	/*
	 * @requires client != null;
	 */
	/**
	 * Sends a move to the server.
	 * 
	 * @param value
	 *            - the move to be send.
	 */
	public void sendMove(int value) {
		if (client.getGame().getBoard().isLegalMove(value)) {
			int index = client.getGame().getBoard().getColumn(value).getEmptyField();
			if (index < Board.HEIGHT) {
				client.toServer(doMove + msgSeperator + (index * Board.LENGTH + value));
			}
		} else {
			print(invalidMove);
		}
	}

	/*
	 * @requires client != null;
	 */
	/**
	 * Prints the current game situation.
	 */
	public void update(Observable o, Object v) {
		print("\ncurrent game situation: \n\n" + client.getGame().getBoard().toString() + "\n");
		if (!client.getGame().getBoard().gameOver()) {
			print(giveTurnString());
		}
		if (!client.getGame().getBoard().gameOver()) {
			if (client.getComputer() != null && !local) {
				if (client.getGame().getCurrentPlayer().getName().equals(client.getClientName())) {
					Strategy strategy = client.getComputer();
					int move = strategy
									.determineMove(client.getGame().getBoard(), client.getMark());
					try {
						sleep(sleep);
					} catch (InterruptedException e) {
						print("The computer doesn't want to sleep.");
					}
					print("The computer chooses " + move);
					sendMove(move);
				}
			}
		}
	}

	/*
	 * @requires client != null;
	 */
	/**
	 * Makes a string for who's turn it is.
	 * 
	 * @return String for the turn
	 */
	private String giveTurnString() {
		String result = "";
		client.getGame().determineCurrent();
		if (!local) {
			if (!client.getGame().getCurrentPlayer().getName().equals(client.getClientName())) {
				result = "Wait on the opponents turn.";
			} else {
				result = "> " + client.getClientName() + " (" + client.getMark().toString() + ")";
				if (client.getComputer() == null) {
					result = result + ", what is your choice? ";
				} else {
					result = result + ", wait on what the computer chooses";
				}
			}
		} else {
			result = "> " + client.getGame().getCurrentPlayer().getName() + " ("
					+ client.getGame().getCurrentPlayer().getMark().toString() + ")"
					+ ", what is your choice? ";
		}
		return result;
	}

	/*
	 * @requires client != null;
	 */
	/**
	 * Asks some things to starts a game over the network.
	 */
	public synchronized void startGameNetwork() {
		String host = null;
		local = false;
		int port = 0;
		do {
			print("On which host do you want to connect?");
			scan = new Scanner(scanner.nextLine());
			if (scan.hasNext()) {
				host = scan.next();
			}
			print("On which port do you want to connect?(1337)");
			scan = new Scanner(scanner.nextLine());
			if (scan.hasNextInt()) {
				port = scan.nextInt();
			}
		} while (!client.makeServerHandler(host, port));

		boolean door = true;
		while (door) {
			print("Do you want to play with a computerplayer? (SMART/NAIVE/no)");
			scan = new Scanner(scanner.nextLine());
			if (scan.hasNext()) {
				String temp = scan.next();
				if (temp.equals("SMART")) {
					client.setComputer(new SmartStrategy());
					door = false;
				} else if (temp.equals("NAIVE")) {
					client.setComputer(new NaiveStrategy());
					door = false;
				} else if (temp.equals("no")) {
					door = false;
				}
			}
		}

		do {
			print("Your name:");
			scan = new Scanner(scanner.nextLine());
			String player = null;
			client.setAccepted(false);
			if (scan.hasNext()) {
				player = scan.next();
				client.toServer(player);
			}

			try {
				wait();
			} catch (InterruptedException e) {
				System.out.println("");
			}
		} while (!client.getAccepted());
		started = true;

	}

	/*
	 * @requires client != null;
	 */
	/**
	 * Asks some things to starts a local game.
	 */
	public void startGameLocal() {
		print("Name of Player 1: (Write 'SMART' or 'NAIVE' to make Player 1 a smart or naive AI)");
		scan = new Scanner(scanner.nextLine());
		String player1 = null;
		while (scan.hasNext() && player1 == null) {
			player1 = scan.next();
		}
		print("Name of Player 2: (Write 'SMART' or 'NAIVE' to make Player 1 a smart or naive AI)");
		scan = new Scanner(scanner.nextLine());
		String player2 = null;
		while (scan.hasNext() && player2 == null) {
			player2 = scan.next();
		}
		String[] args = new String[2];
		args[0] = player1;
		args[1] = player2;
		Mark mark = Mark.RED;
		Player[] player = new Player[2];
		for (int i = 0; i < 2; i++) {
			mark = mark.other();
			if (args[i].equals("NAIVE")) {
				player[i] = new ComputerPlayer(mark);
			} else if (args[i].equals("SMART")) {
				player[i] = new ComputerPlayer(mark, new SmartStrategy());
			} else {
				player[i] = new HumanPlayer(args[i], mark);
			}
		}
		client.makeGame(new Game(player[0], player[1], this));
		client.getGame().reset();
	}

	/*
	 * @requires local != null;
	 * 
	 * @pure;
	 */
	/**
	 * You can start a next game with different settings.
	 */
	public void another() {
		if (local) {
			startGameLocal();
		} else {
			startGameNetwork();
		}
	}

	/**
	 * Asks if you want a rematch or not.
	 */
	public void startOverNetwork() {
		print("Opnieuw?  (y/n)");
		started = false;
		rematcher = true;
	}

	/*
	 * @requires client != null;
	 */
	/**
	 * Starst a new local game.
	 */
	public void startOver() {
		if (client.getGame().getBoard().gameOver()) {
			print("Opnieuw?(y/n/a)");
			scan = new Scanner(scanner.nextLine());
			while (scan.hasNext()) {
				String temp = scan.next();
				if (temp.equals("y") || temp.equals("n") || temp.equals("a")) {
					if (temp.equals("y")) {
						client.getGame().reset();
						break;
					} else if (temp.equals("n")) {
						print("Thanks for playing!");
						System.exit(0);
						break;
					} else if (temp.equals("a")) {
						another();
						break;
					}
				} else {
					System.out.println("invalid input");
					scan = new Scanner(scanner.nextLine());
				}

			}
		}
	}

	/*
	 * @pure;
	 */
	/**
	 * Returns if the clients starts a game or not.
	 * 
	 * @return started
	 */
	public boolean getStarted() {
		return started;
	}

	/*
	 * @pure;
	 */
	/**
	 * Prints the text the method gets.
	 * 
	 * @param text
	 *            - the text to be printed
	 */
	public void print(String text) {
		System.out.println(text);
	}

	/*
	 * @ensures \result == true || \result == false;
	 * 
	 * @pure;
	 */
	/**
	 * Returns if the answer is the yes argument.
	 * 
	 * @param yes
	 *            - the yes argument
	 * @param no
	 *            - the no argument
	 * @param answer
	 *            - The given answer
	 * @return true - the answer is the yes argument, false - otherwise
	 */
	public boolean readBoolean(String yes, String no, String answer) {
		if (answer == null) {
			return false;
		} else {
			return answer.equals(yes);
		}
	}

	/*
	 * @requires slaapA >= 0;
	 */
	/**
	 * Sets how long a computerplayer has to sleep when he does a move.
	 * 
	 * @param sleepA
	 *            - how long the computerplayer has to sleep
	 */
	public void setSleep(int sleepA) {
		sleep = sleepA;
	}

}
