package model.score;

public class Score {
    private String username;
    private int score;
    private String timestamp;

    public Score(String username, int score, String timestamp) {
        this.username = username;
        this.score = score;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
