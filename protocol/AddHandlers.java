package protocol;

import java.util.Observable;
import java.util.Observer;

import fouronarow.Board;
import fouronarow.Mark;

public class AddHandlers extends Thread implements Observer, ProtocolControl, ProtocolConstants {
	private Server server;

	/**
	 * Creates a AddHandlers instance.
	 * Replaces value of the local variable server for the value of
	 * the parameter serverA
	 * 
	 * @param serverA
	 */
	public AddHandlers(Server serverA) {
		this.server = serverA;
	}

	/**
	 * Executes the method server.addHandlers and handles 
	 * the rematch request.
	 * 
	 */
	@Override
	public void update(Observable o, Object arg1) {
		server.addHandlers();
		
		int i = 0;
		while (i >= 0 && i < server.rematch.size()) {
			ClientHandler handler = server.rematch.get(i);
			ClientHandler other = server.otherClient(handler);
			if (!handler.getPlaying() && !other.getPlaying()) {
				for (int j = i; j < server.rematch.size(); j++) {
					if (server.rematch.get(j).equals(other)) {
						server.broadcast(rematchConfirm, handler);
						Board board = new Board();
						server.addToClientHandlers(handler, board);
						handler.sendMessage(acceptRequest + msgSeperator
											+ (Mark.STARTMARK).markString());
						server.addToClientHandlers(other, board);
						other.sendMessage(acceptRequest + msgSeperator
											+ (Mark.STARTMARK.other()).markString());
						server.putPair(handler, other);
						server.broadcast(startGame + msgSeperator + handler.getClientName()
											+ msgSeperator + other.getClientName(), handler);
						other.setPlaying(true);
						handler.setPlaying(true);
						server.rematch.remove(j);
						server.rematch.remove(i);
						i -= 2;
					}
				}
			} else {
				server.rematch.remove(i);
				i--;
			}
			i++;
		}
	}
}
