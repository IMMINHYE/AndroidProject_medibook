package org.third.medicalapp.medicalInfo.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.third.medicalapp.medicalInfo.MedicalInfoModifyActivity
import org.third.medicalapp.R
import org.third.medicalapp.databinding.ItemMedicalinfoBinding
import org.third.medicalapp.medicalInfo.model.MediInfo

class MediInfoViewHolder(val binding: ItemMedicalinfoBinding) : RecyclerView.ViewHolder(binding.root)
class MediInfoAdapter (val context: Context, val datas: MutableList<MediInfo>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    val icoUrl = arrayOf(R.drawable.medi_info1, R.drawable.medi_info2, R.drawable.medi_info3, R.drawable.medi_info4,
        R.drawable.medi_info5, R.drawable.medi_info6, R.drawable.medi_info7, R.drawable.medi_info8)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MediInfoViewHolder(ItemMedicalinfoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return datas!!.size ?:0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MediInfoViewHolder).binding

        binding.tvName.text = datas!![position].siteName
        binding.tvIntro.text = datas!![position].siteIntro

        // 이미지를 설정
        Glide.with(context)
            .load(icoUrl[position])
            .into(holder.binding.siteImage)

        Log.d("siteName",datas!![position].siteName)

        binding.root.setOnClickListener {
            val siteUri = Uri.parse(datas!![position].siteUrl)
            val intent = Intent(Intent.ACTION_VIEW, siteUri)
            context.startActivity(intent)
        }

        binding.root.setOnLongClickListener {
            val intent = Intent(context, MedicalInfoModifyActivity::class.java)
            intent.putExtra("id", datas[position].id)
            intent.putExtra("siteName", datas[position].siteName)
            intent.putExtra("siteUrl", datas[position].siteUrl)
            intent.putExtra("siteIntro", datas[position].siteIntro)
            intent.putExtra("icoUrl", icoUrl[position])
            context.startActivity(intent)
            true
        }
    }
}