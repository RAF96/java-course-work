package ru.ifmo.java.gui;

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
import ru.ifmo.java.common.Constant;
import ru.ifmo.java.common.enums.MetricType;
import ru.ifmo.java.common.enums.ServerType;
import ru.ifmo.java.common.enums.TypeOfVariableToChange;
import ru.ifmo.java.common.utils.Point;
import ru.ifmo.java.gui.utils.GUIChart;
import ru.ifmo.java.run.utils.RunOneClientsBunch;
import ru.ifmo.java.run.utils.RunSettings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;


public class GUI extends Application {

    private RunSettings settings = new RunSettings();
    private BorderPane borderPane;
    private List<Consumer<RunSettings>> settingsChanges = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
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

        Function<MetricType, List<Point>> readListPoint = (MetricType type) -> {
            Path path = Paths.get(Constant.metricsPath.toString(), type.name());
            if (!path.toFile().exists()) {
                return new ArrayList<>();
            }
            List<Point> result = new ArrayList<>();
            try (Scanner scanner = new Scanner(path)) {
                while (scanner.hasNext()) {
                    int x = scanner.nextInt();
                    double y = scanner.nextDouble();
                    result.add(new Point(x, y));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        };

        Chart individualThreadServerChart =
                GUIChart.getChart(readListPoint.apply(MetricType.MEAN_ONE_REQUEST_BY_CLIENT),
                        "MEAN_ONE_REQUEST_BY_CLIENT");
        Chart blockingServerChart =
                GUIChart.getChart(readListPoint.apply(MetricType.REQUEST_PROCESSING),
                        "REQUEST_PROCESSING");
        Chart notBlockingServerChart =
                GUIChart.getChart(readListPoint.apply(MetricType.CLIENT_PROCESSING),
                        "CLIENT_PROCESSING");


        gridPane.add(getSettingsShow(), 0, 0);
        gridPane.add(individualThreadServerChart, 1, 0);
        gridPane.add(blockingServerChart, 0, 1);
        gridPane.add(notBlockingServerChart, 1, 1);
        return gridPane;
    }

    // MOCK
    private Node getSettingsShow() {
        Label label = new Label();
        label.setText("App for launching clients and showing stats");
        return label;
    }

    private Node getUIControls() {
        HBox box = new HBox();
        box.getChildren().addAll(getMenuOfChangeableVariableMenu(), getTypeOfServer(), getNumberOfClients(),
                getTimeBetweenRequest(), getSizeOfArray(),
                getNumberOfRequestByOneClient(), getButtons());
        return box;
    }

    private Node getButtons() {
        VBox box = new VBox();
        box.getChildren().addAll(getRun(), getSave());
        return box;
    }

    private Node getSave() {
        Button button = new Button();
        button.setText("Save");

        EventHandler<ActionEvent> action = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    String variable = settings.typeOfVariableToChange.name();
                    String server = settings.serverType.name();
                    for (MetricType metric : MetricType.values()) {
                        Path src = Paths.get(Constant.metricsPath.toString(), metric.name());
                        Path dst = Paths.get(Constant.dataSavePath.toString(), variable, server, metric.name());
                        if (!dst.toFile().exists()) {
                            File parentFile = dst.toFile().getParentFile();
                            if (!parentFile.exists()) {
                                parentFile.mkdirs();
                            }
                            dst.toFile().createNewFile();
                        }
                            Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        button.setOnAction(action);
        return button;
    }

    private Node getMenuOfChangeableVariableMenu() {
        HBox box = new HBox();
        box.getChildren().addAll(getWhichVariableChange(), getRangeOfChangeableVariable());
        return box;
    }

    private Node getWhichVariableChange() {
        VBox vBox = new VBox();

        ToggleGroup toggleGroup = new ToggleGroup();

        RadioButton radioButton1 = new RadioButton();
        radioButton1.setText("Size of array in request");
        radioButton1.setToggleGroup(toggleGroup);
        radioButton1.setSelected(true);


        RadioButton radioButton2 = new RadioButton();
        radioButton2.setText("Sleep time after response");
        radioButton2.setToggleGroup(toggleGroup);


        RadioButton radioButton3 = new RadioButton();
        radioButton3.setText("Number of clients");
        radioButton3.setToggleGroup(toggleGroup);


        RadioButton radioButton4 = new RadioButton();
        radioButton4.setText("Nothing");
        radioButton4.setToggleGroup(toggleGroup);

        settingsChanges.add(new Consumer<RunSettings>() {
            @Override
            public void accept(RunSettings runSettings) {
                Toggle selectedToggle = toggleGroup.getSelectedToggle();
                if (radioButton1.equals(selectedToggle)) {
                    runSettings.typeOfVariableToChange = TypeOfVariableToChange.SIZE_OF_ARRAY_IN_REQUEST;
                } else if (radioButton2.equals(selectedToggle)) {
                    runSettings.typeOfVariableToChange = TypeOfVariableToChange.SLEEP_TIME_AFTER_RESPONSE;
                } else if (radioButton3.equals(selectedToggle)) {
                    runSettings.typeOfVariableToChange = TypeOfVariableToChange.NUMBER_OF_CLIENTS;
                } else if (radioButton4.equals(selectedToggle)) {
                    runSettings.typeOfVariableToChange = TypeOfVariableToChange.NONE;
                } else {
                    throw new RuntimeException("not correct radio button in changeable variable type");
                }
            }
        });

        vBox.getChildren().addAll(radioButton1, radioButton2, radioButton3, radioButton4);
        return vBox;
    }

    private Node getRangeOfChangeableVariable() {
        VBox vBox = new VBox();
        Label label = new Label();
        label.setText("from ... to ... delta ...");
        TextField textField1 = new TextField("1");
        TextField textField2 = new TextField("10");
        TextField textField3 = new TextField("1");
        vBox.getChildren().addAll(label, textField1, textField2, textField3);

        settingsChanges.add(new Consumer<RunSettings>() {
            @Override
            public void accept(RunSettings runSettings) {
                int min = Integer.parseInt(textField1.getText());
                int max = Integer.parseInt(textField2.getText());
                int delta = Integer.parseInt(textField3.getText());
                runSettings.range = new RunSettings.Range(min, max, delta);
            }
        });
        return vBox;
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
        TextField textField = new TextField("1");
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
        TextField textField = new TextField("0");
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
        TextField textField = new TextField("2");
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
        TextField textField = new TextField("10");
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
                    runSettings.clientsSettings.serverPort = Constant.individualThreadServerPort;
                } else if (radioButton2.equals(selectedToggle)) {
                    runSettings.serverType = ServerType.BLOCKING_SERVER;
                    runSettings.clientsSettings.serverPort = Constant.blockingServerPort;
                } else if (radioButton3.equals(selectedToggle)) {
                    runSettings.serverType = ServerType.NOT_BLOCKING_SERVER;
                    runSettings.clientsSettings.serverPort = Constant.individualThreadServerPort;
                    runSettings.clientsSettings.serverPort = Constant.notBlockingServerPort;
                } else {
                    throw new RuntimeException("not correct radio button in server type");
                }
            }
        });

        vBox.getChildren().addAll(radioButton1, radioButton2, radioButton3);
        return vBox;
    }
}
