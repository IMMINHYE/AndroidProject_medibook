package org.third.medicalapp.hospital

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.third.medicalapp.databinding.ActivityDepartSelectBinding

class DepartSelectActivity : AppCompatActivity() {
    lateinit var binding:ActivityDepartSelectBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDepartSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = Intent(this, HospitalListActivity::class.java)
        val dong = intent.getStringExtra("dong")
        Log.d("동 데이터", "$dong")
        binding.btnCloseX.setOnClickListener {
            startActivity(intent)
            finish()
        }

        binding.btn00.setOnClickListener { //모두
            intent.putExtra("hcode", "")
            intent.putExtra("dong", dong)
            startActivity(intent)
            finish()
        }
        binding.btn01.setOnClickListener {//외과
            intent.putExtra("hcode", "외과")
            intent.putExtra("dong", dong)
            startActivity(intent)
            finish()
        }
        binding.btn10.setOnClickListener {//성형외과
            intent.putExtra("hcode", "성형외과")
            intent.putExtra("dong", dong)
            startActivity(intent)
            finish()
        }
        binding.btn11.setOnClickListener {//내과
            intent.putExtra("hcode", "내과")
            intent.putExtra("dong", dong)
            startActivity(intent)
            finish()
        }
        binding.btn20.setOnClickListener {//산부인과
            intent.putExtra("hcode", "산부인과")
            intent.putExtra("dong", dong)
            startActivity(intent)
            finish()
        }
        binding.btn21.setOnClickListener {//이비인후과
            intent.putExtra("hcode", "이비인후과")
            intent.putExtra("dong", dong)
            startActivity(intent)
            finish()
        }
        binding.btn30.setOnClickListener {//비뇨기과
            intent.putExtra("hcode", "비뇨기과")
            intent.putExtra("dong", dong)
            startActivity(intent)
            finish()
        }
        binding.btn31.setOnClickListener {//소아과
            intent.putExtra("hcode", "소아과")
            intent.putExtra("dong", dong)
            startActivity(intent)
            finish()
        }
        binding.btn40.setOnClickListener {//정신과
            intent.putExtra("hcode", "정신과")
            intent.putExtra("dong", dong)
            startActivity(intent)
            finish()
        }
        binding.btn41.setOnClickListener {//안과
            intent.putExtra("hcode", "안과")
            intent.putExtra("dong", dong)
            startActivity(intent)
            finish()
        }
        binding.btn50.setOnClickListener {//통증의학과
            intent.putExtra("hcode", "통증의학과")
            intent.putExtra("dong", dong)
            startActivity(intent)
            finish()
        }
        binding.btn51.setOnClickListener {//정형외과
            intent.putExtra("hcode", "정형외과")
            intent.putExtra("dong", dong)
            startActivity(intent)
            finish()
        }
        binding.btn60.setOnClickListener {//치과
            intent.putExtra("hcode", "치과")
            intent.putExtra("dong", dong)
            startActivity(intent)
            finish()
        }
        binding.btn61.setOnClickListener {//가정의학과
            intent.putExtra("hcode", "가정의학과")
            intent.putExtra("dong", dong)
            startActivity(intent)
            finish()
        }
        binding.btn70.setOnClickListener {//건강의학과
            intent.putExtra("hcode", "건강의학과")
            intent.putExtra("dong", dong)
            startActivity(intent)
            finish()
        }
        binding.btn71.setOnClickListener {//재활의학과
            intent.putExtra("hcode", "재활의학과")
            intent.putExtra("dong", dong)
            startActivity(intent)
            finish()
        }
        binding.btn80.setOnClickListener {//병리과
            intent.putExtra("hcode", "병리과")
            intent.putExtra("dong", dong)
            startActivity(intent)
            finish()
        }
        binding.btn81.setOnClickListener {//신경외과
            intent.putExtra("hcode", "신경외과")
            intent.putExtra("dong", dong)
            startActivity(intent)
            finish()
        }
        binding.btn90.setOnClickListener { //대학병원
            intent.putExtra("hcode", "대학병원")
            intent.putExtra("dong", dong)
            startActivity(intent)
            finish()
        }
        binding.btn91.setOnClickListener { //종합병원
            intent.putExtra("hcode", "종합병원")
            intent.putExtra("dong", dong)
            startActivity(intent)
            finish()
        }

    }
}