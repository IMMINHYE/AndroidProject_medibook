package org.third.medicalapp.community

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.third.medicalapp.MainActivity
import org.third.medicalapp.R
import org.third.medicalapp.databinding.ActivityCommunityDetailBinding
import org.third.medicalapp.util.MyApplication
import org.third.medicalapp.util.dateToString
import org.third.medicalapp.util.updateLikeCount
import java.util.Date

class CommunityDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityCommunityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.title = "커뮤니티 "

        // Intent로부터 전달된 데이터 받기
        val docId = intent.getStringExtra("docId")
        val email = intent.getStringExtra("email")
        val nick = intent.getStringExtra("nick")
        val date = intent.getStringExtra("date")
        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")
        var likeCount = intent.getIntExtra("likeCount", 0)

        // 데이터를 화면에 바인딩
        binding.apply {
            tvTitle.text = title
            tvContent.text = content
            tvWriter.text = nick
            tvDate.text = date

            // 좋아요 상태에 따라 이미지 설정
            CoroutineScope(Dispatchers.Main).launch {
                val isLiked = org.third.medicalapp.util.isSavedLike(docId, MyApplication.email)
                if (isLiked == false) {
                    imageLike.setImageResource(R.drawable.like)
                } else {
                    imageLike.setImageResource(R.drawable.like_full)
                }
            }

            MyApplication.db.collection("community").document(docId!!).get()
                .addOnSuccessListener { document ->
                    val likeCount = document.getLong("likeCount")
                    // 좋아요 텍스트 설정
                    likeCountView.text = likeCount.toString()
                }
        }

        val imgRef = MyApplication.storage.reference.child("images/$docId.jpg")
        imgRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Glide.with(this@CommunityDetailActivity)
                    .load(task.result)
                    .into(binding.imageView)
            }
        }

        // 좋아요 이미지를 클릭했을 때 이벤트 처리
        binding.imageLike.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val isLiked = org.third.medicalapp.util.isSavedLike(docId, MyApplication.email)
                // 여기에서 isLiked를 사용하여 다음 작업 수행
                if (isLiked == false) {
                    // 좋아요 버튼을 누르면
                    org.third.medicalapp.util.saveLikeStore(docId, MyApplication.email)
                    updateLikeCount(docId, 1)
                    binding.imageLike.setImageResource(R.drawable.like_full)

                    val currentText = binding.likeCountView.text.toString()
                    val currentLikeCount = if (currentText.isEmpty()) 0 else currentText.toInt()
                    val updatedLikeCount = currentLikeCount + 1
                    binding.likeCountView.text = updatedLikeCount.toString()
                } else {
                    // 좋아요 취소 버튼을 누르면
                    org.third.medicalapp.util.deleteLike(docId, MyApplication.email)
                    updateLikeCount(docId, -1)
                    binding.imageLike.setImageResource(R.drawable.like)

                    val currentText = binding.likeCountView.text.toString()
                    val currentLikeCount = if (currentText.isEmpty()) 0 else currentText.toInt()
                    val updatedLikeCount = currentLikeCount - 1
                    binding.likeCountView.text = updatedLikeCount.toString()
                }
            }
        }


        binding.icoSend.setOnClickListener {
            // 이미지가 선택되었고 제목이 입력되었는지 확인
            if (binding.edComment.text.isNotEmpty()) {
                // stroe에 데이터 저장
                saveStore()
                binding.edComment.setText("")
                binding.edComment.clearFocus()
                // 키보드 숨기기
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.icoSend.windowToken, 0)
            } else {
                Toast.makeText(
                    baseContext,
                    "댓글 업로드에 실패하였습니다. 필수 항목을 모두 작성해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            Handler(Looper.getMainLooper()).postDelayed({
                makeCommentRecyclerVIew(docId)
            }, 1500)


        }

        // 게시글 수정
        binding.icoModify.setOnClickListener {
            if (MyApplication.email == email) {
                val intent = Intent(this, CommunityModifyActivity::class.java)
                intent.putExtra("docId", docId)
                intent.putExtra("title", title)
                intent.putExtra("content", content)
                this.startActivity(intent)
            }
        }

        binding.communityDelete.setOnClickListener {
            if (MyApplication.email == email) {
                // fire store에 저장되어 있는 커뮤니티 게시글 삭제
                MyApplication.db.collection("community").document(docId!!).delete()
                    .addOnSuccessListener {
                        Log.d("community_db delete success", "데이터 삭제에 성공하였습니다.")
                    }
                    .addOnFailureListener { e ->
                        Log.d("community delete failure", "데이터 삭제에 실패하였습니다.")
                    }
                // fire store에 저장되어 있는 해당 게시글의 댓글 삭제
                MyApplication.db.collection("comment").whereEqualTo("docId", docId).get()
                    .addOnSuccessListener { documents ->
                        // documents에는 쿼리 결과에 해당하는 문서들이 포함됩니다.
                        for (document in documents) {
                            // 각 문서를 삭제합니다.
                            MyApplication.db.collection("comment").document(document.id).delete()
                                .addOnSuccessListener {
                                    Log.d("comment_db delete success", "데이터 삭제에 성공하였습니다.")
                                }
                                .addOnFailureListener { e ->
                                    Log.d("comment_db delete failure", "데이터 삭제에 실패하였습니다.")
                                }
                        }
                    }
            } else {
                Toast.makeText(baseContext, "작성자만 삭제 가능합니다.", Toast.LENGTH_SHORT).show()
            }

            // fire store에 저장되어 있는 해당 게시글의 좋아요 정보 삭제
            MyApplication.db.collection("like").whereEqualTo("docId", docId).get()
                .addOnSuccessListener { documents ->
                    // documents에는 쿼리 결과에 해당하는 문서들이 포함됩니다.
                    for (document in documents) {
                        // 각 문서를 삭제합니다.
                        MyApplication.db.collection("like").document(document.id).delete()
                            .addOnSuccessListener {
                                Log.d("like_db delete success", "데이터 삭제에 성공하였습니다.")
                            }
                            .addOnFailureListener { e ->
                                Log.d("like_db delete failure", "데이터 삭제에 실패하였습니다.")
                            }
                    }
                }

            var intent = Intent(this, CommunityActivity::class.java)
            startActivity(intent)
            Toast.makeText(baseContext, "게시글이 삭제되었습니다", Toast.LENGTH_SHORT).show()
        }

        makeCommentRecyclerVIew(docId)
        getCommentCount(docId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 저장 메뉴 아이템을 선택한 경우
        if (item.itemId == R.id.menu_main) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    // 댓글 데이터를 Firestore에 저장하는 함수
    fun saveStore() {
        val sharedPref = getSharedPreferences("User", MODE_PRIVATE)

        val data = mapOf(
            "docId" to intent.getStringExtra("docId"),
            "email" to MyApplication.email,
            "nick" to sharedPref.getString("nickName", "-"),
            "comment" to binding.edComment.text.toString(),
            "date" to dateToString(Date()),
            "isLiked" to false
        )

        MyApplication.db.collection("comment")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "댓글이 추가되었습니다.", Toast.LENGTH_SHORT).show()

                // 댓글이 성공적으로 추가되면 RecyclerView를 새로 고침
                makeCommentRecyclerVIew(intent.getStringExtra("docId"))
                getCommentCount(intent.getStringExtra("docId"))

                binding.edComment.setText("")
                binding.edComment.clearFocus()
            }
            .addOnFailureListener { exception ->
                // 데이터 저장 실패 시 메시지 표시
                Log.d("saveStore", "Error adding document", exception)
                Toast.makeText(this, "댓글 데이터 저장 실패", Toast.LENGTH_SHORT).show()
            }
    }

    // CommentRecyclerView를 생성하고 데이터를 로드하는 함수
    fun makeCommentRecyclerVIew(docId: String?) {
        // Firestore에서 커뮤니티 게시물 데이터 가져오기
        MyApplication.db.collection("comment").whereEqualTo("docId", docId)
            .get()
            .addOnSuccessListener { result ->
                val itemList = mutableListOf<CommentData>()
                // 가져온 데이터를 CommentData 객체로 변환하여 itemList에 추가
                for (document in result) {
                    val item = document.toObject(CommentData::class.java)
                    item.commentId = document.id
                    itemList.add(item)
                }

                // RecyclerView 설정
                binding.commentRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.commentRecyclerView.adapter = CommentAdapter(this, itemList)

                // 댓글 목록이 변경될 때마다 RecyclerView의 높이를 다시 계산하여 설정
                binding.commentRecyclerView.viewTreeObserver.addOnGlobalLayoutListener {
                    val height = binding.commentRecyclerView.computeVerticalScrollRange()
                    binding.commentRecyclerView.layoutParams.height = height
                    binding.commentRecyclerView.requestLayout()
                }
            }
            .addOnFailureListener { exception ->
                // 데이터 가져오기 실패 시 로그와 메시지 출력
                Log.d("aaaa", "error... getting document..", exception)
                Toast.makeText(this, "댓글 데이터 획득 실패", Toast.LENGTH_SHORT).show()
            }
    }
    // 댓글 수 계산 함수
    fun getCommentCount(docId: String?) {
        if (docId != null) {
            MyApplication.db.collection("comment").whereEqualTo("docId", docId)
                .get()
                .addOnSuccessListener { result ->
                    val commentCount = result.size() // 댓글 수 계산
                    updateCommentCountUI(commentCount) // UI에 댓글 수 업데이트
                }
                .addOnFailureListener { exception ->
                    Log.d("getCommentCount", "Failed to get comment count: $exception")
                }
        }
    }

    fun updateCommentCountUI(commentCount: Int) {
        binding.commentCount.text = commentCount.toString() // commentCount 텍스트뷰에 값을 설정
    }

}