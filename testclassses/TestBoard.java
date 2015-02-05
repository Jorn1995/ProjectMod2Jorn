package testclassses;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import fouronarow.*;

public class TestBoard implements TestClass {

	private Board board;

	@Before
	public void setUp() {
		board = new Board();
	}

	@Test
	public void testGetColumn() {
		board.reset();
		assertEquals("Check if A/ column is returned", true, !(board.getColumn(0) == null));
		assertEquals("Check if A/ column is returned", true, board.getColumn(7) == null);
		assertEquals("Check if A/ column is returned", true, board.getColumn(-1) == null);
	}

	@Test
	public void testGetField() {
		board.reset();
		assertEquals("Check if the right field has been returned", true,
						Mark.EMPTY.equals(board.getField(5, 0)));
		assertEquals("Check if the right field has been returned", false,
						Mark.RED.equals(board.getField(5, 0)));

		board.getColumn(0).setColumn(Mark.YELLOW);
		board.getColumn(0).setColumn(Mark.RED);
		assertEquals("Check if the right field has been returned", true,
						Mark.YELLOW.equals(board.getField(5, 0)));
		assertEquals("Check if the right field has been returned", true,
						Mark.RED.equals(board.getField(4, 0)));
		assertEquals("Check if the right field has been returned", false,
						Mark.EMPTY.equals(board.getField(5, 7)));
		assertEquals("Check if the right field has been returned", false,
						Mark.RED.equals(board.getField(10, 0)));

	}

	@Test
	public void testGetLength() {
		board.reset();
		assertEquals("Check if the right field has been returned", 7, board.getLength());
	}

	@Test
	public void testGetHeight() {
		board.reset();
		assertEquals("Check if the right field has been returned", 6, board.getHeight());
	}

	@Test
	public void testIsColumn() {
		board.reset();
		assertEquals("Check if the column exists", true, board.isColumn(4));
		assertEquals("Check if the column exists", false, board.isColumn(7));
		assertEquals("Check if the column exists", false, board.isColumn(-1));
	}

	@Test
	public void testIsFull() {
		board.reset();
		assertEquals("Check if the board isFull", false, board.isFull());
		for (int j = 0; j < board.getLength(); j++) {
			if (j == 0 || j == 1 || j == 5 || j == 6) {
				board.getColumn(j).setColumn(Mark.YELLOW);
				board.getColumn(j).setColumn(Mark.RED);
				board.getColumn(j).setColumn(Mark.YELLOW);
				board.getColumn(j).setColumn(Mark.RED);
				board.getColumn(j).setColumn(Mark.YELLOW);
				board.getColumn(j).setColumn(Mark.RED);
			}
			if (j == 2 || j == 3 || j == 4) {
				board.getColumn(j).setColumn(Mark.RED);
				board.getColumn(j).setColumn(Mark.YELLOW);
				board.getColumn(j).setColumn(Mark.RED);
				board.getColumn(j).setColumn(Mark.YELLOW);
				board.getColumn(j).setColumn(Mark.RED);
				board.getColumn(j).setColumn(Mark.YELLOW);
			}
		}
		assertEquals("Check if the board isFull", true, board.isFull());
	}

	@Test
	public void testGameOver() {
		board.reset();
		assertEquals("Check if the boardgame is over", false, board.gameOver());
		for (int j = 0; j < board.getLength(); j++) {
			if (j == 0 || j == 1 || j == 5 || j == 6) {
				board.getColumn(j).setColumn(Mark.YELLOW);
				board.getColumn(j).setColumn(Mark.RED);
				board.getColumn(j).setColumn(Mark.YELLOW);
				board.getColumn(j).setColumn(Mark.RED);
				board.getColumn(j).setColumn(Mark.YELLOW);
				board.getColumn(j).setColumn(Mark.RED);
			}
			if (j == 2 || j == 3 || j == 4) {
				board.getColumn(j).setColumn(Mark.RED);
				board.getColumn(j).setColumn(Mark.YELLOW);
				board.getColumn(j).setColumn(Mark.RED);
				board.getColumn(j).setColumn(Mark.YELLOW);
				board.getColumn(j).setColumn(Mark.RED);
				board.getColumn(j).setColumn(Mark.YELLOW);
			}
		}
		assertEquals("Check if the boardgame is over", true, board.gameOver());
		board.reset();
		for (int j = 0; j < 3; j++) {
			board.getColumn(j).setColumn(Mark.YELLOW);
			board.getColumn(j).setColumn(Mark.RED);
		}
		board.getColumn(3).setColumn(Mark.YELLOW);
		assertEquals("Check if the boardgame is over", true, board.gameOver());
	}

	@Test
	public void testDeepCopy() {
		board.reset();
		Board copy = board.deepCopy();
		assertEquals("Check if the copy is a proper Copy", true, copy.equals(board));

		board.setColumn(2, Mark.YELLOW);
		assertEquals("Check if the copy is a proper Copy", false, board.equals(copy));
	}

	@Test
	public void testCountMark() {
		board.reset();
		assertEquals("Check if the CountMark gives back the good count", 0,
						board.countMark(Mark.RED));
		assertEquals("Check if the CountMark gives back the good count", 0,
						board.countMark(Mark.YELLOW));
	
		board.getColumn(0).setColumn(Mark.YELLOW);
		board.getColumn(0).setColumn(Mark.RED);
		board.getColumn(0).setColumn(Mark.YELLOW);
		assertEquals("Check if the CountMark gives back the good count", 1,
						board.countMark(Mark.RED));
		assertEquals("Check if the CountMark gives back the good count", 2,
						board.countMark(Mark.YELLOW));
	}

	@Test
	public void testGetBoardString() {
		board.reset();
		assertEquals("Check if there is a BoardString", true, board.getBoardString() != null);
	}

	@Test
	public void testHasColumn() {
		board.reset();
		assertEquals("Check if there is a winning column", false, board.hasColumn(Mark.RED));
		assertEquals("Check if there is a winning column", false, board.hasColumn(Mark.YELLOW));

		board.getColumn(0).setColumn(Mark.YELLOW);
		board.getColumn(1).setColumn(Mark.RED);
		board.getColumn(0).setColumn(Mark.YELLOW);
		board.getColumn(1).setColumn(Mark.RED);
		board.getColumn(0).setColumn(Mark.YELLOW);
		board.getColumn(1).setColumn(Mark.RED);
		board.getColumn(0).setColumn(Mark.YELLOW);
		assertEquals("Check if there is a winning column", false, board.hasColumn(Mark.RED));
		assertEquals("Check if there is a winning column", true, board.hasColumn(Mark.YELLOW));

	}

	@Test
	public void testHasDiagonal() {
		board.reset();
		assertEquals("Check if there is a winning diagonal", false, board.hasDiagonal(Mark.RED));
		assertEquals("Check if there is a winning diagonal", false, board.hasDiagonal(Mark.YELLOW));

		board.getColumn(0).setColumn(Mark.YELLOW);
		board.getColumn(1).setColumn(Mark.RED);
		board.getColumn(1).setColumn(Mark.YELLOW);
		board.getColumn(2).setColumn(Mark.RED);
		board.getColumn(3).setColumn(Mark.YELLOW);
		board.getColumn(2).setColumn(Mark.RED);
		board.getColumn(2).setColumn(Mark.YELLOW);
		board.getColumn(3).setColumn(Mark.RED);
		board.getColumn(3).setColumn(Mark.YELLOW);
		board.getColumn(4).setColumn(Mark.RED);
		board.getColumn(3).setColumn(Mark.YELLOW);
		assertEquals("Check if there is a winning diagonal", false, board.hasDiagonal(Mark.RED));
		assertEquals("Check if there is a winning diagonal", true, board.hasDiagonal(Mark.YELLOW));

		board.getColumn(4).setColumn(Mark.RED);
		board.getColumn(4).setColumn(Mark.YELLOW);
		board.getColumn(4).setColumn(Mark.RED);
		board.getColumn(4).setColumn(Mark.YELLOW);
		assertEquals("Check if there is a winning diagonal", false, board.hasDiagonal(Mark.RED));
		assertEquals("Check if there is a winning diagonal", true, board.hasDiagonal(Mark.YELLOW));

		board.reset();
		board.getColumn(6).setColumn(Mark.YELLOW);
		board.getColumn(5).setColumn(Mark.RED);
		board.getColumn(5).setColumn(Mark.YELLOW);
		board.getColumn(4).setColumn(Mark.RED);
		board.getColumn(3).setColumn(Mark.YELLOW);
		board.getColumn(4).setColumn(Mark.RED);
		board.getColumn(4).setColumn(Mark.YELLOW);
		board.getColumn(3).setColumn(Mark.RED);
		board.getColumn(3).setColumn(Mark.YELLOW);
		board.getColumn(2).setColumn(Mark.RED);
		board.getColumn(3).setColumn(Mark.YELLOW);
		assertEquals("Check if there is a winning diagonal", false, board.hasDiagonal(Mark.RED));
		assertEquals("Check if there is a winning diagonal", true, board.hasDiagonal(Mark.YELLOW));

		board.getColumn(2).setColumn(Mark.RED);
		board.getColumn(2).setColumn(Mark.YELLOW);
		board.getColumn(2).setColumn(Mark.RED);
		board.getColumn(2).setColumn(Mark.YELLOW);
		assertEquals("Check if there is a winning diagonal", false, board.hasDiagonal(Mark.RED));
		assertEquals("Check if there is a winning diagonal", true, board.hasDiagonal(Mark.YELLOW));

	}

	@Test
	public void testHasRow() {
		board.reset();
		assertEquals("Check if there is a winning row", false, board.hasRow(Mark.RED));
		assertEquals("Check if there is a winning row", false, board.hasRow(Mark.YELLOW));

		board.getColumn(0).setColumn(Mark.YELLOW);
		board.getColumn(0).setColumn(Mark.RED);
		board.getColumn(1).setColumn(Mark.YELLOW);
		board.getColumn(1).setColumn(Mark.RED);
		board.getColumn(2).setColumn(Mark.YELLOW);
		board.getColumn(2).setColumn(Mark.RED);
		board.getColumn(3).setColumn(Mark.YELLOW);
		assertEquals("Check if there is a winning row", false, board.hasRow(Mark.RED));
		assertEquals("Check if there is a winning row", true, board.hasRow(Mark.YELLOW));

	}

	@Test
	public void testHasWinner() {
		board.reset();
		assertEquals("Check if the board has a Winner", false, board.hasWinner());

		board.getColumn(0).setColumn(Mark.YELLOW);
		board.getColumn(0).setColumn(Mark.RED);
		board.getColumn(1).setColumn(Mark.YELLOW);
		board.getColumn(1).setColumn(Mark.RED);
		board.getColumn(2).setColumn(Mark.YELLOW);
		board.getColumn(2).setColumn(Mark.RED);
		board.getColumn(3).setColumn(Mark.YELLOW);
		assertEquals("Check if the board has a Winner", true, board.hasWinner());

		board.reset();
		board.getColumn(0).setColumn(Mark.YELLOW);
		board.getColumn(0).setColumn(Mark.RED);
		board.getColumn(1).setColumn(Mark.YELLOW);
		board.getColumn(1).setColumn(Mark.RED);
		board.getColumn(2).setColumn(Mark.YELLOW);
		board.getColumn(2).setColumn(Mark.RED);
		board.getColumn(6).setColumn(Mark.YELLOW);
		board.getColumn(3).setColumn(Mark.RED);
		board.getColumn(6).setColumn(Mark.YELLOW);
		board.getColumn(3).setColumn(Mark.RED);
		assertEquals("Check if the board has a Winner", true, board.hasWinner());
	}

	@Test
	public void testToString() {
		board.reset();
		assertEquals("Check if there is a String", true, board.toString() != null);
	}

	@Test
	public void testIsLegalMove() {
		board.reset();
		assertEquals("Check if a move is legal", true, board.isLegalMove(0));
		assertEquals("Check if a move is legal", true, board.isLegalMove(41));
		assertEquals("Check if a move is legal", false, board.isLegalMove(42));

		board.getColumn(0).setColumn(Mark.YELLOW);

		assertEquals("Check if a move is legal", true, board.isLegalMove(0));
		assertEquals("Check if a move is legal", false, board.isLegalMove(-1));
		assertEquals("Check if a move is legal", false, board.isLegalMove(42));

	}
}
