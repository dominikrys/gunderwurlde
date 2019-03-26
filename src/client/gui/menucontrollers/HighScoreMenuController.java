package client.gui.menucontrollers;

import client.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * HighScoreMenuController class. Contains loader and controller for the high score menu
 *
 * @author Dominik Rys
 */
public class HighScoreMenuController extends VBox implements MenuController {
    /**
     * Stage to display menu on
     */
    private Stage stage;

    /**
     * Settings object
     */
    private Settings settings;

    @FXML
    private Button backButton;

    @FXML
    private GridPane singlePlayerGrid;

    /**
     * Constructor
     *
     * @param stage    Stage to show menu on
     * @param settings Settings object
     */
    public HighScoreMenuController(Stage stage, Settings settings) {
        // Set variables
        this.stage = stage;
        this.settings = settings;

        // Load FXML and set appropriate methods
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/gui/fxml/high_score_menu.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (
                IOException exception) {
            throw new RuntimeException(exception);
        }

        settings.addSinglePlayerHighScore("aaaaaaaaaa", 76346);
        settings.addSinglePlayerHighScore("aaaaaaaa", 5);
        settings.addSinglePlayerHighScore("aaaaaaa", 2);
        settings.addSinglePlayerHighScore("aaaaaaaaaa", 4);
        settings.addSinglePlayerHighScore("aaaaaaa", 1);
        settings.addSinglePlayerHighScore("aaaaaaaaaa", 90321312);
        settings.addSinglePlayerHighScore("aaaaaa", 4);
        settings.addSinglePlayerHighScore("aaaaaaaaaa", 3);
        settings.addSinglePlayerHighScore("aaaaaaaaa", 901634632);
        settings.addSinglePlayerHighScore("aaaaaaaaaa", 9166312);
        settings.addSinglePlayerHighScore("aaaaaaa", 903215312);

        // Populate high scores
        HashMap<String, Integer> singlePlayerScores = settings.getSinglePlayerHighScores();
        singlePlayerScores = (HashMap<String, Integer>) sortByValue(singlePlayerScores, false);

        int maxRows = 10;
        int currentRow = 0;

        for (Map.Entry<String, Integer> entry : singlePlayerScores.entrySet()) {
            if (currentRow < maxRows) {
                System.out.println(entry.getValue());

                // Add name
                Label nameLabel = new Label(entry.getKey());
                nameLabel.getStyleClass().add("highScoreEntry");
                singlePlayerGrid.add(nameLabel, 0, currentRow);

                // Add score
                Label scoreLabel = new Label(Integer.toString(entry.getValue()));
                scoreLabel.getStyleClass().add("highScoreEntry");
                singlePlayerGrid.add(scoreLabel, 1, currentRow);

                // Increment row counter
                currentRow++;
            } else {
                break;
            }
        }
    }

    /**
     * Sort hashmap by value. Code from https://stackoverflow.com/a/13913206/10621058
     *
     * @param unsortMap Unsorted map
     * @param order     Order for map to be sorted in. True for ascending, false for descending order.
     * @return Sorted Map
     */
    private Map<String, Integer> sortByValue(Map<String, Integer> unsortMap, final boolean order) {
        List<Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());

        // Sorting the list based on values
        list.sort((o1, o2) -> order ? o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey())
                : o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()) == 0
                ? o2.getKey().compareTo(o1.getKey())
                : o2.getValue().compareTo(o1.getValue()));
        return list.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b, LinkedHashMap::new));
    }

    /**
     * Go back to the main menu
     *
     * @param event Button press
     */
    @FXML
    void backButtonPress(ActionEvent event) {
        // Switch to main menu and clear this screen
        (new MainMenuController(stage, settings)).show();
        this.getChildren().clear();
    }

    /**
     * Show menu on stage
     */
    @Override
    public void show() {
        this.stage.getScene().setRoot(this);
    }
}
