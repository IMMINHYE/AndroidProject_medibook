package org.third.medicalapp.hospital.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.third.medicalapp.databinding.ItemListBinding
import org.third.medicalapp.hospital.HospitalDetailActivity
import org.third.medicalapp.hospital.model.Hospital

class ListViewHolder(val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root)
class HospitalAdapter(val context: Context, val data: List<Hospital>?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder {
        return ListViewHolder(
            ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as ListViewHolder).binding
        val hospital = data?.get(position)

        // 데이터 설정
        binding.tvHospitalName.text = hospital?.hname
        binding.tvHospitalCode.text = hospital?.hcode
        binding.tvHospitalAddress.text = hospital?.addr

        binding.itemHospital.setOnClickListener {
            // HospitalDetailActivity로 이동하는 Intent 설정
            val intent = Intent(context, HospitalDetailActivity::class.java)
            intent.putExtra("id", hospital?.id)
            intent.putExtra("hname", hospital?.hname)
            intent.putExtra("hcode", hospital?.hcode)
            intent.putExtra("addr", hospital?.addr)
            intent.putExtra("x", hospital?.x)
            intent.putExtra("y", hospital?.y)
            intent.putExtra("tel", hospital?.tel)


            context.startActivity(intent)
        }

    }

}
