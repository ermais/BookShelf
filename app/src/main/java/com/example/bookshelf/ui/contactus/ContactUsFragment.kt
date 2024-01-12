package com.example.bookshelf.ui.contactus

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.bookshelf.R
import com.example.bookshelf.databinding.FragmentContactUsBinding
import com.example.bookshelf.util.callActionWithPermission
import com.example.bookshelf.util.isPermissionGranted
import com.example.bookshelf.util.requestPermission
import com.google.android.material.navigation.NavigationView

class ContactUsFragment : Fragment() {


    private lateinit var viewModel: ContactUsViewModel
    private var _binding : FragmentContactUsBinding? = null
    val binding : FragmentContactUsBinding get() = _binding!!
    private lateinit var toolbar : Toolbar
    private lateinit var navView : NavigationView
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactUsBinding.inflate(inflater,container,false)
        toolbar = binding.contactusToolbar
        navController = findNavController()
        val _drawer = view?.findViewById<DrawerLayout>(R.id.drawer_layout)
        appBarConfiguration = AppBarConfiguration(setOf(),_drawer)
        NavigationUI.setupWithNavController(toolbar,navController,appBarConfiguration)
        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ContactUsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iconMessage.setOnClickListener{
            try {
                callActionWithPermission(
                    requireContext(),
                    requireActivity(),
                    SEND_SMS_PERMISSION,
                    SEND_SMS_PERMISSION_CODE,
                    this::smsToBookshelf
                )
            }catch (e:Exception){
                val toast = Toast.makeText(requireContext(),"couldn't send sms",Toast.LENGTH_SHORT)
                toast.show()
            }

        }

        binding.iconAudioCall.setOnClickListener {
            callActionWithPermission(
                requireContext(),
                requireActivity(),
                CALL_DIAL_PERMISSION,
                CALL_DIAL_PERMISSION_CODE,
                this::callToBookshelf
            )
        }

        binding.telegramCard.setOnClickListener {
            try {
                callTelegramBookshelf()
            }catch (e : Exception){
                val toast = Toast.makeText(requireContext(),"couldn't find telegram app",Toast.LENGTH_SHORT)
                toast.show()
            }
        }

        binding.facebookCard.setOnClickListener {
            try {
                callFacebookBookshelf()
            }catch (e : Exception){

            }
        }
    }

    fun smsToBookshelf(){
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+viewModel.phoneNumber.value))
        startActivity(intent)
    }

    fun callToBookshelf(){
        val intent = Intent(
            Intent.ACTION_DIAL,
            Uri.parse(
                "tel:"+viewModel.phoneNumber.value
            )
        )
        startActivity(intent)
    }

    fun callVideoToBookshelf(){

    }

    fun callTelegramBookshelf(){
        val intent = Intent(Intent.ACTION_VIEW,Uri.parse("https://telegram.me/Movies_Land_TM"))
        intent.`package` = "org.telegram.messenger"
        startActivity(Intent.createChooser(intent,"Share with"))
    }

    fun callFacebookBookshelf(){

        val intent = Intent(Intent.ACTION_VIEW,Uri.parse("fb://page/100069625922455"))
        intent.`package` = "com.facebook.katana"
        startActivity(Intent.createChooser(intent,"Share with"))
    }

    fun callTwitterBookshelf(){
        val intent = Intent(
            Intent.ACTION_VIEW,Uri.parse(
                "twitter://user?user_id="
            )
        )
    }

    fun callWhatsappBookshelf(){

    }

    companion object {

        val SEND_SMS_PERMISSION = android.Manifest.permission.SEND_SMS
        val CALL_DIAL_PERMISSION = android.Manifest.permission.CALL_PHONE

        val SEND_SMS_PERMISSION_CODE = 1237
        val CALL_DIAL_PERMISSION_CODE = 1238

    }
}

