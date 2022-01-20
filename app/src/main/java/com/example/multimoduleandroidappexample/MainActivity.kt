package com.example.multimoduleandroidappexample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
//import com.example.newmodule1.MainActivity2
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus

class MainActivity : AppCompatActivity() {

    lateinit var splitInstallManager: SplitInstallManager
    lateinit var request: SplitInstallRequest
    val DYNAMIC_FEATURE = "Dyanamicfeature"
    lateinit var buttonClick :Button
    lateinit var buttonDeleteDynamicModule :Button
    lateinit var buttonOpenDynamicModule :Button
    lateinit var buttonOpenLibraryModule :Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

         initDynamicModules()
         buttonClick = findViewById<Button>(R.id.buttonClick)
         buttonDeleteDynamicModule = findViewById<Button>(R.id.buttonDeleteNewsModule)
         buttonOpenDynamicModule = findViewById<Button>(R.id.buttonOpenNewsModule)
         buttonOpenLibraryModule = findViewById<Button>(R.id.buttonOpenLibraryModule)
         setClickListeners()




        var mySessionId = 0
        val listener = SplitInstallStateUpdatedListener {
            mySessionId = it.sessionId()
            when (it.status()) {
                SplitInstallSessionStatus.DOWNLOADING -> {
                    val totalBytes = it.totalBytesToDownload()
                    val progress = it.bytesDownloaded()
                    // Update progress bar.
                }
                SplitInstallSessionStatus.INSTALLING -> Log.d("Status", "INSTALLING")
                SplitInstallSessionStatus.INSTALLED -> Log.d("Status", "INSTALLED")
                SplitInstallSessionStatus.FAILED -> Log.d("Status", "FAILED")
                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> startIntentSender(
                    it.resolutionIntent()?.intentSender,
                    null,
                    0,
                    0,
                    0
                )

            }
        }

    }

    private fun unistallDynamicFeature(list: ArrayList<String>) {
        splitInstallManager.deferredUninstall(list)
            .addOnSuccessListener {
                buttonDeleteDynamicModule.visibility = View.GONE
                buttonOpenDynamicModule.visibility = View.GONE
            }
    }

    private fun initDynamicModules() {
        splitInstallManager = SplitInstallManagerFactory.create(this)
        request = SplitInstallRequest
            .newBuilder()
            .addModule(DYNAMIC_FEATURE)
            .build()
    }


    private fun setClickListeners(){
        buttonClick.setOnClickListener {
            if (! isDynamicFeatureDownloaded(DYNAMIC_FEATURE)){
                downloadFeature()
            } else{
                buttonDeleteDynamicModule.visibility = View.VISIBLE
                buttonOpenDynamicModule.visibility = View.VISIBLE
            }
        }

        buttonOpenDynamicModule.setOnClickListener {
            val intent = Intent().setClassName(this, "com.example.dyanamicfeature.DynamicFeatureActivity")
            startActivity(intent)

        }

        buttonDeleteDynamicModule.setOnClickListener {
            val list = ArrayList<String>()
            list.add(DYNAMIC_FEATURE)
            unistallDynamicFeature(list)
        }

        buttonOpenLibraryModule.setOnClickListener {

            try {
               val intent = Intent().setClassName(applicationContext, "com.example.newlibrarymodule.LibraryModuleActivity")
               //val intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }


        }

    }

    private fun isDynamicFeatureDownloaded(feature: String): Boolean =
        splitInstallManager.installedModules.contains(feature)


    private fun downloadFeature() {
        splitInstallManager.startInstall(request)
            .addOnFailureListener {
                Log.d("MainActivity", it.localizedMessage.toString())

            }.addOnSuccessListener {

                buttonDeleteDynamicModule.visibility = View.VISIBLE
                buttonOpenDynamicModule.visibility = View.VISIBLE

            }.addOnCompleteListener {
                Log.d("MainActivity", it.result.toString())

            }
    }


}