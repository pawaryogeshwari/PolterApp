package com.polter.mobipolter.activities.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.polter.mobipolter.R
import com.polter.mobipolter.activities.LogEntryDetailActivity
import com.polter.mobipolter.activities.adapters.PhotoAdapter
import com.polter.mobipolter.activities.adapters.PhotoListener
import com.polter.mobipolter.activities.model.PhotoTabEntity
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_photos.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import id.zelory.compressor.Compressor

class PhotosFragment : Fragment(), PhotoListener {


    lateinit var mCameraFileUri: Uri
    internal var mCropFileUri: Uri? = null
    internal val IMG_CHOOSER_REQUEST_CODE = 102
    internal var mCurrentFile: File? = null

    internal var UPLOADING_IMAGE: File? = null
    internal val IMG_CHOOSER_REQ_CODE = 103
    internal val OPEN_SETTINGS_REQ_CODE = 104

    internal var isFromGoLive: Boolean = false
    internal var isRemovePhoto: Boolean = false
    var adapter : PhotoAdapter ?= null
    var photoTabList : ArrayList<PhotoTabEntity> ?= null
    var imagePathList : ArrayList<String> ? = null
    var imageUriList : ArrayList<String> ? = null


    private var lastVisibleItemPosition: Int = 0
    private var linearLayoutManager : LinearLayoutManager ?=null
    private var logId : Int = 0
    private var mPermissionsGranted = true
    private lateinit var scrollListener: RecyclerView.OnScrollListener

    private val mRequiredPermissions = arrayOf(Manifest.permission.CAMERA)

    var photoTabEntity : PhotoTabEntity ? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_photos, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        photoTabList = ArrayList()
        logId = (this.activity as LogEntryDetailActivity).logID

        photoTabEntity = ((activity as LogEntryDetailActivity)).viewModel.findLogPhotoTabById(logId)

        if(photoTabEntity != null){
            imagePathList = photoTabEntity!!.imageURLs as ArrayList<String>
            imageUriList = photoTabEntity!!.imageUriList as ArrayList<String>
            if(imagePathList!!.size >0){
                txtPhotoDelete.setTextColor(activity!!.resources.getColor(R.color.text_enabled_color))
            }else{
                txtPhotoDelete.setTextColor(activity!!.resources.getColor(R.color.gray_color))

            }
          //  txtPhotoCount.text = "Empty"
        }else{
            imagePathList = ArrayList()
            imageUriList = ArrayList()
            var newPhotoEntity = PhotoTabEntity(logId,0,imagePathList,imageUriList,0)
            ((activity as LogEntryDetailActivity)).viewModel?.insertPhotoTabDetail(newPhotoEntity)

        }
       // rvImageList.layoutManager = LinearLayoutManager(activity!!) as RecyclerView.LayoutManager?
        linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL ,false)
        rvImageList.layoutManager = linearLayoutManager
        adapter  = PhotoAdapter(imagePathList!!,imageUriList!!,activity!!, this)
        rvImageList.adapter = adapter


        setRecyclerViewScrollListener()



        txtPhotoNew.setOnClickListener{

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mPermissionsGranted = hasPermissions(activity!!, mRequiredPermissions)
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

        txtPhotoSelect.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK
            startActivityForResult(intent, IMG_CHOOSER_REQ_CODE)
            // finish();
        }

        txtPhotoDelete.setOnClickListener{

            var position = (txtPhotoCount.text.toString()).split("\\s".toRegex())[0];
            Log.d("Delete", ""+position)
            if (position.equals("empty")or(position.equals("keine")))
            {
                return@setOnClickListener
            }

            // val photoEntity = photoTabList?.get(position.toInt()-1)
            imagePathList!!.removeAt(position.toInt()-1)
            imageUriList!!.removeAt(position.toInt()-1)
          //  photoTabList?.remove(photoEntity)
            var updatedPhotoEntity = PhotoTabEntity(logId,imagePathList!!.size,imagePathList,imageUriList,0)
            ((activity as LogEntryDetailActivity)).viewModel?.updatePhotoTabDetail(updatedPhotoEntity)
            adapter?.photoList = imagePathList!!
            adapter?.photoUriList = imageUriList!!
            adapter?.notifyDataSetChanged()


            if(imagePathList?.size == 0){
                txtPhotoCount.text = getString(R.string.empty)
                txtPhotoDelete.setTextColor(activity!!.resources.getColor(R.color.gray_color))
            }else{
                txtPhotoDelete.setTextColor(activity!!.resources.getColor(R.color.text_enabled_color))
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
                    options.inSampleSize = calculateInSampleSize(options, 100, 100)
                    options.inJustDecodeBounds = false
                    val bitmap = BitmapFactory.decodeFile(mCropFileUri!!.path, options)
                    var byteArrayOutputStream = ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    var byteArray = byteArrayOutputStream .toByteArray();
                    val encoded = Base64.encode(byteArray, Base64.DEFAULT)
                    imagePathList?.add(String(encoded))
                    imageUriList?.add(mCropFileUri!!.path!!)
                    var photoEntity = PhotoTabEntity(logId,imagePathList!!.size,imagePathList!!,imageUriList!!,0)
                    ((activity as LogEntryDetailActivity)).viewModel.updatePhotoTabDetail(photoEntity)
                    ((activity as LogEntryDetailActivity)).viewModel.updateLogPhotoEntityById(photoEntity,photoEntity.photoLogDetailID)

                    adapter?.photoList = imagePathList!!
                    adapter?.photoUriList = imageUriList!!
                    adapter?.notifyDataSetChanged()

                    if(imagePathList?.size == 0){
                        txtPhotoCount.text = getString(R.string.empty)
                        txtPhotoDelete.setTextColor(activity!!.resources.getColor(R.color.gray_color))
                    }else{
                        txtPhotoDelete.setTextColor(activity!!.resources.getColor(R.color.text_enabled_color))
                    }
                   // val photoIndex = linearLayoutManager!!.findLastVisibleItemPosition() + 1
                   // txtPhotoCount.text =  ""+photoIndex+"of "+photoTabList?.size

                    try {
                        if (mCurrentFile != null) {
                            val fileSize = mCurrentFile!!.length().toInt()
                            val compressedFile = Compressor(activity!!).compressToFile(mCurrentFile)
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

    companion object {

        internal val DATE_PICKER_ID = 1111
        val PERMISSIONS_REQUEST_CODE = 1234


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
           mCurrentFile = createImageFile("Crop", logId)

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
                        .start(context!!,this)

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
                        .start(context!!,this)

            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    @Throws(IOException::class)
    private fun createImageFile(type: String, id: Int): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        // String imageFileName = type + "_JPG_" + timeStamp + "_";
        val imageFileName = "Polter"+id+"_" + "Photo_"+timeStamp
        val cw = ContextWrapper(activity)
        val directory = cw.getDir("Images", Context.MODE_PRIVATE)
        val storageDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(storageDir,imageFileName+".jpg")
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
            photoFile = createImageFile("Camera",logId)
           // mCurrentFile = photoFile
        }
        catch (ex:IOException) {
            ex.printStackTrace()
        }

        if (photoFile != null)
        {
            mCameraFileUri = FileProvider.getUriForFile(activity!!,
                    "com.mobipolter.provider",
                    photoFile!!)
        }

        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, mCameraFileUri)
        startActivityForResult(takePicture, IMG_CHOOSER_REQ_CODE)//zero can be replaced with any action code

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



    private fun setRecyclerViewScrollListener() {
        rvImageList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val totalItemCount = recyclerView.layoutManager!!.itemCount
                lastVisibleItemPosition = linearLayoutManager!!.findLastVisibleItemPosition()
                val currentPhotoIndex = lastVisibleItemPosition + 1
//                if (totalItemCount == lastVisibleItemPosition + 1) {
//                                    txtPhotoCount.text = "Empty"hPhotoCount.text = ""+lastVisibleItemPosition+" of "+totalItemCount
//                }
                if(currentPhotoIndex == 0){
                    txtPhotoCount.text = getString(R.string.empty)
                }else{
                    txtPhotoCount.text = ""+currentPhotoIndex+" of "+totalItemCount
                }

            }
        })
    }

    override fun onItemDisplay() {
        lastVisibleItemPosition = linearLayoutManager!!.findLastVisibleItemPosition()
        var currentPhotoIndex = 0
        if(lastVisibleItemPosition == -1){
            currentPhotoIndex = 1
        }else{
            currentPhotoIndex = lastVisibleItemPosition + 1
        }

//                if (totalItemCount == lastVisibleItemPosition + 1) {
//                    txtPhotoCount.text = ""+lastVisibleItemPosition+" of "+totalItemCount

        if(currentPhotoIndex == 0){
            txtPhotoCount.text = getString(R.string.empty)
        }else{
            txtPhotoCount.text = ""+currentPhotoIndex+" of "+imagePathList?.size

        }
//                }

    }


    fun writeFileOnInternalStorage(sFileName: String, sBody: ByteArray) {
        try {
            var compressFile = createImageFile("Compress",logId)
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


}// Required empty public constructor