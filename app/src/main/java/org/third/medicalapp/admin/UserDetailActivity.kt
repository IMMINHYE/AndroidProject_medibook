package org.third.medicalapp.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import org.third.medicalapp.R
import org.third.medicalapp.community.CommunityData
import org.third.medicalapp.community.MyAdapter
import org.third.medicalapp.databinding.ActivityUserDetailBinding
import org.third.medicalapp.util.MyApplication

class UserDetailActivity : AppCompatActivity() {
    lateinit var binding:ActivityUserDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences = getSharedPreferences("User", MODE_PRIVATE)
        val editor=sharedPreferences.edit()

        // 사용자 정보 설정
        binding.userNameText.text=intent.getStringExtra("userName")
        binding.nickNameText.text=intent.getStringExtra("nickName")
        binding.phoneNumber.text=intent.getStringExtra("phoneNumber")
        binding.regiDate.text=intent.getStringExtra("regiDate")

        //네비게이션 및 drawer 설정
        setSupportActionBar(binding.appBarMain.toolbar)
        val storageRef = MyApplication.storage.reference.child("images/profile/${intent.getStringExtra("userName")}.jpg")

        // 프로필 이미지 로드(파일 존재 여부 확인)
        storageRef.metadata.addOnSuccessListener { metadata ->
            storageRef.downloadUrl.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Glide.with(this)
                        .load(task.result)
                        .into(binding.profilePicture)
                }
            }
        }.addOnFailureListener {exception->
            binding.profilePicture.setImageResource(R.drawable.basic_profile)
        }
        val sharedPref = getSharedPreferences("User", MODE_PRIVATE)

        // 사용자가 작성한 커뮤니티 게시물 조회
        MyApplication.db.collection("community")
            .whereEqualTo("email",intent.getStringExtra("userName"))
            .get()
            .addOnSuccessListener { result ->
                val itemList = mutableListOf<CommunityData>()
                for (document in result) {
                    val item = document.toObject(CommunityData::class.java)
                    item.docId = document.id
                    itemList.add(item)
                }
                // RecyclerView 설정
                if(itemList.size!=0 && itemList!=null){
                    binding.myWriteRecycler.layoutManager = LinearLayoutManager(this)
                    binding.myWriteRecycler.adapter = MyAdapter(this, itemList)
                    binding.nonWrite.visibility= View.GONE
                    binding.myWriteRecycler.visibility= View.VISIBLE
                }else{
                    binding.nonWrite.visibility= View.VISIBLE
                    binding.myWriteRecycler.visibility= View.GONE
                }
            }
            .addOnFailureListener { exception ->
                Log.d("aaaa", "error... getting document..", exception)
                Toast.makeText(this, "서버 데이터 획득 실패", Toast.LENGTH_SHORT).show()
            }
    }
}