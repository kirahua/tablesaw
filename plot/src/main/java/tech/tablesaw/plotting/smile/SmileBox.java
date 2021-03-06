package tech.tablesaw.plotting.smile;

import smile.plot.BoxPlot;
import smile.plot.PlotCanvas;
import tech.tablesaw.api.Table;
import tech.tablesaw.table.TemporaryView;
import tech.tablesaw.table.ViewGroup;
import tech.tablesaw.util.DoubleArrays;

import static tech.tablesaw.plotting.smile.SmilePlotUtils.getjFrame;

import javax.swing.*;

/**
 *
 */
public class SmileBox {

    public static void show(String title, ViewGroup data, int columnIndex) {

        PlotCanvas canvas = create(title, data, columnIndex);
        JFrame frame = getjFrame(600, 400);
        frame.add(canvas);
        frame.setVisible(true);
    }

    public static void show(String title, Table table, String summaryColumnName, String groupingColumnName) {

        PlotCanvas canvas = create(title, table, summaryColumnName, groupingColumnName);
        JFrame frame = getjFrame(600, 400);
        frame.add(canvas);
        frame.setVisible(true);
    }

    public static PlotCanvas create(String plotTitle, ViewGroup groups, int columnNumber) {
        double[][] dataArray = DoubleArrays.to2dArray(groups, columnNumber);
        String[] grounpNames = groupNames(groups);
        PlotCanvas canvas = BoxPlot.plot(dataArray, grounpNames);
        canvas.setTitle(plotTitle);
        canvas.setAxisLabel(0, "");
        canvas.setAxisLabel(1, groups.getSortedOriginal().column(columnNumber).name());
        return canvas;
    }

    public static PlotCanvas create(String plotTitle, Table table, String summaryColumnName, String
            groupingColumnName) {
        ViewGroup groups = table.splitOn(table.column(groupingColumnName));
        int columnNumber = table.columnIndex(summaryColumnName);
        double[][] dataArray = DoubleArrays.to2dArray(groups, columnNumber);
        String[] grounpNames = groupNames(groups);
        PlotCanvas canvas = BoxPlot.plot(dataArray, grounpNames);
        canvas.setTitle(plotTitle);
        canvas.setAxisLabel(0, groupingColumnName);
        canvas.setAxisLabel(1, summaryColumnName);
        return canvas;
    }

    private static String[] groupNames(ViewGroup groups) {
        String[] result = new String[groups.size()];
        for (int i = 0; i < groups.size(); i++) {
            TemporaryView view = groups.get(i);
            result[i] = view.name();
        }
        return result;
    }
}
