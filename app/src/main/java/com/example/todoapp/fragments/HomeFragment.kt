package com.example.todoapp.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.todoapp.Booking
import com.example.todoapp.CheckIn
import com.example.todoapp.DataStore
import com.example.todoapp.MessageBooking
import com.example.todoapp.MessageResponse
import com.example.todoapp.MyHostApduService
import com.example.todoapp.R
import com.example.todoapp.RetrofitClient
import com.example.todoapp.SharedPreferencesManager
import com.example.todoapp.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


fun convertStringToDate(dateString: String): Date? {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Định dạng ngày tháng
    return format.parse(dateString) // Phân tích chuỗi thành đối tượng Date
}

fun generateTimeHash(): String {
    val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

    // Chuyển chuỗi thời gian thành hash (SHA-256)
    val messageDigest = MessageDigest.getInstance("SHA-256")
    val hashBytes = messageDigest.digest(currentTime.toByteArray(Charsets.UTF_8))

    // Chuyển đổi hash bytes thành chuỗi hex
    val hashCode =  hashBytes.joinToString("") { "%02x".format(it) }.take(32)
    return hashCode
}


class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding
    private lateinit var navController: NavController
    private lateinit var prefsManager: SharedPreferencesManager
    private var timeCheckIn : Long = 0
    private var timeCheckOut :Long = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)

        if (!prefsManager.isLoggedIn()) {
            navController.navigate(R.id.action_homeFragment_to_signInFragment)
            return
        }

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)



        binding.checkInBtn.setOnClickListener{
            // Hiển thị DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Lưu ý: Tháng trả về từ DatePicker bắt đầu từ 0
                    calendar.set(selectedYear,selectedMonth,selectedDay,0,0,0)
                    val currentDate = Calendar.getInstance()
                    if (calendar.before(currentDate)) {
                        Toast.makeText(context, "Ngày đặt phòng không được trước hiện tại", Toast.LENGTH_SHORT).show()
                    } else {
                        timeCheckIn = calendar.timeInMillis
                        if (timeCheckIn > timeCheckOut && timeCheckOut != 0L) {
                            Toast.makeText(context, "Ngày checkin không được sau ngày checkout", Toast.LENGTH_SHORT).show()
                        } else {
                            val formattedDate = "$selectedYear-${selectedMonth+1}-${selectedDay}"
                            binding.checkInText.text = "$formattedDate"
                        }

                    }


                },
                year, month, day
            )
            datePickerDialog.show()
        }

        binding.checkOutBtn.setOnClickListener{
            // Hiển thị DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Lưu ý: Tháng trả về từ DatePicker bắt đầu từ 0
                    calendar.set(selectedYear,selectedMonth,selectedDay,0,0,0)
                    val currentDate = Calendar.getInstance()
                    if (calendar.before(currentDate)) {
                        Toast.makeText(context, "Ngày đặt phòng không được trước ngày hiện tại", Toast.LENGTH_SHORT).show()
                    } else {
                        timeCheckOut = calendar.timeInMillis
                        if (timeCheckOut < timeCheckIn) {
                            Toast.makeText(context, "Ngày checkout không được trước ngày checkin", Toast.LENGTH_SHORT).show()
                        } else {
                            val formattedDate = "$selectedYear-${selectedMonth+1}-${selectedDay}"
                            binding.checkOutText.text = "$formattedDate"
                        }

                    }

                },
                year, month, day
            )
            datePickerDialog.show()
        }




        binding.button.setOnClickListener {
            prefsManager.clearSession()
            MyHostApduService.dataToSend=""
            navController.navigate(R.id.action_homeFragment_to_signInFragment)
        }

        binding.booking.setOnClickListener {
            var checkInDate = binding.checkInText.text.toString()
            var checkOutDate = binding.checkOutText.text.toString()
            var email = prefsManager.getEmail()
            Log.d("SignIn",email)

            val bookingRequest = Booking(email,checkInDate,checkOutDate)
            Log.d("SignIn",bookingRequest.toString())
            RetrofitClient.instance.booking(bookingRequest).enqueue(object :
                retrofit2.Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        val successRes = Gson().fromJson(response.body()?.string(), MessageBooking::class.java)
                        Toast.makeText(context, "${successRes.message} - Phòng ${successRes.roomNumber}", Toast.LENGTH_SHORT).show()
                        Log.d("SignIn",successRes.toString())
                    } else {
                        val failureRes = Gson().fromJson(response.errorBody()?.string(),MessageResponse::class.java)
                        Toast.makeText(context, "${failureRes.message}", Toast.LENGTH_SHORT).show()
                    }

                }



                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                }


            })
        }

        binding.checkIn.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_doorLockFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MyHostApduService.dataToSend = ""
    }


    private fun init(view: View) {
        navController =Navigation.findNavController(view)
        prefsManager = SharedPreferencesManager(requireContext())
    }

}