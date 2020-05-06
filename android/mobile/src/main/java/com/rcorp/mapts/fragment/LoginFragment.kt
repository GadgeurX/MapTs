package com.rcorp.mapts.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.rcorp.mapts.R
import com.rcorp.mapts.activity.MapActivity
import com.rcorp.mapts.databinding.FragmentLoginBinding
import com.rcorp.mapts.model.user.User
import com.rcorp.mapts.presenter.login.LoginContract
import com.rcorp.mapts.presenter.login.LoginPresenter

class LoginFragment : Fragment(), LoginContract.View {

    private val presenter: LoginPresenter = LoginPresenter()
    private lateinit var binding: FragmentLoginBinding
    private val user = User()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textViewCreateAccount.setOnClickListener {
            it.findNavController().navigate(R.id.openRegisterFragment)
        }
        binding.user = user
        binding.presenter = presenter
        binding.buttonLogin.setOnClickListener {
            Log.d("MapTs", "Login USER")
            presenter.loginUser(user)
        }
        presenter.start(this)
    }

    override fun onLoginSuccess(user: User) {
        binding.textViewError.visibility = View.GONE
        activity?.finish()
        activity?.startActivity(Intent(activity, MapActivity::class.java))
    }

    override fun onLoginFail() {
        binding.textViewError.visibility = View.VISIBLE
    }
}