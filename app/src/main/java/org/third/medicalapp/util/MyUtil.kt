package org.third.medicalapp.util

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FieldValue
import org.third.medicalapp.community.LikeData
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// 권한 승인
fun myCheckPermission(activity: AppCompatActivity) {
    val requestPermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(activity, "권한 승인", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "권한 거부", Toast.LENGTH_SHORT).show()
        }
    }

    if (ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) !== PackageManager.PERMISSION_GRANTED
    ) {
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    if(ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.CALL_PHONE
        ) !== PackageManager.PERMISSION_GRANTED
    ){
        requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
    }
}

// 날짜 형식
fun dateToString(date: Date?): String {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return format.format(date)
}

// firebase에 좋아요 db 저장
fun saveLikeStore(docId: String?, email: String?) {
    val data = mapOf(
        "like_docId" to docId,
        "like_email" to email,
        "isLiked" to true
    )
    var likeDataList = mutableListOf<LikeData>()
    MyApplication.db.collection("like")
        .add(data)
        .addOnSuccessListener {
            Log.d("like_db save success", "데이터 업로드에 성공하였습니다.")
            MyApplication.db.collection("like")
                .get()
                .addOnSuccessListener { result ->

                    for (document in result) {
                        val like = document.toObject(LikeData::class.java)
                        like.likeId = document.id
                        likeDataList.add(like)
                    }
                    Log.d("like_db save success", "데이터 업로드에 성공하였습니다.")
                }
                .addOnFailureListener {
                    Log.d("like_db save failure", "데이터 업로드에 실패하였습니다.")
                }

        }
        .addOnFailureListener {
            Log.d("like_db save failure", "데이터 업로드에 실패하였습니다.")

        }
}

// 좋아요 수 업데이트
fun updateLikeCount(docId: String?, number: Long) {
    Log.d("updateLikeCount", "updateLikeCount 함수 호출")
    MyApplication.db.collection("community").document(docId!!).update("likeCount", FieldValue.increment(number))
        .addOnSuccessListener {
            Log.d("likeCount update success", "좋아요수 업데이트에 성공하였습니다.")
        }
        .addOnFailureListener { e ->
            Log.d("likeCount update failure", "좋아요수 업데이트에 실패하였습니다.")
        }
}

// 좋아요 여부 확인
suspend fun isSavedLike(docId: String?, email: String?): Boolean {
    var like: LikeData? = null
    // 비동기 작업
    return suspendCoroutine { continuation ->
        MyApplication.db.collection("like").whereEqualTo("like_docId", docId)
            .whereEqualTo("like_email", email)
            .get()
            .addOnSuccessListener { result ->
                var flag = false
                for (document in result) {
                    like = document.toObject(LikeData::class.java)
                    like?.likeId = document.id
                    like?.isLiked = true

                    flag = true
                    break
                }
                continuation.resume(flag) // 결과 반환
                Log.d("flag", "$flag")
            }
            .addOnFailureListener { exception ->
                Log.d("isSavedLike", "Failed to get like data: $exception")
                continuation.resume(false) // 실패 시 기본값 반환
            }
    }
}

// 좋아요 db 삭제
fun deleteLike(docId: String?, email: String?) {
    MyApplication.db.collection("like").whereEqualTo("like_docId", docId)
        .whereEqualTo("like_email", email)
        .get()
        .addOnSuccessListener { documents ->
            // documents에는 쿼리 결과에 해당하는 문서들이 포함됩니다.
            for (document in documents) {
                // 각 문서를 삭제합니다.
                MyApplication.db.collection("like").document(document.id).delete()
                    .addOnSuccessListener {
                        Log.d("like_db delete success", "데이터 삭제에 성공하였습니다.")
                    }
                    .addOnFailureListener { e ->
                        Log.d("like_db delete failure", "데이터 삭제에 실패하였습니다.")
                    }
            }
        }
        .addOnFailureListener { exception ->
            Log.d("like_db delete failure", "데이터 삭제에 실패하였습니다.")
        }
    MyApplication.db.collection("community").whereEqualTo("docId", docId)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                // 가져온 문서의 likeCount 값을 감소시킵니다.
                val currentLikeCount = document.getLong("likeCount")?: 0
                val updatedLikeCount = currentLikeCount - 1

                // 증가된 likeCount 값을 문서에 업데이트합니다.
                val documentRef = MyApplication.db.collection("community").document(document.id)
                documentRef.update("likeCount", updatedLikeCount)
                    .addOnSuccessListener {
                        Log.d("likeCount update success", "좋아요 수 감소에 성공하였습니다.")
                    }
                    .addOnFailureListener { e ->
                        Log.d("likeCount update failure", "좋아요 수 감소에 실패하였습니다.")
                    }
            }
        }
        .addOnFailureListener { exception ->
            Log.d("likeCount update failure", "좋아요 수 증가에 실패하였습니다.")
        }
}

