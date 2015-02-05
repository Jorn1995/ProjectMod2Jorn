package testclassses;


import org.junit.Before;
import org.junit.Test;

import protocol.Client;
import fouronarow.*;

public class TestGameLocally implements TestClass {

	private TUI view;

	/*
	 * Follow these instructions We want to test our game as best as we can, so
	 * we are going to do that by playing to games. To play these games we never
	 * leave our RUN. First we want to play a human vs. Computer game. After
	 * this we're going to start another game This time we will play it with 2
	 * ComputerPlayers. If the ComputerPlayers are Smart or Naive is up to the
	 * one who writes the messages to the TUI
	 * 
	 * Run the class Write "START" Select Local, in other words write "n" Write
	 * your name Write "SMART" or "NAIVE" Play the game At the end write "a", to
	 * start another game Write "SMART" or "NAIVE" Again Write "SMART" or
	 * "NAIVE" At the end write "n"
	 */
	@Before
	public void setUp() {
		Client client = new Client();
		view = new TUI(client);
		
	}

	@Test
	public void test() {
		new Game(new HumanPlayer("jorn", Mark.STARTMARK), new HumanPlayer("jorn",
				Mark.STARTMARK), view);
	}

}
