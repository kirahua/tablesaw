package tech.tablesaw.api.plot;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.plotting.fx.FxBar;
import tech.tablesaw.plotting.fx.FxPlot;
import tech.tablesaw.reducing.NumericSummaryTable;

import javax.swing.*;

public class Bar extends FxPlot {

    private static final String WINDOW_TITLE = "Tablesaw";

    public static void show(String title, CategoryColumn categoryColumn, NumericColumn numericColumn) throws Exception {

        SwingUtilities.invokeLater(() -> {
            try {
                initAndShowGUI(title, categoryColumn, numericColumn, 640, 480);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void show(String title, ShortColumn categoryColumn, NumericColumn numericColumn) throws Exception {

        SwingUtilities.invokeLater(() -> {
            try {
                initAndShowGUI(title, categoryColumn, numericColumn, 640, 480);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void show(String title, IntColumn categoryColumn, NumericColumn numericColumn) throws Exception {

        SwingUtilities.invokeLater(() -> {
            try {
                initAndShowGUI(title, categoryColumn, numericColumn, 640, 480);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void show(String title, NumericSummaryTable table) throws Exception {

        SwingUtilities.invokeLater(() -> {
            try {
                if (table.column(0) instanceof CategoryColumn) {
                    initAndShowGUI(title, table.categoryColumn(0), table.nCol(1), 640, 480);
                }
                if (table.column(0) instanceof ShortColumn) {
                    initAndShowGUI(title, table.shortColumn(0), table.nCol(1), 640, 480);
                }
                if (table.column(0) instanceof IntColumn) {
                    initAndShowGUI(title, table.intColumn(0), table.nCol(1), 640, 480);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void initAndShowGUI(String title,
                                       CategoryColumn categoryColumn,
                                       NumericColumn numericColumn,
                                       int width,
                                       int height) throws Exception {

        final JFXPanel fxPanel = getJfxPanel(WINDOW_TITLE, width, height);
        BarChart<String, Number> chart = FxBar.chart(title, categoryColumn, numericColumn);
        Platform.runLater(() -> initFX(fxPanel, chart));
    }

    private static void initAndShowGUI(String title,
                                       ShortColumn categoryColumn,
                                       NumericColumn numericColumn,
                                       int width,
                                       int height) throws Exception {

        final JFXPanel fxPanel = getJfxPanel(WINDOW_TITLE, width, height);
        BarChart<String, Number> chart = FxBar.chart(title, categoryColumn, numericColumn);
        Platform.runLater(() -> initFX(fxPanel, chart));
    }

    private static void initAndShowGUI(String title,
                                       IntColumn categoryColumn,
                                       NumericColumn numericColumn,
                                       int width,
                                       int height) throws Exception {

        final JFXPanel fxPanel = getJfxPanel(WINDOW_TITLE, width, height);
        BarChart<String, Number> chart = FxBar.chart(title, categoryColumn, numericColumn);
        Platform.runLater(() -> initFX(fxPanel, chart));
    }

    private static void initFX(JFXPanel fxPanel, BarChart<String, Number> chart) {
        // This method is invoked on the JavaFX thread
        Scene scene = new Scene(chart, chart.getWidth(), chart.getHeight());
        fxPanel.setScene(scene);
    }
}
