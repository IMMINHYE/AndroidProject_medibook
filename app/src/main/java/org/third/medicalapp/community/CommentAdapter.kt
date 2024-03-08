package org.third.medicalapp.community

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import org.third.medicalapp.databinding.ItemCommunityCommentBinding
import org.third.medicalapp.util.MyApplication

class CommentViewHolder(val binding: ItemCommunityCommentBinding) :
    RecyclerView.ViewHolder(binding.root)

class CommentAdapter(val context: Context, val itemList: MutableList<CommentData>) :
    RecyclerView.Adapter<CommentViewHolder>() {

    init {
        Log.d("CommentAdapter", "Item list size: ${itemList.size}")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(ItemCommunityCommentBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    // 댓글 삭제 이벤트 핸들러
    fun deleteComment(position: Int) {
        val commentId = itemList[position].commentId
        if (commentId != null) {
            // Firestore에서 댓글 삭제
            MyApplication.db.collection("comment").document(commentId).delete()
                .addOnSuccessListener {
                    // 댓글 데이터 제거 및 리사이클러뷰 갱신
                    itemList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyDataSetChanged()
                    Toast.makeText(context, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Log.e("deleteComment", "Error deleting document", exception)
                    Toast.makeText(context, "댓글 삭제 실패", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val data = itemList.get(position)
        Log.d("Comment","${data.comment}")
        Log.d("date","${data.date}")
        holder.binding.run{
            tvCommentWriter.text = "${data.nick}"
            tvCommentDate.text = data.date
            tvComment.text = data.comment

            // 댓글 삭제 버튼 클릭 이벤트
            commentDelete.setOnClickListener {
                deleteComment(holder.adapterPosition)
            }
        }
    }
}