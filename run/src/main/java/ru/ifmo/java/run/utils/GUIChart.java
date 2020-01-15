package ru.ifmo.java.run.utils;

import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import ru.ifmo.java.common.utils.Point;

import java.util.List;

public class GUIChart {
    public static Chart getChart(List<Point> points, String name) {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        final LineChart<Number, Number> lineChart =
                new LineChart<Number, Number>(xAxis, yAxis);

        XYChart.Series series = new XYChart.Series();
        series.setName(name);
        for (Point point : points) {
            series.getData().add(new XYChart.Data(point.x, point.y));
        }

        lineChart.getData().add(series);
        return lineChart;
    }
}
