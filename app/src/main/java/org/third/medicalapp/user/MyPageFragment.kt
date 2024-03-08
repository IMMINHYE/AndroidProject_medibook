package org.third.medicalapp.user

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import org.third.medicalapp.R
import org.third.medicalapp.databinding.FragmentMyPageBinding
import org.third.medicalapp.util.MyApplication
import org.third.medicalapp.util.MyApplication.Companion.auth
import org.third.medicalapp.util.MyApplication.Companion.email
import org.third.medicalapp.util.MyApplication.Companion.storage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyPageFragment : Fragment() {

    private var _binding: FragmentMyPageBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.myWrite.setOnClickListener{
            findNavController().navigate(R.id.nav_write)
        }
        binding.myHospital.setOnClickListener{
            findNavController().navigate(R.id.nav_hospital)
        }
        binding.myPhar.setOnClickListener{
            findNavController().navigate(R.id.nav_pharmacy)
        }

        binding.modiInfo.setOnClickListener{
            startActivity(Intent(context, ModifyInfoActivity::class.java))
        }
        binding.changePass.setOnClickListener {
            startActivity(Intent(context, ChangePassActivity::class.java))
        }
        binding.logoutView.setOnClickListener {
            auth.signOut()
            email = null
            (activity as UserMainActivity).finish()
        }
        binding.unregi.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("회원탈퇴")
            builder.setMessage("정말 탈퇴하시겠습니까?")
            builder.setPositiveButton("Yes") { dialog, which ->
                Log.d("aaaa", "확인")
                val networkService =
                    ((activity as UserMainActivity).applicationContext as MyApplication).netWorkService
                val delete = networkService.delete(email.toString())
                delete.enqueue(object : Callback<Boolean> {
                    override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                        if (response.body() == true) {
                            Log.d("aaa", "server 탈퇴성공")
                            val user = auth.currentUser
                            Log.d("aaaa","${user?.email}")
                            user?.delete()?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    auth.signOut()
                                    email = null
                                    Log.d("aaa", "탈퇴성공")
                                    (activity as UserMainActivity).finish()
                                } else {
                                    Log.d("aaa","${task.exception?.message}")
                                    Log.d("aaa", "탈퇴 실패")
                                }
                            }
                        }else{
                            Log.d("aaa","존재하지 않음")
                        }
                    }

                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                        Log.d("aaa", "연결실패")
                        call.cancel()
                    }

                })
            }
            builder.setNegativeButton("No") { dialog, which ->
                Log.d("aaa", "취소")
            }
            val dialog = builder.create()
            dialog.show()
        }
        return root
    }
    // on create 종료
    override fun onStart() {
        super.onStart()

        val activity = activity as UserMainActivity
        //회원정보 값 받아오기
        val sharedPref = activity.getSharedPreferences("User", AppCompatActivity.MODE_PRIVATE)
        binding.userNameText.text = email.toString()
        binding.nickNameText.text = sharedPref.getString("nickName", "-")
        // networtService 종료
        // profile 지정
        val storageRef = storage.reference.child("images/profile/${email}.jpg")
        // 파일 존재 여부 확인
        storageRef.metadata.addOnSuccessListener { metadata ->
            storageRef.downloadUrl.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Glide.with(requireContext())
                        .load(task.result)
                        .into(binding.profilePicture)
                }
            }
        }.addOnFailureListener { exception ->
            binding.profilePicture.setImageResource(R.drawable.basic_profile)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}