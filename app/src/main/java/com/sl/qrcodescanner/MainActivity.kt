package com.sl.qrcodescanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner
    val MY_CAMERA_PERMISSION_REQUEST = 1111
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        codeScanner = CodeScanner(this,scannerview)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                Toast.makeText(this,"Scan Result: ${it.text}", Toast.LENGTH_LONG).show()
                val openURL = Intent(Intent.ACTION_VIEW)
                openURL.data = Uri.parse("${it.text}")
                startActivity(openURL)
            }
        }

        codeScanner.errorCallback = ErrorCallback {

            runOnUiThread {
                Toast.makeText(this,"Camera Error: ${it.message}",Toast.LENGTH_LONG).show()
            }
        }

        checkPermission()
        codeScanner.startPreview()
    }

    fun checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!== PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),MY_CAMERA_PERMISSION_REQUEST)
        }else{
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()

    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}
