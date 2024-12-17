package com.example.todoapp.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.todoapp.R
import com.example.todoapp.SharedPreferencesManager
import com.google.firebase.auth.FirebaseAuth


class SplashFragment : Fragment() {
    private lateinit var navControl: NavController
    private lateinit var prefsManager: SharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefsManager = SharedPreferencesManager(requireContext())
        navControl = Navigation.findNavController(view)
        Handler(Looper.myLooper()!!).postDelayed(Runnable {
            if (prefsManager.isLoggedIn()) {
                navControl.navigate(R.id.action_splashFragment_to_homeFragment)
            } else {
                navControl.navigate(R.id.action_splashFragment_to_signInFragment)

            }
        },2000)
    }


}