package lilcode.aop.p3.c04.bookreview

import androidx.room.Database
import androidx.room.RoomDatabase
import lilcode.aop.p3.c04.bookreview.dao.HistoryDao
import lilcode.aop.p3.c04.bookreview.dao.ReviewDao
import lilcode.aop.p3.c04.bookreview.model.History
import lilcode.aop.p3.c04.bookreview.model.Review

@Database(entities = [History::class, Review::class], version = 2)
abstract class AppDatabase : RoomDatabase(){
    abstract fun historyDao(): HistoryDao
    abstract fun reviewDao(): ReviewDao

}