// File: PacMan.java
package finalPacman;

import javafx.geometry.Point2D;

public class PacMan extends Character {
    private int score;
    private int level;
    private PacManModel.Direction currentDirection;
    private PacManModel.Direction lastDirection;

    public PacMan(Point2D location, Point2D velocity) {
        super(location, velocity);
        this.score = 0;
        this.level = 1;
        this.currentDirection = PacManModel.Direction.NONE;
        this.lastDirection = PacManModel.Direction.NONE;
    }

    // Getter dan Setter untuk skor
    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        this.score += points;
        GameManager.addScore(points);
    }

    // Getter dan Setter untuk level
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        GameManager.setLevel(level);
    }

    // Getter dan Setter untuk arah
    public PacManModel.Direction getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(PacManModel.Direction direction) {
        this.currentDirection = direction;
    }

    public PacManModel.Direction getLastDirection() {
        return lastDirection;
    }

    public void setLastDirection(PacManModel.Direction direction) {
        this.lastDirection = direction;
    }

    @Override
    public void move(PacManModel model) {
        model.movePacman(currentDirection);
    }
}
