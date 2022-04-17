package com.jaikeerthick.qrscanner

import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.jaikeerthick.qrscanner.databinding.ActivityMainBinding
import com.jaikeerthick.qrscanner.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var codeScanner: CodeScanner? = null
    private val TAG = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        //
        codeScanner = context?.let { CodeScanner(it, binding.scannerView) }

        codeScanner?.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false
        }

        codeScanner?.decodeCallback = DecodeCallback {
            activity?.runOnUiThread {
                Log.d(TAG, "Result: ${it.text}")
                Log.d(TAG, "format: ${it.barcodeFormat.name}")
                Log.d(TAG, "rawbytes: ${it.rawBytes.toString()}")
                vibratePhone()
                findNavController().navigate(R.id.action_homeFragment_to_resultFragment, bundleOf("result" to it.text))
            }
        }
        codeScanner?.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            activity?.runOnUiThread {
                Toast.makeText(context, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
                vibratePhone()
            }
        }

        binding.scannerView.setOnClickListener {
            codeScanner?.startPreview()
        }

        binding.versionName.text = "version: ${BuildConfig.VERSION_NAME}"

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        codeScanner?.startPreview()
    }

    override fun onPause() {
        codeScanner?.releaseResources()
        super.onPause()
    }

    //
    private fun vibratePhone() {
        val vibrator = activity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}