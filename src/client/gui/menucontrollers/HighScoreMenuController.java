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

    @FXML
    private GridPane multiPlayerGrid;

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

        settings.addSinglePlayerHighScore("a", 76346);
        settings.addSinglePlayerHighScore("b", 566);
        settings.addSinglePlayerHighScore("c", 43442);
        settings.addSinglePlayerHighScore("d", 434);
        settings.addSinglePlayerHighScore("asdfghjklp", 1);
        settings.addSinglePlayerHighScore("1234567890", 9051312);
        settings.addSinglePlayerHighScore("23121", 444434);
        settings.addSinglePlayerHighScore("133gg", 3);
        settings.addSinglePlayerHighScore("fsefsf", 901672);
        settings.addSinglePlayerHighScore("ef33f", 9166312);
        settings.addSinglePlayerHighScore("3425", 903215312);
        settings.addMultiPlayerHighScore("BLUE: 1234567890, 1234567899", 123123123);
        settings.addMultiPlayerHighScore("BLUE: 1234567567899", 123123123);
        settings.addMultiPlayerHighScore("BLUE: 123456789234567899", 123123123);
        settings.addMultiPlayerHighScore("BLU234567890, 1234567899", 123123123);
        settings.addMultiPlayerHighScore("BLUE: 127890, 1234567899", 123123123);
        settings.addMultiPlayerHighScore("BL 1234567890, 1234567899", 123123123);
        settings.addMultiPlayerHighScore("BLUE: 123456890, 1234567899", 123123123);
        settings.addMultiPlayerHighScore("BLUE: 1234567234567899", 123123123);
        settings.addMultiPlayerHighScore("BLUE: 1234567, 1234567899", 123123123);
        settings.addMultiPlayerHighScore("BLUE: 12345, 1234567899", 123123123);
        settings.addMultiPlayerHighScore("BLUE: 1234567234567899", 123123123);

        // Set style sheet to stage to allow styling of labels
        stage.getScene().getStylesheets().add("file:src/client/gui/css/menu_stylesheet.css");

        // Populate grids from data stored in settings
        singlePlayerGrid = populateGridPane(settings.getSinglePlayerHighScores(), singlePlayerGrid);
        multiPlayerGrid = populateGridPane(settings.getMultiPlayerHighScores(), multiPlayerGrid);
    }

    /**
     * Populate grid pane with scores hashmap in descending order
     * @param scores HashMap containing player namees as keys and scores as values
     * @param inputGridPane Input gridpane to populate
     * @return Populated GridPane
     */
    private GridPane populateGridPane(HashMap<String, Integer> scores, GridPane inputGridPane) {
        // Sort high scores HashMap
        scores = (HashMap<String, Integer>) sortByValue(scores, false);

        // Set up trackers for amount of rows traversed. Only top 10 or so entries are displayed.
        int maxRows = 10;
        int currentRow = 0;

        // Go through every entry
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            // Check if not gone over the row limit
            if (currentRow < maxRows) {
                // Create name label
                Label nameLabel = new Label(entry.getKey());
                nameLabel.getStyleClass().add("highScoreEntry");
                nameLabel.getStyleClass().add("manaspaceFontWhite");
                inputGridPane.add(nameLabel, 0, currentRow);

                // CCreate score label
                Label scoreLabel = new Label(Integer.toString(entry.getValue()));
                scoreLabel.getStyleClass().add("highScoreEntry");
                scoreLabel.getStyleClass().add("manaspaceFontWhite");
                inputGridPane.add(scoreLabel, 1, currentRow);

                // Increment row counter
                currentRow++;
            } else {
                // Break if completely populated
                break;
            }
        }

        // Return populated gridpane
        return inputGridPane;
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
