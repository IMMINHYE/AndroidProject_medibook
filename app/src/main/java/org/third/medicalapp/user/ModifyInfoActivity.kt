package org.third.medicalapp.user

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.Task
import org.third.medicalapp.R
import org.third.medicalapp.databinding.ActivityModifyInfoBinding
import org.third.medicalapp.sign.model.UserModel
import org.third.medicalapp.util.MyApplication
import org.third.medicalapp.util.MyApplication.Companion.email
import org.third.medicalapp.util.MyApplication.Companion.storage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ModifyInfoActivity : AppCompatActivity() {
    lateinit var binding: ActivityModifyInfoBinding
    lateinit var filePath: String
    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifyInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        checkProfile()
        sharedPref = getSharedPreferences("User", MODE_PRIVATE)
        binding.modiEditNickName.setText(sharedPref.getString("nickName", ""))
        binding.modiEditPhoneNumber.setText(sharedPref.getString("phoneNumber", ""))
        binding.modiProfilePicture.setOnClickListener {
            Log.d("aaaa", "click")
            showPopUpMenu(it)
        }
        binding.modiEditNickName.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val networkService =
                    (applicationContext as MyApplication).netWorkService
                val nickname = binding.modiEditNickName.text.toString()
                val result = networkService.checkNick(nickname)
                result.enqueue(object : Callback<Boolean> {
                    override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                        if (response.body() == true) {
                            Log.d("aaa", "성공")
                            binding.userNickNameError.visibility = View.GONE
                        } else {
                            if (binding.modiEditNickName.text.toString() == sharedPref.getString(
                                    "nickName",
                                    ""
                                )
                            ) {
                                binding.userNickNameError.visibility = View.GONE
                            } else {
                                Log.d("aaa", "중복")
                                binding.userNickNameError.visibility = View.VISIBLE

                            }
                        }
                    }

                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                        Log.d("aaa", "check nick 오류")
                        call.cancel()
                    }

                })
            }
        }
    }


    // 사진값 가져오기
    val requestLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode === android.app.Activity.RESULT_OK) {
            Glide
                .with(getApplicationContext())
                .load(it.data?.data)
                .apply(RequestOptions().override(200, 200))
                .centerCrop()
                .into(binding.modiProfilePicture)

            val cursor = contentResolver.query(
                it.data?.data as Uri,
                arrayOf<String>(MediaStore.Images.Media.DATA), null, null, null
            )
            cursor?.moveToFirst().let {
                filePath = cursor?.getString(0) as String
                Log.d("aaaa", "$filePath")
            }
        }
    }

    //팝업 메뉴 띄우기
    private fun showPopUpMenu(v: View) {
        val popupMenu = PopupMenu(this, v)
        popupMenu.menuInflater.inflate(R.menu.profile_popup_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.choose_picture -> {
                    var intent = Intent(Intent.ACTION_PICK)
                    intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*"
                    )
                    requestLauncher.launch(intent)
                    true
                }

                R.id.base_image -> {
                    binding.modiProfilePicture.setImageResource(R.drawable.basic_profile)
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_my_modify, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                // db 수정
                val networkService =
                    (applicationContext as MyApplication).netWorkService
                val nickname = binding.modiEditNickName.text.toString()
                val phoneNumber = binding.modiEditPhoneNumber.text.toString()
                var userModel = UserModel(email.toString(), nickname, phoneNumber, null, null)
                if (nickname != "") {
                    val result = networkService.modify(userModel)
                    result.enqueue(object : Callback<Boolean> {
                        override fun onResponse(
                            call: Call<Boolean>,
                            response: Response<Boolean>
                        ) {
                            if (response.body() == true) {
                                // 이미지 업로드
                                val profile = binding.modiProfilePicture.drawable
                                val baseProfile = ContextCompat.getDrawable(
                                    this@ModifyInfoActivity,
                                    R.drawable.basic_profile
                                )
                                Log.d(
                                    "aaa",
                                    "${profile?.constantState != baseProfile?.constantState}"
                                )
                                deleteImage()

                                if (profile?.constantState != baseProfile?.constantState) {
                                    uploadImage()
                                }
                                val editor = sharedPref.edit()
                                editor.putString("nickName", nickname)
                                editor.putString("phoneNumber", phoneNumber)
                                editor.apply()
                                Log.d("aaa", "수정성공")
                            } else {
                                binding.userNickNameError.visibility = View.VISIBLE
                                Toast.makeText(baseContext, "수정실패", Toast.LENGTH_SHORT).show()
                                Log.d("aaa", "수정 실패")
                            }
                        }

                        override fun onFailure(call: Call<Boolean>, t: Throwable) {
                            Log.d("aaa", "modi 서버 연결 실패")
                            call.cancel()
                        }
                    })
                    //딜레이
                    Handler(Looper.getMainLooper()).postDelayed({
                        finish()
                    }, 1500) // 1000ms = 1초
                    return true
                } else {
                    Log.d("aaaa", "닉네임 입력")
                    return false
                }

            }

            R.id.cancel -> {
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    // 프로필 이미지 삭제
    fun deleteImage() {
        val storage = MyApplication.storage
        val storageRef = storage.reference.child("images/profile/${email}.jpg")
        // 파일 존재 여부 확인
        storageRef.metadata.addOnSuccessListener { metadata ->
            val deleteTask: Task<Void> = storageRef.delete()
            deleteTask.addOnSuccessListener {
                Log.d("aaaa", "기존 profile 삭제")
            }.addOnFailureListener {
                Log.d("aaaa", "기존 profile 삭제 실패")
            }
        }.addOnFailureListener { exception ->
        }
    }

    // 프로필 이미지 업로드
    fun uploadImage() {
        val storage = MyApplication.storage
        val storageRef = storage.reference
        val imgRef = storageRef.child("images/profile/${email}.jpg")
        val file = Uri.fromFile(File(filePath))
        imgRef.putFile(file)
            .addOnSuccessListener {
                Toast.makeText(this, "save ok...", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Log.d("kkang", "file save error", it)
            }
    }

    fun checkProfile() {
        val storageRef = storage.reference.child("images/profile/${email}.jpg")
        storageRef.metadata.addOnSuccessListener { metadata ->
            storageRef.downloadUrl.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Glide.with(this)
                        .load(task.result)
                        .into(binding.modiProfilePicture)
                }
            }
        }.addOnFailureListener {
            binding.modiProfilePicture.setImageResource(R.drawable.basic_profile)
        }
    }
}