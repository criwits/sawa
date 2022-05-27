package top.criwits.sawa.ranking;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {RankingEntry.class}, version = 1, exportSchema = false)
public abstract class RankingDatabase extends RoomDatabase {
    public abstract RankingDAO rankingDAO();

    //数据库的名字
    private static final String DATABASE_NAME = "SAWARanking";
    private static RankingDatabase instance;

    //将MyDataBase设置为单例模式
    public static synchronized RankingDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), RankingDatabase.class, DATABASE_NAME)
                    //  .allowMainThreadQueries()//运行在主线成中进行耗时任务
                    .build();
        }
        return instance;
    }


}
