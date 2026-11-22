package battleship;

import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the IMove interface.
 * Cyclomatic Complexity for each method:
 * - readMove: 2
 */
@DisplayName("Testes da interface IMove")
class IMoveTest {

	/**
	 * Test for the readMove method with several shots.
	 */
	@Test
	@DisplayName("readMove lê o número de tiros e as coordenadas de cada tiro")
	void testReadMoveSeveralShots() {
		Move move = IMove.readMove(4, new Scanner("3 0 0 1 2 9 9"));

		assertEquals(4, move.getNumber(), "Error: The number of the move is incorrect.");
		List<IPosition> shots = move.getShots();
		assertEquals(3, shots.size(), "Error: The move should have 3 shots.");
		assertEquals(new Position(0, 0), shots.get(0), "Error: The first shot is incorrect.");
		assertEquals(new Position(1, 2), shots.get(1), "Error: The second shot is incorrect.");
		assertEquals(new Position(9, 9), shots.get(2), "Error: The third shot is incorrect.");
		assertTrue(move.getShotResults().isEmpty(), "Error: A move that was just read should have no results.");
	}

	/**
	 * Test for the readMove method without shots.
	 */
	@Test
	@DisplayName("readMove cria uma jogada vazia quando o número de tiros é zero")
	void testReadMoveNoShots() {
		Move move = IMove.readMove(1, new Scanner("0"));

		assertEquals(1, move.getNumber(), "Error: The number of the move is incorrect.");
		assertTrue(move.getShots().isEmpty(), "Error: The move should have no shots.");
	}
}
