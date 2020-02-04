package jp.techacademy.hiroki.autoslideshowapp

import android.content.ContentUris
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.Manifest

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100
    private var mTimer: Timer? = null
    private var mTimerSec = 0.0
    private var mHandler = Handler()
    private  var stopflag: Boolean = false
    private val imageArrayList = arrayListOf<Long>()
    private var imageArrayListposition = 0
    private  var firstflag = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        slidebutton.setOnClickListener{
            if(mTimer == null || !stopflag){
                slidebutton.setText("停止")
                mTimer = Timer()
                stopflag = true
                mTimer!!.schedule(object: TimerTask(){
                    override fun run() {
                        mTimerSec += 2.0

                        mHandler.post{
                            //timer.text = String.format("%.1f",mTimerSec)
                            checkpermission()
                            nextbutton.isEnabled = false
                            backbutton.isEnabled = false
                            if(imageArrayList.size - 1 == imageArrayListposition){
                                imageArrayListposition = 0
                            } else {
                                imageArrayListposition++
                            }
                        }
                   }
                },2000,2000)
            }
            else {
                mTimer!!.cancel()
                if(imageArrayListposition == 0){
                    imageArrayListposition = imageArrayList.size - 1
                } else {
                    imageArrayListposition--
                }
                stopflag = false
                slidebutton.setText("再生")
                nextbutton.isEnabled = true
                backbutton.isEnabled = true
                // timer.text = String.format("%.1f",mTimerSec)
            }
        }

        nextbutton.setOnClickListener{
            val resolver = contentResolver
            val cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
            )
            if(imageArrayList.size - 1 == imageArrayListposition){
                imageArrayListposition = 0
            } else {
                imageArrayListposition++
            }
            val id = imageArrayList[imageArrayListposition]
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)
            cursor.close()
        }

        backbutton.setOnClickListener{
            val resolver = contentResolver
            val cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
            )
            if(imageArrayListposition == 0){
                imageArrayListposition = imageArrayList.size - 1
            } else {
                imageArrayListposition--
            }
            val id = imageArrayList[imageArrayListposition]
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)
            cursor.close()
        }
    }

    private fun getContentsInfo() {
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )

        if(cursor!!.moveToFirst() && firstflag){
            firstflag = false
            do {
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                imageArrayList.add(id)
            }while (cursor.moveToNext())
        }

        if(imageArrayList != null){
            val id = imageArrayList[imageArrayListposition]
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)
            cursor.close()
        }
    }

    private fun checkpermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContentsInfo()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
        } else {
            getContentsInfo()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }

}
