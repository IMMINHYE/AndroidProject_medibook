package org.third.medicalapp.pharmacy.apater

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.third.medicalapp.databinding.ItemListPhBinding
import org.third.medicalapp.pharmacy.PharmacyDetailActivity
import org.third.medicalapp.pharmacy.model.Pharmacy

class ListViewHolder(val binding: ItemListPhBinding) : RecyclerView.ViewHolder(binding.root)
class PharmacyAdapter(val context: Context, val data:List<Pharmacy>?):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder {
        return ListViewHolder(
            ItemListPhBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return data?.size?:0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as ListViewHolder).binding
        val pharmacy = data?.get(position)

        binding.tvPharmacyName.text = pharmacy?.pharmacy
        binding.tvPharmacyAddress.text = pharmacy?.addr

        // 약국 항목을 클릭하면 약국 상세 화면으로 이동
        binding.itemPharmacy.setOnClickListener {
            val intent = Intent(context, PharmacyDetailActivity::class.java)
            intent.putExtra("id", pharmacy?.id)
            intent.putExtra("pharmacy", pharmacy?.pharmacy)
            intent.putExtra("addr", pharmacy?.addr)
            intent.putExtra("x", pharmacy?.x)
            intent.putExtra("y", pharmacy?.y)
            intent.putExtra("tel", pharmacy?.tel)

            context.startActivity(intent)
        }
    }

}