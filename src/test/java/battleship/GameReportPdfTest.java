package battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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

    /**
     * Fires the given number of moves at a game without ships, one water shot per move.
     */
    private static Game gameWithMoves(int numberOfMoves) {
        Game game = new Game(new Fleet());
        for (int i = 0; i < numberOfMoves; i++) {
            Position pos = new Position(i % Game.BOARD_SIZE, (i / Game.BOARD_SIZE) % Game.BOARD_SIZE);
            game.fireShots(List.of(pos, pos, pos));
        }
        return game;
    }

    @Test
    @DisplayName("export sem ficheiro grava o relatório na pasta reports")
    void exportToReportsFolder() throws Exception {
        // A segunda exportação encontra a pasta reports já criada pela primeira
        File first = GameReportPdf.export(new Game(new Fleet()));
        File second = GameReportPdf.export(new Game(new Fleet()));
        try {
            assertTrue(first.exists() && second.exists(), "Error: The reports should have been created.");
            assertEquals(GameReportPdf.REPORTS_DIR, first.getParentFile().getName(), "Error: The report should be in the reports folder.");
            assertTrue(first.getName().startsWith("jogo_") && first.getName().endsWith(".pdf"), "Error: The name of the report is incorrect.");
        } finally {
            first.delete();
            second.delete();
        }
    }

    @Test
    @DisplayName("Um jogo sem rajadas e sem navios mostra as mensagens próprias")
    void exportEmptyGame(@TempDir Path tempDir) throws Exception {
        File pdf = tempDir.resolve("vazio.pdf").toFile();
        GameReportPdf.export(new Game(new Fleet()), pdf);

        try (PDDocument doc = Loader.loadPDF(pdf)) {
            String text = new PDFTextStripper().getText(doc);
            assertTrue(text.contains("Ainda não foi disparada nenhuma rajada."), "Error: The report should say that there are no moves.");
            assertTrue(text.contains("Resultado: frota toda afundada."), "Error: A game without floating ships should be over.");
        }
    }

    @Test
    @DisplayName("Um relatório com muitas rajadas passa para uma segunda página")
    void exportSeveralPages(@TempDir Path tempDir) throws Exception {
        for (int moves : new int[] {24, 60}) {
            File pdf = tempDir.resolve("rajadas_" + moves + ".pdf").toFile();
            GameReportPdf.export(gameWithMoves(moves), pdf);

            try (PDDocument doc = Loader.loadPDF(pdf)) {
                assertEquals(2, doc.getNumberOfPages(), "Error: A report with " + moves + " moves should have 2 pages.");
                assertTrue(new PDFTextStripper().getText(doc).contains("Rajada " + moves + ":"), "Error: The last move should be in the report.");
            }
        }
    }

    @Test
    @DisplayName("export rejeita argumentos nulos")
    void exportNullArguments(@TempDir Path tempDir) {
        File pdf = tempDir.resolve("nulo.pdf").toFile();
        Game game = new Game(new Fleet());

        assertThrows(AssertionError.class, () -> GameReportPdf.export(null), "Error: A null game should be rejected.");
        assertThrows(AssertionError.class, () -> GameReportPdf.export(null, pdf), "Error: A null game should be rejected.");
        assertThrows(AssertionError.class, () -> GameReportPdf.export(game, null), "Error: A null file should be rejected.");
    }

    @Test
    @DisplayName("Uma rajada ainda sem resultados mostra só as coordenadas")
    void describeMoveWithoutResults() {
        Move move = new Move(1, List.of(new Position(0, 0), new Position(1, 1)), new ArrayList<>());

        assertEquals("Rajada 1: A1, B2", GameReportPdf.describeMove(move), "Error: The description of the move is incorrect.");
    }

    @Test
    @DisplayName("O tabuleiro marca os tiros em navios, na água, repetidos e ignora os de fora")
    void boardLinesWithShots() {
        Fleet fleet = new Fleet();
        fleet.addShip(new Barge(Compass.NORTH, new Position(0, 0)));
        List<IPosition> shots = List.of(new Position(0, 0), new Position(0, 0), new Position(1, 1), new Position(1, 1), new Position(-1, 0));
        Move move = new Move(1, shots, new ArrayList<>());

        List<String> lines = GameReportPdf.boardLines(fleet, List.of(move));

        assertEquals("A  *  .  .  .  .  .  .  .  .  .", lines.get(1), "Error: The hit on the barge should be marked with '*'.");
        assertEquals("B  .  o  .  .  .  .  .  .  .  .", lines.get(2), "Error: The shot in the water should be marked with 'o'.");
    }

    @Test
    @DisplayName("Um jogo com navios a flutuar aparece como ainda em curso")
    void exportGameInProgress(@TempDir Path tempDir) throws Exception {
        Fleet fleet = new Fleet();
        fleet.addShip(new Barge(Compass.NORTH, new Position(0, 0)));
        File pdf = tempDir.resolve("em_curso.pdf").toFile();
        GameReportPdf.export(new Game(fleet), pdf);

        try (PDDocument doc = Loader.loadPDF(pdf)) {
            assertTrue(new PDFTextStripper().getText(doc).contains("Resultado: jogo ainda em curso."), "Error: A game with floating ships should still be in progress.");
        }
    }
}
