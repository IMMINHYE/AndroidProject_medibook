package org.third.medicalapp.pharmacy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.third.medicalapp.databinding.ActivityPhNameBinding

class PhNameActivity : AppCompatActivity() {
    lateinit var binding: ActivityPhNameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhNameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = Intent(this, PharmacyListActivity::class.java)
        binding.btnCloseX.setOnClickListener {
            startActivity(intent)
            finish()
        }

        binding.btnPharmacySearch.setOnClickListener {
            val pharmacy = binding.tvPharmacySearch.text.toString()
            intent.putExtra("pharmacy", pharmacy)
            startActivity(intent)
            finish()
        }
    }
}