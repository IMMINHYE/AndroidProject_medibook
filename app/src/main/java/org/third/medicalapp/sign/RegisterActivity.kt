package org.third.medicalapp.sign

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import org.third.medicalapp.databinding.ActivityRegisterBinding
import org.third.medicalapp.sign.model.UserModel
import org.third.medicalapp.util.MyApplication
import org.third.medicalapp.util.MyApplication.Companion.auth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 가입
        val networkService = (applicationContext as MyApplication).netWorkService
        binding.registerBtn.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val nickName = binding.nickNameEditText.text.toString()
            val phone = binding.phoneEditText.text.toString()
            val userModel = UserModel(email, nickName, phone, null, "user")
            Log.d("aaaa", "${nickName}")

            networkService.checkNick(nickName).enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    Log.d("aaa", "Response : ${response.body()}")
                    // 아이디 형식 확인
                    if (validateInput(email, password)) {
                        // 닉네임 중복확인
                        if (response.body() == true) {
                            binding.userNickNameError.visibility = View.GONE
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this@RegisterActivity) { task ->
                                    if (task.isSuccessful) {
                                        binding.emailEditText.text.clear()
                                        binding.passwordEditText.text.clear()
                                        binding.nickNameEditText.text.clear()
                                        binding.phoneEditText.text.clear()
                                        auth.currentUser?.sendEmailVerification()
                                            ?.addOnCompleteListener { sendTask ->
                                                // 메일 인증
                                                if (sendTask.isSuccessful) {
                                                    Toast.makeText(
                                                        baseContext,
                                                        "인증성공",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    Log.d("aaaa", "인증성공")

                                                    // db insert
                                                    val result = networkService.insert(userModel)
                                                    result.enqueue(object : Callback<Boolean> {
                                                        override fun onResponse(
                                                            call: Call<Boolean>,
                                                            response: Response<Boolean>
                                                        ) {
                                                            if (response.body() == true) {
                                                                finish()
                                                                Log.d("aaa", "성공")
                                                            } else {

                                                                Log.d("aaa", "실패")
                                                            }
                                                        }

                                                        override fun onFailure(
                                                            call: Call<Boolean>,
                                                            t: Throwable
                                                        ) {
                                                            Log.d("aaa", "실패")
                                                            call.cancel()
                                                        }
                                                    })
                                                } else {
                                                    Toast.makeText(
                                                        baseContext,
                                                        "인증실패",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    Log.d("aaaa", "인증실패")
                                                }
                                            }
                                    } else {
                                        Toast.makeText(baseContext, "회원가입 실패", Toast.LENGTH_SHORT)
                                            .show()
                                        binding.userNameError.text = "중복된 아이디가 존재합니다."
                                        binding.userNameError.visibility = View.VISIBLE
                                        Log.d("aaaa", "회원가입 실패")
                                    }
                                }


                        } else {
                            binding.userNickNameError.visibility = View.VISIBLE
                            Toast.makeText(this@RegisterActivity, "중복된 닉네임", Toast.LENGTH_SHORT)
                                .show()
                        }

                    } else {
                        Toast.makeText(baseContext, "회원가입 실패", Toast.LENGTH_SHORT).show()
                        binding.userNameError.text = "이메일 형식으로 입력해주세요."
                        binding.userNameError.visibility = View.VISIBLE
                        Log.d("aaaa", "회원가입 실패")
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    call.cancel()
                }
            })

        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.userNameError.text = "이메일 형식으로 입력해주세요"
            binding.userNameError.visibility = View.VISIBLE
            return false
        }

        if (password.isEmpty() || password.length < 6) {
            binding.passwordError.visibility = View.VISIBLE
            return false
        }

        return true
    }

}

