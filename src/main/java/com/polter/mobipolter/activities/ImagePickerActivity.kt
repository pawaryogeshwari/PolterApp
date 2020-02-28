package com.polter.mobipolter.activities

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.polter.mobipolter.R

import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date

import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.img_chooser_dialog.view.*


class ImagePickerActivity : AppCompatActivity() {

    internal var mCameraFileUri: Uri ?= null
    internal var mCropFileUri: Uri? = null
    internal val IMG_CHOOSER_REQ_CODE = 101
    internal val OPEN_SETTINGS_REQ_CODE = 104

    internal var mCurrentFile: File? = null
    internal var isFromGoLive: Boolean = false
    internal var isRemovePhoto: Boolean = false


    internal var UPLOADING_IMAGE: File ? = null
    private var mPermissionsGranted = true

    private val mRequiredPermissions = arrayOf(Manifest.permission.CAMERA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imagepicker)

        if (intent.extras != null) {
            isFromGoLive = intent.getBooleanExtra("isFromGoLive", false)
            isRemovePhoto = intent.getBooleanExtra("isRemovePhoto", false)
        }


        selectImage()

        /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPermissionsGranted = hasPermissions(this, mRequiredPermissions);
            if (!mPermissionsGranted){
                ActivityCompat.requestPermissions(ImagePickerActivity.this, mRequiredPermissions, PERMISSIONS_REQUEST_CODE);
               // finish();
            }
            else
                openImageChooserIntent();
        }else{
            mPermissionsGranted = true;

            if (mPermissionsGranted) {
                openImageChooserIntent();
            }else{
                finish();
            }
        }*/
        // openImageChooserIntent();
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mPermissionsGranted = true
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {

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
                            val dialog = permissionPopup()
                            dialog.show()

                        } else {
                            finish()
                            // user did NOT check "never ask again"
                            // this is a good place to explain the user
                            // why you need the permission and ask if he wants
                            // to accept it (the rationale)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        finish()
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
                        isCamera = action == android.provider.MediaStore.ACTION_IMAGE_CAPTURE
                    }
                }
                val selectedImageUri: Uri?
                if (isCamera) {
                    performCrop(mCameraFileUri!!)
                } else {
                    selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        deleteFile(mCameraFileUri)
                        performCrop(selectedImageUri)
                    } else {
                        performCrop(mCameraFileUri!!)
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
                /* BitmapFactory.decodeFile(mCropFileUri.getPath(), options);
                options.inSampleSize = calculateInSampleSize(options, 100, 100);
                options.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeFile(mCropFileUri.getPath(), options);
                imgProfilePic.setImageBitmap(bitmap);*/
                //  imgRemoveIcon.setVisibility(View.VISIBLE);
                try {
                    if (mCurrentFile != null) {
                        val fileSize = mCurrentFile!!.length().toInt()
                        val compressedFile = Compressor(this).compressToFile(mCurrentFile!!)
                        println(compressedFile.path)
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

                        writeFileOnInternalStorage(compressedFile.name, bytes)

                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val returnIntent = Intent()
                returnIntent.putExtra("bundleFile", UPLOADING_IMAGE)
                if (mCropFileUri != null) {
                    returnIntent.putExtra("bundleUri", mCropFileUri!!.toString())
                }
                setResult(Activity.RESULT_OK, returnIntent)
                finish()


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                deleteFile(mCropFileUri)
                val returnIntent = Intent()
                // returnIntent.putExtra("outputImage",UPLOADING_IMAGE);
                setResult(Activity.RESULT_CANCELED, returnIntent)
                finish()

            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            deleteFile(mCameraFileUri)
            deleteFile(mCropFileUri)
            val returnIntent = Intent()
            // returnIntent.putExtra("outputImage",UPLOADING_IMAGE);
            setResult(Activity.RESULT_CANCELED, returnIntent)
            finish()

        }
    }


    private fun openImageChooserIntent() {

        val targets = ArrayList<Intent>()

        var photoFile: File? = null
        try {
            photoFile = createImageFile("Camera")
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        if (photoFile != null) {
            mCameraFileUri = FileProvider.getUriForFile(this,
                    "live.vio.provider",
                    photoFile)
        }

        val captureIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        val packageManager = this.packageManager
        val listCam = packageManager.queryIntentActivities(captureIntent, 0)
        // Collections.sort(listCam, new ResolveInfo.DisplayNameComparator(this.getPackageManager()));
        for (res in listCam) {
            val packageName = res.activityInfo.packageName
            val intents = Intent(captureIntent)
            intents.component = ComponentName(packageName, res.activityInfo.name)
            intents.setPackage(packageName)
            intents.putExtra(MediaStore.EXTRA_OUTPUT, mCameraFileUri)
            targets.add(intents)
        }


        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        //intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        val candidates = this.packageManager.queryIntentActivities(intent, 0)
        //  Collections.sort(candidates, new ResolveInfo.DisplayNameComparator(this.getPackageManager()));
        for (candidate in candidates) {
            val packageName = candidate.activityInfo.packageName
            if (packageName != "com.google.android.apps.plus" && packageName != "com.android.documentsui") {
                val addChooserItem = Intent()
                addChooserItem.type = "image/*"
                addChooserItem.action = Intent.ACTION_PICK
                //addChooserItem.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                addChooserItem.setPackage(packageName)
                targets.add(addChooserItem)
            }
        }


        val target: Intent
        if (targets.isEmpty()) {
            target = Intent()
        } else {
            target = targets[targets.size - 1]
            targets.removeAt(targets.size - 1)
        }


        // startActivityForResult(create(this.getPackageManager(),target,"Profile Picture",listCam),IMG_CHOOSER_REQ_CODE);

        val chooserIntent = Intent.createChooser(target, "Profile Picture")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targets.toTypedArray<Parcelable>())
        startActivityForResult(chooserIntent, IMG_CHOOSER_REQ_CODE)
    }

    @Throws(IOException::class)
    private fun createImageFile(type: String): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        // String imageFileName = type + "_JPG_" + timeStamp + "_";
        val imageFileName = type
        val cw = ContextWrapper(this.applicationContext)
        val directory = cw.getDir("Images", Context.MODE_PRIVATE)
        val storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        )
    }

    private fun performCrop(uri: Uri) {
        try {
            mCurrentFile = createImageFile("Crop")
            mCropFileUri = Uri.fromFile(mCurrentFile)
            if (isFromGoLive) {
                CropImage.activity(uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setActivityTitle("CROP")
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setCropMenuCropButtonTitle("Done")
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
                        .setActivityTitle("CROP")
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setCropMenuCropButtonTitle("Done")
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

    private fun deleteFile(uri: Uri?) {
        try {
            if (uri != null) {
                val contentResolver = this.contentResolver
                contentResolver.delete(uri, null, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                val file = File(uri!!.path!!)
                file.canonicalFile.delete()
            } catch (io: IOException) {
                io.printStackTrace()
            }

        }


    }

    private fun permissionPopup(): AlertDialog {
        return AlertDialog.Builder(this)
                .setTitle("Permission Needed")
                .setMessage("To change Profile photo, app requires access to camera. Please go to settings and give the permission")
                .setPositiveButton("OK") { dialog, whichButton ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivityForResult(intent, OPEN_SETTINGS_REQ_CODE)
                    finish()
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                    finish()
                }
                .setCancelable(false)
                .create()
    }

    fun writeFileOnInternalStorage(sFileName: String, sBody: ByteArray) {
        try {
            val compressFile = createImageFile("Compress")
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


    private fun selectImage() {


        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Remove Photo")

        val builder = AlertDialog.Builder(this@ImagePickerActivity)

        //  builder.setTitle("Profile Picture");

        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.img_chooser_dialog, null)
        builder.setView(dialogView)
        builder.setCancelable(true)
        builder.setOnCancelListener { dialog ->
            dialog.dismiss()
            finish()
        }

        val dialog = builder.create()

        if (!isRemovePhoto) {
            dialogView.txtRemove.setTextColor(resources.getColor(R.color.colorAccent))
        }

        dialogView.txtCam.setOnClickListener(View.OnClickListener {
            dialog.dismiss()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mPermissionsGranted = hasPermissions(this@ImagePickerActivity, mRequiredPermissions)
                if (!mPermissionsGranted) {
                    ActivityCompat.requestPermissions(this@ImagePickerActivity, mRequiredPermissions, PERMISSIONS_REQUEST_CODE)
                    // finish();
                } else
                    openCameraIntent()
            } else {
                mPermissionsGranted = true

                if (mPermissionsGranted) {
                    openCameraIntent()
                } else {
                    finish()
                }
            }
        })

        dialogView.txtGallery.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK
            startActivityForResult(intent, IMG_CHOOSER_REQ_CODE)
            // finish();
        })


        dialogView.txtRemove.setOnClickListener(View.OnClickListener {
            if (isRemovePhoto) {
                dialog.dismiss()
                val returnIntent = Intent()
                setResult(10, returnIntent)
                finish()
            }
        })
// builder.setView()

        builder.show()


}


private fun openCameraIntent() {

var photoFile:File? = null
try
{
photoFile = createImageFile("Camera")
}
catch (ex:IOException) {
ex.printStackTrace()
}

if (photoFile != null)
{
mCameraFileUri = FileProvider.getUriForFile(this,
"live.swing.provider",
photoFile!!)
}

val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
takePicture.putExtra(MediaStore.EXTRA_OUTPUT, mCameraFileUri)
startActivityForResult(takePicture, IMG_CHOOSER_REQ_CODE)//zero can be replaced with any action code

}

private fun delPicPopup():AlertDialog {
return AlertDialog.Builder(this)
.setMessage("Remove Profile Photo?")

.setPositiveButton("Remove", object:DialogInterface.OnClickListener {
public override fun onClick(dialog:DialogInterface, whichButton:Int) {
 /*if (mCropFileUri != null) {
                            mCropFileUri = null;
                            imgProfilePic.setImageDrawable(getResources().getDrawable(R.drawable.ic_default_pic));
                            imgRemoveIcon.setVisibility(View.GONE);
                        }*/


                        // profileImage = "";
                        // imgProfilePic.setImageDrawable(getResources().getDrawable(R.drawable.ic_default_pic));
                        //imgRemoveIcon.setVisibility(View.GONE);
                    }
})
.setNegativeButton("Cancel", object:DialogInterface.OnClickListener {
public override fun onClick(dialog:DialogInterface, which:Int) {
dialog.dismiss()
}
})
.setCancelable(false)
.create()
}

companion object {
private val PERMISSIONS_REQUEST_CODE = 0x2


private fun hasPermissions(context:Context, permissions:Array<String>):Boolean {
for (permission in permissions)
if (context.checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
return false

return true
}
}

}

