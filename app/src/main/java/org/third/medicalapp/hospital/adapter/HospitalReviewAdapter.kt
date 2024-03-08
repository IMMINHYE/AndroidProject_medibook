package org.third.medicalapp.pharmacy.apater

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.third.medicalapp.R
import org.third.medicalapp.databinding.ItemReviewBinding
import org.third.medicalapp.hospital.model.HospitalReview

class HospitalReviewViewHolder(val binding: ItemReviewBinding) :
    RecyclerView.ViewHolder(binding.root)
class HospitalReviewAdapter(val context: Context, val itemList: MutableList<HospitalReview>) :
    RecyclerView.Adapter<HospitalReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalReviewViewHolder {
        return HospitalReviewViewHolder(ItemReviewBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HospitalReviewViewHolder, position: Int) {
        val data = itemList.get(position)

        // 데이터 설정
        holder.binding.run{
            tvWriter.text = data.nick
            tvDate.text = data.date
            tvReview.text = data.review

            // 추천 버튼 클릭 리스너 설정
            tvGood.setOnClickListener {
                holder.binding.imageReview.setImageResource(R.drawable.good)    // 추천 이미지 설정
                tvGood.setText("")
                tvBad.setText("")
                tv.setText("")
            }
            // 비추천 버튼 클릭 리스너 설정
            tvBad.setOnClickListener {
                holder.binding.imageReview.setImageResource(R.drawable.bad)     // 비추천 이미지 설정
                tvGood.setText("")
                tvBad.setText("")
                tv.setText("")
            }
        }
    }



}