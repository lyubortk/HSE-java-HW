package ru.hse.lyubortk.test5;

import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

public class PairsGUI extends Application {
    private static final int BUTTON_MIN_SIZE = 40;
    private static final int BUTTON_PREFFERED_SIZE = 70;

    private static final String ARGUMENTS_EROOR = "Wrong arguments";
    private static final String USAGE = "You should pass exactly one argument N:\n"
                                        + "one positive even integer less than 15\n"
                                        + "(to play on N x N board)";

    private BiConsumer<String, String> alertCreator = (title, message) -> {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        Button exitButton = (Button) alert.getDialogPane().lookupButton(
                ButtonType.OK
        );
        exitButton.setText("Exit");
        exitButton.setOnTouchPressed(event -> Platform.exit());
        alert.setHeaderText(title);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOnHidden(event -> Platform.exit());
        alert.show();
    };

    private int N;
    private PairsLogic logic;
    private List<Button> buttons = new ArrayList<>();
    private GridPane grid;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        List<String> arguments = getParameters().getRaw();
        if (arguments.size() != 1) {
            alertCreator.accept(ARGUMENTS_EROOR, USAGE);
            return;
        }

        try {
            N = Integer.parseInt(arguments.get(0));
        } catch (NumberFormatException exception) {
            alertCreator.accept(ARGUMENTS_EROOR, USAGE);
            return;
        }

        if (N <= 0 || N * N > PairsLogic.MAX_SIZE || N % 2 == 1) {
            alertCreator.accept(ARGUMENTS_EROOR, USAGE);
            return;
        }

        logic = new PairsLogic(N * N, new Random(System.currentTimeMillis()));
        logic = new PairsLogic(N * N, new Random(0));
        setListeners();
        initGridPane();

        Scene scene = new Scene(grid);
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(N * BUTTON_MIN_SIZE);
        primaryStage.setMinWidth(N * BUTTON_MIN_SIZE);
        primaryStage.setHeight(N * BUTTON_PREFFERED_SIZE);
        primaryStage.setWidth(N * BUTTON_PREFFERED_SIZE);
        primaryStage.show();
    }

    private void setListeners() {
        logic.setOnCardOpenListener((index, text) -> buttons.get(index).setText(text));
        logic.setOnCardCloseListener(index -> buttons.get(index).setText(" "));
        logic.setOnGameOverListener(() -> alertCreator.accept("Game Over", "You won"));
        logic.setOnDelayedActionCreator((timeMillis, action) -> {
            var pause = new PauseTransition(new Duration(timeMillis));
            pause.setOnFinished(event -> action.run());
            pause.play();
            return (() -> {
                if (pause.getStatus() == Animation.Status.RUNNING) {
                    pause.stop();
                    pause.getOnFinished().handle(null);
                }
            });
        });
    }

    private void initGridPane() {
        grid = new GridPane();
        for (int row = 0; row < N; row++ ){
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(100.0 / N);
            rowConstraints.setFillHeight(true);
            rowConstraints.setVgrow(Priority.ALWAYS);
            grid.getRowConstraints().add(rowConstraints);
        }
        for (int column = 0; column < N; column++ ) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100.0 / N);
            columnConstraints.setFillWidth(true);
            columnConstraints.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(columnConstraints);
        }

        for (int i = 0 ; i < N * N; i++) {
            Button button = createButton(i);
            grid.add(button, i % N, i / N);
            buttons.add(button);
        }

    }
    private Button createButton(int index) {
        Button button = new Button(" ");
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        button.setMinSize(0, 0);
        button.setOnAction(event -> logic.pickCard(index));
        return button;
    }
}
