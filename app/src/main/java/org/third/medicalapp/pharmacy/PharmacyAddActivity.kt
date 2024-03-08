package org.third.medicalapp.pharmacy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import org.third.medicalapp.databinding.ActivityPharmacyAddBinding
import org.third.medicalapp.pharmacy.model.Pharmacy
import org.third.medicalapp.util.MyApplication
import retrofit2.Call
import retrofit2.Response

class PharmacyAddActivity : AppCompatActivity() {
    lateinit var binding: ActivityPharmacyAddBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPharmacyAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.postBtn.setOnClickListener {

            var pharmacy: String = binding.tvInputPharmacy.text.toString()
            var addr: String = binding.tvInputAddr.text.toString()
            var citycode: String = binding.tvInputCitycode.text.toString()
            var siguncode: String = binding.tvInputSiguncode.text.toString()
            var dong: String = binding.tvInputDong.text.toString()
            var x: Double = binding.tvInputX.text.toString().toDouble()
            var y: Double = binding.tvInputY.text.toString().toDouble()
            var tel: String = binding.tvInputTel.text.toString()

            val pharmacyModel = Pharmacy(null, pharmacy, citycode, siguncode, dong, addr, tel, x, y)

            val networkService = (applicationContext as MyApplication).pharmacyService
            val result = networkService.insert(pharmacyModel)
            result.enqueue(object : retrofit2.Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if (response.body() == true) {
                        Toast.makeText(this@PharmacyAddActivity, "약국 입력 성공", Toast.LENGTH_SHORT)
                            .show()
                        Log.d("insert hospital", "성공")
                        finish()
                    } else {
                        Toast.makeText(this@PharmacyAddActivity, "약국 입력 성공", Toast.LENGTH_SHORT)
                            .show()
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