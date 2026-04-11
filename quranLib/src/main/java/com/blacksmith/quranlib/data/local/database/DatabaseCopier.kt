package com.blacksmith.quranlib.data.local.database

import android.content.Context
import java.io.FileOutputStream

object DatabaseCopier {
    fun copyDatabase(context: Context, dbName: String) {
        val dbPath = context.getDatabasePath(dbName)

        if (dbPath.exists()) return

        dbPath.parentFile?.mkdirs()

        context.assets.open(dbName).use { input ->
            FileOutputStream(dbPath).use { output ->
                input.copyTo(output)
            }
        }
    }
}