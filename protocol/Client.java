package protocol;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import fouronarow.*;

public class Client extends Thread implements protocol.ProtocolControl, protocol.ProtocolConstants {

	/** Starts a Client-application. */
	public static void main(String[] args) {
		new Client();
	}

	private String clientName;
	private ServerHandler serverHandler;
	private static final String EXIT = "EXIT";
	private boolean accepted = false;
	private Mark mark;
	private TUI tui;
	private Game game;
	private Strategy computer;

	/**
	 * Creates new instance of Client.
	 */
	public Client() {
		tui = new TUI(this);
		tui.start();
	}

	/**
	 * Creates ServerHandler.
	 * 
	 * @param hostString
	 * @param port
	 * @return true if hostname is correct and serverHandler that is created
	 * is not null
	 */
	public boolean makeServerHandler(String hostString, int port) {
		InetAddress host = null;
		try {
			host = InetAddress.getByName(hostString);
		} catch (UnknownHostException e) {
			tui.print("client-ERROR: no valid hostname!");
			return false;
		}
		try {
			serverHandler = new ServerHandler(host, this, port);
		} catch (IOException e) {
			tui.print("client-ERROR: no valid port!");
		}
		if (serverHandler == null) {
			tui.print("client-ERROR: could not create a socket on " + host + " and port " + port);
			return false;
		}
		serverHandler.start();
		return true;
	}

	/**
	 * Takes care of sending a message to the clientHandler.
	 * 
	 * @param msg
	 * 	- String with message that needs to be sent
	 */
	public void toServer(String msg) {
		String message = msg;
		if (msg.equals(EXIT)) {
			shutdown();
		} else {
			if (!accepted) {
				clientName = msg;
				message = msg;
			} else {
				if (isInteger(msg)) {
					int value = Integer.parseInt(msg);

					int index = getGame().getBoard().getColumn(value).getEmptyField();
					if (index < Board.HEIGHT) {
						message = doMove + msgSeperator + (index * Board.LENGTH + value);
					}
				}
			}
			serverHandler.sendMessage(message);
		}
	}

	/** close the socket connection. 
	 * 
	 */
	private void shutdown() {
		serverHandler.shutdown();
		System.exit(0);
	}

	/**
	 * Starts a game.
	 * 
	 * @param name1
	 * 		- String with name of first player
	 * @param name2
	 * 		- String with name of second player
	 */
	public void startGame(String name1, String name2) {
		game = new Game(new HumanPlayer(name1, Mark.STARTMARK), new HumanPlayer(name2,
				Mark.STARTMARK.other()), tui);
		game.reset();
	}

	/**
	 * Gives name of client.
	 * 
	 * @return clientName
	 *      - String with name of Client
	 */
	public String getClientName() {
		return clientName;
	}

	/**
	 * Gives game of the client.
	 * 
	 * @return game
	 *      - Game of client
	 */
	public Game getGame() {
		return game;
	}
	
	/**
	 * Gives boolean.
	 * 
	 * @return accepted
	 *      - boolean.
	 */
	public boolean getAccepted() {
		return accepted;
	}

	/**
	 * Gives mark of client.
	 * 
	 * @return mark
	 *      - Mark(YELLOW, RED or EMPTY)
	 */
	public Mark getMark() {
		return mark;
	}

	/**
	 * Gives "EXIT".
	 * 
	 * @return clientName
	 *      - String with "EXIT"
	 */
	public String getExit() {
		return EXIT;
	}

	/**
	 * Gives TUI of client.
	 * 
	 * @return tui
	 *      - TUI of client
	 */
	public TUI getTUI() {
		return tui;
	}

	/**
	 * Replaces local variable clientName in value of the parameter name.
	 * 
	 * @param name
	 * 		- String with name of client
	 */
	public void setClientName(String name) {
		this.clientName = name;
	}

	/**
	 * Replaces local variable game in value of the parameter gameA.
	 * 
	 * @param gameA
	 * 		- instance of Game
	 */
	public void makeGame(Game gameA) {
		this.game = gameA;
	}

	/**
	 * Replaces local variable mark in value of the parameter markA.
	 * 
	 * @param markA
	 * 		- Mark(YELLOW, RED or EMPTY)
	 */
	public void setMark(Mark markA) {
		this.mark = markA;
	}

	/**
	 * Replaces local variable accepted in value of the parameter acceptedA.
	 * 
	 * @param accepted
	 * 		- boolean
	 */
	public void setAccepted(boolean acceptedA) {
		this.accepted = acceptedA;
	}

	
	/**
	 * Gives Turn to the right client.
	 * 
	 * @param turnA
	 * 		- String
	 */
	public void setTurn(String turnA) {
		int current = 0;
		if (turnA.equals(clientName) && mark.equals(Mark.STARTMARK.other())) {
			current = 1;
		} else if (!turnA.equals(clientName) && mark.equals(Mark.STARTMARK)) {
			current = 1;
		}
		game.setCurrent(current);
	}

	
	/**
	 * Returns if input is an integer.
	 * 
	 * @param input
	 * 		- String with possible integer
	 * @return true if string is an integer, false if not
	 */
	private boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Replaces local variable computer in value of the parameter computerA.
	 * 
	 * @param computerA
	 * 		- Strategy 
	 */
	public void setComputer(Strategy computerA) {
		this.computer = computerA;
	}
	
	/**
	 * Gives Strategy of client.
	 * 
	 * @return computer
	 *      - Strategy of client
	 */
	public Strategy getComputer() {
		return computer;
	}
}
