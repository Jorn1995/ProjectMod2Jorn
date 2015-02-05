package protocol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import fouronarow.Board;
import fouronarow.Mark;

public class ServerHandler extends Thread implements ProtocolConstants, ProtocolControl {

	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	private Client client;

	/**
	 * Makes a new ServerHandler.
	 * @param host - the host where you wants to connect
	 * @param clientA - the client that wants to connect to the host
	 * @param port - the port on the host the client wants to connect
	 * @throws IOException
	 */
	public ServerHandler(InetAddress host, Client clientA, int port) throws IOException {
		if (host != null) {
			sock = new Socket(host, port);
			this.in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			this.out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			this.client = clientA;
		}
	}

	/**
	 * Reads the messages in the socket connection.
	 */
	public void run() {
		try {
			String text = in.readLine();
			while (text != null) {
				String[] msg = text.split(msgSeperator);
				if (msg[0].equals(sendBoard) && msg.length == Board.LENGTH * Board.HEIGHT + 1) {
					handleSendBoard(text);
				} else if (msg[0].equals(acceptRequest) && msg.length == 2) {
					handleAcceptRequest(msg);
				} else if (msg[0].equals(startGame) && msg.length == 3) {
					handleStartGame(msg);
				} else if (msg[0].equals(moveResult) && msg.length == 5) {
					handleMoveResult(msg);
				} else if (msg[0].equals(turn) && msg.length == 2) {
					handleTurn(msg);
				} else if (msg[0].equals(endGame) && msg.length == 3) {
					handleEndGame(msg);
				} else if (msg[0].equals(invalidCommand)) {
					handleInvalidCommand(text);
				} else if (msg[0].equals(rematchConfirm) && msg.length == 1) {
					handleRematch();
				} else {
					client.getTUI().print(invalidCommand + " from server");
				}
				text = in.readLine();
			}
		} catch (IOException e) {
			String text = endGame + msgSeperator + client.getClientName() + msgSeperator
							+ connectionlost;
			String[] msg = text.split(msgSeperator);
			handleEndGame(msg);
		}
	}
	
	/**
	 * prints that the rematch starts.
	 */
	private void handleRematch() {
		client.getTUI().print("Rematch starts!");
	}
	
	/**
	 * Checks if the given board is different than the board of the client, 
	 * if so, the given board becomes the new board.
	 * @param text - String with the board
	 */
	private void handleSendBoard(String text) {
		String boardString = sendBoard + client.getGame().getBoard().getBoardString();
		if (!boardString.equals(text)) {
			String[] msg = text.split(msgSeperator);
			String[] values = new String[Board.LENGTH * Board.HEIGHT];
			System.arraycopy(msg, 1, values, 0, Board.LENGTH * Board.HEIGHT);
			Board board = makeBoard(values);
			client.getGame().replaceBoard(board);
		}
	}
	
	/**
	 * Sets the mark for the client and notifies the TUI that something is changed.
	 * @param msg - String[] with the mark for the client
	 */
	private void handleAcceptRequest(String[] msg) {
		synchronized (client.getTUI()) {
			client.setAccepted(true);
			client.setMark(Mark.stringToMark(msg[1]));
			client.getTUI().print("Request is accepted, wait on the next player...");
			client.getTUI().notifyAll();
		}
	}
	
	/**
	 * Starts a new game between the players in the msg.
	 * @param msg - String[] with game information
	 */
	private void handleStartGame(String[] msg) {
		String fst = msg[1];
		String snd = msg[2];
		client.getTUI().print("Start the game between " + fst + " and " + snd);
		client.startGame(fst, snd);
		client.setTurn(msg[1]);
	}
	
	/**
	 * If there is done a move, the client asks for the current board.
	 * @param msg - the result of the move
	 */
	private void handleMoveResult(String[] msg) {
		if (msg[2].equals(client.getClientName())) {
			if (msg[3].equals("false")) {
				client.getTUI().print("Wrong move");
			}
		}
		sendMessage(getBoard);
		client.setTurn(msg[4]);
	}
	
	/**
	 * Prints who's turn it is.
	 * @param msg - String[] with who's turn it is
	 */
	private void handleTurn(String[] msg) {
		client.setTurn(msg[1]);
		client.getTUI().print("The next turn is by " + msg[1]);
	}
	
	/**
	 * Prints an appropriate message to say that the game ended. If there is 
	 * need to, System.exit(0) is called.
	 * @param msg - String[] with information about the end of the game
	 */
	private void handleEndGame(String[] msg) {
		if (msg[2].equals(draw)) {
			client.getTUI().print("There is a " + draw);
		} else if (msg[2].equals(winner)) {
			client.getTUI().print("The winner of this game is " + msg[1] + ".");
		} else if (msg[2].equals(connectionlost)) {
			client.getTUI().print("The winner of this game is " + msg[1]
								+ " because there is no connection.");
			System.exit(0);
		} else {
			client.getTUI().print("The winner of this game is " + msg[1] 
										+ " because of an unknown error.");
			System.exit(0);
		}
		client.getTUI().startOverNetwork();
	}
	
	/**
	 * Prints an appropriate message to say that there was an invalidCommand.
	 * @param text - invalidCommand
	 */
	private void handleInvalidCommand(String text) {
		synchronized (client.getTUI()) {
			String[] msg = text.split(msgSeperator);
			if (msg.length == 3 && msg[2].equals(invalidUserTurn)) {
				client.getTUI().print("It is not your turn.");
			} else if (msg.length == 3 && msg[1].equals(invalidUsername)
							&& msg[2].equals(client.getClientName())) {
				client.getTUI().print("No valid username.");
			} else if (msg.length == 2 && msg[1].equals(usernameInUse)) {
				client.getTUI().print("This username is already in use.");

			} else {
				client.getTUI().print(text);
			}
			client.getTUI().notifyAll();
		}
	}
	
	/**
	 * Makes a board from the values given.
	 * @param values - values for on the board
	 * @return the board maked by the values
	 */
	private Board makeBoard(String[] values) {
		Board board = new Board();
		for (int i = Board.LENGTH * Board.HEIGHT - 1; i >= 0; i--) {
			Mark mark = Mark.stringToMark(values[i]);
			board.setColumn(i % Board.LENGTH, mark);
		}
		return board;
	}

	/**
	 * send a message to a ClientHandler.
	 * @param msg - the message that has to be send to the clientHandler.
	 */
	public void sendMessage(String msg) {
		String message = msg;
		if (!msg.equals(client.getExit())) {
			try {
				if (!client.getAccepted()) {
					client.setClientName(msg);
					message = joinRequest + msgSeperator + msg;

				}
				out.write(message);
				out.newLine();
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			shutdown();
		}
	}

	/**
	 * close the socket connection.
	 */
	public void shutdown() {
		try {
			sock.shutdownInput();
			sock.shutdownOutput();
			sock.close();
		} catch (IOException e) {
			System.out.println(invalidCommand);
		}
	}
}
