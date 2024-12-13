// File: Character.java
package finalPacman;

import javafx.geometry.Point2D;

public abstract class Character {
    private Point2D location;
    private Point2D velocity;

    public Character(Point2D location, Point2D velocity) {
        this.location = location;
        this.velocity = velocity;
    }

    // Getter dan Setter untuk lokasi
    public Point2D getLocation() {
        return location;
    }

    public void setLocation(Point2D location) {
        this.location = location;
    }

    // Getter dan Setter untuk velocity
    public Point2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Point2D velocity) {
        this.velocity = velocity;
    }

    // Metode abstrak untuk pergerakan
    public abstract void move(PacManModel model);
}
