package org.third.medicalapp.community

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import org.third.medicalapp.R
import org.third.medicalapp.databinding.ActivityCommunityModifyBinding
import org.third.medicalapp.util.MyApplication

class CommunityModifyActivity : AppCompatActivity() {
    lateinit var binding: ActivityCommunityModifyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.title = "게시글 수정"

        // Intent로부터 전달된 데이터 받기
        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")

        binding.edTitle.setText(title)
        binding.edContent.setText(content)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_community_modify, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 저장 메뉴 아이템을 선택한 경우
        if (item.itemId == R.id.menu_add_save) {
            // 제목과 내용이 입력되었는지 확인
            if (binding.edTitle.text.isNotEmpty() && binding.edContent.text.isNotEmpty()) {
                val docId = intent.getStringExtra("docId")

                val newTitle = binding.edTitle.text.toString()
                val newContent = binding.edContent.text.toString()
                // stroe에 데이터 업데이트
                MyApplication.db.collection("community").document(docId!!).update("title", newTitle)
                MyApplication.db.collection("community").document(docId!!).update("content", newContent)
                Toast.makeText(
                    baseContext,
                    "게시물 업로드에 성공하였습니다.",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this, CommunityActivity::class.java)
                startActivity(intent)

            } else {
                Toast.makeText(
                    baseContext,
                    "게시물 업로드에 실패하였습니다. 필수 항목을 모두 작성해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}