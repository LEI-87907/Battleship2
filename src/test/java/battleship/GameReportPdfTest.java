package battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes da exportação do histórico de rajadas para PDF.
 */
public class GameReportPdfTest {

    @Test
    @DisplayName("Cada tipo de resultado de tiro tem a descrição certa")
    void describeShot() {
        IShip nau = new Carrack(Compass.NORTH, new Position(0, 0));

        assertEquals("fora do tabuleiro", GameReportPdf.describeShot(new IGame.ShotResult(false, false, null, false)));
        assertEquals("repetido", GameReportPdf.describeShot(new IGame.ShotResult(true, true, null, false)));
        assertEquals("água", GameReportPdf.describeShot(new IGame.ShotResult(true, false, null, false)));
        assertEquals("tiro em Nau", GameReportPdf.describeShot(new IGame.ShotResult(true, false, nau, false)));
        assertEquals("Nau ao fundo", GameReportPdf.describeShot(new IGame.ShotResult(true, false, nau, true)));
    }

    @Test
    @DisplayName("A descrição de uma rajada inclui o número, as coordenadas e os resultados")
    void describeMove() {
        Game game = new Game(new Fleet());
        game.fireShots(List.of(new Position('A', 1), new Position('A', 1), new Position('K', 4)));

        String text = GameReportPdf.describeMove(game.getAlienMoves().get(0));

        assertTrue(text.startsWith("Rajada "));
        assertTrue(text.contains("A1 (água)"));
        assertTrue(text.contains("A1 (repetido)"));
        assertTrue(text.contains("K4 (fora do tabuleiro)"));
    }

    @Test
    @DisplayName("Um jogo completo é exportado para um ficheiro PDF válido")
    void exportFullGame(@TempDir Path tempDir) throws Exception {
        Game game = new Game(Fleet.createRandom());
        while (game.getRemainingShips() > 0)
            game.randomEnemyFire();

        File pdf = tempDir.resolve("jogo.pdf").toFile();
        GameReportPdf.export(game, pdf);

        assertTrue(pdf.exists());
        byte[] header = Files.readAllBytes(pdf.toPath());
        assertEquals("%PDF", new String(header, 0, 4));
    }

    @Test
    @DisplayName("O tabuleiro tem um cabeçalho e uma linha por cada letra")
    void boardLines() {
        List<String> lines = GameReportPdf.boardLines(new Fleet(), List.of());

        assertEquals(Game.BOARD_SIZE + 1, lines.size());
        assertTrue(lines.get(1).startsWith("A"));
        assertFalse(lines.get(1).contains("#"));
    }
}