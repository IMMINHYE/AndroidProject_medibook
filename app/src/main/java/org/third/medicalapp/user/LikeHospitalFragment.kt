package org.third.medicalapp.user

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import org.third.medicalapp.databinding.FragmentLikeHospitalBinding
import org.third.medicalapp.hospital.adapter.HospitalAdapter
import org.third.medicalapp.hospital.model.HospitalLike
import org.third.medicalapp.hospital.model.HospitalList
import org.third.medicalapp.util.MyApplication
import org.third.medicalapp.util.MyApplication.Companion.email
import retrofit2.Call
import retrofit2.Response

class LikeHospitalFragment : Fragment() {

    private var _binding: FragmentLikeHospitalBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLikeHospitalBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val activity = activity as UserMainActivity
        val hospitalList = mutableListOf<Long>()
        // Firestore에서 사용자가 찜한 병원 출력
        MyApplication.db.collection("hospital_like")
            .whereEqualTo("like_email", email)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val item = document.toObject(HospitalLike::class.java)
                    var hospitalId = item.like_hospitalId
                    Log.d("aaaa", "${hospitalId.toString()}")
                    hospitalList.add(hospitalId!!)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("aaaa", "error... getting document..", exception)
                Toast.makeText(context, "서버 데이터 획득 실패", Toast.LENGTH_SHORT).show()
            }
        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("aaa", "${hospitalList.toList().size}")
            if (hospitalList.toList().size == 0) {
                binding.nonWrite.visibility = View.VISIBLE
                binding.hospitalRecyclerView.visibility = View.GONE
            } else {
                binding.nonWrite.visibility = View.GONE
                binding.hospitalRecyclerView.visibility = View.VISIBLE
                val networkService = (activity.applicationContext as MyApplication).hospitalServie
                // 전체리스트 호출
                val hospitalListCall = networkService.findById(hospitalList.toList())
                hospitalListCall.enqueue(object : retrofit2.Callback<HospitalList> {
                    override fun onResponse(
                        call: Call<HospitalList>,
                        response: Response<HospitalList>
                    ) {
                        if (response.isSuccessful) {
                            binding.hospitalRecyclerView.layoutManager =
                                LinearLayoutManager(context)
                            val hospital = response.body()?.hospitalList
                            val adapter = HospitalAdapter(activity, hospital)
                            binding.hospitalRecyclerView.adapter = adapter
                            binding.hospitalRecyclerView.addItemDecoration(
                                DividerItemDecoration(
                                    context,
                                    LinearLayoutManager.VERTICAL
                                )
                            )
                        }

                    }

                    override fun onFailure(call: Call<HospitalList>, t: Throwable) {
                        call.cancel()
                    }
                })
            }
        }, 500) // 1000ms = 1초
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}