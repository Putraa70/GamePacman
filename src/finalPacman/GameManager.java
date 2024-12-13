// File: GameManager.java
package finalPacman;

public class GameManager {
    private static int score = 0;
    private static int level = 1;

    // Getter dan Setter untuk skor
    public static int getScore() {
        return score;
    }

    public static void addScore(int points) {
        score += points;
    }

    public static void setScore(int newScore) {
        score = newScore;
    }

    // Getter dan Setter untuk level
    public static int getLevel() {
        return level;
    }

    public static void setLevel(int newLevel) {
        level = newLevel;
    }

    // Metode statis lainnya jika diperlukan
}
