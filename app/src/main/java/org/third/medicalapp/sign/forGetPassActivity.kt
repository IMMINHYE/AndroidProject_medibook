package org.third.medicalapp.sign

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import org.third.medicalapp.databinding.ActivityForGetPassBinding
import org.third.medicalapp.util.MyApplication.Companion.auth

class forGetPassActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityForGetPassBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 사용자의 이메일 주소를 입력받습니다.
        binding.findPassBtn.setOnClickListener {
            Log.d("aaa","click")
            val emailAddress = binding.passwordEditText.text.toString()
            if(emailAddress!=null && emailAddress!=""){
                auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "메일을 보냈습니다.", Toast.LENGTH_SHORT).show()
                            Log.d("aaa","click")
                            finish()
                        } else {
                            binding.userNameError.visibility= View.VISIBLE
                        }
                    }
            }else{
                binding.userNameError.visibility= View.VISIBLE
            }
        }
    }
}