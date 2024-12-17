package com.example.todoapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.todoapp.MessageResponse
import com.example.todoapp.R
import com.example.todoapp.RegisterRequest
import com.example.todoapp.RetrofitClient
import com.example.todoapp.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import okhttp3.Callback
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class SignUpFragment : Fragment() {
    private lateinit var auth:FirebaseAuth
    private lateinit var navControl:NavController
    private lateinit var binding: FragmentSignUpBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        registerEvents()
    }

    private fun init(view: View) {
        navControl = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
    }

    private fun registerEvents() {
        binding.textViewSignIn.setOnClickListener {
            navControl.navigate(R.id.action_signUpFragment_to_signInFragment)
        }


        binding.nextBtn.setOnClickListener {
            val username = binding.nameEt.text.toString().trim()
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passEt.text.toString().trim()
            val phone = binding.phoneEt.text.toString().trim()

            val registerRequest = RegisterRequest(username,email,phone,pass)

            RetrofitClient.instance.regiter(registerRequest).enqueue(object :
                retrofit2.Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        val successRes = Gson().fromJson(response.body()?.string(), MessageResponse::class.java)
                        if (successRes.message == "Đăng ký thành công") {
                            navControl.navigate(R.id.action_signUpFragment_to_signInFragment)
                        }
                    } else {
                        Toast.makeText(context, "Đăng ký không thành công", Toast.LENGTH_SHORT).show()

                    }

                    }



                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                }


            })


        }
    }
}

