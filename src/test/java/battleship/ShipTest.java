package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test class for Ship.
 * Author: ${user.name}
 * Date: ${current_date}
 * Time: ${current_time}
 * Cyclomatic Complexity for each method:
 * - Constructor: 1
 * - getCategory: 1
 * - getSize: 1
 * - getBearing: 1
 * - getPositions: 1
 * - stillFloating: 2
 * - shoot: 2
 * - occupies: 2
 * - tooCloseTo (IShip): 2
 * - tooCloseTo (IPosition): 2
 * - getTopMostPos: 2
 * - getBottomMostPos: 2
 * - getLeftMostPos: 2
 * - getRightMostPos: 2
 */
public class ShipTest {

    private Ship ship, shipBorder;

    @BeforeEach
    void setUp() {
        // Since Ship is abstract, instantiate it with a concrete subclass (e.g., Barge)
        ship = new Barge(Compass.NORTH, new Position(5, 5));
        shipBorder = new Barge(Compass.NORTH, new Position(0, 0));
    }

    @AfterEach
    void tearDown() {
        ship = null;
    }

    /**
     * Test for the constructor.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testConstructor() {
        assertNotNull(ship, "Error: Ship instance should not be null.");
        assertEquals("Barca", ship.getCategory(), "Error: Ship category is incorrect.");
        assertEquals(Compass.NORTH, ship.getBearing(), "Error: Ship bearing is incorrect.");
        assertEquals(1, ship.getSize(), "Error: Ship size is incorrect.");
        assertFalse(ship.getPositions().isEmpty(), "Error: Ship positions should not be empty.");
    }

    /**
     * Test for the getCategory method.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testGetCategory() {
        assertEquals("Barca", ship.getCategory(), "Error: Ship category should be 'Barca'.");
    }

    /**
     * Test for the getSize method.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testGetSize() {
        assertEquals(1, ship.getSize(), "Error: Ship size should be 1.");
    }

    /**
     * Test for the getBearing method.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testGetBearing() {
        assertEquals(Compass.NORTH, ship.getBearing(), "Error: Ship bearing should be NORTH.");
    }

    /**
     * Test for the getPositions method.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testGetPositions() {
        List<IPosition> positions = ship.getPositions();
        assertNotNull(positions, "Error: Ship positions should not be null.");
        assertEquals(1, positions.size(), "Error: Ship should have exactly one position.");
        assertEquals(5, positions.get(0).getRow(), "Error: Position's row should be 5.");
        assertEquals(5, positions.get(0).getColumn(), "Error: Position's column should be 5.");
    }

    /**
     * Test for the stillFloating method (all positions intact).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testStillFloating1() {
        assertTrue(ship.stillFloating(), "Error: Ship should still be floating.");
    }

    /**
     * Test for the stillFloating method (all positions hit).
     */
    @Test
    void testStillFloating2() {
        ship.getPositions().get(0).shoot();
        assertFalse(ship.stillFloating(), "Error: Ship should no longer be floating after being hit.");
    }

    /**
     * Test for the shoot method (valid position).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testShoot1() {
        Position target = new Position(5, 5);
        ship.shoot(target);
        assertTrue(ship.getPositions().get(0).isHit(), "Error: Position should be marked as hit.");
    }

    /**
     * Test for the shoot method (invalid position).
     */
    @Test
    void testShoot2() {
        Position target = new Position(0, 0);
        ship.shoot(target); // No exception expected
        assertFalse(ship.getPositions().get(0).isHit(), "Error: Position should not be marked as hit for an invalid target.");
    }

    /**
     * Test for the occupies method (position occupied).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testOccupies1() {
        Position pos = new Position(5, 5);
        assertTrue(ship.occupies(pos), "Error: Ship should occupy position (5, 5).");
    }

    /**
     * Test for the occupies method (position not occupied).
     */
    @Test
    void testOccupies2() {
        Position pos = new Position(1, 1);
        assertFalse(ship.occupies(pos), "Error: Ship should not occupy position (1, 1).");
    }

    /**
     * Test for the tooCloseTo method with another IShip (ships too close).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testTooCloseToShip1() {
        Ship nearbyShip = new Barge(Compass.NORTH, new Position(5, 6));
        assertTrue(ship.tooCloseTo(nearbyShip), "Error: Ships should be too close.");
    }

    /**
     * Test for the tooCloseTo method with another IShip (ships not close).
     */
    @Test
    void testTooCloseToShip2() {
        Ship farShip = new Barge(Compass.NORTH, new Position(10, 10));
        assertFalse(ship.tooCloseTo(farShip), "Error: Ships should not be too close.");
    }

    /**
     * Test for the tooCloseTo method with an IPosition (positions adjacent).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testTooCloseToPosition1() {
        Position pos = new Position(5, 6); // Adjacent position
        assertTrue(ship.tooCloseTo(pos), "Error: Ship should be too close to the given position.");
    }

    /**
     * Test for the tooCloseTo method with an IPosition (positions not adjacent).
     */
    @Test
    void testTooCloseToPosition2() {
        Position pos = new Position(7, 7); // Non-adjacent position
        assertFalse(ship.tooCloseTo(pos), "Error: Ship should not be too close to the given position.");
    }

    /**
     * Test for the getTopMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    void testGetTopMostPos() {
        assertEquals(5, ship.getTopMostPos(), "Error: The topmost position should be 5.");
    }

    /**
     * Test for the getBottomMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    void testGetBottomMostPos() {
        assertEquals(5, ship.getBottomMostPos(), "Error: The bottommost position should be 5.");
    }

    /**
     * Test for the getLeftMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    void testGetLeftMostPos() {
        assertEquals(5, ship.getLeftMostPos(), "Error: The leftmost position should be 5.");
    }

    /**
     * Test for the getRightMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    void testGetRightMostPos() {
        assertEquals(5, ship.getRightMostPos(), "Error: The rightmost position should be 5.");
    }

    /**
     * Test for the getRightMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    void testGetAdjacentPositions() {
        assertEquals(8, ship.getAdjacentPositions().size(), "Error: The number of adjacent positions of a barge in the middle of the tray should be 8.");
        assertEquals(3, shipBorder.getAdjacentPositions().size(), "Error: The number of adjacent positions of a barge in the middle of the tray should be 8.");
    }

    /**
     * Test for the buildShip factory method with every known kind of ship.
     */
    @Test
    @DisplayName("buildShip cria o tipo de navio certo para cada nome")
    void testBuildShipKnownKinds() {
        Position pos = new Position(2, 2);
        assertInstanceOf(Barge.class, Ship.buildShip("barca", Compass.NORTH, pos), "Error: 'barca' should build a Barge.");
        assertInstanceOf(Caravel.class, Ship.buildShip("caravela", Compass.NORTH, pos), "Error: 'caravela' should build a Caravel.");
        assertInstanceOf(Carrack.class, Ship.buildShip("nau", Compass.NORTH, pos), "Error: 'nau' should build a Carrack.");
        assertInstanceOf(Frigate.class, Ship.buildShip("fragata", Compass.NORTH, pos), "Error: 'fragata' should build a Frigate.");
        assertInstanceOf(Galleon.class, Ship.buildShip("galeao", Compass.NORTH, pos), "Error: 'galeao' should build a Galleon.");
    }

    /**
     * Test for the buildShip factory method with an unknown kind of ship.
     */
    @Test
    @DisplayName("buildShip devolve null para um tipo de navio desconhecido")
    void testBuildShipUnknownKind() {
        assertNull(Ship.buildShip("submarino", Compass.NORTH, new Position(2, 2)), "Error: an unknown kind should return null.");
    }

    /**
     * Test for the getPosition method.
     */
    @Test
    @DisplayName("getPosition devolve a posição inicial do navio")
    void testGetPosition() {
        assertEquals(new Position(5, 5), ship.getPosition(), "Error: The initial position of the ship is incorrect.");
    }

    /**
     * Test for the toString method.
     */
    @Test
    @DisplayName("toString mostra a categoria, a orientação e a posição")
    void testToString() {
        assertEquals("[Barca n F6]", ship.toString(), "Error: The text of the ship is incorrect.");
    }

    /**
     * Test for the sink method.
     */
    @Test
    @DisplayName("sink atinge todas as posições e o navio deixa de flutuar")
    void testSink() {
        Ship frigate = new Frigate(Compass.EAST, new Position(5, 5));
        frigate.sink();
        assertTrue(frigate.getPositions().stream().allMatch(IPosition::isHit), "Error: All positions should be hit.");
        assertFalse(frigate.stillFloating(), "Error: A sunk ship should not be floating.");
    }

    /**
     * Test for the stillFloating method when only the last position is intact.
     */
    @Test
    @DisplayName("stillFloating é verdadeiro enquanto houver uma posição por atingir")
    void testStillFloatingLastPositionIntact() {
        Ship frigate = new Frigate(Compass.EAST, new Position(5, 5));
        for (int i = 0; i < 3; i++)
            frigate.getPositions().get(i).shoot();
        assertTrue(frigate.stillFloating(), "Error: The ship should still be floating with one position intact.");
    }

    /**
     * Test for the extreme positions of a ship whose positions are not sorted.
     */
    @Test
    @DisplayName("As posições extremas são calculadas mesmo com as posições fora de ordem")
    void testExtremePositionsUnsorted() {
        Ship custom = new Ship("Teste", Compass.NORTH, new Position(7, 7), 3) { };
        custom.getPositions().add(new Position(7, 7));
        custom.getPositions().add(new Position(6, 6));
        custom.getPositions().add(new Position(5, 5));

        assertEquals(5, custom.getTopMostPos(), "Error: The topmost position should be 5.");
        assertEquals(7, custom.getBottomMostPos(), "Error: The bottommost position should be 7.");
        assertEquals(5, custom.getLeftMostPos(), "Error: The leftmost position should be 5.");
        assertEquals(7, custom.getRightMostPos(), "Error: The rightmost position should be 7.");
    }

    /**
     * Test for the extreme positions of a ship with several positions.
     */
    @Test
    @DisplayName("As posições extremas de uma fragata vertical estão corretas")
    void testExtremePositionsSorted() {
        Ship frigate = new Frigate(Compass.NORTH, new Position(5, 5));
        assertEquals(5, frigate.getTopMostPos(), "Error: The topmost position should be 5.");
        assertEquals(8, frigate.getBottomMostPos(), "Error: The bottommost position should be 8.");
        assertEquals(5, frigate.getLeftMostPos(), "Error: The leftmost position should be 5.");
        assertEquals(5, frigate.getRightMostPos(), "Error: The rightmost position should be 5.");
    }

    /**
     * Test for the occupies method on a ship with several positions.
     */
    @Test
    @DisplayName("occupies verifica todas as posições de um navio grande")
    void testOccupiesSeveralPositions() {
        Ship frigate = new Frigate(Compass.EAST, new Position(5, 5));
        assertTrue(frigate.occupies(new Position(5, 8)), "Error: The last position should be occupied.");
        assertFalse(frigate.occupies(new Position(6, 8)), "Error: A position outside the ship should not be occupied.");
    }

    /**
     * Test for the tooCloseTo method with a ship far away.
     */
    @Test
    @DisplayName("tooCloseTo é falso para um navio com várias posições afastado")
    void testTooCloseToDistantShip() {
        Ship frigate = new Frigate(Compass.EAST, new Position(0, 0));
        Ship other = new Frigate(Compass.EAST, new Position(9, 0));
        assertFalse(frigate.tooCloseTo(other), "Error: Distant ships should not be too close.");
    }

    /**
     * Test for the isInsideBoard method with a ship inside the board.
     */
    @Test
    @DisplayName("isInsideBoard é verdadeiro para um navio dentro do tabuleiro")
    void testIsInsideBoardTrue() {
        assertTrue(ship.isInsideBoard(), "Error: The ship should be inside the board.");
        assertTrue(new Frigate(Compass.EAST, new Position(9, 6)).isInsideBoard(), "Error: A ship touching the border should be inside the board.");
    }

    /**
     * Test for the isInsideBoard method with ships outside each border.
     */
    @Test
    @DisplayName("isInsideBoard é falso quando o navio sai por qualquer um dos lados")
    void testIsInsideBoardFalse() {
        assertFalse(new Barge(Compass.NORTH, new Position(5, -1)).isInsideBoard(), "Error: The ship is outside on the left.");
        assertFalse(new Frigate(Compass.EAST, new Position(5, 7)).isInsideBoard(), "Error: The ship is outside on the right.");
        assertFalse(new Barge(Compass.NORTH, new Position(-1, 5)).isInsideBoard(), "Error: The ship is outside on the top.");
        assertFalse(new Frigate(Compass.NORTH, new Position(7, 5)).isInsideBoard(), "Error: The ship is outside on the bottom.");
    }

    /**
     * Test for the getAdjacentPositions method on a ship with several positions.
     */
    @Test
    @DisplayName("getAdjacentPositions não repete posições nem inclui as do próprio navio")
    void testGetAdjacentPositionsSeveralPositions() {
        Ship frigate = new Frigate(Compass.EAST, new Position(5, 5));
        List<IPosition> adjacents = frigate.getAdjacentPositions();

        assertEquals(14, adjacents.size(), "Error: A horizontal frigate in the middle of the board should have 14 adjacent positions.");
        assertFalse(adjacents.contains(new Position(5, 6)), "Error: A position of the ship should not be adjacent to itself.");
        assertEquals(adjacents.size(), (int) adjacents.stream().distinct().count(), "Error: Adjacent positions should not be repeated.");
    }

    /**
     * Test for the assertions of the buildShip method.
     */
    @Test
    @DisplayName("buildShip rejeita argumentos nulos")
    void testBuildShipNullArguments() {
        Position pos = new Position(2, 2);
        assertThrows(AssertionError.class, () -> Ship.buildShip(null, Compass.NORTH, pos), "Error: A null kind should be rejected.");
        assertThrows(AssertionError.class, () -> Ship.buildShip("barca", null, pos), "Error: A null bearing should be rejected.");
        assertThrows(AssertionError.class, () -> Ship.buildShip("barca", Compass.NORTH, null), "Error: A null position should be rejected.");
    }

    /**
     * Test for the assertions of the methods that receive a position or a ship.
     */
    @Test
    @DisplayName("Os métodos que recebem uma posição ou um navio rejeitam valores inválidos")
    void testMethodsRejectInvalidArguments() {
        assertThrows(AssertionError.class, () -> ship.occupies(null), "Error: occupies should reject null.");
        assertThrows(AssertionError.class, () -> ship.tooCloseTo((IShip) null), "Error: tooCloseTo should reject a null ship.");
        assertThrows(AssertionError.class, () -> ship.tooCloseTo((IPosition) null), "Error: tooCloseTo should reject a null position.");
        assertThrows(AssertionError.class, () -> ship.shoot(null), "Error: shoot should reject null.");
        assertThrows(AssertionError.class, () -> ship.shoot(new Position(10, 10)), "Error: shoot should reject a position outside the board.");
    }


}