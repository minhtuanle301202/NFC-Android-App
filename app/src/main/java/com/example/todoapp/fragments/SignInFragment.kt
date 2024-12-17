package com.example.todoapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.todoapp.ApiService
import com.example.todoapp.AuthResponse
import com.example.todoapp.DataStore
import com.example.todoapp.LoginRequest
import com.example.todoapp.MessageResponse
import com.example.todoapp.R
import com.example.todoapp.RetrofitClient
import com.example.todoapp.SharedPreferencesManager
import com.example.todoapp.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignInFragment : Fragment() {
    private lateinit var binding:FragmentSignInBinding
    private lateinit var navControl:NavController
    private lateinit var prefsManager: SharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        regiterEvents()
    }

    private fun init(view: View) {
        navControl = Navigation.findNavController(view)
        prefsManager = SharedPreferencesManager(requireContext())
    }

    private fun regiterEvents() {
        binding.textViewSignUp.setOnClickListener {
            navControl.navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        binding.nextBtn.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passEt.text.toString().trim()

            val loginRequest = LoginRequest(email,pass)
            RetrofitClient.instance.login(loginRequest).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        val successRes = Gson().fromJson(response.body()?.string(),AuthResponse::class.java)
                        prefsManager.saveLoginSession(successRes.token,email)
                        Toast.makeText(requireContext(),"Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                        navControl.navigate(R.id.action_signInFragment_to_homeFragment)
                    } else {
                        val errorRes = Gson().fromJson(response.errorBody()?.string(),MessageResponse::class.java)

                        Toast.makeText(context, "${errorRes.message}", Toast.LENGTH_SHORT).show()

                    }

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                }


            })
        }
    }

}