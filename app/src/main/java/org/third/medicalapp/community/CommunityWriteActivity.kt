package org.third.medicalapp.community

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import org.third.medicalapp.R
import org.third.medicalapp.databinding.ActivityCommunityWriteBinding
import org.third.medicalapp.util.MyApplication
import org.third.medicalapp.util.MyApplication.Companion.email
import org.third.medicalapp.util.dateToString
import java.io.File
import java.util.Date

class CommunityWriteActivity : AppCompatActivity() {
    lateinit var binding : ActivityCommunityWriteBinding
    lateinit var filePath : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.title = "게시글 작성"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_community_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // 액티비티 결과를 처리하기 위한 람다식
    val requestLancher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        // 결과가 성공적으로 돌아왔을 때
        if(it.resultCode == android.app.Activity.RESULT_OK) {
            // Glide를 사용하여 이미지를 로드하고 ImageView에 표시
            Glide
                .with(getApplicationContext())
                .load(it.data?.data)
                .override(250, 200)
                .centerCrop()
                .into(binding.addImageView)

            // 선택된 이미지의 파일 경로를 가져옵니다.
            val cursor = contentResolver.query(it.data?.data as Uri,
                arrayOf<String>(MediaStore.Images.Media.DATA), null, null, null)
            cursor?.moveToFirst().let {
                filePath = cursor?.getString(0) as String
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 갤러리 메뉴 아이템을 선택한 경우
        if(item.itemId === R.id.menu_add_gallery) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
            requestLancher.launch(intent)

            // 저장 메뉴 아이템을 선택한 경우
        } else if(item.itemId === R.id.menu_add_save) {
            // 제목과 내용이 입력되었는지 확인
            if(binding.edTitle.text.isNotEmpty() && binding.edContent.text.isNotEmpty()) {
                // stroe에 데이터 저장
                saveStore()
            } else {
                Toast.makeText(baseContext,"게시물 업로드에 실패하였습니다. 필수 항목을 모두 작성해주세요.",Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 데이터를 Firestore에 저장하는 함수
    fun saveStore() {
        val sharedPref = getSharedPreferences("User", MODE_PRIVATE)

        val data = mapOf(
            "email" to email.toString(),
            "nick" to sharedPref.getString("nickName", "-"),
            "title" to binding.edTitle.text.toString(),
            "content" to binding.edContent.text.toString(),
            "date" to dateToString(Date()),
            "likeCount" to 0.toLong()
        )

        MyApplication.db.collection("community")
            .add(data)
            .addOnSuccessListener {
                uploadImage(it.id)
            }
            .addOnFailureListener {
                Log.d("db_save_failure", "데이터 업로드에 실패하였습니다.", it)
            }
    }

    // 이미지를 Firebase Storage에 업로드하는 함수
    fun uploadImage(docId : String) {
        val storage = MyApplication.storage
        val storageRef = storage.reference
        val imgRef = storageRef.child("images/${docId}.jpg")
        val file = Uri.fromFile(File(filePath))
        imgRef.putFile(file)    // 파일 업로드
            .addOnSuccessListener {
                Toast.makeText(this, "save ok...", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Log.d("img_save_failure", "이미지 업로드에 실패하였습니다.", it)
            }
    }
}