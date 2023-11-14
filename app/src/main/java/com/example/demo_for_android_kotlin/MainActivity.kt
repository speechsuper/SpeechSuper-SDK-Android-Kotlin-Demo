package com.example.demo_for_android_kotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private val appKey = "Insert your appKey here"
    private val secretKey = "Insert your secretKey here"

    private val mBtn_en_word: Button by lazy {
        findViewById(R.id.mBtn_en_word)
   }
    private val mBtn_en_sent: Button by lazy {
        findViewById(R.id.mBtn_en_sent)
    }
    private val mBtn_en_para: Button by lazy {
        findViewById(R.id.mBtn_en_para)
    }

    private val mBtn_cn_word: Button by lazy {
        findViewById(R.id.mBtn_cn_word)
    }

    private val mBtn_cn_sent: Button by lazy {
        findViewById(R.id.mBtn_cn_sent)
    }
    private val mBtn_cn_para: Button by lazy {
        findViewById(R.id.mBtn_cn_para)
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermissionsAndInitSDK()
        mBtn_en_word.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, TestActivity::class.java)
            val bundle = Bundle()
            bundle.putString("coreType","word.eval")
            intent.putExtras(bundle)
            startActivity(intent)


        }
        mBtn_en_sent.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, TestActivity::class.java)
            val bundle = Bundle()
            bundle.putString("coreType","sent.eval")
            intent.putExtras(bundle)
            startActivity(intent)
        }
        mBtn_en_para.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, TestActivity::class.java)
            val bundle = Bundle()
            bundle.putString("coreType","para.eval")
            intent.putExtras(bundle)
            startActivity(intent)
        }
        mBtn_cn_word.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, TestActivity::class.java)
            val bundle = Bundle()
            bundle.putString("coreType","word.eval.cn")
            intent.putExtras(bundle)
            startActivity(intent)
        }
        mBtn_cn_sent.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, TestActivity::class.java)
            val bundle = Bundle()
            bundle.putString("coreType","sent.eval.cn")
            intent.putExtras(bundle)
            startActivity(intent)
        }
        mBtn_cn_para.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, TestActivity::class.java)
            val bundle = Bundle()
            bundle.putString("coreType","para.eval.cn")
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    private fun initSDK() {
        val ret = SkegnManager.getInstance(this).initEngine(appKey, secretKey);
        if(ret != 0) {
            Toast.makeText(this, "Engine initialization failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Engine initialization succeeded", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestPermissionsAndInitSDK() {
        if(checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_DENIED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED) {
            initSDK()
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.size>0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initSDK()
            } else {
                Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }
}