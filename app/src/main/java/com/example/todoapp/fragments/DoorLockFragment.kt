package com.example.todoapp.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.todoapp.CheckIn
import com.example.todoapp.MyHostApduService
import com.example.todoapp.R
import com.example.todoapp.RetrofitClient
import com.example.todoapp.SharedPreferencesManager
import com.example.todoapp.databinding.FragmentDoorLockBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response


class DoorLockFragment : Fragment() {
    private lateinit var binding: FragmentDoorLockBinding
    private lateinit var navController: NavController
    private lateinit var prefsManager: SharedPreferencesManager
    private var lockStatus:Boolean = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDoorLockBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        binding.lockButton.setOnClickListener {
            binding.lockImage.setImageResource(R.drawable.lock)
            lockStatus = true
            MyHostApduService.dataToSend=""
        }

        binding.unlockButton.setOnClickListener {
            // Đổi ảnh thành mở khóa
            var email = prefsManager.getEmail()
            var roomNumber = 101
            var code = generateTimeHash()
            val checkIn = CheckIn(email,roomNumber,code)
            Log.d("SignIn",checkIn.toString())
            RetrofitClient.instance.checkIn(checkIn).enqueue(object :
                retrofit2.Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        MyHostApduService.dataToSend = code
                        binding.unlockButton.isEnabled = false
                        binding.lockImage.setImageResource(R.drawable.unlock) // Ảnh cái khóa mở
                        lockStatus = false
                        // Sau 10 giây đổi lại thành khóa đóng
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (!lockStatus) {
                                binding.lockImage.setImageResource(R.drawable.lock) // Ảnh cái khóa đóng
                                lockStatus=true
                                MyHostApduService.dataToSend=""
                            }
                            binding.unlockButton.isEnabled = true
                        }, 5000)
                    } else {
                        Toast.makeText(context, "Bạn không đặt phòng vào ngày hôm nay", Toast.LENGTH_SHORT).show()
                    }
                }


                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                }


            })

        }
        binding.backButton.setOnClickListener{
            navController.navigate(R.id.action_doorLockFragment_to_homeFragment)
        }
    }

    private fun init(view: View) {
        prefsManager = SharedPreferencesManager(requireContext())
        navController = Navigation.findNavController(view)

    }

}