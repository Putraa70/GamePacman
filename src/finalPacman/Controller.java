// File: Controller.java
package finalPacman;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.animation.AnimationTimer;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class Controller implements javafx.event.EventHandler<KeyEvent> {
    final private static double FRAMES_PER_SECOND = 5.0;

    @FXML private Label scoreLabel;
    @FXML private Label levelLabel;
    @FXML private Label gameOverLabel;
    @FXML private PacManView pacManView;

    private PacManModel pacManModel;
    private boolean paused;
    private AnimationTimer timer; // Menambahkan variabel timer
    private Stage primaryStage;    // Menambahkan referensi ke Stage

    public Controller() {
        this.paused = false;
    }

    /**
     * Setter untuk Stage
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * Inisialisasi controller dan mulai permainan
     */
    public void initialize() {
        this.pacManModel = new PacManModel();
        this.updateView();

        timer = new AnimationTimer() {
            private long lastUpdate = 0;
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 200_000_000) { // Update setiap 200ms (~5 FPS)
                    if (!paused) {
                        pacManModel.step();
                        updateView();
                        checkGameState();
                        lastUpdate = now;
                    }
                }
            }
        };
        timer.start();
    }

    /**
     * Memperbarui tampilan game
     */
    private void updateView() {
        this.pacManView.update(pacManModel);
        this.scoreLabel.setText("Score: " + GameManager.getScore());
        this.levelLabel.setText("Level: " + GameManager.getLevel());
    }

    /**
     * Memeriksa status permainan setelah langkah
     */
    private void checkGameState() {
        if (pacManModel.isYouWon()) {
            this.gameOverLabel.setText("YOU WON!");
            pause();
        } else if (pacManModel.isGameOver()) {
            this.gameOverLabel.setText("GAME OVER");
            pause();
        }
    }

    @Override
    public void handle(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();
        PacManModel.Direction direction = PacManModel.Direction.NONE;
        boolean keyRecognized = true;

        switch (code) {
            case LEFT:
                direction = PacManModel.Direction.LEFT;
                break;
            case RIGHT:
                direction = PacManModel.Direction.RIGHT;
                break;
            case UP:
                direction = PacManModel.Direction.UP;
                break;
            case DOWN:
                direction = PacManModel.Direction.DOWN;
                break;
            case G:
                restartGame();
                break;
            case M:
                returnToMenu();
                break;
            default:
                keyRecognized = false;
        }

        if (keyRecognized) {
            keyEvent.consume();
            pacManModel.getPacMan().setCurrentDirection(direction);
        }
    }

    /**
     * Mengulang permainan
     */
    private void restartGame() {
        pause();
        this.pacManModel.startNewGame();
        this.gameOverLabel.setText("");
        this.paused = false;
        this.updateView();
        timer.start(); // Memulai kembali timer
    }

    /**
     * Menjeda permainan
     */
    public void pause() {
        this.paused = true;
    }

    /**
     * Mendapatkan lebar papan permainan
     */
    public double getBoardWidth() {
        return PacManView.CELL_WIDTH * this.pacManView.getColumnCount();
    }

    /**
     * Mendapatkan tinggi papan permainan
     */
    public double getBoardHeight() {
        return PacManView.CELL_WIDTH * this.pacManView.getRowCount();
    }

    /**
     * Mengembalikan pemain ke menu utama
     */
    private void returnToMenu() {
        // Konfirmasi sebelum kembali ke menu
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi");
        alert.setHeaderText("Kembali ke Menu Utama");
        alert.setContentText("Apakah Anda yakin ingin kembali ke menu utama? Permainan akan dijeda.");

        // Tampilkan dialog dan tunggu respons
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Hentikan timer
                    if (timer != null) {
                        timer.stop();
                    }

                    // Load mainMenu.fxml
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/finalPacman/mainMenu.fxml"));
                    Parent menuRoot = loader.load();

                    // Dapatkan controller dari MainMenu
                    MainMenuController menuController = loader.getController();

                    // Dapatkan stage saat ini
                    Stage stage = this.primaryStage;

                    // Set judul dan scene baru
                    stage.setTitle("PacMan - Main Menu");
                    Scene scene = new Scene(menuRoot, 420, 500); // Sesuaikan ukuran jika diperlukan

                    // Set event handler untuk key presses di menu
                    menuRoot.setOnKeyPressed(null); // Nonaktifkan handler key presses di menu

                    // Set scene ke stage
                    stage.setScene(scene);
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
