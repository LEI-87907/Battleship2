package battleship;

import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Tasks class.
 * Cyclomatic Complexity for each method:
 * - menu: 22
 * - menuHelp: 1
 * - exportReport: 2
 * - buildFleet: 4
 * - readShip: 1
 * - readPosition: 1
 * - readClassicPosition: 6
 *
 * Os comandos rajada (quando afunda a última embarcação) e simula terminam com System.exit,
 * por isso não são testados até ao fim: isso terminaria também a execução dos testes.
 */
@DisplayName("Testes da classe Tasks")
class TasksTest {

	/** Uma frota válida de 11 barcas, precedida de um navio desconhecido e de um navio encostado a outro. */
	private static final String FLEET =
			"submarino 5 5 n\n" +
			"barca 0 0 n\nbarca 0 1 n\n" +
			"barca 0 2 n\nbarca 0 4 n\nbarca 0 6 n\nbarca 0 8 n\n" +
			"barca 2 0 n\nbarca 2 2 n\nbarca 2 4 n\nbarca 2 6 n\nbarca 2 8 n\n" +
			"barca 4 0 n\n";

	private InputStream originalIn;
	private PrintStream originalOut;
	private ByteArrayOutputStream output;

	@BeforeEach
	void setUp() {
		originalIn = System.in;
		originalOut = System.out;
		output = new ByteArrayOutputStream();
		System.setOut(new PrintStream(output));
	}

	@AfterEach
	void tearDown() {
		System.setIn(originalIn);
		System.setOut(originalOut);
	}

	/**
	 * Runs the menu with the given commands as if they were typed by the player.
	 */
	private String runMenu(String commands) {
		System.setIn(new ByteArrayInputStream(commands.getBytes()));
		Tasks.menu();
		return output.toString();
	}

	/**
	 * Returns the names of the files currently in the reports folder.
	 */
	private static Set<String> reportFiles() {
		String[] names = new File(GameReportPdf.REPORTS_DIR).list();
		return names == null ? new HashSet<>() : new HashSet<>(Arrays.asList(names));
	}

	@Test
	@DisplayName("Sem frota, os comandos do jogo não fazem nada e um comando desconhecido é avisado")
	void testMenuWithoutFleet() {
		String text = runMenu("estado\nmapa\nrajada\nsimula\ntiros\npdf\nxpto\najuda\ndesisto\n");

		assertTrue(text.contains("Primeiro tem de gerar ou ler uma frota."), "Error: pdf without a fleet should warn the player.");
		assertTrue(text.contains("Que comando é esse??? Repete ..."), "Error: An unknown command should be reported.");
		assertEquals(2, text.split("AJUDA DO MENU", -1).length - 1, "Error: The help should be shown at the start and after 'ajuda'.");
		assertTrue(text.contains("Bons ventos!"), "Error: The menu should end with the goodbye message.");
	}

	@Test
	@DisplayName("Com uma frota aleatória, os comandos mostram o estado, o mapa, os tiros e exportam o PDF")
	void testMenuWithRandomFleet() {
		Set<String> before = reportFiles();
		try {
			String text = runMenu("gerafrota\nestado\nmapa\ntiros\npdf\ndesisto\n");

			assertTrue(text.contains("Estado da Frota: 11 a flutuar, 0 afundados!"), "Error: The status of the fleet is incorrect.");
			assertTrue(text.contains("Relatório gravado em"), "Error: The PDF report should have been exported.");
		} finally {
			for (String name : reportFiles())
				if (!before.contains(name))
					new File(GameReportPdf.REPORTS_DIR, name).delete();
		}
	}

	@Test
	@DisplayName("Com uma frota lida do teclado, uma rajada afunda uma barca sem acabar o jogo")
	void testMenuWithFleetAndShots() {
		String text = runMenu("lefrota\n" + FLEET + "rajada A1 J9 J10\ndesisto\n");

		assertTrue(text.contains("1 Barca ao fundo"), "Error: The shot at A1 should sink a barge.");
		assertTrue(text.contains("Estado da Frota: 10 a flutuar, 1 afundados!"), "Error: The status after the shots is incorrect.");
		assertTrue(text.contains("Bons ventos!"), "Error: The game should not be over yet.");
	}

	@Test
	@DisplayName("buildFleet ignora navios desconhecidos e navios encostados a outros")
	void testBuildFleet() {
		Fleet fleet = Tasks.buildFleet(new Scanner(FLEET));

		assertEquals(Fleet.FLEET_SIZE, fleet.getShips().size(), "Error: The fleet should have 11 ships.");
		assertNull(fleet.shipAt(new Position(0, 1)), "Error: The barge next to another barge should not be added.");
		assertNull(fleet.shipAt(new Position(5, 5)), "Error: The unknown ship should not be added.");
	}

	@Test
	@DisplayName("readShip lê o tipo, a posição e a orientação de um navio")
	void testReadShip() {
		Ship ship = Tasks.readShip(new Scanner("nau 1 2 e"));

		assertInstanceOf(Carrack.class, ship, "Error: 'nau' should build a Carrack.");
		assertEquals(new Position(1, 2), ship.getPosition(), "Error: The position of the ship is incorrect.");
		assertEquals(Compass.EAST, ship.getBearing(), "Error: The bearing of the ship is incorrect.");
		assertNull(Tasks.readShip(new Scanner("submarino 1 2 e")), "Error: An unknown ship should return null.");
	}

	@Test
	@DisplayName("readPosition lê a linha e a coluna como números")
	void testReadPosition() {
		assertEquals(new Position(3, 4), Tasks.readPosition(new Scanner("3 4")), "Error: The position read is incorrect.");
	}

	@Test
	@DisplayName("readClassicPosition aceita 'A3' e 'a 3'")
	void testReadClassicPositionValid() {
		assertEquals(new Position('A', 3), Tasks.readClassicPosition(new Scanner("A3")), "Error: 'A3' should be read.");
		assertEquals(new Position('A', 3), Tasks.readClassicPosition(new Scanner("a 3")), "Error: 'a 3' should be read.");
	}

	@Test
	@DisplayName("readClassicPosition recusa texto vazio e formatos inválidos")
	void testReadClassicPositionInvalid() {
		assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(new Scanner("")), "Error: Empty input should be rejected.");
		assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(new Scanner("33")), "Error: A position without a letter should be rejected.");
		assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(new Scanner("A +3")), "Error: A signed row should be rejected.");
	}

	@Test
	@DisplayName("Os métodos de leitura rejeitam um Scanner nulo")
	void testNullArguments() {
		assertThrows(AssertionError.class, () -> Tasks.buildFleet(null), "Error: buildFleet should reject null.");
		assertThrows(AssertionError.class, () -> Tasks.readShip(null), "Error: readShip should reject null.");
		assertThrows(AssertionError.class, () -> Tasks.readPosition(null), "Error: readPosition should reject null.");
	}
}
