package top.criwits.sawa.ranking;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RankingDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEntry(RankingEntry ... rankingEntries);

    @Query("SELECT * FROM rankings")
    List<RankingEntry> queryAll();

    @Delete
    void delete(RankingEntry rankingEntry);
}
