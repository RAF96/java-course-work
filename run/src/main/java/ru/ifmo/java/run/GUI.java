package ru.ifmo.java.run;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.Chart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.ifmo.java.client.ClientsSettings;
import ru.ifmo.java.common.enums.ServerType;
import ru.ifmo.java.common.utils.Point;
import ru.ifmo.java.run.utils.GUIChart;
import ru.ifmo.java.run.utils.RunOneClientsBunch;
import ru.ifmo.java.run.utils.RunSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

// MOCK. It will be better do some refactoring later
public class GUI extends Application {

    private RunSettings settings = new RunSettings(ServerType.INDIVIDUAL_THREAD_SERVER, new ClientsSettings());
    private Stage stage;
    private BorderPane borderPane;
    private List<Consumer<RunSettings>> settingsChanges = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        Scene scene = new Scene(getAll());
        stage.setScene(scene);
        stage.show();
    }

    private BorderPane getAll() {
        borderPane = new BorderPane();
        borderPane.setBottom(getUIControls());
        borderPane.setCenter(getCharts());
        return borderPane;
    }

    private Node getCharts() {
        GridPane gridPane = new GridPane();
        List<Point> points = Arrays.asList(new Point(0, 0), new Point(1, 1)); // MOCK

        Chart individualThreadServerChart = GUIChart.getChart(points, "IndividualThreadServer");
        Chart blockingServerChart = GUIChart.getChart(points, "BlockingServer");
        Chart notBlockingServerChart = GUIChart.getChart(points, "NotBlockingServer");


        gridPane.add(getSettingsShow(), 0, 0);
        gridPane.add(individualThreadServerChart, 1, 0);
        gridPane.add(blockingServerChart, 0, 1);
        gridPane.add(notBlockingServerChart, 1, 1);
        return gridPane;
    }

    private Node getSettingsShow() {
        Label label = new Label();
        label.setText(settings.serverType.name());
        return label;
    }

    private Node getUIControls() {
        HBox box = new HBox();
        box.getChildren().addAll(getTypeOfServer(), getNumberOfClients(),
                getTimeBetweenRequest(), getSizeOfArray(),
                getNumberOfRequestByOneClient(), getRun());
        return box;
    }


    private Node getRun() {
        Button button = new Button();
        button.setText("Run");

        EventHandler<ActionEvent> action = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                settings = collectSettings();

                try {
                    RunOneClientsBunch.runCase(settings);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                borderPane.setCenter(getCharts());
            }
        };
        button.setOnAction(action);
        return button;
    }

    private RunSettings collectSettings() {
        for (Consumer<RunSettings> consumer : settingsChanges) {
            consumer.accept(settings);
        }
        return settings;
    }

    private Node getNumberOfClients() {
        VBox vBox = new VBox();
        Label label = new Label();
        label.setText("numberOfClients");
        TextField textField = new TextField();
        vBox.getChildren().addAll(label, textField);

        settingsChanges.add(new Consumer<RunSettings>() {
            @Override
            public void accept(RunSettings runSettings) {
                runSettings.clientsSettings.numberOfClients = Integer.parseInt(textField.getText());
            }
        });
        return vBox;
    }

    private Node getTimeBetweenRequest() {
        VBox vBox = new VBox();
        Label label = new Label();
        label.setText("timeBetweenRequest (ms):");
        TextField textField = new TextField();
        vBox.getChildren().addAll(label, textField);

        settingsChanges.add(new Consumer<RunSettings>() {
            @Override
            public void accept(RunSettings runSettings) {
                runSettings.clientsSettings.clientSettings.sleepTimeAfterResponse
                        = Integer.parseInt(textField.getText());
            }
        });
        return vBox;
    }


    private Node getNumberOfRequestByOneClient() {
        VBox vBox = new VBox();
        Label label = new Label();
        label.setText("numberOfRequestByOneClient:");
        TextField textField = new TextField();
        vBox.getChildren().addAll(label, textField);

        settingsChanges.add(new Consumer<RunSettings>() {
            @Override
            public void accept(RunSettings runSettings) {
                runSettings.clientsSettings.clientSettings.numberOfRequestByClient = Integer.parseInt(textField.getText());
            }
        });
        return vBox;
    }

    private Node getSizeOfArray() {
        VBox vBox = new VBox();
        Label label = new Label();
        label.setText("sizeOfArray:");
        TextField textField = new TextField();
        vBox.getChildren().addAll(label, textField);

        settingsChanges.add(new Consumer<RunSettings>() {
            @Override
            public void accept(RunSettings runSettings) {
                runSettings.clientsSettings.clientSettings.sizeOfArrayInRequest
                        = Integer.parseInt(textField.getText());
            }
        });
        return vBox;
    }

    private Node getTypeOfServer() {
        VBox vBox = new VBox();

        ToggleGroup toggleGroup = new ToggleGroup();

        RadioButton radioButton1 = new RadioButton();
        radioButton1.setText("IndividualThreadServer");
        radioButton1.setToggleGroup(toggleGroup);
        radioButton1.setSelected(true);


        RadioButton radioButton2 = new RadioButton();
        radioButton2.setText("BlockingServer");
        radioButton2.setToggleGroup(toggleGroup);


        RadioButton radioButton3 = new RadioButton();
        radioButton3.setText("NotBlockingThreadServer");
        radioButton3.setToggleGroup(toggleGroup);

        settingsChanges.add(new Consumer<RunSettings>() {
            @Override
            public void accept(RunSettings runSettings) {
                Toggle selectedToggle = toggleGroup.getSelectedToggle();
                if (radioButton1.equals(selectedToggle)) {
                    runSettings.serverType = ServerType.INDIVIDUAL_THREAD_SERVER;
                } else if (radioButton2.equals(selectedToggle)) {
                    runSettings.serverType = ServerType.BLOCKING_SERVER;
                } else if (radioButton3.equals(selectedToggle)) {
                    runSettings.serverType = ServerType.NOT_BLOCKING_SERVER;
                } else {
                    throw new RuntimeException("not correct radio button in server type");
                }
            }
        });

        vBox.getChildren().addAll(radioButton1, radioButton2, radioButton3);
        return vBox;
    }
}
