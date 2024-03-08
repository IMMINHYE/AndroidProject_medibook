package org.third.medicalapp.user

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.third.medicalapp.community.CommunityData
import org.third.medicalapp.community.MyAdapter
import org.third.medicalapp.databinding.FragmentUserWriteBinding
import org.third.medicalapp.util.MyApplication
import org.third.medicalapp.util.MyApplication.Companion.email

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UserWriteFragment : Fragment() {

    private var _binding: FragmentUserWriteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserWriteBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onStart() {
        super.onStart()
        val activity = activity as UserMainActivity
        val sharedPref = activity.getSharedPreferences("User", AppCompatActivity.MODE_PRIVATE)
        MyApplication.db.collection("community")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { result ->
                val itemList = mutableListOf<CommunityData>()
                for (document in result) {
                    val item = document.toObject(CommunityData::class.java)
                    item.docId = document.id
                    itemList.add(item)
                }
                // RecyclerView 설정
                if(itemList.size!=0 && itemList!=null){
                    binding.myWriteRecycler.layoutManager = LinearLayoutManager(context)
                    binding.myWriteRecycler.adapter = MyAdapter(activity as UserMainActivity, itemList)
                    binding.nonWrite.visibility=View.GONE
                    binding.myWriteRecycler.visibility=View.VISIBLE
                }else{
                    binding.nonWrite.visibility=View.VISIBLE
                    binding.myWriteRecycler.visibility=View.GONE
                }
            }
            .addOnFailureListener { exception ->
                Log.d("aaaa", "error... getting document..", exception)
                Toast.makeText(context, "서버 데이터 획득 실패", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}