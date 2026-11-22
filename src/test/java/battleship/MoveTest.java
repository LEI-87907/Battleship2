package battleship;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Move class.
 * Cyclomatic Complexity for each method:
 * - Constructor, toString, getNumber, getShots, getShotResults: 1
 * - processEnemyFire: 37
 */
@DisplayName("Testes da classe Move")
class MoveTest {

	private final IShip barge = new Barge(Compass.NORTH, new Position(0, 0));
	private final IShip carrack = new Carrack(Compass.NORTH, new Position(5, 5));

	private PrintStream originalOut;
	private ByteArrayOutputStream output;

	@BeforeEach
	void setUp() {
		originalOut = System.out;
		output = new ByteArrayOutputStream();
		System.setOut(new PrintStream(output));
	}

	@AfterEach
	void tearDown() {
		System.setOut(originalOut);
	}

	private static IGame.ShotResult outside() {
		return new IGame.ShotResult(false, false, null, false);
	}

	private static IGame.ShotResult repeated() {
		return new IGame.ShotResult(true, true, null, false);
	}

	private static IGame.ShotResult water() {
		return new IGame.ShotResult(true, false, null, false);
	}

	private static IGame.ShotResult hit(IShip ship) {
		return new IGame.ShotResult(true, false, ship, false);
	}

	private static IGame.ShotResult sunk(IShip ship) {
		return new IGame.ShotResult(true, false, ship, true);
	}

	/**
	 * Processes a move with the given results and returns the message printed for the player.
	 */
	private String message(IGame.ShotResult... results) {
		new Move(7, new ArrayList<>(), List.of(results)).processEnemyFire(true);
		return output.toString().lines().findFirst().orElse("");
	}

	@Test
	@DisplayName("Os getters e o toString devolvem os dados da jogada")
	void testGettersAndToString() {
		List<IPosition> shots = List.of(new Position(0, 0), new Position(1, 1), new Position(2, 2));
		List<IGame.ShotResult> results = List.of(water());
		Move move = new Move(3, shots, results);

		assertEquals(3, move.getNumber(), "Error: The number of the move is incorrect.");
		assertSame(shots, move.getShots(), "Error: The shots of the move are incorrect.");
		assertSame(results, move.getShotResults(), "Error: The results of the move are incorrect.");
		assertEquals("Move{number=3, shots=3, results=1}", move.toString(), "Error: The text of the move is incorrect.");
	}

	@Test
	@DisplayName("Sem verbose não é escrita a mensagem da jogada, mas o JSON está certo")
	void testProcessEnemyFireNotVerbose() throws Exception {
		Move move = new Move(1, new ArrayList<>(), List.of(water(), hit(carrack), sunk(barge)));
		JsonNode json = new ObjectMapper().readTree(move.processEnemyFire(false));

		assertFalse(output.toString().contains("Jogada"), "Error: Without verbose the message should not be printed.");
		assertEquals(3, json.get("validShots").asInt(), "Error: There should be 3 valid shots.");
		assertEquals(0, json.get("outsideShots").asInt(), "Error: There should be no outside shots.");
		assertEquals(0, json.get("repeatedShots").asInt(), "Error: There should be no repeated shots.");
		assertEquals(1, json.get("missedShots").asInt(), "Error: There should be 1 missed shot.");
		assertEquals("Barca", json.get("sunkBoats").get(0).get("type").asText(), "Error: The sunk boat should be a barge.");
		assertEquals("Nau", json.get("hitsOnBoats").get(0).get("type").asText(), "Error: The hit boat should be a carrack.");
	}

	@Test
	@DisplayName("Só tiros repetidos")
	void testOnlyRepeatedShots() {
		assertEquals("Jogada nº7 -> 3 tiros repetidos", message(repeated(), repeated(), repeated()));
	}

	@Test
	@DisplayName("Um tiro repetido e dois fora do tabuleiro")
	void testOneRepeatedAndOutsideShots() {
		assertEquals("Jogada nº7 -> 1 tiro repetido, 2 tiros exteriores", message(repeated(), outside(), outside()));
	}

	@Test
	@DisplayName("Só tiros fora do tabuleiro")
	void testOnlyOutsideShots() {
		assertEquals("Jogada nº7 -> 3 tiros exteriores", message(outside(), outside(), outside()));
	}

	@Test
	@DisplayName("Dois navios do mesmo tipo afundados e um tiro na água")
	void testTwoSunkBoatsAndWater() {
		IShip otherBarge = new Barge(Compass.NORTH, new Position(9, 9));
		assertEquals("Jogada nº7 -> 3 tiros válidos: 2 Barcas ao fundo + 1 tiro na água", message(sunk(barge), sunk(otherBarge), water()));
	}

	@Test
	@DisplayName("Um navio afundado e dois tiros noutro navio, sem tiros na água")
	void testSunkBoatAndHitsWithoutWater() {
		// O código apaga o "+" final mas deixa um espaço no fim da mensagem
		assertEquals("Jogada nº7 -> 3 tiros válidos: 1 Barca ao fundo + 2 tiros num(a) Nau", message(sunk(barge), hit(carrack), hit(carrack)).strip());
	}

	@Test
	@DisplayName("Um tiro num navio, um repetido e um fora do tabuleiro")
	void testHitRepeatedAndOutside() {
		String text = message(hit(carrack), repeated(), outside());
		assertTrue(text.startsWith("Jogada nº7 -> 1 tiro válido: 1 tiro num(a) Nau"), "Error: The hit on the carrack is missing.");
		assertTrue(text.endsWith("1 tiro repetido, 1 tiro exterior"), "Error: The repeated and outside shots are missing.");
	}

	@Test
	@DisplayName("Dois tiros na água e um fora do tabuleiro")
	void testWaterAndOutside() {
		assertEquals("Jogada nº7 -> 2 tiros válidos: 2 tiros na água, 1 tiro exterior", message(water(), water(), outside()));
	}

	@Test
	@DisplayName("Um tiro na água e dois tiros repetidos")
	void testWaterAndRepeatedShots() {
		assertEquals("Jogada nº7 -> 1 tiro válido: 1 tiro na água, 2 tiros repetidos", message(water(), repeated(), repeated()));
	}
}
