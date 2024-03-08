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
import org.third.medicalapp.databinding.FragmentLikePharmacyBinding
import org.third.medicalapp.pharmacy.apater.PharmacyAdapter
import org.third.medicalapp.pharmacy.model.PharmacyLike
import org.third.medicalapp.pharmacy.model.PharmacyList
import org.third.medicalapp.util.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LikePharmacyFragment : Fragment() {

    private var _binding: FragmentLikePharmacyBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLikePharmacyBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val activity = activity as UserMainActivity
        val pharmacyList = mutableListOf<Long>()
        // Firestore에서 사용자가 찜한 약국 출력
        MyApplication.db.collection("pharmacy_like")
            .whereEqualTo("like_email", MyApplication.email)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val item = document.toObject(PharmacyLike::class.java)
                    var pharmacyId = item.like_pharmacyId
                    Log.d("aaaa1","${pharmacyId}")
                    pharmacyList.add(pharmacyId!!)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("aaaa", "error... getting document..", exception)
                Toast.makeText(context, "서버 데이터 획득 실패", Toast.LENGTH_SHORT).show()
            }
        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("aaa", "${pharmacyList.toList().size}")
            if (pharmacyList.toList().size == 0) {
                binding.nonWrite.visibility = View.VISIBLE
                binding.pharmacyRecyclerView.visibility = View.GONE
            } else {
                binding.nonWrite.visibility = View.GONE
                binding.pharmacyRecyclerView.visibility = View.VISIBLE
                val networkService = (activity.applicationContext as MyApplication).pharmacyService
                // 전체리스트 호출
                val pharmacyListCall = networkService.findById(pharmacyList.toList())
                pharmacyListCall.enqueue(object : Callback<PharmacyList> {
                    override fun onResponse(
                        call: Call<PharmacyList>,
                        response: Response<PharmacyList>
                    ) {
                        if (response.isSuccessful) {
                            binding.pharmacyRecyclerView.layoutManager =
                                LinearLayoutManager(context)
                            val pharmacy = response.body()?.pharmacyList
                            val adapter = PharmacyAdapter(activity, pharmacy)
                            binding.pharmacyRecyclerView.adapter = adapter
                            binding.pharmacyRecyclerView.addItemDecoration(
                                DividerItemDecoration(
                                    context,
                                    LinearLayoutManager.VERTICAL
                                )
                            )
                        }

                    }

                    override fun onFailure(call: Call<PharmacyList>, t: Throwable) {
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