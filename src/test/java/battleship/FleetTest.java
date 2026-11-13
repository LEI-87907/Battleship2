package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Test class for Fleet.
 * Author: ${user.name}
 * Date: ${current_date}
 * Time: ${current_time}
 * Cyclomatic Complexity for each method:
 * - Constructor: 1
 * - addShip: 3
 * - getShips: 1
 * - getShipsLike: 2
 * - getFloatingShips: 2
 * - shipAt: 2
 * - isInsideBoard: 3
 * - colisionRisk: 2
 */
	public class FleetTest {

		private Fleet fleet;

		@BeforeEach
		void setUp() {
			fleet = new Fleet();
		}

		@AfterEach
		void tearDown() {
			fleet = null;
		}

		/**
		 * Test for the Fleet constructor.
		 * Cyclomatic Complexity: 1
		 */
		@Test
		void testConstructor() {
			assertNotNull(fleet, "Error: Instance of Fleet should not be null.");
			assertTrue(fleet.getShips().isEmpty(), "Error: Fleet should be initialized with empty ships list.");
		}

		@Test
		void testCreateRandom(){
				IFleet r = Fleet.createRandom();
				assertNotNull(r, "Error: createRandom should return a non-null fleet instance.");
				assertEquals(Fleet.FLEET_SIZE, r.getShips().size(), "Error: createRandom should create a fleet with the correct number of ships.");
		}

		/**
		 * Test for the addShip method (all conditions true).
		 * Cyclomatic Complexity: 3
		 */
		@Test
		void testAddShip1() {
			IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
			assertTrue(fleet.addShip(ship), "Error: Valid ship should be added successfully.");
			assertEquals(1, fleet.getShips().size(), "Error: Fleet should contain one ship after addition.");
		}

		/**
		 * Test for the addShip method (fleet size limit reached).
		 */
		@Test
		void testAddShip2() {
			for (int i = 0; i < Fleet.FLEET_SIZE; i++) {
				fleet.addShip(new Barge(Compass.NORTH, new Position(i, 0)));
			}
			IShip anotherShip = new Barge(Compass.NORTH, new Position(10, 10));
			assertFalse(fleet.addShip(anotherShip), "Error: Should not add ship when fleet size limit is reached.");
		}

		/**
		 * Test for the addShip method (ship outside the board).
		 */
		@Test
		void testAddShip3() {
			IShip shipOutside = new Barge(Compass.NORTH, new Position(99, 99));
			assertFalse(fleet.addShip(shipOutside), "Error: Should not add ship outside the board.");
		}

		/**
		 * Test for the addShip method (collision risk).
		 */
		@Test
		void testAddShip4() {
			IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
			IShip ship2 = new Barge(Compass.NORTH, new Position(1, 1));  // Overlapping position
			fleet.addShip(ship1);
			assertFalse(fleet.addShip(ship2), "Error: Should not add ship with a collision risk.");
		}

		/**
		 * Test for the getShips method.
		 * Cyclomatic Complexity: 1
		 */
		@Test
		void testGetShips() {
			assertTrue(fleet.getShips().isEmpty(), "Error: Fleet's ships list should initially be empty.");
			IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
			fleet.addShip(ship);
			assertEquals(1, fleet.getShips().size(), "Error: Fleet should have size 1 after adding a ship.");
			assertEquals(ship, fleet.getShips().get(0), "Error: Fleet's first ship should match the added ship.");
		}

		/**
		 * Test for the getShipsLike method (ships of specific category).
		 * Cyclomatic Complexity: 2
		 */
		@Test
		void testGetShipsLike() {
			IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
			IShip ship2 = new Caravel(Compass.NORTH, new Position(2, 1));
			fleet.addShip(ship1);
			fleet.addShip(ship2);

			List<IShip> barges = fleet.getShipsLike("Barca");
			assertEquals(1, barges.size(), "Error: There should be exactly one ship of category 'Barca'.");
			assertEquals(ship1, barges.get(0), "Error: The ship of category 'Barca' does not match.");
		}

		/**
		 * Test for the getFloatingShips method.
		 * Cyclomatic Complexity: 2
		 */
		@Test
		void testGetFloatingShips() {
			IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
			IShip ship2 = new Caravel(Compass.NORTH, new Position(4, 4));
			fleet.addShip(ship1);
			fleet.addShip(ship2);

			List<IShip> floatingShips = fleet.getFloatingShips();
			assertEquals(2, floatingShips.size(), "Error: All ships should be floating initially.");

			ship1.getPositions().get(0).shoot();  // Sink ship1
			floatingShips = fleet.getFloatingShips();
			assertEquals(1, floatingShips.size(), "Error: Only one ship should be floating after sinking one.");
			assertEquals(ship2, floatingShips.get(0), "Error: The floating ship should match the expected result.");
		}

		/**
		 * Test for the shipAt method.
		 * Cyclomatic Complexity: 2
		 */
		@Test
		void testShipAt() {
			IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
			fleet.addShip(ship);

			assertEquals(ship, fleet.shipAt(new Position(1, 1)), "Error: Should return the correct ship at the position.");
			assertNull(fleet.shipAt(new Position(5, 5)), "Error: Should return null for empty positions in the fleet.");
		}


		/**
		 * Test for private method colisionRisk.
		 * Cyclomatic Complexity: 2
		 */
		@Test
		void testColisionRisk() throws Exception {
			var method = Fleet.class.getDeclaredMethod("colisionRisk", IShip.class);
			method.setAccessible(true);

			IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
			IShip ship2 = new Barge(Compass.NORTH, new Position(1, 1));  // Overlapping position
			fleet.addShip(ship1);

			assertTrue((Boolean) method.invoke(fleet, ship2), "Error: Overlapping ships should be at collision risk.");
			assertFalse((Boolean) method.invoke(fleet, new Barge(Compass.NORTH, new Position(5, 5))),
					"Error: Ships at non-overlapping positions should not have a collision risk.");
		}

		/**
		 * Test for the printStatus method.
		 * Cyclomatic Complexity: 1
		 */
		@Test
		void testPrintStatus() {
			IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
			fleet.addShip(ship);
			assertDoesNotThrow(fleet::printStatus, "Error: printStatus should not throw any exceptions.");
		}

	/**
	 * Test for the addShip method when the fleet is already full.
	 */
	@Test
	@DisplayName("addShip recusa navios quando a frota já está cheia")
	void testAddShipFleetFull() {
		for (int row = 0; row < Game.BOARD_SIZE; row += 2)
			for (int column = 0; column < Game.BOARD_SIZE; column += 2)
				fleet.addShip(new Barge(Compass.NORTH, new Position(row, column)));

		int size = fleet.getShips().size();
		assertTrue(size >= Fleet.FLEET_SIZE, "Error: The fleet should have reached its size limit.");
		assertFalse(fleet.addShip(new Barge(Compass.NORTH, new Position(9, 9))), "Error: A full fleet should not accept more ships.");
		assertEquals(size, fleet.getShips().size(), "Error: The size of a full fleet should not change.");
	}

	/**
	 * Test for the getSunkShips method.
	 */
	@Test
	@DisplayName("getSunkShips devolve só os navios afundados")
	void testGetSunkShips() {
		IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		IShip ship2 = new Barge(Compass.NORTH, new Position(5, 5));
		fleet.addShip(ship1);
		fleet.addShip(ship2);
		assertTrue(fleet.getSunkShips().isEmpty(), "Error: No ship should be sunk at the beginning.");

		ship1.sink();
		List<IShip> sunkShips = fleet.getSunkShips();
		assertEquals(1, sunkShips.size(), "Error: Only one ship should be sunk.");
		assertSame(ship1, sunkShips.get(0), "Error: The sunk ship is incorrect.");
	}

	/**
	 * Test for the methods that print ships.
	 */
	@Test
	@DisplayName("Os métodos print mostram os navios na consola")
	void testPrintMethods() {
		IShip barge = new Barge(Compass.NORTH, new Position(1, 1));
		IShip caravel = new Caravel(Compass.EAST, new Position(5, 5));
		fleet.addShip(barge);
		fleet.addShip(caravel);

		java.io.PrintStream original = System.out;
		java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
		System.setOut(new java.io.PrintStream(output));
		try {
			fleet.printAllShips();
			fleet.printFloatingShips();
			fleet.printShipsByCategory("Barca");
		} finally {
			System.setOut(original);
		}

		String text = output.toString();
		assertTrue(text.contains(barge.toString()), "Error: The barge should be printed.");
		assertTrue(text.contains(caravel.toString()), "Error: The caravel should be printed.");
	}

	/**
	 * Test for the assertions of the methods that receive arguments.
	 */
	@Test
	@DisplayName("Os métodos da frota rejeitam argumentos nulos")
	void testNullArguments() throws Exception {
		assertThrows(AssertionError.class, () -> fleet.addShip(null), "Error: addShip should reject null.");
		assertThrows(AssertionError.class, () -> fleet.getShipsLike(null), "Error: getShipsLike should reject null.");
		assertThrows(AssertionError.class, () -> fleet.shipAt(null), "Error: shipAt should reject null.");
		assertThrows(AssertionError.class, () -> fleet.printShips(null), "Error: printShips should reject null.");
		assertThrows(AssertionError.class, () -> fleet.printShipsByCategory(null), "Error: printShipsByCategory should reject null.");

		var method = Fleet.class.getDeclaredMethod("colisionRisk", IShip.class);
		method.setAccessible(true);
		var exception = assertThrows(java.lang.reflect.InvocationTargetException.class,
				() -> method.invoke(fleet, (Object) null), "Error: colisionRisk should reject null.");
		assertInstanceOf(AssertionError.class, exception.getCause(), "Error: colisionRisk should fail with an AssertionError.");
	}
	}