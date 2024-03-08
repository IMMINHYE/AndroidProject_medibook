package org.third.medicalapp.community

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import org.third.medicalapp.MainActivity
import org.third.medicalapp.R
import org.third.medicalapp.databinding.ActivityCommunityBinding
import org.third.medicalapp.sign.LoginActivity
import org.third.medicalapp.util.MyApplication
import org.third.medicalapp.util.myCheckPermission

class CommunityActivity : AppCompatActivity() {
    lateinit var binding: ActivityCommunityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.title = "커뮤니티"

        // 앱 권한을 확인하는 함수 호출
        myCheckPermission(this)

        // FAB(플로팅 액션 버튼)을 클릭했을 때 이벤트 처리
        binding.addFab.setOnClickListener {
            // 사용자가 로그인되어 있는지 확인
            if (MyApplication.checkAuth()) {
                // 사용자가 로그인되어 있으면 게시물 작성 액티비티로 이동
                val intent = Intent(this, CommunityWriteActivity::class.java)
                startActivity(intent)
            } else {
                // 사용자가 로그인되어 있지 않으면 로그인 액티비티로 이동
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
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

    override fun onStart() {
        super.onStart()
        // 사용자가 로그인되어 있는지 확인하고 화면 구성 변경
        // 로그인 되었을 때
        if (MyApplication.checkAuth()) {
            binding.logoutTextView.visibility = View.GONE
            binding.mainRecyclerView.visibility = View.VISIBLE
            // RecyclerView에 데이터 로드
            makeRecyclerVIew()
            // 로그인 되어있지 않을 때
        } else {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            binding.mainRecyclerView.visibility = View.GONE
        }
    }

    // RecyclerView를 생성하고 데이터를 로드하는 함수
    fun makeRecyclerVIew() {
        // Firestore에서 커뮤니티 게시물 데이터 가져오기

        MyApplication.db.collection("community")
            .get()
            .addOnSuccessListener { result ->
                val itemList = mutableListOf<CommunityData>()
                // 가져온 데이터를 CommunityData 객체로 변환하여 itemList에 추가
                for (document in result) {
                    val item = document.toObject(CommunityData::class.java)
                    item.docId = document.id
                    itemList.add(item)
                }

                // RecyclerView 설정
                binding.mainRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.mainRecyclerView.adapter = MyAdapter(this, itemList)
            }
            .addOnFailureListener { exception ->
                // 데이터 가져오기 실패 시 로그와 메시지 출력
                Log.d("aaaa", "error... getting document..", exception)
                Toast.makeText(this, "서버 데이터 획득 실패", Toast.LENGTH_SHORT).show()
            }

    }
}