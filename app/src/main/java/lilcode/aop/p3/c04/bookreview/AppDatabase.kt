package lilcode.aop.p3.c04.bookreview

import androidx.room.Database
import androidx.room.RoomDatabase
import lilcode.aop.p3.c04.bookreview.dao.HistoryDao
import lilcode.aop.p3.c04.bookreview.model.History

@Database(entities = [History::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun historyDao(): HistoryDao

}