package org.third.medicalapp.admin

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.third.medicalapp.R
import org.third.medicalapp.databinding.ItemUserListBinding
import org.third.medicalapp.sign.model.UserModel
import org.third.medicalapp.util.MyApplication

class UserViewHolder(val binding: ItemUserListBinding) : RecyclerView.ViewHolder(binding.root)

class UserAdapter(val context: Context, val itemList:MutableList<UserModel>):RecyclerView.Adapter<UserViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return UserViewHolder(ItemUserListBinding.inflate(layoutInflater))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val data = itemList.get(position)
        val storageRef = MyApplication.storage.reference.child("images/profile/${data.userName}.jpg")
        // 사용자 프로필 이미지 로드(파일 존재 여부 확인)
        storageRef.metadata.addOnSuccessListener { metadata ->
            storageRef.downloadUrl.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Glide.with(context)
                        .load(task.result)
                        .into(holder.binding.profilePicture)
                }
            }
        }.addOnFailureListener {exception->
            holder.binding.profilePicture.setImageResource(R.drawable.basic_profile)
        }
        // 사용자 정보 바인딩
        holder.binding.run{
            userName.text = "${data.userName}"
            nickName.text = "${data.nickName}"
        }
        // 아이템 클릭 이벤트 설정
        holder.binding.itemLayout.setOnClickListener{
            val intent= Intent(context, UserDetailActivity::class.java)
            intent.putExtra("userName",data.userName)
            intent.putExtra("nickName",data.nickName)
            intent.putExtra("phoneNumber",data.phoneNumber)
            intent.putExtra("regiDate",data.regiDate)
            context.startActivity(intent)
        }
    }
}