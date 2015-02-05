package testclassses;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import fouronarow.*;

public class TestColumn implements TestClass {

	private Board board;
	private Column column;
	private String yellow = Mark.YELLOW.toString();
	private String empty = Mark.EMPTY.toString();

	@Before
	public void setUp() {
		board = new Board();
		column = board.getColumn(0);
	}

	@Test
	public void testGetEmptyField() {
		column.reset();
		assertEquals("Check if/which EmptyField is left", 5, column.getEmptyField());

		column.setColumn(Mark.YELLOW);
		assertEquals("Check if/which EmptyField is left", 4, column.getEmptyField());

		column.setColumn(Mark.RED);
		column.setColumn(Mark.YELLOW);
		column.setColumn(Mark.RED);
		column.setColumn(Mark.YELLOW);
		column.setColumn(Mark.RED);

		// Returns 6, so there is no EmptyField left, a column only has 6
		// fields(0...5);
		assertEquals("Check if/which EmptyField is left", 6, column.getEmptyField());
	}

	@Test
	public void testIsEmptyField() {
		column.reset();
		assertEquals("Check if the field is a Empty", true, column.isEmptyField(5));

		column.setColumn(Mark.YELLOW);
		assertEquals("Check if the field is a Empty", false, column.isEmptyField(5));
	}

	@Test
	public void testGetField() {
		column.reset();
		assertEquals("Check if the fields are given backn properly", true,
						Mark.EMPTY.equals(column.getField(5)));

		column.setColumn(Mark.YELLOW);
		assertEquals("Check if the fields are given backn properly", false,
						Mark.EMPTY.equals(column.getField(5)));
		assertEquals("Check if the fields are given backn properly", false,
						Mark.EMPTY.equals(column.getField(-1)));
		assertEquals("Check if the fields are given backn properly", false,
						Mark.EMPTY.equals(column.getField(7)));
	}

	@Test
	public void testToString() {
		column.reset();
		assertEquals("Check if the String version of a column is proper", 
						empty, column.toString(5));

		column.setColumn(Mark.YELLOW);
		assertEquals("Check if the String version of a column is proper", yellow,
						column.toString(5));
	}

	@Test
	public void testDeepCopy() {
		column.reset();
		Column copy = column.deepCopy();
		assertEquals("Check if the DeepCopy is a proper Copy", true, column.equals(copy));

		column.setColumn(Mark.YELLOW);
		assertEquals("Check if the DeepCopy is a proper Copy", false, column.equals(copy));
	}

	@Test
	public void testIsFull() {
		column.reset();
		assertEquals("Check if the column isFull", false, column.isFull());

		column.setColumn(Mark.YELLOW);
		column.setColumn(Mark.RED);
		column.setColumn(Mark.YELLOW);
		column.setColumn(Mark.RED);
		column.setColumn(Mark.YELLOW);
		column.setColumn(Mark.RED);
		assertEquals("Check if the column isFull", true, column.isFull());
	}

	@Test
	public void testHasColumn() {
		column.reset();
		assertEquals("Checks if a Row by one Mark is Detected", false, column.hasColumn(Mark.RED));
		assertEquals("Checks if a Row by one Mark is Detected", false,
						column.hasColumn(Mark.YELLOW));

		column.setColumn(Mark.YELLOW);
		column.setColumn(Mark.YELLOW);
		column.setColumn(Mark.YELLOW);
		column.setColumn(Mark.YELLOW);
		assertEquals("Checks if a Row by one Mark is Detected", false, column.hasColumn(Mark.RED));
		assertEquals("Checks if a Row by one Mark is Detected", true, 
						column.hasColumn(Mark.YELLOW));
	}

	@Test
	public void testCountMark() {
		column.reset();
		assertEquals("Checks the count how often a Mark is in a column", 0,
						column.countMark(Mark.RED));
		assertEquals("Checks the count how often a Mark is in a column", 0,
						column.countMark(Mark.YELLOW));

		column.setColumn(Mark.YELLOW);
		column.setColumn(Mark.RED);
		column.setColumn(Mark.YELLOW);
		column.setColumn(Mark.RED);
		column.setColumn(Mark.YELLOW);
		assertEquals("Checks the count how often a Mark is in a column", 2,
						column.countMark(Mark.RED));
		assertEquals("Checks the count how often a Mark is in a column", 3,
						column.countMark(Mark.YELLOW));
	}

	@Test
	public void testSetColumn() {
		column.reset();
		assertEquals("Checks if SetColumn works Succesful", true, column.setColumn(Mark.YELLOW));
		assertEquals("Checks if SetColumn works Succesful", true, column.setColumn(Mark.RED));

		column.setColumn(Mark.YELLOW);
		column.setColumn(Mark.RED);
		column.setColumn(Mark.YELLOW);
		column.setColumn(Mark.RED);
		assertEquals("Checks if SetColumn works Succesful", false, column.setColumn(Mark.YELLOW));
		assertEquals("Checks if SetColumn works Succesful", false, column.setColumn(Mark.RED));
	}

}