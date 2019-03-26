package client.gui.menucontrollers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LoadScreenController {
    @FXML
    private Label ipLabel;

    @FXML
    private Label portLabel;

    /**
     * Initialise the loading screen
     */
    public void initialise() {
        // Hide port and IP labels
        portLabel.setVisible(false);
        portLabel.setManaged(false);
        ipLabel.setVisible(false);
        ipLabel.setManaged(false);
    }

    /**
     * Update load screen controller with IP and port values
     */
    public void update(String ip, String port) {
        // Show port and IP labels
        portLabel.setVisible(true);
        portLabel.setManaged(true);
        ipLabel.setVisible(true);
        ipLabel.setManaged(true);

        // Update the IP and port labels with the obtained IP and port
        Platform.runLater(() -> ipLabel.setText("Server IP: " + ip));
        Platform.runLater(() -> portLabel.setText("Port: " + port));

    }
}
