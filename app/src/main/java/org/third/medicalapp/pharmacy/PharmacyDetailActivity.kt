package org.third.medicalapp.pharmacy

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.third.medicalapp.R
import org.third.medicalapp.MainActivity
import org.third.medicalapp.databinding.ActivityPharmacyDetailBinding
import org.third.medicalapp.pharmacy.apater.PharmacyReviewAdapter
import org.third.medicalapp.pharmacy.model.Pharmacy
import org.third.medicalapp.pharmacy.model.PharmacyLike
import org.third.medicalapp.pharmacy.model.PharmacyReview
import org.third.medicalapp.util.MyApplication
import org.third.medicalapp.util.dateToString
import retrofit2.Call
import retrofit2.Response
import java.util.Date
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PharmacyDetailActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var binding: ActivityPharmacyDetailBinding
    private var pharmacyId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPharmacyDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.title = "약국 정보"

        MapView()

        val id = intent.getLongExtra("id", 0)

        val networkService = (application as MyApplication).pharmacyService
        val pharmacyListCall = networkService.doGetPharmacyId(id)
        pharmacyListCall.enqueue(object : retrofit2.Callback<Pharmacy> {
            override fun onResponse(call: Call<Pharmacy>, response: Response<Pharmacy>) {
                val pharmacy = response.body()
                pharmacyId = intent.getLongExtra("id", 0)

                Log.d("pharmacyId","${pharmacyId}")

                binding.tvPharmacyName.text = intent.getStringExtra("pharmacy")
                binding.tvPharmacyAddress.text = intent.getStringExtra("addr")
                binding.tvPhoneNumber.text = intent.getStringExtra("tel")

                pharmacyId?.let { id ->
                    makePharmacyReviewRecyclerView(id)
                }
            }

            override fun onFailure(call: Call<Pharmacy>, t: Throwable) {
                call.cancel()
            }

        })

        binding.btnReview.setOnClickListener {
            // 리뷰로 스크롤 이동
            binding.pharmacyDetail.post {
                binding.pharmacyDetail.smoothScrollTo(0, binding.reviewRecyclerView.top)
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
            pharmacyId?.let { id ->
                makePharmacyReviewRecyclerView(id)
            }
        }

        // 좋아요 상태에 따라 이미지 설정
        CoroutineScope(Dispatchers.Main).launch {
            pharmacyId?.let { id ->
                val isLiked = isSavedPharmacyLike(id, MyApplication.email)
                Log.d("aaa","${isLiked}")
                if (isLiked == false) {
                    binding.imageLike.setImageResource(R.drawable.like)
                } else {
                    binding.imageLike.setImageResource(R.drawable.like_full)
                }
            }
        }

        // 좋아요 이미지를 클릭했을 때 이벤트 처리
        binding.imageLike.setOnClickListener {
            Log.d("aaa","click")

            CoroutineScope(Dispatchers.Main).launch {
                pharmacyId?.let { id ->
                    val isLiked = isSavedPharmacyLike(id, MyApplication.email)
                    Log.d("isLiked","${isLiked}")
                    // 여기에서 isLiked를 사용하여 다음 작업 수행
                    if (isLiked == false) {
                        // 좋아요 버튼을 누르면
                        savePharmacyLikeStore()
                        binding.imageLike.setImageResource(R.drawable.like_full)
                    } else {
                        // 좋아요 취소 버튼을 누르면
                        deletePharmacyLike(id, MyApplication.email)
                        binding.imageLike.setImageResource(R.drawable.like)
                    }
                }
            }
        }
    }


    private fun MapView() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment_PH) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment_PH, it).commit()
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

        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.title = "약국 정보"
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

    // 리뷰 데이터를 Firestore에 저장하는 함수
    fun saveStore() {
        val sharedPref = getSharedPreferences("User", MODE_PRIVATE)

        val data = mapOf(
            "pharmacyId" to pharmacyId,
            "email" to MyApplication.email,
            "nick" to sharedPref.getString("nickName", "-"),
            "review" to binding.edReview.text.toString(),
            "date" to dateToString(Date()),
            "isLiked" to false
        )

        MyApplication.db.collection("PharmacyReview")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "병원 후기가 추가되었습니다.", Toast.LENGTH_SHORT).show()

                // 댓글이 성공적으로 추가되면 RecyclerView를 새로 고침
                pharmacyId?.let { id ->
                    makePharmacyReviewRecyclerView(id)
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
    fun makePharmacyReviewRecyclerView(id: Long?) {

        // Firestore에서 커뮤니티 게시물 데이터 가져오기
        MyApplication.db.collection("PharmacyReview").whereEqualTo("pharmacyId", id)
            .get()
            .addOnSuccessListener { result ->
                Log.d("-------Pharmacy Review", "$id")
                val itemList = mutableListOf<PharmacyReview>()
                // 가져온 데이터를 CommentData 객체로 변환하여 itemList에 추가
                for (document in result) {
                    val item = document.toObject(PharmacyReview::class.java)
                    item.pharmacyReviewId = document.id
                    itemList.add(item)
                }

                // RecyclerView 설정
                binding.reviewRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.reviewRecyclerView.adapter = PharmacyReviewAdapter(this, itemList)

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
    fun savePharmacyLikeStore() {
        val data = mapOf(
            "like_pharmacyId" to pharmacyId,
            "like_email" to MyApplication.email,
            "isLiked" to true
        )

        Log.d("함수호출","aaa")
        var likeDataList = mutableListOf<PharmacyLike>()
        MyApplication.db.collection("pharmacy_like")
            .add(data)
            .addOnSuccessListener {
                Log.d("pharmacy_like save success", "데이터 업로드에 성공하였습니다.")
                MyApplication.db.collection("pharmacy_like")
                    .get()
                    .addOnSuccessListener { result ->

                        for (document in result) {
                            val pharmacyLike = document.toObject(PharmacyLike::class.java)
                            pharmacyLike.likeId = document.id
                            likeDataList.add(pharmacyLike)
                        }
                        Log.d("pharmacy_like save success", "데이터 업로드에 성공하였습니다.")
                    }
                    .addOnFailureListener {
                        Log.d("pharmacy_like save failure", "데이터 업로드에 실패하였습니다.")
                    }

            }
            .addOnFailureListener {
                Log.d("like_db save failure", "데이터 업로드에 실패하였습니다.")

            }
    }

    fun deletePharmacyLike(pharmacy_likeId: Long?, email: String?) {
        MyApplication.db.collection("pharmacy_like").whereEqualTo("pharmacy_likeId", pharmacy_likeId)
            .whereEqualTo("like_email", email)
            .get()
            .addOnSuccessListener { documents ->
                // documents에는 쿼리 결과에 해당하는 문서들이 포함됩니다.
                for (document in documents) {
                    // 각 문서를 삭제합니다.
                    MyApplication.db.collection("pharmacy_like").document(document.id).delete()
                        .addOnSuccessListener {
                            Log.d("pharmacy_like delete success", "데이터 삭제에 성공하였습니다.")
                        }
                        .addOnFailureListener { e ->
                            Log.d("pharmacy_like delete failure", "데이터 삭제에 실패하였습니다.")
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("pharmacy_like delete failure", "데이터 삭제에 실패하였습니다.")
            }
    }

    suspend fun isSavedPharmacyLike(like_pharmacyId: Long?, email: String?): Boolean {
        Log.d("like_pharmacyId","$like_pharmacyId")
        var pharmacyLike: PharmacyLike? = null
        // 비동기 작업
        return suspendCoroutine { continuation ->
            MyApplication.db.collection("pharmacy_like")
                .whereEqualTo("like_pharmacyId", like_pharmacyId)
                .whereEqualTo("like_email", email)
                .get()
                .addOnSuccessListener { result ->
                    var flag = false
                    for (document in result) {
                        pharmacyLike = document.toObject(PharmacyLike::class.java)
                        pharmacyLike?.likeId = document.id
                        pharmacyLike?.isLiked = true

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