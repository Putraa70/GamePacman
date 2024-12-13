// File: MainMenuController.java
package finalPacman;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class MainMenuController {

    @FXML private Button startButton;
    @FXML private Button exitButton;
    @FXML private ImageView backgroundImageView;

    /**
     * Inisialisasi controller dengan menambahkan event handler dan memuat gambar latar belakang
     */
    @FXML
    private void initialize() {
        // Set event handler untuk tombol
        startButton.setOnAction(this::handleStart);
        exitButton.setOnAction(this::handleExit);

        // Muat gambar latar belakang secara programatis
        try {
            // Path absolut mulai dari root resources
            Image backgroundImage = new Image(getClass().getResourceAsStream("/finalPacman/res/pacman.png"));
            if (backgroundImage.isError()) {
                System.err.println("Error loading background image: " + backgroundImage.getException());
            } else {
                backgroundImageView.setImage(backgroundImage);
                System.out.println("Background image loaded successfully.");
            }
        } catch (Exception e) {
            System.err.println("Exception while loading background image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handler untuk tombol Start
     */
    private void handleStart(ActionEvent event) {
        try {
            // Load pacman.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/finalPacman/pacman.fxml"));
            Parent gameRoot = loader.load();

            // Dapatkan stage saat ini
            Stage stage = (Stage) startButton.getScene().getWindow();

            // Set judul dan scene baru
            stage.setTitle("PacMan");
            Controller controller = loader.getController();
            controller.setPrimaryStage(stage); // Mengirim referensi Stage ke Controller

            double sceneWidth = controller.getBoardWidth() + 20.0;
            double sceneHeight = controller.getBoardHeight() + 100.0;
            Scene scene = new Scene(gameRoot, sceneWidth, sceneHeight);

            // Set event handler untuk key presses
            gameRoot.setOnKeyPressed(controller);

            stage.setScene(scene);
            stage.show();
            gameRoot.requestFocus();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handler untuk tombol Exit
     */
    private void handleExit(ActionEvent event) {
        // Keluar dari aplikasi
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}
