package battleship;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Exporta para PDF o histórico de rajadas de um jogo, usando a biblioteca Apache PDFBox.
 * <p>
 * O relatório contém a data e hora, cada rajada com as coordenadas dos tiros e o respetivo
 * resultado, um resumo final e o tabuleiro com os tiros recebidos.
 */
public final class GameReportPdf {

    /** Pasta onde os relatórios são gravados. */
    public static final String REPORTS_DIR = "reports";

    private static final float MARGIN = 50;
    private static final float FONT_SIZE = 11;
    private static final float LEADING = 15;

    private static final PDType1Font FONT = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDType1Font FONT_BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private static final PDType1Font FONT_MONO = new PDType1Font(Standard14Fonts.FontName.COURIER);

    private GameReportPdf() {
    }

    /**
     * Gera o relatório do jogo numa pasta {@value #REPORTS_DIR}, com um nome que inclui a data e a hora,
     * para não apagar relatórios anteriores.
     *
     * @param game o jogo a exportar
     * @return o ficheiro PDF criado
     * @throws IOException se não for possível escrever o ficheiro
     */
    public static File export(IGame game) throws IOException {
        assert game != null;

        File dir = new File(REPORTS_DIR);
        if (!dir.exists() && !dir.mkdirs())
            throw new IOException("Não foi possível criar a pasta " + dir.getAbsolutePath());

        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        File file = new File(dir, "jogo_" + stamp + ".pdf");
        export(game, file);
        return file;
    }

    /**
     * Gera o relatório do jogo no ficheiro indicado.
     *
     * @param game o jogo a exportar
     * @param file o ficheiro de destino
     * @throws IOException se não for possível escrever o ficheiro
     */
    public static void export(IGame game, File file) throws IOException {
        assert game != null && file != null;

        try (PDDocument doc = new PDDocument()) {
            PageWriter out = new PageWriter(doc);

            out.line(FONT_BOLD, 16, "Batalha Naval - Relatório do jogo");
            out.line(FONT, FONT_SIZE, "Gerado em "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            out.blank();

            out.line(FONT_BOLD, 13, "Rajadas");
            List<IMove> moves = game.getAlienMoves();
            if (moves.isEmpty())
                out.line(FONT, FONT_SIZE, "Ainda não foi disparada nenhuma rajada.");
            for (IMove move : moves)
                out.line(FONT, FONT_SIZE, describeMove(move));
            out.blank();

            out.line(FONT_BOLD, 13, "Resumo");
            out.line(FONT, FONT_SIZE, "Rajadas disparadas: " + moves.size());
            out.line(FONT, FONT_SIZE, "Tiros certeiros: " + game.getHits());
            out.line(FONT, FONT_SIZE, "Navios afundados: " + game.getSunkShips());
            out.line(FONT, FONT_SIZE, "Navios ainda a flutuar: " + game.getRemainingShips());
            out.line(FONT, FONT_SIZE, "Tiros repetidos: " + game.getRepeatedShots());
            out.line(FONT, FONT_SIZE, "Tiros fora do tabuleiro: " + game.getInvalidShots());
            out.line(FONT_BOLD, FONT_SIZE, game.getRemainingShips() == 0
                    ? "Resultado: frota toda afundada."
                    : "Resultado: jogo ainda em curso.");
            out.blank();

            out.ensureSpace(Game.BOARD_SIZE + 3); // o tabuleiro não deve ficar partido entre páginas
            out.line(FONT_BOLD, 13, "Tabuleiro final");
            for (String row : boardLines(game.getMyFleet(), moves))
                out.line(FONT_MONO, FONT_SIZE, row);
            out.line(FONT, 9, "Legenda: '#' navio, '*' tiro certeiro, 'o' tiro na água, '.' água");

            out.close();
            doc.save(file);
        }
    }

    /**
     * Descreve uma rajada numa linha de texto, por exemplo
     * {@code "Rajada 3: A5 (água), C10 (tiro em Nau), F5 (Barca ao fundo)"}.
     *
     * @param move a rajada
     * @return a descrição da rajada
     */
    static String describeMove(IMove move) {
        StringBuilder sb = new StringBuilder("Rajada ").append(move.getNumber()).append(": ");
        List<IPosition> shots = move.getShots();
        List<IGame.ShotResult> results = move.getShotResults();
        for (int i = 0; i < shots.size(); i++) {
            if (i > 0)
                sb.append(", ");
            sb.append(shots.get(i));
            if (i < results.size())
                sb.append(" (").append(describeShot(results.get(i))).append(")");
        }
        return sb.toString();
    }

    /**
     * Traduz o resultado de um tiro para texto.
     *
     * @param result o resultado do tiro
     * @return a descrição do resultado
     */
    static String describeShot(IGame.ShotResult result) {
        if (!result.valid())
            return "fora do tabuleiro";
        if (result.repeated())
            return "repetido";
        if (result.ship() == null)
            return "água";
        if (result.sunk())
            return result.ship().getCategory() + " ao fundo";
        return "tiro em " + result.ship().getCategory();
    }

    /**
     * Constrói o tabuleiro em texto, com os navios e os tiros recebidos.
     *
     * @param fleet a frota
     * @param moves as rajadas recebidas
     * @return as linhas do tabuleiro
     */
    static List<String> boardLines(IFleet fleet, List<IMove> moves) {
        char[][] map = new char[Game.BOARD_SIZE][Game.BOARD_SIZE];
        for (char[] row : map)
            java.util.Arrays.fill(row, '.');

        for (IShip ship : fleet.getShips())
            for (IPosition pos : ship.getPositions())
                map[pos.getRow()][pos.getColumn()] = '#';

        for (IMove move : moves)
            for (IPosition shot : move.getShots())
                if (shot.isInside()) {
                    char c = map[shot.getRow()][shot.getColumn()];
                    map[shot.getRow()][shot.getColumn()] = (c == '#' || c == '*') ? '*' : 'o';
                }

        List<String> lines = new ArrayList<>();
        StringBuilder header = new StringBuilder("   ");
        for (int c = 1; c <= Game.BOARD_SIZE; c++)
            header.append(String.format("%-3d", c));
        lines.add(header.toString().stripTrailing());
        for (int r = 0; r < Game.BOARD_SIZE; r++) {
            StringBuilder sb = new StringBuilder().append((char) ('A' + r)).append("  ");
            for (int c = 0; c < Game.BOARD_SIZE; c++)
                sb.append(map[r][c]).append("  ");
            lines.add(sb.toString().stripTrailing());
        }
        return lines;
    }

    /**
     * Escreve linhas de texto de cima para baixo, criando uma página nova quando a atual enche.
     */
    private static final class PageWriter {
        private final PDDocument doc;
        private PDPageContentStream stream;
        private float y;

        PageWriter(PDDocument doc) throws IOException {
            this.doc = doc;
            newPage();
        }

        private void newPage() throws IOException {
            if (stream != null)
                stream.close();
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            stream = new PDPageContentStream(doc, page);
            y = page.getMediaBox().getHeight() - MARGIN;
        }

        void line(PDType1Font font, float size, String text) throws IOException {
            if (y < MARGIN)
                newPage();
            stream.beginText();
            stream.setFont(font, size);
            stream.newLineAtOffset(MARGIN, y);
            stream.showText(text);
            stream.endText();
            y -= Math.max(LEADING, size + 4);
        }

        void ensureSpace(int lines) throws IOException {
            if (y - lines * LEADING < MARGIN)
                newPage();
        }

        void blank() {
            y -= LEADING / 2;
        }

        void close() throws IOException {
            stream.close();
        }
    }
}