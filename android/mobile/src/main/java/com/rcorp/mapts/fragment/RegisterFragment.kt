package com.rcorp.mapts.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.rcorp.mapts.R
import com.rcorp.mapts.databinding.FragmentRegisterBinding
import com.rcorp.mapts.model.user.User
import com.rcorp.mapts.presenter.login.LoginPresenter
import com.rcorp.mapts.presenter.register.RegisterContract
import com.rcorp.mapts.presenter.register.RegisterPresenter

class RegisterFragment: Fragment(), RegisterContract.View {

    private lateinit var binding: FragmentRegisterBinding
    private val presenter: RegisterPresenter = RegisterPresenter()
    private val user = User()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.user = user
        binding.presenter = presenter
        binding.button.setOnClickListener {
            Log.d("MapTs", "Login USER")
            presenter.registerUser(user)
        }
        presenter.start(this)
    }

    override fun onRegisterSuccess() {
        binding.textViewError.visibility = View.GONE
        activity?.onBackPressed()
    }

    override fun onRegisterFail() {
        binding.textViewError.visibility = View.VISIBLE
    }
}