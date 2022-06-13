package top.criwits.sawa.ranking;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "rankings")
public class RankingEntry {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "difficulty")
    public int difficulty;
    @ColumnInfo(name = "player_name")
    public String playerName;
    @ColumnInfo(name = "score")
    public int score;
    @ColumnInfo(name = "enroll_time")
    public int enrollTime;

    public RankingEntry(int difficulty, String playerName, int score, int enrollTime) {
        this.difficulty = difficulty;
        this.playerName = playerName;
        this.score = score;
        this.enrollTime = enrollTime;
    }

    public static String diffToString(int difficulty) {
        switch (difficulty) {
            case 0:
                return "Easy";
            case 1:
                return "Moderate";
            case 2:
                return "Hard";
            default:
                return "Unknown";
        }
    }

    @NonNull
    public static String parseDate(int enrollDate) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date((long) enrollDate * 1000));
    }
}