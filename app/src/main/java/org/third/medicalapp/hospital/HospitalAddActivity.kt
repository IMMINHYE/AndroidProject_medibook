package org.third.medicalapp.hospital

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import org.third.medicalapp.databinding.ActivityHospitalAddBinding
import org.third.medicalapp.hospital.model.Hospital
import org.third.medicalapp.util.MyApplication
import retrofit2.Call
import retrofit2.Response

class HospitalAddActivity : AppCompatActivity() {
    lateinit var binding: ActivityHospitalAddBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHospitalAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.postBtn.setOnClickListener{
            var hname: String = binding.tvInputHname.text.toString()
            var hcode: String = binding.tvInputHcode.text.toString()
            var addr: String = binding.tvInputAddr.text.toString()
            var city: String = binding.tvInputCity.text.toString()
            var sigun: String = binding.tvInputSigun.text.toString()
            var dong: String = binding.tvInputDong.text.toString()
            var x: Double = binding.tvInputX.text.toString().toDouble()
            var y: Double = binding.tvInputY.text.toString().toDouble()
            var tel: String = binding.tvInputTel.text.toString()

            val hospital = Hospital(null, hname, hcode, addr, city, sigun, dong, x, y, tel)

            val networkService = (applicationContext as MyApplication).hospitalServie
            val result = networkService.insert(hospital)
            result.enqueue(object : retrofit2.Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if (response.body() == true) {
                        Toast.makeText(this@HospitalAddActivity,"병원 입력 성공", Toast.LENGTH_SHORT).show()
                        Log.d("insert hospital", "성공")
                        finish()
                    } else {
                        Toast.makeText(this@HospitalAddActivity,"병원 입력 성공", Toast.LENGTH_SHORT).show()
                        Log.d("insert hospital", "실패")
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    TODO("Not yet implemented")
                    Log.d("insert hospital", "서버 연결실패")
                }


            })
        }

    }
}