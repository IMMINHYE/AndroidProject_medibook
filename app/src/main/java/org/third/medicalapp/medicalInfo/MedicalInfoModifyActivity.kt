package org.third.medicalapp.medicalInfo

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import org.third.medicalapp.databinding.ActivityMedicalInfoModifyBinding

class MedicalInfoModifyActivity : AppCompatActivity() {
    lateinit var db : SQLiteDatabase
    val tableName = "mediInfo_db"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMedicalInfoModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = MedicalInfoDBHelper(this).writableDatabase

        val id = intent.getLongExtra("id",-1)
        val siteName = intent.getStringExtra("siteName")
        val siteUrl = intent.getStringExtra("siteUrl")
        val siteIntro = intent.getStringExtra("siteIntro")
        val icoUrl = intent.getIntExtra("icoUrl",0)

        Log.d("icoUrl","$icoUrl")

        icoUrl?.let {
            Glide.with(this)
                .load(icoUrl)
                .into(binding.SiteImageWrite)
        }

        binding.edNameWrite.setText(siteName)
        binding.edUrlWrite.setText(siteUrl)
        binding.edIntroWrite.setText(siteIntro)

        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.btnModify.setOnClickListener {
            val newName = binding.edNameWrite.text.toString()
            val newUrl = binding.edUrlWrite.text.toString()
            val newIntro = binding.edIntroWrite.text.toString()

            // 데이터베이스 업데이트
            updateItem(id, newName, newUrl, newIntro)

            // 결과를 MedicalInfoActivity로 전달
            val intent = Intent()
            intent.putExtra("newName", newName)
            intent.putExtra("newUrl", newUrl)
            intent.putExtra("newIntro", newIntro)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        binding.btnDelete.setOnClickListener {
            // 데이터베이스에서 해당 항목 삭제
            deleteItem(id)

            // 결과를 MedicalInfoActivity로 전달
            val intent = Intent()
            intent.putExtra("deleteName", siteName)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun updateItem(id: Long, name: String, url: String, intro: String) {
        val values = ContentValues().apply {
            put("siteName", name)
            put("siteUrl", url)
            put("siteIntro", intro)
        }
        val selection = "id = ?"
        val selectionArgs = arrayOf(id.toString())
        db.update(tableName, values, selection, selectionArgs)
    }

    private fun deleteItem(id: Long) {
        val selection = "id = ?"
        val selectionArgs = arrayOf(id.toString())
        db.delete(tableName, selection, selectionArgs)
    }
}