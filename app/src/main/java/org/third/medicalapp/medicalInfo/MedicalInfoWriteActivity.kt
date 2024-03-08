package org.third.medicalapp.medicalInfo

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import org.third.medicalapp.R
import org.third.medicalapp.databinding.ActivityMedicalInfoWriteBinding

class MedicalInfoWriteActivity : AppCompatActivity() {
    lateinit var binding: ActivityMedicalInfoWriteBinding
    lateinit var db: SQLiteDatabase
    val tableName = "mediInfo_db"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicalInfoWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.title = "질병/약품 관련 사이트 추가"

        db = MedicalInfoDBHelper(this).writableDatabase

        binding.btnClose.setOnClickListener {
            finish()
        }
    }

    // 옵션 메뉴 생성
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // 옵션 메뉴 항목 선택 시 동작 정의
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save_add -> {

                // 입력한 정보를 데이터베이스에 저장하고 액티비티 종료
                val values = ContentValues()
                val siteName = binding.edNameWrite.text.toString()
                val siteUrl = binding.edUrlWrite.text.toString()
                val siteIntro = binding.edIntroWrite.text.toString()
                values.put("siteName", siteName)
                values.put("siteUrl", siteUrl)
                values.put("siteIntro", siteIntro)
                db.insert(tableName, null, values)

                binding.edNameWrite.text = null
                binding.edUrlWrite.text = null
                binding.edIntroWrite.text = null
                finish()
                true
            }

            else -> true
        }
        return true
    }
}