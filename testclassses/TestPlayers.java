package testclassses;

import static org.junit.Assert.*;

import org.junit.*;

import fouronarow.*;

public class TestPlayers implements TestClass {

	private HumanPlayer human;
	private ComputerPlayer sComputer;
	private ComputerPlayer nComputer;
	private Board board;
	private SmartStrategy smart;

	@Before
	public void setUp() {
		board = new Board();
		human = new HumanPlayer("Jorn", Mark.STARTMARK);
		smart = new SmartStrategy();
		sComputer = new ComputerPlayer(Mark.STARTMARK, smart);
		nComputer = new ComputerPlayer(Mark.STARTMARK);
	}

	@Test
	public void testGetName() {
		assertEquals("Check if the name is given back properly", "Jorn", human.getName());
		assertEquals("Check if the name is given back properly", "Smart-X", sComputer.getName());
		assertEquals("Check if the name is given back properly", "Naive-X", nComputer.getName());
	}

	@Test
	public void testGetMark() {
		assertEquals("Check if the mark is given back properly", true,
						human.getMark().equals(Mark.STARTMARK));
		assertEquals("Check if the mark is given back properly", true,
						sComputer.getMark().equals(Mark.STARTMARK));
		assertEquals("Check if the mark is given back properly", true,
						nComputer.getMark().equals(Mark.STARTMARK));
	}

	@Test
	public void testDetermineMove() {
		assertEquals("Check if the move is given back properly", 0, human.determineMove(board));
		assertEquals("Check if the move is given back properly", 3,
						((Player) sComputer).determineMove(board));
		assertEquals("Check if the move is given back properly", true, nComputer != null);

		// Smart
		board.reset();
		board.setColumn(2, Mark.STARTMARK.other());
		board.setColumn(2, Mark.STARTMARK.other());
		board.setColumn(2, Mark.STARTMARK.other());
		assertEquals("Check if the move is given back properly", 2,
						smart.determineMove(board, Mark.STARTMARK));

		board.setColumn(smart.determineMove(board, Mark.STARTMARK), Mark.STARTMARK);
		board.setColumn(smart.determineMove(board, Mark.STARTMARK), Mark.STARTMARK);
		board.setColumn(smart.determineMove(board, Mark.STARTMARK), Mark.STARTMARK);
		assertEquals("Check if the move is given back properly", 3,
						smart.determineMove(board, Mark.STARTMARK));

		board.setColumn(smart.determineMove(board, Mark.STARTMARK), Mark.STARTMARK);
		assertEquals("Check if the move is given back properly", 3,
						smart.determineMove(board, Mark.STARTMARK));

		board.reset();
		board.setColumn(3, Mark.STARTMARK.other());
		board.setColumn(smart.determineMove(board, Mark.STARTMARK), Mark.STARTMARK);
		board.setColumn(3, Mark.STARTMARK.other());
		board.setColumn(smart.determineMove(board, Mark.STARTMARK), Mark.STARTMARK);
		board.setColumn(3, Mark.STARTMARK.other());
		board.setColumn(smart.determineMove(board, Mark.STARTMARK), Mark.STARTMARK);
		assertEquals("Check if the move is given back properly", 2,
						smart.determineMove(board, Mark.STARTMARK));

		board.setColumn(2, Mark.STARTMARK.other());
		board.setColumn(smart.determineMove(board, Mark.STARTMARK), Mark.STARTMARK);
		board.setColumn(2, Mark.STARTMARK.other());
		board.setColumn(smart.determineMove(board, Mark.STARTMARK), Mark.STARTMARK);
		board.setColumn(2, Mark.STARTMARK.other());
		board.setColumn(smart.determineMove(board, Mark.STARTMARK), Mark.STARTMARK);
		assertEquals("Check if the move is given back properly", 4,
						smart.determineMove(board, Mark.STARTMARK));

		board.reset();
		for (int i = 0; i < Board.LENGTH; i++) {
			if (i == 0 || i == 1 || i == 6 || i == 5) {
				board.setColumn(i, Mark.STARTMARK.other());
				board.setColumn(i, Mark.STARTMARK);
				board.setColumn(i, Mark.STARTMARK.other());
				board.setColumn(i, Mark.STARTMARK);
				board.setColumn(i, Mark.STARTMARK.other());
				board.setColumn(i, Mark.STARTMARK);
			}
			if (i == 4 || i == 2 || i == 3) {
				board.setColumn(i, Mark.STARTMARK);
				board.setColumn(i, Mark.STARTMARK.other());
				board.setColumn(i, Mark.STARTMARK);
				board.setColumn(i, Mark.STARTMARK.other());
				board.setColumn(i, Mark.STARTMARK);
				board.setColumn(i, Mark.STARTMARK.other());
			}
			if (i == 4) {
				board.setColumn(i, Mark.EMPTY);
			}
		}
		assertEquals("Check if the move is given back properly", -1,
						smart.determineMove(board, Mark.STARTMARK));

	}

	@Test
	public void testMakeMove() {
		board.reset();
		sComputer.makeMove(board);
		human.makeMove(board, 2);
		nComputer.makeMove(board);
		human.makeMove(board, 7);

		assertEquals("Check if the move is a Succes ", true,
						board.getField(5, 3).equals(Mark.STARTMARK));
		assertEquals("Check if the move is a Succes ", true,
						board.getField(5, 2).equals(Mark.STARTMARK));
		assertEquals("Check if the move is a Succes ", 3, board.countMark(Mark.STARTMARK));
	}
}
