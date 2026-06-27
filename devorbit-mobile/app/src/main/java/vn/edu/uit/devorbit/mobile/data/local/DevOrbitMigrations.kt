package vn.edu.uit.devorbit.mobile.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DevOrbitMigrations {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // No-op: schema unchanged between v1 and v2
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // No-op: schema unchanged between v2 and v3
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `daily_activity` (`date` TEXT NOT NULL, `reposViewed` INTEGER NOT NULL DEFAULT 0, `tasksCompleted` INTEGER NOT NULL DEFAULT 0, `tasksTotal` INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(`date`))")
            db.execSQL("CREATE TABLE IF NOT EXISTS `semester_courses` (`courseId` INTEGER NOT NULL, `addedAt` INTEGER NOT NULL, PRIMARY KEY(`courseId`))")
        }
    }

    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `daily_activity_new` (`studentCode` TEXT NOT NULL, `date` TEXT NOT NULL, `reposViewed` INTEGER NOT NULL DEFAULT 0, `tasksCompleted` INTEGER NOT NULL DEFAULT 0, `tasksTotal` INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(`studentCode`, `date`))")
            db.execSQL("INSERT INTO daily_activity_new (studentCode, date, reposViewed, tasksCompleted, tasksTotal) SELECT '', date, reposViewed, tasksCompleted, tasksTotal FROM daily_activity")
            db.execSQL("DROP TABLE daily_activity")
            db.execSQL("ALTER TABLE daily_activity_new RENAME TO daily_activity")
        }
    }

    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `tech_stacks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL)")
        }
    }

    val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `daily_activity` ADD COLUMN `studyMinutes` INTEGER NOT NULL DEFAULT 0")
        }
    }

    val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `courses` ADD COLUMN `semester` INTEGER")
            db.execSQL("ALTER TABLE `courses` ADD COLUMN `loaiMonHoc` TEXT")
        }
    }

    val MIGRATION_8_9 = object : Migration(8, 9) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `tasks` ADD COLUMN `recurrence` TEXT DEFAULT NULL")
            db.execSQL("ALTER TABLE `tasks` ADD COLUMN `recurrenceDaysOfWeek` INTEGER DEFAULT NULL")
            db.execSQL("ALTER TABLE `tasks` ADD COLUMN `recurrenceStartDate` INTEGER DEFAULT NULL")
            db.execSQL("ALTER TABLE `tasks` ADD COLUMN `recurrenceEndDate` INTEGER DEFAULT NULL")
        }
    }

    val MIGRATION_9_10 = object : Migration(9, 10) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("DROP TABLE IF EXISTS tasks")
        }
    }

    val ALL_MIGRATIONS = arrayOf(
        MIGRATION_1_2,
        MIGRATION_2_3,
        MIGRATION_3_4,
        MIGRATION_4_5,
        MIGRATION_5_6,
        MIGRATION_6_7,
        MIGRATION_7_8,
        MIGRATION_8_9,
        MIGRATION_9_10
    )
}
