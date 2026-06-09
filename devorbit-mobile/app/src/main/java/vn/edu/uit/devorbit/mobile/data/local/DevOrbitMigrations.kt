package vn.edu.uit.devorbit.mobile.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room database migrations for DevOrbitDatabase.
 *
 * Migration pattern:
 * - Each Migration(fromVersion, toVersion) defines SQL to upgrade the schema.
 * - Always test migrations on a device/emulator before shipping.
 * - Exported schemas live in app/schemas/ for verification.
 */
object DevOrbitMigrations {

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE courses ADD COLUMN repoCount INTEGER NOT NULL DEFAULT 0")
        }
    }

    /** List of all migrations, passed to Room.databaseBuilder().addMigrations(...) */
    val ALL_MIGRATIONS = arrayOf(
        MIGRATION_3_4
    )
}
