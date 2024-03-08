package org.third.medicalapp.medicalInfo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MedicalInfoDBHelper(context : Context) : SQLiteOpenHelper(context, "mediInfo_db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val sql = """
            create table mediInfo_db(
            id integer primary key autoincrement,
            siteName not null,
            siteUrl not null,
            siteIntro not null)
        """.trimIndent()
        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}