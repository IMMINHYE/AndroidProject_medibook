package org.third.medicalapp.hospital

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.third.medicalapp.MainActivity
import org.third.medicalapp.R
import org.third.medicalapp.databinding.ActivityHospitalDetailBinding
import org.third.medicalapp.hospital.model.Hospital
import org.third.medicalapp.hospital.model.HospitalLike
import org.third.medicalapp.hospital.model.HospitalReview
import org.third.medicalapp.pharmacy.apater.HospitalReviewAdapter
import org.third.medicalapp.util.MyApplication
import org.third.medicalapp.util.dateToString
import retrofit2.Call
import retrofit2.Response
import java.util.Date
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class HospitalDetailActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var binding: ActivityHospitalDetailBinding
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private var hospitalId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHospitalDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.title = "병원 정보"

        MapView()

        val id = intent.getLongExtra("id", 0)
        val networkService = (application as MyApplication).hospitalServie
        val hospitalListCall = networkService.doGetHospitalId(id)
        hospitalListCall.enqueue(object : retrofit2.Callback<Hospital> {
            override fun onResponse(call: Call<Hospital>, response: Response<Hospital>) {
                val hospital = response.body()
                hospitalId = hospital?.id
                binding.tvHospitalName.text = "${hospital?.hname}"
                binding.tvHospitalCode.text = "${hospital?.hcode}"
                binding.tvHospitalAddress.text = "${hospital?.addr}"
                binding.tvHospitalDepart.text = "${hospital?.hcode}"
                binding.tvPhoneNumber.text = "${hospital?.tel}"
                Log.d("aaa","${hospitalId}")

                hospitalId?.let { id ->
                    makeHospitalReviewRecyclerView(id)
                }
            }

            override fun onFailure(call: Call<Hospital>, t: Throwable) {
                call.cancel()
            }

        })

        binding.btnReview.setOnClickListener {
            // 리뷰로 스크롤 이동
            binding.hospitalDetail.post {
                binding.hospitalDetail.smoothScrollTo(0, binding.reviewRecyclerView.top)
            }
        }

        val tel = intent.getStringExtra("tel")
        binding.btnCall.setOnClickListener { //버튼을 누르면 전화가 되게끔
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$tel")
            startActivity(intent)
        }

        binding.icoSend.setOnClickListener {
            // 이미지가 선택되었고 제목이 입력되었는지 확인
            if (binding.edReview.text.isNotEmpty()) {
                // stroe에 데이터 저장
                saveStore()
                binding.edReview.setText("")
                binding.edReview.clearFocus()
                // 키보드 숨기기
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.icoSend.windowToken, 0)
            } else {
                Toast.makeText(
                    baseContext,
                    "데이터 업로드에 실패하였습니다. 필수 항목을 모두 작성해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            hospitalId?.let { id ->
                makeHospitalReviewRecyclerView(id)
            }
        }


        // 찜 상태에 따라 이미지 설정
        CoroutineScope(Dispatchers.Main).launch {
            hospitalId?.let { id ->
                Log.d("aaa","${id}")
                val isLiked = isSavedHospitalLike(id, MyApplication.email)
                if (isLiked == false) {
                    binding.imageLike.setImageResource(R.drawable.like)
                } else {
                    binding.imageLike.setImageResource(R.drawable.like_full)
                }
            }
        }


        // 찜 이미지를 클릭했을 때 이벤트 처리
        binding.imageLike.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                hospitalId?.let { id ->
                    val isLiked = isSavedHospitalLike(id, MyApplication.email)
                    // 여기에서 isLiked를 사용하여 다음 작업 수행
                    if (isLiked == false) {
                        // 좋아요 버튼을 누르면
                        saveHospitalLikeStore()
                        binding.imageLike.setImageResource(R.drawable.like_full)
                    } else {
                        // 좋아요 취소 버튼을 누르면
                        deleteHospitalLike(id, MyApplication.email)
                        binding.imageLike.setImageResource(R.drawable.like)
                    }
                }
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

    private fun MapView() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        val x = intent.getDoubleExtra("x", 0.0)
        val y = intent.getDoubleExtra("y", 0.0)
        val marker = Marker()

        marker.position = LatLng(y, x)
        marker.map = naverMap

        val cameraUpdate = CameraUpdate.scrollTo(LatLng(y, x))
            .animate(CameraAnimation.None, 0)
        naverMap.moveCamera(cameraUpdate)
    }

    // 리뷰 데이터를 Firestore에 저장하는 함수
    fun saveStore() {
        val sharedPref = getSharedPreferences("User", MODE_PRIVATE)

        val data = mapOf(
            "hospitalId" to hospitalId,
            "email" to MyApplication.email,
            "nick" to sharedPref.getString("nickName", "-"),
            "review" to binding.edReview.text.toString(),
            "date" to dateToString(Date()),
            "isLiked" to false
        )

        MyApplication.db.collection("HospitalReview")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "병원 후기가 추가되었습니다.", Toast.LENGTH_SHORT).show()

                // 댓글이 성공적으로 추가되면 RecyclerView를 새로 고침
                hospitalId?.let { id ->
                    makeHospitalReviewRecyclerView(id)
                }

                binding.edReview.setText("")
                binding.edReview.clearFocus()
            }
            .addOnFailureListener { exception ->
                // 데이터 저장 실패 시 메시지 표시
                Log.d("saveStore", "Error adding document", exception)
                Toast.makeText(this, "데이터 저장 실패", Toast.LENGTH_SHORT).show()
            }
    }

    // ReviewRecyclerView를 생성하고 데이터를 로드하는 함수
    fun makeHospitalReviewRecyclerView(id: Long?) {

        Log.d("Hospital Review", "Hospital Review")

        // Firestore에서 커뮤니티 게시물 데이터 가져오기
        MyApplication.db.collection("HospitalReview").whereEqualTo("hospitalId", id)
            .get()
            .addOnSuccessListener { result ->
                Log.d("-------Hospital Review", "$id")
                val itemList = mutableListOf<HospitalReview>()
                // 가져온 데이터를 CommentData 객체로 변환하여 itemList에 추가
                for (document in result) {
                    val item = document.toObject(HospitalReview::class.java)
                    item.hospitalReviewId = document.id
                    itemList.add(item)
                }

                // RecyclerView 설정
                binding.reviewRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.reviewRecyclerView.adapter = HospitalReviewAdapter(this, itemList)

                // 댓글 목록이 변경될 때마다 RecyclerView의 높이를 다시 계산하여 설정
                binding.reviewRecyclerView.viewTreeObserver.addOnGlobalLayoutListener {
                    val height = binding.reviewRecyclerView.computeVerticalScrollRange()
                    binding.reviewRecyclerView.layoutParams.height = height
                    binding.reviewRecyclerView.requestLayout()
                }
            }
            .addOnFailureListener { exception ->
                // 데이터 가져오기 실패 시 로그와 메시지 출력
                Toast.makeText(this, "데이터 획득 실패", Toast.LENGTH_SHORT).show()
            }
    }

    // 찜 데이터를 Firestore에 저장하는 함수
    fun saveHospitalLikeStore() {
        val data = mapOf(
            "like_hospitalId" to hospitalId,
            "like_email" to MyApplication.email,
            "isLiked" to true
        )
        var likeDataList = mutableListOf<HospitalLike>()
        MyApplication.db.collection("hospital_like")
            .add(data)
            .addOnSuccessListener {
                Log.d("hospital_like save success", "데이터 업로드에 성공하였습니다.")
                MyApplication.db.collection("hospital_like")
                    .get()
                    .addOnSuccessListener { result ->

                        for (document in result) {
                            val hodpitalLike = document.toObject(HospitalLike::class.java)
                            hodpitalLike.likeId = document.id
                            likeDataList.add(hodpitalLike)
                        }
                        Log.d("hospital_like save success", "데이터 업로드에 성공하였습니다.")
                    }
                    .addOnFailureListener {
                        Log.d("hospital_like save failure", "데이터 업로드에 실패하였습니다.")
                    }

            }
            .addOnFailureListener {
                Log.d("like_db save failure", "데이터 업로드에 실패하였습니다.")

            }
    }

    fun deleteHospitalLike(like_hospitalId: Long?, email: String?) {
        MyApplication.db.collection("hospital_like").whereEqualTo("like_hospitalId", like_hospitalId)
        .whereEqualTo("like_email", email)
            .get()
            .addOnSuccessListener { documents ->
                // documents에는 쿼리 결과에 해당하는 문서들이 포함됩니다.
                for (document in documents) {
                    // 각 문서를 삭제합니다.
                    MyApplication.db.collection("hospital_like").document(document.id).delete()
                        .addOnSuccessListener {
                            Log.d("hospital_like delete success", "데이터 삭제에 성공하였습니다.")
                        }
                        .addOnFailureListener { e ->
                            Log.d("hospital_like delete failure", "데이터 삭제에 실패하였습니다.")
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("hospital_like delete failure", "데이터 삭제에 실패하였습니다.")
            }
    }

    suspend fun isSavedHospitalLike(like_hospitalId: Long?, email: String?): Boolean {
        var hodpitalLike: HospitalLike? = null
        // 비동기 작업
        return suspendCoroutine { continuation ->
            MyApplication.db.collection("hospital_like")
                .whereEqualTo("like_hospitalId", like_hospitalId)
                .whereEqualTo("like_email", email)
                .get()
                .addOnSuccessListener { result ->
                    var flag = false
                    for (document in result) {
                        hodpitalLike = document.toObject(HospitalLike::class.java)
                        hodpitalLike?.likeId = document.id
                        hodpitalLike?.isLiked = true

                        flag = true
                        break
                    }
                    continuation.resume(flag) // 결과 반환
                    Log.d("flag", "$flag")
                }
                .addOnFailureListener { exception ->
                    Log.d("isSavedLike", "Failed to get like data: $exception")
                    continuation.resume(false) // 실패 시 기본값 반환
                }
        }
    }
}