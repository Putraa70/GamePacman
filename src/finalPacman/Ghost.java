// File: Ghost.java
package finalPacman;

import finalPacman.PacManModel.CellValue;
import javafx.geometry.Point2D;

public class Ghost extends Character {
    private boolean isEdible;
    private CellValue homeCell;
    private String type; // Menambahkan tipe hantu (misalnya, "RED", "YELLOW")

    public Ghost(Point2D location, Point2D velocity, CellValue homeCell, String type) {
        super(location, velocity);
        this.isEdible = false;
        this.homeCell = homeCell;
        this.type = type;
    }

    // Getter dan Setter untuk isEdible
    public boolean isEdible() {
        return isEdible;
    }

    public void setEdible(boolean isEdible) {
        this.isEdible = isEdible;
    }

    // Getter untuk homeCell
    public CellValue getHomeCell() {
        return homeCell;
    }

    // Getter untuk tipe hantu
    public String getType() {
        return type;
    }

    @Override
    public void move(PacManModel model) {
        model.moveAGhost(getVelocity(), getLocation(), this);
    }
}
