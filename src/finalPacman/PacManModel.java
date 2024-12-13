// File: PacManModel.java
package finalPacman;

import javafx.geometry.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import javafx.fxml.FXML;

public class PacManModel {
    public enum CellValue {
        EMPTY, SMALLDOT, BIGDOT, WALL, GHOST1HOME, GHOST2HOME, PACMANHOME
    };

    public enum Direction {
        UP, DOWN, LEFT, RIGHT, NONE
    };

    @FXML private int rowCount;
    @FXML private int columnCount;
    private CellValue[][] grid;
    private int dotCount;
    private boolean gameOver;
    private boolean youWon;
    private boolean ghostEatingMode;
    private int ghostEatingModeCounter;
    private static final String[] levelFiles = {"src/levels/level1.txt", "src/levels/level2.txt", "src/levels/level3.txt"};

    private PacMan pacMan;
    private List<Ghost> ghosts;

    public PacManModel() {
        this.startNewGame();
    }

    /**
     * Mulai permainan baru
     */
    public void startNewGame() {
        this.gameOver = false;
        this.youWon = false;
        this.ghostEatingMode = false;
        this.dotCount = 0;
        GameManager.setScore(0);
        GameManager.setLevel(1);
        System.out.println("Permainan dimulai. Level: " + GameManager.getLevel());
        this.ghosts = new ArrayList<>();
        this.initializeLevel(getLevelFile(0));
        System.out.println("Level " + GameManager.getLevel() + " dimulai. Total dot: " + dotCount);
    }

    /**
     * Inisialisasi level berdasarkan file
     */
    public void initializeLevel(String fileName) {
        // Reset dotCount sebelum memuat level baru
        dotCount = 0;

        File file = new File(fileName);
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.err.println("File level tidak ditemukan: " + fileName);
            e.printStackTrace();
            this.gameOver = true;
            return;
        }

        // Hitung baris dan kolom
        rowCount = 0;
        columnCount = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Scanner lineScanner = new Scanner(line);
            while (lineScanner.hasNext()) {
                lineScanner.next();
                columnCount++;
            }
            rowCount++;
        }
        columnCount = columnCount / rowCount;
        scanner.close();

        // Inisialisasi grid
        grid = new CellValue[rowCount][columnCount];
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.err.println("File level tidak ditemukan saat inisialisasi grid: " + fileName);
            e.printStackTrace();
            this.gameOver = true;
            return;
        }

        int row = 0;
        int pacmanRow = 0, pacmanColumn = 0;
        int ghost1Row = 0, ghost1Column = 0;
        int ghost2Row = 0, ghost2Column = 0;

        while (scanner.hasNextLine()) {
            int column = 0;
            String line = scanner.nextLine();
            Scanner lineScanner = new Scanner(line);
            while (lineScanner.hasNext()) {
                String value = lineScanner.next();
                CellValue cellValue;
                switch (value) {
                    case "W":
                        cellValue = CellValue.WALL;
                        break;
                    case "S":
                        cellValue = CellValue.SMALLDOT;
                        dotCount++;
                        break;
                    case "B":
                        cellValue = CellValue.BIGDOT;
                        dotCount++;
                        break;
                    case "1":
                        cellValue = CellValue.GHOST1HOME;
                        ghost1Row = row;
                        ghost1Column = column;
                        break;
                    case "2":
                        cellValue = CellValue.GHOST2HOME;
                        ghost2Row = row;
                        ghost2Column = column;
                        break;
                    case "P":
                        cellValue = CellValue.PACMANHOME;
                        pacmanRow = row;
                        pacmanColumn = column;
                        break;
                    default:
                        cellValue = CellValue.EMPTY;
                        break;
                }
                grid[row][column] = cellValue;
                column++;
            }
            row++;
        }
        scanner.close();

        // Inisialisasi PacMan
        pacMan = new PacMan(new Point2D(pacmanRow, pacmanColumn), new Point2D(0, 0));

        // Reset Daftar Ghosts
        ghosts.clear();

        // Inisialisasi Ghosts sebagai objek Ghost dengan homeCell dan tipe yang sesuai
        Ghost ghost1 = new Ghost(new Point2D(ghost1Row, ghost1Column), new Point2D(-1, 0), CellValue.GHOST1HOME, "RED");
        Ghost ghost2 = new Ghost(new Point2D(ghost2Row, ghost2Column), new Point2D(-1, 0), CellValue.GHOST2HOME, "YELLOW");
        ghosts.add(ghost1);
        ghosts.add(ghost2);

        // Initialize directions and counters
        ghostEatingModeCounter = 25;

        // Tambahkan pernyataan debugging
        System.out.println("Level " + GameManager.getLevel() + " telah dimuat dengan " + dotCount + " dot.");
    }

    /**
     * Memulai level berikutnya
     */
    public void startNextLevel() {
        // Tingkatkan level melalui GameManager
        GameManager.setLevel(GameManager.getLevel() + 1);
        System.out.println("Level ditingkatkan ke: " + GameManager.getLevel());

        if (GameManager.getLevel() - 1 < levelFiles.length) {
            initializeLevel(getLevelFile(GameManager.getLevel() - 1));
        } else {
            // Tidak ada level lagi, permainan dimenangkan
            youWon = true;
            gameOver = true;
        }
    }

    /**
     * Langkah permainan (step)
     */
    public void step() {
        if (!gameOver && !youWon) {
            // Gerakkan PacMan
            pacMan.move(this);

            // Gerakkan Ghosts
            for (Ghost ghost : ghosts) {
                ghost.move(this);
            }

            // Periksa status permainan
            checkGameStatus();
        }
    }

    /**
     * Memeriksa status permainan setelah langkah
     */
    private void checkGameStatus() {
        // Periksa apakah semua dot telah dikonsumsi
        if (isLevelComplete()) {
            startNextLevel();
            return;
        }

        // Periksa apakah PacMan berada pada posisi Ghost
        for (Ghost ghost : ghosts) {
            if (pacMan.getLocation().equals(ghost.getLocation())) {
                if (ghost.isEdible()) {
                    // Ghost dimakan
                    pacMan.addScore(100);
                    sendGhostHome(ghost);
                } else {
                    // PacMan dimakan
                    gameOver = true;
                }
            }
        }

        // Periksa mode ghostEatingMode
        if (ghostEatingMode) {
            ghostEatingModeCounter--;
            if (ghostEatingModeCounter <= 0) {
                ghostEatingMode = false;
                // Set semua ghost menjadi tidak edible
                for (Ghost ghost : ghosts) {
                    ghost.setEdible(false);
                }
            }
        }
    }

    /**
     * Mengirim kembali Ghost ke rumahnya
     */
    private void sendGhostHome(Ghost ghost) {
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                if (grid[r][c] == ghost.getHomeCell()) {
                    ghost.setLocation(new Point2D(r, c));
                    ghost.setVelocity(new Point2D(-1, 0));
                    break;
                }
            }
        }
    }

    /**
     * Memindahkan PacMan berdasarkan arah
     */
    public void movePacman(Direction direction) {
        Direction finalDirection = direction;
        if (direction == Direction.NONE) {
            finalDirection = pacMan.getLastDirection();
        }

        Point2D potentialVelocity = changeVelocity(finalDirection);
        Point2D potentialLocation = pacMan.getLocation().add(potentialVelocity);

        // Jika PacMan keluar layar, wrap around
        potentialLocation = setGoingOffscreenNewLocation(potentialLocation);

        // Periksa apakah ada dinding
        if (grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] != CellValue.WALL) {
            pacMan.setLocation(potentialLocation);
            pacMan.setVelocity(potentialVelocity);
            pacMan.setLastDirection(finalDirection);
        }

        // Periksa isi cell
        CellValue currentCell = grid[(int) pacMan.getLocation().getX()][(int) pacMan.getLocation().getY()];
        if (currentCell == CellValue.SMALLDOT) {
            grid[(int) pacMan.getLocation().getX()][(int) pacMan.getLocation().getY()] = CellValue.EMPTY;
            dotCount--;
            pacMan.addScore(10);
        } else if (currentCell == CellValue.BIGDOT) {
            grid[(int) pacMan.getLocation().getX()][(int) pacMan.getLocation().getY()] = CellValue.EMPTY;
            dotCount--;
            pacMan.addScore(50);
            ghostEatingMode = true;
            ghostEatingModeCounter = 25;

            // Set semua ghost menjadi edible
            for (Ghost ghost : ghosts) {
                ghost.setEdible(true);
            }
        }
    }

    /**
     * Memindahkan Ghost
     */
    public void moveAGhost(Point2D velocity, Point2D location, Ghost ghost) {
        // Logika pergerakan Ghost
        // Implementasi pergerakan sederhana ke arah PacMan atau random
        Random generator = new Random();
        Direction direction = Direction.NONE;

        // Jika Ghost dapat melihat PacMan dalam baris atau kolom yang sama
        if (location.getY() == pacMan.getLocation().getY()) {
            if (location.getX() > pacMan.getLocation().getX()) {
                direction = Direction.UP;
            } else {
                direction = Direction.DOWN;
            }
        } else if (location.getX() == pacMan.getLocation().getX()) {
            if (location.getY() > pacMan.getLocation().getY()) {
                direction = Direction.LEFT;
            } else {
                direction = Direction.RIGHT;
            }
        } else {
            // Gerak acak
            int rand = generator.nextInt(4);
            switch (rand) {
                case 0:
                    direction = Direction.UP;
                    break;
                case 1:
                    direction = Direction.DOWN;
                    break;
                case 2:
                    direction = Direction.LEFT;
                    break;
                case 3:
                    direction = Direction.RIGHT;
                    break;
            }
        }

        // Jika dalam mode ghostEatingMode, gerak menjauhi PacMan
        if (ghostEatingMode) {
            if (direction == Direction.UP) direction = Direction.DOWN;
            else if (direction == Direction.DOWN) direction = Direction.UP;
            else if (direction == Direction.LEFT) direction = Direction.RIGHT;
            else if (direction == Direction.RIGHT) direction = Direction.LEFT;
        }

        Point2D newVelocity = changeVelocity(direction);
        Point2D newLocation = location.add(newVelocity);

        // Jika Ghost keluar layar, wrap around
        newLocation = setGoingOffscreenNewLocation(newLocation);

        // Periksa apakah ada dinding
        if (grid[(int) newLocation.getX()][(int) newLocation.getY()] != CellValue.WALL) {
            ghost.setLocation(newLocation);
            ghost.setVelocity(newVelocity);
        }
    }

    /**
     * Mengatur lokasi objek jika keluar layar (wrap around)
     */
    public Point2D setGoingOffscreenNewLocation(Point2D objectLocation) {
        double x = objectLocation.getX();
        double y = objectLocation.getY();

        if (y >= columnCount) {
            y = 0;
        } else if (y < 0) {
            y = columnCount - 1;
        }

        if (x >= rowCount) {
            x = 0;
        } else if (x < 0) {
            x = rowCount - 1;
        }

        return new Point2D(x, y);
    }

    /**
     * Mengubah Direction menjadi velocity
     */
    public Point2D changeVelocity(Direction direction) {
        switch (direction) {
            case LEFT:
                return new Point2D(0, -1);
            case RIGHT:
                return new Point2D(0, 1);
            case UP:
                return new Point2D(-1, 0);
            case DOWN:
                return new Point2D(1, 0);
            default:
                return new Point2D(0, 0);
        }
    }

    /**
     * Memeriksa apakah level telah selesai
     */
    public boolean isLevelComplete() {
        return this.dotCount == 0;
    }

    /**
     * Mendapatkan file level berdasarkan index
     */
    public static String getLevelFile(int x) {
        return levelFiles[x];
    }

    // Getter dan Setter untuk atribut lainnya

    public PacMan getPacMan() {
        return pacMan;
    }

    public List<Ghost> getGhosts() {
        return ghosts;
    }

    public CellValue[][] getGrid() {
        return grid;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isYouWon() {
        return youWon;
    }

    public boolean isGhostEatingMode() {
        return ghostEatingMode;
    }

    public int getGhostEatingModeCounter() {
        return ghostEatingModeCounter;
    }

    public void setGhostEatingModeCounter(int counter) {
        this.ghostEatingModeCounter = counter;
    }
}
