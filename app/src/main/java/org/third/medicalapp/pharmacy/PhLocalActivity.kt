package org.third.medicalapp.pharmacy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.third.medicalapp.databinding.ActivityPhLocalBinding

class PhLocalActivity : AppCompatActivity() {
    lateinit var binding: ActivityPhLocalBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhLocalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = Intent(this, PharmacyListActivity::class.java)

        binding.btnCloseX.setOnClickListener {
            startActivity(intent)
            finish()
        }

        binding.btnDongSearchPh.setOnClickListener {
            val dong = binding.tvDongPh.text.toString()
            intent.putExtra("dong", dong)
            startActivity(intent)
            finish()
        }

    }
}