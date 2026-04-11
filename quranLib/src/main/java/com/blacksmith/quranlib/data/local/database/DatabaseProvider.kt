package com.blacksmith.quranlib.data.local.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase

object DatabaseProvider {
    fun openDatabase(context: Context, dbName: String): SQLiteDatabase {
        val dbFile = context.getDatabasePath(dbName)
        return SQLiteDatabase.openDatabase(
            dbFile.path,
            null,
            SQLiteDatabase.OPEN_READONLY
        )
    }
}