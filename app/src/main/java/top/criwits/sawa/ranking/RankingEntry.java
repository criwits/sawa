package top.criwits.sawa.ranking;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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
}