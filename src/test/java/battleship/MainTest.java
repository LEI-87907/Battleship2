package battleship;

import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Main class.
 * Cyclomatic Complexity for each method:
 * - main: 1
 */
@DisplayName("Testes da classe Main")
class MainTest {

	/**
	 * Test for the main method when the player gives up straight away.
	 */
	@Test
	@DisplayName("main mostra o título, abre o menu e termina com o comando desisto")
	void testMainEndsWithDesisto() {
		InputStream originalIn = System.in;
		PrintStream originalOut = System.out;
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		System.setIn(new ByteArrayInputStream("desisto\n".getBytes()));
		System.setOut(new PrintStream(output));
		try {
			Main.main(new String[0]);
		} finally {
			System.setIn(originalIn);
			System.setOut(originalOut);
		}

		String text = output.toString();
		assertTrue(text.contains("***  Battleship  ***"), "Error: The title of the game should be printed.");
		assertTrue(text.contains("AJUDA DO MENU"), "Error: The menu help should be printed.");
		assertTrue(text.contains("Bons ventos!"), "Error: The goodbye message should be printed.");
	}
}
