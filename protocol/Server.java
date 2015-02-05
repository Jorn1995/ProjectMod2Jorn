package protocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

import fouronarow.Board;
import fouronarow.Mark;

public class Server implements protocol.ProtocolControl, protocol.ProtocolConstants {
	private static final int NUMBER_PLAYERS = 2;

	private Map<ClientHandler, Board> clientHandlers;
	private ClientHandler last;
	public List<ClientHandler> acceptedHandlers;
	private AddHandlers addHandlers;
	private ServerUI tui;
	private ServerSocket serverSock;
	private Map<ClientHandler, ClientHandler> pair;
	public List<ClientHandler> rematch;
	private int port;
	private Scanner scanner;

	/** Start een Server-applicatie op. */
	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

	/**
	 * Constructs a new Server object.
	 */
	public Server() {
		this.tui = new ServerUI();
		clientHandlers = new HashMap<ClientHandler, Board>();
		pair = new HashMap<ClientHandler, ClientHandler>();
		acceptedHandlers = new LinkedList<ClientHandler>();
		addHandlers = new AddHandlers(this);
		last = null;
		rematch = new LinkedList<ClientHandler>();
	}

	/**
	 * Listens to a port of this Server if there are any Clients that would like
	 * to connect. For every new socket connection a new ClientHandler thread is
	 * started that takes care of the further communication with the Client.
	 */
	public void run() {
		scanner = new Scanner(System.in);
		System.out.println("On which host do you want the server to listen to? (1337)");
		if (scanner.hasNextInt()) {
			port = scanner.nextInt();
		}
		scanner.close();
		addHandlers.start();
		try {
			serverSock = new ServerSocket(port);
			tui.print("Server created!");
			while (true) {
				Socket sock;
				try {
					sock = serverSock.accept();
					ClientHandler clientHandler = new ClientHandler(this, sock);
					(new Thread(clientHandler)).start();
				} catch (IOException e) {
					System.out.println(invalidCommand);
				}
			}

		} catch (IOException e) {
			tui.print("server-ERROR: could not create a socket on port " + port);
		}
	}

	/**
	 * Checks if there is already a clienHandler with this name.
	 * 
	 * @param name
	 *            - the name you have to check
	 * @return true - name is in use, false - name isn't in use
	 */
	public boolean nameIsUsed(String name) {
		boolean result = false;
		for (ClientHandler clientHandler : clientHandlers.keySet()) {
			if (clientHandler.getClientName().equals(name)) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * Gives boards to the acceptedHandlers.
	 */
	public void addHandlers() {
		for (int i = acceptedHandlers.size() - 1; i >= 0; i--) {
			addHandlerBoardCombination(acceptedHandlers.get(i));
			acceptedHandlers.remove(i);
		}
	}

	/**
	 * Gives a board to a ClientHandler. If there is an even amount of
	 * ClientHandlers the handler gets the same board as the previous
	 * ClientHandler.
	 * 
	 * @param handler
	 *            - the ClientHandler who wants to play a game
	 * @return true - handler is accepted, false - handler isn't accepted
	 */
	private boolean addHandlerBoardCombination(ClientHandler handler) {
		if (handler.isAccepted()) {
			int l = clientHandlers.size();
			if ((l % NUMBER_PLAYERS) == 0) {
				clientHandlers.put(handler, new Board());
				handler.sendMessage(acceptRequest + msgSeperator + (Mark.STARTMARK).markString());
			} else {
				clientHandlers.put(handler, clientHandlers.get(last));
				handler.sendMessage(acceptRequest + msgSeperator
									+ (Mark.STARTMARK.other()).markString());
				pair.put(last, handler);
				broadcast(
								startGame + msgSeperator + last.getClientName() + msgSeperator
								+ handler.getClientName(), last);
				last.setPlaying(true);
				handler.setPlaying(true);
			}
			last = handler;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Remove a ClientHandler from the collection of ClientHanlders.
	 * 
	 * @param handler
	 *            ClientHandler that will be removed
	 */
	public void removeHandler(ClientHandler handler) {
		if (clientHandlers.containsKey(handler)) {
			clientHandlers.remove(handler);
		}
	}

	/**
	 * Sends a message to the given clientHandler and his opponent.
	 * 
	 * @param msg
	 *            - message that is send
	 */
	public void broadcast(String msg, ClientHandler clientHandler) {
		String[] msgs = msg.split(msgSeperator);
		if (msgs[0].equals(endGame)) {
			clientHandler.setPlaying(false);
		}
		ClientHandler other = otherClient(clientHandler);
		if (other != null) {
			if (msgs[0].equals(endGame)) {
				other.setPlaying(false);
			}
			other.sendMessage(msg);
			clientHandler.sendMessage(msg);
		}
	}

	/**
	 * Gives the addHandlers.
	 * 
	 * @return addHandlers
	 */
	public AddHandlers getAddHandlers() {
		return addHandlers;
	}

	/**
	 * Gives the opponent of the handler.
	 * 
	 * @param handler
	 *            - the handler from which you want to know the opponent
	 * @return null - there is no opponent, else the opponent of handler
	 */
	public ClientHandler otherClient(ClientHandler handler) {
		ClientHandler other = null;
		if (pair.containsKey(handler)) {
			other = pair.get(handler);
		} else if (pair.containsValue(handler)) {
			for (Map.Entry<ClientHandler, ClientHandler> hand : pair.entrySet()) {
				if (hand.getValue().equals(handler)) {
					other = hand.getKey();
				}
			}
		}
		return other;
	}

	/**
	 * Gives the board of the clientHandler.
	 * 
	 * @param clientHandler
	 *            - the clientHandler of which you want the board
	 * @return the board of the clientHandler
	 */
	public Board getBoard(ClientHandler clientHandler) {
		Board board = null;
		if (clientHandlers.containsKey(clientHandler)) {
			board = clientHandlers.get(clientHandler);
		}
		return board;
	}

	/**
	 * Gives the mark of the clientHandler who's turn it is in the game of.
	 * handler
	 * 
	 * @param handler
	 *            - the ClientHandler who wants to know who's turn it is
	 * @return the mark of who's turn it is.
	 */
	public Mark getTurn(ClientHandler handler) {
		Board board = getBoard(handler);
		int startMark = board.countMark(Mark.STARTMARK);
		int otherMark = board.countMark(Mark.STARTMARK.other());
		Mark mark = Mark.STARTMARK;
		if (startMark > otherMark) {
			mark = Mark.STARTMARK.other();
		}
		return mark;
	}

	/**
	 * Gives the tui of the server.
	 * 
	 * @return tui
	 */
	public ServerUI getUI() {
		return tui;
	}

	/**
	 * Add the given handler with the given board to clientHandlers.
	 * 
	 * @param handler
	 *            - the ClientHandler you want to put in clientHandlers
	 * @param board
	 *            - the board for the handler you want to put in clientHandlers
	 */
	public void addToClientHandlers(ClientHandler handler, Board board) {
		clientHandlers.put(handler, board);
	}

	/**
	 * Put a pair of clientHandlers in the list of pairs.
	 * 
	 * @param handler1
	 *            - the first clientHandler
	 * @param handler2
	 *            - the secont clientHandler
	 */
	public void putPair(ClientHandler handler1, ClientHandler handler2) {
		pair.put(handler1, handler2);
	}
}