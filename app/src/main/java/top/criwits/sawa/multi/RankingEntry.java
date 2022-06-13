package top.criwits.sawa.multi;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RankingEntry {
    private String username;
    private int score;
    private int enrollDate;
    private int difficulty;

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public int getEnrollDate() {
        return enrollDate;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public RankingEntry(String username, int score, int enrollDate, int difficulty) {
        this.enrollDate = enrollDate;
        this.username = username;
        this.score = score;
        this.difficulty = difficulty;
    }

    @NonNull
    public static String parseDate(int enrollDate) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date((long) enrollDate * 1000));
    }
}
