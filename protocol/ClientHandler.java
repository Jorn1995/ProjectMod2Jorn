package protocol;

import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Observable;

import fouronarow.Mark;
import fouronarow.Board;

/**
 * @author Jorn and Ingrid
 */
public class ClientHandler extends Observable 
							implements Runnable, ProtocolControl, ProtocolConstants {
	private Server server;
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	private String clientName;
	private boolean accepted = false;
	private Mark mark;
	private boolean isPlaying = false;

	/*
	 *@requires server != null && sock != null;
	 */
	/**
	 * Constructs a ClientHandler object. Initialises both Data streams.
	 * @param serverArg - the server who wants to make the ClientHandler
	 * @param sockArg - the socket for the ClientHandler
	 */
	public ClientHandler(Server serverArg, Socket sockArg) {
		if (serverArg != null && sockArg != null) {
			this.server = serverArg;
			this.sock = sockArg;
			try {
				this.in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				this.out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			} catch (IOException e) {
				server.getUI().print("Can't create ClientHandler");
			}
		}
		addObserver(server.getAddHandlers());
	}
	
	/*
	 * @requires server != null;
	 */
	/**
	 * Reads the name of a Client from the input stream. If it is right 
	 * the clientHandler will put in server.acceptedHandlers. Else there 
	 * goes an invalidCommand to the client.
	 * @param msg - the message you want to send
	 */
	private void announce(String msg) {
		String[] text = msg.split(msgSeperator);
		String error = "";
		if (text.length == 2) {
			if (text[0].equals(joinRequest)) {
				if (!server.nameIsUsed(text[1])) {
					if (text[1].matches(charRegex)) {
						clientName = text[1];
						accepted = true;
						(server.acceptedHandlers).add(this);
						setChanged();
						notifyObservers();
					} else {
						error = invalidUsername;
					}
				} else {
					error = usernameInUse;
				}
			} else {
				error = invalidCommand;
			}
		} else {
			error = invalidCommand;
		}
		if (!error.equals("")) {
			sendMessage(invalidCommand + msgSeperator + error);
		}
	}
	
	/*
	 * @requires in != null;
	 * @requires server != null;
	 */
	/**
	 * This method takes care of sending messages from the Client. 
	 * If an IOException is thrown while reading the message, the method 
	 * concludes that the socket connection is broken and shutdown() will be called.
	 */
	public void run() {
		try {
			String text = in.readLine();
			while (text != null) {
				server.getUI().print(clientName + " -> server: " + text);
				if (accepted) {
					String[] msg = text.split(msgSeperator);
					if (msg[0].equals(getBoard) && msg.length == 1) {
						handleGetBoard();
					} else if (msg[0].equals(playerTurn) && msg.length == 1) {
						handlePlayerTurn();
					} else if (msg[0].equals(doMove) && msg.length == 2) {
						handleDoMove(msg[1]);
					} else if (msg[0].equals(rematch) && msg.length == 1) {
						handleRematch();
					} else {
						sendMessage(invalidCommand + msgSeperator + invalidCommand);
					}
				} else {
					announce(text);
				}
				text = in.readLine();
			}
		} catch (IOException e1) {
			ClientHandler other = server.otherClient(this);
			if (other != null) {
				other.sendMessage(endGame + msgSeperator + other.getClientName() + msgSeperator
									+ connectionlost);
				other.setPlaying(false);
				isPlaying = false;
			}
			shutdown();
		}
	}
	
	/*
	 * @requires server != null;
	 */
	/**
	 * Adds this ClientHandler to server.rematch and says to the observers 
	 * that something is changed.
	 */
	private void handleRematch() {
		server.rematch.add(this);
		setChanged();
		notifyObservers();
	}
	
	/*
	 * @requires server != null;
	 * @requires board != null;
	 * @pure;
	 */
	/**
	 * Sends a string of the board to the client.
	 */
	private void handleGetBoard() {
		Board board = server.getBoard(this);
		String boardString = board.getBoardString();
		sendMessage(sendBoard + boardString);
	}
	
	/*
	 * @requires server != null;
	 * @requires server.otherClient(this) != null;
	 * @pure;
	 */
	/**
	 * Checks on the right board on the server who´s turn it is and sends it toe the client.
	 */
	private void handlePlayerTurn() {
		Mark nextMark = server.getTurn(this);
		String nextPlayer = clientName;
		if (!nextMark.equals(this.mark)) {
			nextPlayer = server.otherClient(this).getClientName();
		}
		sendMessage(turn + msgSeperator + nextPlayer);
	}
	
	/*
	 * @requires server != null;
	 * @requires board != null;
	 */
	/**
	 * Handles the move the client wants to do. This method checks if it is a valid move.
	 * If it is a valid move, the result will send to both clientHandlers with this board.
	 * Else an moveresult will only send to the client the move did.
	 * If there is a gameOver after the move, this will send to both players.
	 * @param move - the move the client wants to do.
	 */
	private void handleDoMove(String move) {
		String error = "";
		if (server.getTurn(this).equals(mark)) {
			if (!server.getBoard(this).gameOver()) {
				if (isInteger(move)) {
					int index = Integer.parseInt(move);
					boolean validMove = server.getBoard(this).isLegalMove(index);
					String other;
					String message = null;
					if (validMove) {
						server.getBoard(this).setColumn(index % Board.LENGTH, mark);
						other = server.otherClient(this).getClientName();
						message = moveResult + msgSeperator + index + msgSeperator + clientName
								+ msgSeperator + validMove + msgSeperator + other;
					} else {
						other = clientName;
						message = invalidCommand + msgSeperator + invalidMove;
					}

					sendMessage(message);
					if (server.otherClient(this) != null) {
						server.otherClient(this).sendMessage(message);
					}
					if (validMove) {
						handleGetBoard();
						if (server.otherClient(this) != null) {
							server.otherClient(this).handleGetBoard();
						}
					}

					Board board = server.getBoard(this);
					if (board.gameOver()) {
						String msg = "";
						if (board.hasWinner()) {
							String winnerName = clientName;
							if (!board.isWinner(mark)) {
								if (server.otherClient(this) != null) {
									winnerName = server.otherClient(this).getClientName();
								}
							}
							msg = endGame + msgSeperator + winnerName + msgSeperator + winner;
						} else {
							String player1 = clientName;
							if (!mark.equals(Mark.STARTMARK)) {
								player1 = server.otherClient(this).getClientName();
							}
							msg = endGame + msgSeperator + player1 + draw;
						}
						server.broadcast(msg, this);
					}
				}
			} else {
				error = invalidCommand;
			}
		} else {
			error = invalidUserTurn + msgSeperator + clientName;
		}
		if (!error.equals("")) {
			sendMessage(invalidCommand + msgSeperator + error);
		}
	}

	
	/*
	 * @requires server != null;
	 * @requires out != null;
	 * @pure;
	 */
	/**
	 * This method can be used to send a message over the socket connection to
	 * the Client. If the writing of a message fails, the method concludes that
	 * the socket connection has been lost and shutdown() is called.
	 * @param msg - the message to the client.
	 */
	public void sendMessage(String msg) {
		String message = msg;
		if (msg.startsWith(acceptRequest)) {
			String[] msgs = msg.split(msgSeperator);
			if (msgs.length == 2) {
				String markS = msgs[1];
				if (markS.equals(red)) {
					this.mark = Mark.RED;
				} else if (markS.equals(yellow)) {
					this.mark = Mark.YELLOW;
				} else {
					message = invalidCommand + msgSeperator + invalidCommand;
				}
			}
		}
		server.getUI().print("   server -> " + clientName + ": " + message);
		try {
			out.write(message);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}
	
	/*
	 * @requires accepted != null;
	 * @ensures \result == true || \result == false;
	 * @pure;
	 */
	/**
	 * Returns if the clientHandler is accepted or not.
	 * @return accepted
	 */
	public boolean isAccepted() {
		return accepted;
	}
	
	/*
	 * @requires clientName != null;
	 * @pure;
	 */
	/**
	 * Gives the clientName that belongs to this ClientHandler.
	 * @return clientName
	 */
	public String getClientName() {
		return clientName;
	}
	
	/*
	 * @requires server != null;
	 */
	/**
	 * Removes this clientHandler from the list of clientHandlers.
	 */
	private void shutdown() {
		server.removeHandler(this);
	}
	
	/*
	 * @ensures \result == true || \result == false;
	 * @pure;
	 */
	/**
	 * Checks if the inputstring is an interger or not.
	 * @param input - String of which you want to check if it is an integer.
	 * @return true - String is an integer, false - String is not an integer.
	 */
	private boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/*
	 * @ensures isPlaying == true || isPlaying == false;
	 */
	/**
	 * Sets the isPlaying boolean.
	 * @param play - new value of this.play
	 */
	public void setPlaying(boolean play) {
		isPlaying = play;
	}
	
	/*
	 * @ensures \result == true || \result == false;
	 * @pure;
	 */
	/**
	 * Returns if the clientHandler is playing or not.
	 * @return isPlaying
	 */
	public boolean getPlaying() {
		return isPlaying;
	}

}
