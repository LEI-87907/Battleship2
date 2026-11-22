package battleship;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Game.
 * Author: britoeabreu
 * Date: 2024-03-19
 * Time: 15:30
 * Cyclomatic Complexity for each method:
 * - Game (constructor): 1
 * - fire: 7
 * - getShots: 1
 * - getRepeatedShots: 1
 * - getInvalidShots: 1
 * - getHits: 1
 * - getSunkShips: 1
 * - getRemainingShips: 1
 * - validShot: 3
 * - repeatedShot: 2
 * - printBoard: 1
 * - printValidShots: 1
 * - printFleet: 1
 */
public class GameTest {

	private Game game;

	@BeforeEach
	void setUp() {
		game = new Game(new Fleet()); // Assuming Fleet is a concrete implementation of IFleet
	}

	@AfterEach
	void tearDown() {
		game = null;
	}

	@Test
	void constructor() {
		assertNotNull(game, "Game instance should not be null after construction.");
		assertNotNull(game.getAlienMoves(), "Shots list should not be null after initialization.");
		assertTrue(game.getAlienMoves().isEmpty(), "Shots list should be empty upon initialization.");
		assertEquals(0, game.getInvalidShots(), "Invalid shots count should be zero upon initialization.");
		assertEquals(0, game.getRepeatedShots(), "Repeated shots count should be zero upon initialization.");
		assertEquals(0, game.getHits(), "Hits count should be zero upon initialization.");
		assertEquals(0, game.getSunkShips(), "Sunk ships count should be zero upon initialization.");
	}

	@Test
	void fire2() {
		Position invalidPosition = new Position(-1, 5);
		game.fireSingleShot(invalidPosition, false);
		assertEquals(1, game.getInvalidShots(), "Invalid shots counter should increase for an invalid shot.");
	}

	@Test
	void fire3() {
		Position position = new Position(2, 3);
		game.fireSingleShot(position, false);
		game.fireSingleShot(position, true);
		assertEquals(1, game.getRepeatedShots(), "Repeated shots counter should increase for a repeated shot.");
	}

	@Test
	void repeatedShot1() {
		List<IPosition> positions = List.of(new Position(2, 3), new Position(2, 4), new Position(2, 5));
		game.fireShots(positions);
		Position position = new Position(2, 3);
		assertTrue(game.repeatedShot(position), "Position (2,3) should be marked as repeated after firing.");
	}

	@Test
	void repeatedShot2() {
		Position position = new Position(2, 3);
		assertFalse(game.repeatedShot(position), "Position (2,3) should not be marked as repeated before firing.");
	}

	@Test
	void getAlienMoves() {
		List<IPosition> positions = List.of(new Position(2, 3), new Position(2, 4), new Position(2, 5));
		game.fireShots(positions);
		assertEquals(1, game.getAlienMoves().size(), "Shots list should contain one shot after firing once.");
	}

	@Test
	void getRemainingShips() {
		IFleet fleet = game.getMyFleet();
		Ship ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		Ship ship2 = new Frigate(Compass.EAST, new Position(5, 5));

		fleet.addShip(ship1);
		assertEquals(1, game.getRemainingShips(), "Just one ship was created!");
		fleet.addShip(ship2);
		assertEquals(2, game.getRemainingShips(), "Two ships were created!");
		ship2.sink();
		assertEquals(1, game.getRemainingShips(), "Remaining ships count should be 1 after sinking one of two ships.");
	}

	/**
	 * Runs the given code and returns everything it printed on the console.
	 */
	private static String captureOutput(Runnable code) {
		java.io.PrintStream original = System.out;
		java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
		System.setOut(new java.io.PrintStream(output));
		try {
			code.run();
		} finally {
			System.setOut(original);
		}
		return output.toString();
	}

	/**
	 * Creates a game with a barge at A1 and a horizontal caravel at F6-F7.
	 */
	private static Game gameWithShips() {
		Fleet fleet = new Fleet();
		fleet.addShip(new Barge(Compass.NORTH, new Position(0, 0)));
		fleet.addShip(new Caravel(Compass.EAST, new Position(5, 5)));
		return new Game(fleet);
	}

	@Test
	@DisplayName("A frota e as jogadas do adversário começam vazias")
	void testAlienFleetAndMyMoves() {
		assertTrue(game.getAlienFleet().getShips().isEmpty(), "Error: The alien fleet should start empty.");
		assertTrue(game.getMyMoves().isEmpty(), "Error: My moves should start empty.");
	}

	@Test
	@DisplayName("fireSingleShot distingue tiros fora, repetidos, na água, num navio e o que afunda")
	void testFireSingleShot() {
		Game g = gameWithShips();

		assertFalse(g.fireSingleShot(new Position(-1, 0), false).valid(), "Error: A shot outside the board should be invalid.");
		assertTrue(g.fireSingleShot(new Position(3, 3), true).repeated(), "Error: A shot marked as repeated should be repeated.");
		assertNull(g.fireSingleShot(new Position(3, 3), false).ship(), "Error: A shot in the water should not hit a ship.");

		IGame.ShotResult hit = g.fireSingleShot(new Position(5, 5), false);
		assertEquals("Caravela", hit.ship().getCategory(), "Error: The shot should hit the caravel.");
		assertFalse(hit.sunk(), "Error: One shot should not sink the caravel.");

		assertTrue(g.fireSingleShot(new Position(0, 0), false).sunk(), "Error: One shot should sink the barge.");
		assertEquals(1, g.getInvalidShots(), "Error: There should be 1 invalid shot.");
		assertEquals(1, g.getRepeatedShots(), "Error: There should be 1 repeated shot.");
		assertEquals(2, g.getHits(), "Error: There should be 2 hits.");
		assertEquals(1, g.getSunkShips(), "Error: There should be 1 sunk ship.");
		assertEquals(1, g.getRemainingShips(), "Error: There should be 1 floating ship.");
	}

	@Test
	@DisplayName("repeatedShot só é verdadeiro para posições de jogadas anteriores")
	void testRepeatedShot() {
		captureOutput(() -> game.fireShots(List.of(new Position(1, 1), new Position(2, 2), new Position(3, 3))));

		assertTrue(game.repeatedShot(new Position(2, 2)), "Error: C3 was already shot.");
		assertFalse(game.repeatedShot(new Position(4, 4)), "Error: E5 was never shot.");
	}

	@Test
	@DisplayName("Os métodos do jogo rejeitam argumentos nulos")
	void testNullArguments() {
		assertThrows(AssertionError.class, () -> Game.printBoard(null, List.of(), false, false), "Error: printBoard should reject a null fleet.");
		assertThrows(AssertionError.class, () -> Game.printBoard(new Fleet(), null, false, false), "Error: printBoard should reject null moves.");
		assertThrows(AssertionError.class, () -> Game.jsonShots(null), "Error: jsonShots should reject null.");
		assertThrows(AssertionError.class, () -> game.readEnemyFire(null), "Error: readEnemyFire should reject null.");
		assertThrows(AssertionError.class, () -> game.fireShots(null), "Error: fireShots should reject null.");
		assertThrows(AssertionError.class, () -> game.fireSingleShot(null, false), "Error: fireSingleShot should reject null.");
		assertThrows(AssertionError.class, () -> game.repeatedShot(null), "Error: repeatedShot should reject null.");
	}

	@Test
	@DisplayName("fireShots recusa rajadas que não tenham exatamente 3 tiros")
	void testFireShotsWrongNumber() {
		assertThrows(IllegalArgumentException.class, () -> game.fireShots(List.of(new Position(1, 1))), "Error: A move with 1 shot should be rejected.");
	}

	@Test
	@DisplayName("jsonShots converte os tiros para JSON com a linha e a coluna clássicas")
	void testJsonShots() {
		String json = captureOutput(() -> {}) + Game.jsonShots(List.of(new Position(0, 0), new Position(9, 9)));

		assertTrue(json.contains("\"row\" : \"A\""), "Error: The row A is missing.");
		assertTrue(json.contains("\"column\" : 1"), "Error: The column 1 is missing.");
		assertTrue(json.contains("\"row\" : \"J\""), "Error: The row J is missing.");
		assertTrue(json.contains("\"column\" : 10"), "Error: The column 10 is missing.");
	}

	@Test
	@DisplayName("O tabuleiro mostra navios, zonas adjacentes, tiros e a legenda")
	void testPrintBoardWithShotsAndLegend() {
		Game g = gameWithShips();
		captureOutput(() -> {
			g.fireShots(List.of(new Position(0, 0), new Position(5, 5), new Position(9, 9)));
			g.fireShots(List.of(new Position(1, 1), new Position(-1, 0), new Position(9, 9)));
		});

		String board = captureOutput(() -> g.printMyBoard(true, true));
		assertTrue(board.contains("LEGENDA"), "Error: The legend should be printed.");
		assertTrue(board.contains(" F | . . . . . * # . . . |"), "Error: The hit on the caravel should be shown.");
		assertTrue(board.contains(" B | - o . . . . . . . . |"), "Error: The shot next to the sunk barge should be shown as water.");
		assertTrue(board.contains(" J | . . . . . . . . . o |"), "Error: The shot in the water should be shown.");

		String plain = captureOutput(() -> g.printMyBoard(false, false));
		assertFalse(plain.contains("LEGENDA"), "Error: The legend should not be printed.");
		assertTrue(plain.contains(" A | # - . . . . . . . . |"), "Error: Without shots the sunk barge is shown as a ship.");
	}

	@Test
	@DisplayName("O tabuleiro do adversário começa vazio")
	void testPrintAlienBoard() {
		String board = captureOutput(() -> game.printAlienBoard(true, false));
		assertTrue(board.contains(" A | . . . . . . . . . . |"), "Error: The alien board should be empty.");
	}

	@Test
	@DisplayName("over mostra a mensagem de fim de jogo")
	void testOver() {
		assertTrue(captureOutput(game::over).contains("Java Sparrow"), "Error: The game over message is missing.");
	}

	@Test
	@DisplayName("readEnemyFire aceita as posições juntas ou separadas e ignora tiros a mais")
	void testReadEnemyFireValid() {
		captureOutput(() -> {
			game.readEnemyFire(new java.util.Scanner("A1 b2 C3\n"));
			game.readEnemyFire(new java.util.Scanner("D 4 e 5 F 6 G 7\n"));
		});

		assertEquals(2, game.getAlienMoves().size(), "Error: Two moves should have been fired.");
		assertEquals(List.of(new Position('A', 1), new Position('B', 2), new Position('C', 3)), game.getAlienMoves().get(0).getShots());
		assertEquals(List.of(new Position('D', 4), new Position('E', 5), new Position('F', 6)), game.getAlienMoves().get(1).getShots());
	}

	@Test
	@DisplayName("readEnemyFire recusa posições incompletas e rajadas com menos de 3 tiros")
	void testReadEnemyFireInvalid() {
		assertThrows(IllegalArgumentException.class, () -> game.readEnemyFire(new java.util.Scanner("A B2 C3\n")), "Error: A column without a row should be rejected.");
		assertThrows(IllegalArgumentException.class, () -> game.readEnemyFire(new java.util.Scanner("A1 B2\n")), "Error: Two shots should be rejected.");
	}

	@Test
	@DisplayName("randomEnemyFire dispara 3 tiros diferentes que ainda não foram usados")
	void testRandomEnemyFire() {
		Game g = gameWithShips();
		captureOutput(() -> {
			g.fireShots(List.of(new Position(0, 0), new Position(9, 9), new Position(9, 8)));
			g.randomEnemyFire();
		});

		List<IPosition> shots = g.getAlienMoves().get(1).getShots();
		assertEquals(3, shots.stream().distinct().count(), "Error: The random shots should be different.");
		for (IPosition shot : shots) {
			assertFalse(g.getAlienMoves().get(0).getShots().contains(shot), "Error: A random shot should not repeat an old shot.");
			assertFalse(shot.equals(new Position(0, 1)) || shot.equals(new Position(1, 0)) || shot.equals(new Position(1, 1)),
					"Error: A random shot should not hit a position next to a sunk ship.");
		}
	}

	@Test
	@DisplayName("randomEnemyFire repete o último tiro quando restam menos de 3 posições livres")
	void testRandomEnemyFireFewPositions() {
		List<IPosition> free = List.of(new Position(9, 8), new Position(9, 9));
		List<IPosition> used = new ArrayList<>();
		for (int r = 0; r < Game.BOARD_SIZE; r++)
			for (int c = 0; c < Game.BOARD_SIZE; c++)
				if (!free.contains(new Position(r, c)))
					used.add(new Position(r, c));

		captureOutput(() -> {
			for (int i = 0; i + 3 <= used.size(); i += 3)
				game.fireShots(used.subList(i, i + 3));
			// As 2 posições que sobram (98 = 32 x 3 + 2) vão numa rajada com um tiro repetido
			IPosition last = used.get(used.size() - 1);
			game.fireShots(List.of(used.get(used.size() - 2), last, last));
			game.randomEnemyFire();
		});

		List<IPosition> shots = game.getAlienMoves().get(game.getAlienMoves().size() - 1).getShots();
		assertTrue(free.containsAll(shots), "Error: The random shots should use only the free positions.");
		assertEquals(2, shots.stream().distinct().count(), "Error: Both free positions should be used.");
	}
}
