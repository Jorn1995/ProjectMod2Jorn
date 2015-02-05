package testclassses;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import fouronarow.*;

public class TestMark implements TestClass {

	private Mark yellow;
	private Mark red;
	private Mark empty;

	@Before
	public void setUp() {
		yellow = Mark.YELLOW;
		red = Mark.RED;
		empty = Mark.EMPTY;
	}

	@Test
	public void testOther() {
		assertEquals("Check if Other gives back the right Mark", true,
						yellow.other().equals(Mark.RED));
		assertEquals("Check if Other gives back the right Mark", true,
						red.other().equals(Mark.YELLOW));
		assertEquals("Check if Other gives back the right Mark", true,
						empty.other().equals(Mark.EMPTY));
	}

	@Test
	public void testToString() {
		assertEquals("Check if the String that has been given back is proper", "X",
						yellow.toString());
		assertEquals("Check if the String that has been given back is proper", "O", red.toString());
		assertEquals("Check if the String that has been given back is proper", " ",
						empty.toString());
	}

	@Test
	public void markString() {
		assertEquals("Check if the String that has been given back is proper", "Mark.YELLOW",
						yellow.markString());
		assertEquals("Check if the String that has been given back is proper", "Mark.RED",
						red.markString());
		assertEquals("Check if the String that has been given back is proper", "Mark.EMPTY",
						empty.markString());
	}

	@Test
	public void stringToMark() {
		assertEquals("Check if the String that has been given back is proper", yellow,
						Mark.stringToMark("Mark.YELLOW"));
		assertEquals("Check if the String that has been given back is proper", red,
						Mark.stringToMark("Mark.RED"));
		assertEquals("Check if the String that has been given back is proper", empty,
						Mark.stringToMark("Mark.EMPTY"));
	}
}
