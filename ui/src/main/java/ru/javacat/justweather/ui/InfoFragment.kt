package ru.javacat.justweather.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.javacat.justweather.ui.base.BaseFragment
import ru.javacat.justweather.ui.util.EMAIL
import ru.javacat.justweather.ui.util.PAYMENT_URL
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FragmentInfoBinding


@AndroidEntryPoint
class InfoFragment : BaseFragment<FragmentInfoBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentInfoBinding =
        { inflater, container ->
            FragmentInfoBinding.inflate(inflater, container, false)
        }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        val sendMoneyUrl = PAYMENT_URL
        val sendMoneyIntent = Intent(Intent.ACTION_VIEW)
        sendMoneyIntent.data = Uri.parse(sendMoneyUrl)

        val email = EMAIL
        val sendMailIntent = Intent(Intent.ACTION_SENDTO)
        sendMailIntent.setData(Uri.parse("mailto: $email"))
        sendMailIntent.putExtra(Intent.EXTRA_EMAIL, email)
        sendMailIntent.putExtra(Intent.EXTRA_SUBJECT, "Weather App")


        binding.sendMoneyBtn.setOnClickListener {
            println("here")
            startActivity(sendMoneyIntent)
        }

        binding.sendMailBtn.setOnClickListener {
            startActivity(Intent.createChooser(sendMailIntent, getString(R.string.choose_e_mail_app)))
        }
    }
}