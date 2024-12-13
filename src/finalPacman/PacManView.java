// File: PacManView.java
package finalPacman;

import javafx.fxml.FXML;
import javafx.geometry.Point2D; // Sudah ditambahkan
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import finalPacman.PacManModel.CellValue;
import finalPacman.PacManModel.Direction;

public class PacManView extends Group {
    public final static double CELL_WIDTH = 20.0;

    @FXML private int rowCount;
    @FXML private int columnCount;
    private ImageView[][] cellViews;

    private Image pacmanRightImage;
    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image ghostRedImage;
    private Image ghostYellowImage;
    private Image blueGhostImage;
    private Image wallImage;
    private Image bigDotImage;
    private Image smallDotImage;

    public PacManView() {
        try {
            this.pacmanRightImage = new Image(getClass().getResourceAsStream("/res/pacmanRight.gif"));
            this.pacmanUpImage = new Image(getClass().getResourceAsStream("/res/pacmanUp.gif"));
            this.pacmanDownImage = new Image(getClass().getResourceAsStream("/res/pacmanDown.gif"));
            this.pacmanLeftImage = new Image(getClass().getResourceAsStream("/res/pacmanLeft.gif"));
            this.ghostRedImage = new Image(getClass().getResourceAsStream("/res/redghost.gif"));
            this.ghostYellowImage = new Image(getClass().getResourceAsStream("/res/ghost2.gif"));
            this.blueGhostImage = new Image(getClass().getResourceAsStream("/res/blueghost.gif"));
            this.wallImage = new Image(getClass().getResourceAsStream("/res/wall.png"));
            this.bigDotImage = new Image(getClass().getResourceAsStream("/res/whitedot.png"));
            this.smallDotImage = new Image(getClass().getResourceAsStream("/res/smalldot.png"));
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inisialisasi grid ImageViews
     */
    private void initializeGrid() {
        if (this.rowCount > 0 && this.columnCount > 0) {
            this.cellViews = new ImageView[this.rowCount][this.columnCount];
            for (int row = 0; row < this.rowCount; row++) {
                for (int column = 0; column < this.columnCount; column++) {
                    ImageView imageView = new ImageView();
                    imageView.setX(column * CELL_WIDTH);
                    imageView.setY(row * CELL_WIDTH);
                    imageView.setFitWidth(CELL_WIDTH);
                    imageView.setFitHeight(CELL_WIDTH);
                    this.cellViews[row][column] = imageView;
                    this.getChildren().add(imageView);
                }
            }
        }
    }

    /**
     * Update tampilan berdasarkan model
     */
    public void update(PacManModel model) {
        if (cellViews == null) {
            this.rowCount = model.getGrid().length;
            this.columnCount = model.getGrid()[0].length;
            initializeGrid();
        }

        // Reset grid images
        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                CellValue value = model.getGrid()[row][column];
                if (value == CellValue.WALL) {
                    cellViews[row][column].setImage(wallImage);
                } else if (value == CellValue.BIGDOT) {
                    cellViews[row][column].setImage(bigDotImage);
                } else if (value == CellValue.SMALLDOT) {
                    cellViews[row][column].setImage(smallDotImage);
                } else {
                    cellViews[row][column].setImage(null);
                }
            }
        }

        // Gambar PacMan
        PacMan pacMan = model.getPacMan();
        Point2D pacLocation = pacMan.getLocation();
        Image pacImage = getPacImage(pacMan.getLastDirection());
        cellViews[(int) pacLocation.getX()][(int) pacLocation.getY()].setImage(pacImage);

        // Gambar Ghosts
        for (Ghost ghost : model.getGhosts()) {
            Point2D ghostLocation = ghost.getLocation();
            Image currentGhostImage;

            if (ghost.isEdible()) {
                currentGhostImage = blueGhostImage;
            } else {
                // Menentukan gambar berdasarkan tipe hantu
                if ("RED".equalsIgnoreCase(ghost.getType())) {
                    currentGhostImage = ghostRedImage;
                } else if ("YELLOW".equalsIgnoreCase(ghost.getType())) {
                    currentGhostImage = ghostYellowImage;
                } else {
                    currentGhostImage = ghostRedImage; // Default jika tipe tidak dikenal
                }
            }

            cellViews[(int) ghostLocation.getX()][(int) ghostLocation.getY()].setImage(currentGhostImage);
        }

        // Blinking ghosts saat ghostEatingMode mendekati akhir
        if (model.isGhostEatingMode()) {
            int counter = model.getGhostEatingModeCounter();
            if (counter == 6 || counter == 4 || counter == 2) {
                for (Ghost ghost : model.getGhosts()) {
                    Point2D ghostLocation = ghost.getLocation();
                    cellViews[(int) ghostLocation.getX()][(int) ghostLocation.getY()].setImage(null);
                }
            }
        }
    }

    /**
     * Mendapatkan gambar PacMan berdasarkan arah
     */
    private Image getPacImage(Direction direction) {
        switch (direction) {
            case LEFT:
                return pacmanLeftImage;
            case RIGHT:
                return pacmanRightImage;
            case UP:
                return pacmanUpImage;
            case DOWN:
                return pacmanDownImage;
            default:
                return pacmanRightImage;
        }
    }

    // Getter dan Setter untuk rowCount dan columnCount

    public int getRowCount() {
        return this.rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
        this.initializeGrid();
    }

    public int getColumnCount() {
        return this.columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
        this.initializeGrid();
    }
}
