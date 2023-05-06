package com.example.webauto

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.webauto.databinding.ActivityMainBinding
import java.io.IOException
import java.io.PrintStream
import java.util.*

@SuppressLint("SetJavaScriptEnabled")
class MainActivity : AppCompatActivity() {

    lateinit var binding:ActivityMainBinding
    lateinit var pw:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPW()
    }

    private fun initPW() {
        try {
            val scan = Scanner(openFileInput("pw.txt"))
            pw = scan.next()
            initWebView()
        } catch(e: IOException) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("비번 입력")
                .setPositiveButton("입력") {
                        _, _ ->
                    pw = "example password"
                    val output = PrintStream(openFileOutput("pw.txt", MODE_APPEND))
                    output.println(pw)
                    output.close()
                    initWebView()
                }.setNegativeButton("취소") {
                        dlg, _ ->
                    dlg.dismiss()
                    initPW()
                }
            val dlg = builder.create()
            dlg.show()
        }
    }

    private fun initWebView() {
        binding.apply {
            webview.webViewClient = MyWebViewClient()
            webview.webChromeClient = MyWebChromeClient()
            webview.settings.apply {
                this.setSupportMultipleWindows(true) // 새창 띄우기 허용
                this.setSupportZoom(true) // 화면 확대 허용
                this.javaScriptEnabled = true // 자바스크립트 허용
                this.javaScriptCanOpenWindowsAutomatically = true // 자바스크립트 새창 띄우기 허용
                this.loadWithOverviewMode = true // html의 컨텐츠가 웹뷰보다 클 경우 스크린 크기에 맞게 조정
                this.useWideViewPort = true // html의 viewport 메타 태그 지원
                this.builtInZoomControls = true // 화면 확대/축소 허용
                this.displayZoomControls = true
                this.domStorageEnabled = true // 로컬 저장 허용
                this.databaseEnabled = true
            }
            webview.loadUrl("http://jtility37.ddns.net/web/intro.html")
        }
    }

    inner class MyWebChromeClient: WebChromeClient() {
        // 페이지가 로딩되는 시점 콜백
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            binding.progressBar.progress = newProgress
        }
    }

    inner class MyWebViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
//            Thread.sleep(3000)
            Toast.makeText(this@MainActivity, "loaded", Toast.LENGTH_SHORT).show()
            Log.d("check", url)
            view.loadUrl("javascript:document.getElementById('u_img_device').click();")
            view.evaluateJavascript("document.querySelector('#form1 > div.modalpop > div.popupwrap.deviceconnect > div > p:nth-child(7) > input').value = '$pw';"){}

//            #form1 > div.modalpop > div.popupwrap.deviceconnect > div > p:nth-child(7) > input
//            #captcha_str
//            Log.d("view check", "$view")
        }
    }
}
