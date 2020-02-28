package com.polter.mobipolter.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.util.Base64
import android.view.View
import com.polter.mobipolter.R
import com.polter.mobipolter.activities.fragments.PhotosFragment
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.layout_logos.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class AddLogoActivity : Activity(){

    private var mPermissionsGranted = true
    private val mRequiredPermissions = arrayOf(Manifest.permission.CAMERA)
    lateinit var mCameraFileUri: Uri
    internal var mCropFileUri: Uri? = null
    internal val IMG_CHOOSER_REQUEST_CODE = 102
    internal var mCurrentFile: File? = null

    internal var UPLOADING_IMAGE: File? = null
    internal val IMG_CHOOSER_REQ_CODE = 103
    internal val OPEN_SETTINGS_REQ_CODE = 104

    internal var isFromGoLive: Boolean = false
    internal var isRemovePhoto: Boolean = false
    lateinit var mAppSharedPreferences : SharedPreferences
    lateinit var mAppSharedPreferencesEditor : SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_logos)

        mAppSharedPreferences = getSharedPreferences("App_Pref",0)
        mAppSharedPreferencesEditor = mAppSharedPreferences.edit()

        if(mAppSharedPreferences.getString("imgLogo",null)!= null){

            imgLogo.setImageURI(Uri.parse(mAppSharedPreferences.getString("imgLogo",null)))
            txtLogoDelete.visibility = View.VISIBLE
            txtLogoDelete.setTextColor(resources.getColor(R.color.text_enabled_color))
        }

        imgBack.setOnClickListener {
            finish()
        }

        txtLogoSelect.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK
            startActivityForResult(intent, IMG_CHOOSER_REQ_CODE)
            // finish();
        }

        txtLogoDelete.setOnClickListener{

            imgLogo.setImageDrawable(null)
            mAppSharedPreferencesEditor.putString("imgLogo",null)
            mAppSharedPreferencesEditor.commit()
            txtLogoDelete.setTextColor(resources.getColor(R.color.gray_color))

        }

        txtNewLogo.setOnClickListener{

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mPermissionsGranted = hasPermissions(this, mRequiredPermissions)
                if (!mPermissionsGranted) {
                    requestPermissions(mRequiredPermissions, PERMISSIONS_REQUEST_CODE)
                    // finish();
                }else{
                    openCameraIntent()
                }
                // openCameraIntent()
            } else {
                mPermissionsGranted = true

                if (mPermissionsGranted) {
                    openCameraIntent()
                } else {
                    //finish()
                }
            }
        }
    }

    companion object {

        internal val DATE_PICKER_ID = 1111
        private val PERMISSIONS_REQUEST_CODE = 1234


        internal fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            var inSampleSize = 1
            if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
                val halfHeight = options.outHeight / 2
                val halfWidth = options.outWidth / 2
                while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                    inSampleSize *= 2
                }
            }
            return inSampleSize
        }
    }

    private fun performCrop(uri: Uri) {
        try {
            mCurrentFile = createImageFile("Crop", 1)

            mCropFileUri = Uri.fromFile(mCurrentFile)
            if (isFromGoLive) {
                CropImage.activity(uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setActivityTitle(getString(R.string.crop))
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setCropMenuCropButtonTitle(getString(R.string.done))
                        .setAllowRotation(true)
                        .setAllowFlipping(true)
                        .setOutputUri(mCropFileUri)
                        .setAutoZoomEnabled(false)
                        .setFixAspectRatio(true)
                        .setAspectRatio(16, 9)
                        .setMinCropWindowSize(480, 270)
                        .start(this)

            } else {
                CropImage.activity(uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setActivityTitle(getString(R.string.crop))
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setCropMenuCropButtonTitle(getString(R.string.done))
                        .setAllowRotation(true)
                        .setAllowFlipping(true)
                        .setOutputUri(mCropFileUri)
                        .setAutoZoomEnabled(false)
                        .setFixAspectRatio(true)
                        .setMinCropWindowSize(250, 250)
                        .start(this)

            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    @Throws(IOException::class)
    private fun createImageFile(type: String, id: Int): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        // String imageFileName = type + "_JPG_" + timeStamp + "_";
        val imageFileName = "Polter"+id+"_" + "Logo_"+timeStamp
        val cw = ContextWrapper(this)
        val directory = cw.getDir("Images", Context.MODE_PRIVATE)
        val storageDir = this?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(storageDir,imageFileName+".png")
        return file
        /*return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        )*/
    }

    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions)
            if (context.checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false

        return true
    }

    private fun openCameraIntent() {

        var photoFile:File? = null
        try
        {
            photoFile = createImageFile("Camera",1)
            // mCurrentFile = photoFile
        }
        catch (ex:IOException) {
            ex.printStackTrace()
        }

        if (photoFile != null)
        {
            mCameraFileUri = FileProvider.getUriForFile(this,
                    "com.mobipolter.provider",
                    photoFile!!)
        }

        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, mCameraFileUri)
        startActivityForResult(takePicture, IMG_CHOOSER_REQ_CODE)//zero can be replaced with any action code

    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {


            if (requestCode == IMG_CHOOSER_REQ_CODE) {
                val isCamera: Boolean
                if (data == null) {
                    isCamera = true
                } else {
                    val action = data.action
                    if (action == null) {
                        isCamera = false
                    } else {
                        isCamera = action == MediaStore.ACTION_IMAGE_CAPTURE
                    }
                }
                val selectedImageUri: Uri?
                if (isCamera) {
                    performCrop(mCameraFileUri)
                } else {
                    selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        // deleteFile(mCameraFileUri)
                        performCrop(selectedImageUri)
                    } else {
                        performCrop(mCameraFileUri)
                    }
                }
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFile(mCropFileUri!!.path, options)
                options.inSampleSize = PhotosFragment.calculateInSampleSize(options, 100, 100)
                options.inJustDecodeBounds = false
                val bitmap = BitmapFactory.decodeFile(mCropFileUri!!.path, options)
                var byteArrayOutputStream = ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                var byteArray = byteArrayOutputStream .toByteArray();
                val encoded = Base64.encode(byteArray, Base64.DEFAULT)

                imgLogo.setImageURI(mCropFileUri!!)
                mAppSharedPreferencesEditor.putString("imgLogo",mCropFileUri.toString())
                mAppSharedPreferencesEditor.commit()
                txtLogoDelete.visibility = View.VISIBLE
                txtLogoDelete.setTextColor(resources.getColor(R.color.text_enabled_color))
               // imagePathList?.add(String(encoded))
               // imageUriList?.add(mCropFileUri!!.path!!)
              //  var photoEntity = PhotoTabEntity(logId,imagePathList!!.size,imagePathList!!,imageUriList!!,0)
              //
                // val photoIndex = linearLayoutManager!!.findLastVisibleItemPosition() + 1
                // txtPhotoCount.text =  ""+photoIndex+"of "+photoTabList?.size

                try {
                    if (mCurrentFile != null) {
                        val fileSize = mCurrentFile!!.length().toInt()
                        val compressedFile = Compressor(this).compressToFile(mCurrentFile)
                        println(compressedFile.getPath())
                        val size = compressedFile.length().toInt()
                        val bytes = ByteArray(size)
                        try {
                            val buf = BufferedInputStream(FileInputStream(compressedFile))
                            buf.read(bytes, 0, bytes.size)
                            buf.close()
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                        writeFileOnInternalStorage(compressedFile.getName(), bytes)

                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }


            }
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {


        }
        if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == IMG_CHOOSER_REQUEST_CODE) {

            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mPermissionsGranted = true
        when (requestCode) {
            PhotosFragment.PERMISSIONS_REQUEST_CODE -> {

                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // openImageChooserIntent();
                    openCameraIntent()

                } else {

                    // permission was not granted

                    try {

                        var showRationale = false
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            showRationale = shouldShowRequestPermissionRationale(permissions[0])
                        }
                        if (!showRationale) {
                            // user also CHECKED "never ask again"
                            // you can either enable some fall back,
                            // disable features of your app
                            // or open another dialog explaining
                            // again the permission and directing to
                            // the app setting
                            //  val dialog = permissionPopup()
                            //  dialog.show()

                        } else {
                            // finish()
                            // user did NOT check "never ask again"
                            // this is a good place to explain the user
                            // why you need the permission and ask if he wants
                            // to accept it (the rationale)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // finish()
                    }


                    // mPermissionsGranted = false;
                    //  finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                // Check the result of each permission granted
            }
        }
    }



    fun writeFileOnInternalStorage(sFileName: String, sBody: ByteArray) {
        try {
            var compressFile = createImageFile("Compress",1)
            // compressFile = mCurrentFile!!
            val outputStream: FileOutputStream
            try {
                outputStream = FileOutputStream(compressFile)
                outputStream.write(sBody)
                outputStream.close()
                UPLOADING_IMAGE = compressFile
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }



}