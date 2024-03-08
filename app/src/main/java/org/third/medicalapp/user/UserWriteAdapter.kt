package org.third.medicalapp.user

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.third.medicalapp.R
import org.third.medicalapp.community.CommunityData
import org.third.medicalapp.community.CommunityDetailActivity
import org.third.medicalapp.databinding.ItemUserWriteListBinding
import org.third.medicalapp.util.MyApplication
import org.third.medicalapp.util.deleteLike
import org.third.medicalapp.util.isSavedLike
import org.third.medicalapp.util.saveLikeStore
import org.third.medicalapp.util.updateLikeCount

class UserWriteViewHolder(val binding: ItemUserWriteListBinding) : RecyclerView.ViewHolder(binding.root)

class UserWriteAdapter(val context: Context, val itemList: MutableList<CommunityData>) :RecyclerView.Adapter<UserWriteViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserWriteViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return UserWriteViewHolder(ItemUserWriteListBinding.inflate(layoutInflater))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: UserWriteViewHolder, position: Int) {
        val data = itemList.get(position)
        holder.binding.run {
            tvWriter.text = "${data.email}"
            tvDate.text = data.date
            tvTitle.text = data.title
            val maxLength = 500
            val truncatedContent = if (data.content!!.length > maxLength) {
                data.content!!.substring(0, maxLength) + "..."
            } else {
                data.content
            }
            tvContent.text = truncatedContent

            // 비동기적으로 댓글 수 가져오기
            getCommentCount(data.docId) { count ->
                count?.let {
                    holder.binding.commentCount.text = it.toString()
                }
            }
        }
        val imgRef = MyApplication.storage.reference.child("images/${data.docId}.jpg")
        imgRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Glide.with(context)
                    .load(task.result)
                    .into(holder.binding.imageView)
            }
        }
        holder.binding.root.setOnClickListener {
            val intent = Intent(context, CommunityDetailActivity::class.java)
            intent.putExtra("docId", data.docId)
            intent.putExtra("email", data.email)
            intent.putExtra("date", data.date)
            intent.putExtra("title", data.title)
            intent.putExtra("content", data.content)
            intent.putExtra("likeCount", data.likeCount)
            context.startActivity(intent)
        }

        // 좋아요 상태에 따라 이미지 설정
        CoroutineScope(Dispatchers.Main).launch {
            val isLiked = isSavedLike(data.docId, MyApplication.email)
            if (isLiked == false) {
                holder.binding.imageLike.setImageResource(R.drawable.like)
            } else {
                holder.binding.imageLike.setImageResource(R.drawable.like_full)
            }
        }

        // 좋아요 텍스트 설정
        holder.binding.textViewLike.text = data.likeCount.toString()

        // 좋아요 이미지를 클릭했을 때 이벤트 처리
        holder.binding.imageLike.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val isLiked = isSavedLike(data.docId, MyApplication.email)
                // 여기에서 isLiked를 사용하여 다음 작업 수행
                if (isLiked == false) {
                    // 좋아요 버튼을 누르면
                    saveLikeStore(data.docId, MyApplication.email)
                    updateLikeCount(data.docId, 1)
                    holder.binding.imageLike.setImageResource(R.drawable.like_full)
                    data.likeCount++
                } else {
                    // 좋아요 취소 버튼을 누르면
                    deleteLike(data.docId, MyApplication.email)
                    updateLikeCount(data.docId, -1)
                    holder.binding.imageLike.setImageResource(R.drawable.like)
                    data.likeCount--
                }
                holder.binding.textViewLike.text = data.likeCount.toString()
            }
        }
    }

    // 비동기 방식으로 댓글 수를 가져와서 commentCount에 설정
    fun getCommentCount(docId: String?, callback: (Int?) -> Unit) {
        if (docId != null) {
            MyApplication.db.collection("comment").whereEqualTo("docId", docId)
                .get()
                .addOnSuccessListener { result ->
                    val commentCount = result.size() // 댓글 수 계산
                    callback(commentCount)
                }
                .addOnFailureListener { exception ->
                    Log.d("getCommentCount", "Failed to get comment count: $exception")
                    callback(null) // 실패 시 null을 전달
                }
        } else {
            callback(null)
            // docId가 null인 경우 null을 전달
        }
    }

}