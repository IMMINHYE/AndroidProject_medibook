package org.third.medicalapp.hospital

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.third.medicalapp.databinding.ActivityNameSearchBinding

class NameSearchActivity : AppCompatActivity() {
    lateinit var binding:ActivityNameSearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNameSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = Intent(this, HospitalListActivity::class.java)

        binding.btnCloseX.setOnClickListener {
            startActivity(intent)
            finish()
        }

        binding.btnHospitalSearch.setOnClickListener {
            val hname = binding.tvHospitalSearch.text.toString()
            intent.putExtra("hname", hname)
            startActivity(intent)
            finish()
        }
    }
}