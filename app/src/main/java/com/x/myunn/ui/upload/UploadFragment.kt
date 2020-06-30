package com.x.myunn.ui.upload

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.x.myunn.BuildConfig
import com.x.myunn.R
import com.x.myunn.adapter.ImageAdapter
import com.x.myunn.model.Image
import com.x.myunn.utils.*
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.*
import kotlinx.android.synthetic.main.fragment_upload.view.*
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException


class UploadFragment : Fragment() {

    private lateinit var uploadViewModel: UploadViewModel

    private var uploadFragmentJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + uploadFragmentJob)

    private val REQUEST_CAMERA_CODE = 11
    private val REQUEST_GALLERY_CODE = 22

    private var imageUri = ""

    private var postImagesList = arrayListOf<Image>()

    private var postImagesUploadList = arrayListOf<Image>()

    private var cameraImageUri: Uri? = null

    lateinit var getImage: Bitmap

    lateinit var cameraImageFile: File

    lateinit var galleryImageFile: File

    private lateinit var adapter: ImageAdapter

    private lateinit var postImages: RecyclerView

    private lateinit var fab: FloatingActionButton



    

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_upload, container, false)

//
//
//        val application = requireNotNull(this.activity).application
//
//        val dataSource = cwDatabase.getInstance(application).DatabaseDao
//
        val viewModelFactory = UploadViewModelFactory()

        uploadViewModel = ViewModelProvider(this, viewModelFactory).get(UploadViewModel::class.java)

        postImages = view.findViewById(R.id.upload_post_images)

        fab = view.findViewById(R.id.upload_post_btn)

        view.camera_btn.setOnClickListener  {

            requestStoragePermission(true)

//            val options = arrayOf<CharSequence>("Camera", "Gallery")
//
//            val builder = AlertDialog.Builder(requireContext())
//            builder.setTitle("Select Image from")
//            builder.setIcon(R.drawable.ic_add_a_photo_black_24dp)
//            builder.setItems(options) { dialogInterface, i ->
//                if (i == 0) {
//                    Log.d("TAG-U", "      Camera Clicked")
//                    requestStoragePermission(true)
//
//
//                } else if (i == 1) {
//
//                    Log.d("TAG-U", "      GALLERY Clicked")
//                    requestStoragePermission(false)
//
//                }else{
//                    dialogInterface.cancel()
//                }
//            }
//            builder.show()

        }

        view.gallery_btn.setOnClickListener {
            requestStoragePermission(false)
        }

        view.upload_post_btn.setOnClickListener {
            uiScope.launch {
                uploadFeed(it)
            }
        }

        view.upload_post_images.setLayoutManager(
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )

        adapter = ImageAdapter(postImagesList, requireContext(), 3)

        postImages.setHasFixedSize(true)
        postImages.adapter = adapter

        if (adapter.itemCount == 0){
            postImages.visibility = View.GONE
            Toast.makeText(context, "getItemCount(): ${adapter.itemCount}", Toast.LENGTH_SHORT).show()
        }

        postImages.visibility = View.VISIBLE
        Toast.makeText(context, "getItemCount(): ${adapter.itemCount}", Toast.LENGTH_SHORT).show()




        return view
    }

    /**
     * Capture image from camera
     */
    private fun getImageFromCamera() {

        Log.d("TAG-U", "      getImageFrom CAMERA ()")

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            // Create the File where the photo should go

            try {
                cameraImageFile = createImageFile(requireActivity())
            } catch (ex: IOException) {
                ex.printStackTrace()
                // Error occurred while creating the File
            }
            cameraImageUri = FileProvider.getUriForFile(
                requireContext(), BuildConfig.APPLICATION_ID + ".provider", cameraImageFile)


            takePictureIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
            startActivityForResult(takePictureIntent, REQUEST_CAMERA_CODE)
        }
    }

    /**
     * Select image fro gallery
     */
    private fun getImageFromGallery() {

        Log.d("TAG-U", "      getImageFrom GALLERY ()")
       
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_GALLERY_CODE)

    }

    private fun requestStoragePermission(isCamera: Boolean) {

        Log.d("TAG-U", "      requestStoragePermission()")

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ) {

                Log.d("TAG-U", "      requestStoragePermission():  PERMISSION NOT GRANTED")

                if (isCamera){
                    Log.d("TAG-U", "      requestStoragePermission():  request camera")
                    ActivityCompat.requestPermissions(requireActivity(),
                        arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ), REQUEST_CAMERA_CODE)

                    Log.d("TAG-U", "      requestStoragePermission():  after request camera")
                }


                else {
                    Log.d("TAG-U", "      requestStoragePermission():  request gallery")
                    ActivityCompat.requestPermissions(requireActivity(),
                        arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ), REQUEST_GALLERY_CODE)

                    Log.d("TAG-U", "      requestStoragePermission():  after request gallery")
                }


            }
            else {
                Log.d("TAG-U", "      requestStoragePermission():  PERMISSION GRANTED")
                if (isCamera){
                    Log.d("TAG-U", "      requestStoragePermission():  call camera")
                    getImageFromCamera()
                }else {

                    Log.d("TAG-U", "      requestStoragePermission():  call gallery")
                    getImageFromGallery()
                }
            }
        } else {
            if (isCamera){
                getImageFromCamera()
            }else getImageFromGallery()
        }

    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Need Permissions")
        builder.setMessage(
            "This app needs permission to use this feature. You can grant them in app settings."
        )
        builder.setPositiveButton("GOTO SETTINGS") { dialog: DialogInterface, _: Int ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
            dialog.cancel() }
        builder.show()
    }

    // navigating user to app settings
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri =
            Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Log.d("TAG-U", "      onRequestPermissionsResult()")

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                Log.d("TAG-U", "      onRequestPermissionsResult():  Permission Granted")

                when (requestCode){
                    REQUEST_CAMERA_CODE -> {
                        Log.d("TAG-U", "      onRequestPermissionsResult():   call camera")
                        getImageFromCamera()
                    }

                    REQUEST_GALLERY_CODE -> {
                        Log.d("TAG-U", "      onRequestPermissionsResult():  call gallery")
                        getImageFromGallery()
                    }

                    else -> {
                        Log.d("TAG-U", "      onRequestPermissionsResult():  no code")
                        showSettingsDialog()
                    }

                }

            }
            else {
                Log.d("TAG-U", "      onRequestPermissionsResult():  not Permission Granted")
               showSettingsDialog()
            }

    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {

            when (requestCode) {
                    REQUEST_CAMERA_CODE -> {

//                        getImage = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, photoURI)
//
//                        //val source = ImageDecoder.createSource(requireActivity().contentResolver, photoURI)
//                        //getImage = ImageDecoder.decodeBitmap(source)
//
//
//                        requireView().imagetest.setImageBitmap(getImage)



                        //Get Uri from generated file
                        //uriText.text = imageUri

                        //compressImage(imageUri)

                        //requireView().imagetest.setImageURI(photoURI)
                        Log.d("TAG-U", "      onActivityResult():  photoURI = $cameraImageUri")

//                        val path = getRealPathFromURI(photoURI, requireContext())
//                        val file = File(path!!)
//                        val compressedBitmap = ImageUtil.compressImage(file, path)
//
                       loadImage(cameraImageUri, cameraImageFile)

                    }

                    REQUEST_GALLERY_CODE -> {
                        if (data!!.data != null){
                            val selectedImageURI = data.data


                            Log.d("TAG-U", "      onActivityResult():  selectedImageUri = $selectedImageURI")




//                        val source = ImageDecoder.createSource(requireActivity().contentResolver, selectedImageURI)
//                        getImage = ImageDecoder.decodeBitmap(source)
//
//                        requireView().imagetest.setImageBitmap(getImage)

                            galleryImageFile = FileUtils.getFile(requireContext(), selectedImageURI)!!
                            loadImage(selectedImageURI!!, galleryImageFile)

                        }
                        else{
                            Toast.makeText(requireContext(), "Error, Image is Invalid.",
                                Toast.LENGTH_SHORT).show()
                        }



                    }

                    else -> {
                        Log.d("TAG-U", "      onActivityResult():  No REQUEST_CODE")
                    }
                }
        }
        else{
            Log.d("TAG-U", "      onActivityResult():  RESULT NOT OK")
        }
    }

    private fun loadImage(uri: Uri?, file: File) {

        //Log.d("TAG-U", "      loadImage():  path:  ${photoFile.path}")

        val getBitmap = getBitmapDrawablefromUri(requireActivity(), uri)

        if (getBitmap != null){
            var compressedImageFile: File?

            uiScope.launch {
                withContext(Dispatchers.IO){

                    fab.setImageResource(R.drawable.loading_animation)

                    compressedImageFile = Compressor.compress(requireContext(), file) {
                        resolution(612, 816)
                        quality(10)
                        format(Bitmap.CompressFormat.JPEG)
                        size(2_097_152) // 2 MB
                        destination(createCacheImageFile())
                    }

                    Log.d("TAG-S", "    compressed IO Uri : ${Uri.fromFile(compressedImageFile)}  ")

                    withContext(Dispatchers.Main){
                        fab.setImageResource(R.drawable.ic_menu_send)
                        Log.d("TAG-S", "    compressed Main p: ${compressedImageFile!!.path}  ")


                        val mImage = Image(Uri.fromFile(compressedImageFile!!).toString(),
                                            getBitmap,
                                            compressedImageFile!!)

                        //Log.d("TAG-A", "    uri: $uri  ")

                        //Log.d("TAG-A", "    getImage: $getImage  ")

                        postImagesList.add(mImage)
                        adapter.notifyDataSetChanged()
                        postImages.scrollToPosition(adapter.itemCount - 1)

                    }

                }


            }


//        Log.d("TAG-S", "    Uri.fromFile : ${Uri.fromFile(photoFile)}  ")
//
//        Log.d("TAG-S", "    photoUri : ${photoURI}  ")



            //val getImage = getBitmapfromUri(requireActivity(), Uri.fromFile(compressedImageFile))


            //Log.d("TAG-S", "    compressed : ${}  ")


            //requireView().imagetest.setImageBitmap(getImage)



            // Log.d("TAG-A", "    Image: $mImage  ")

            //Log.d("TAG-A", "    mPostImages: $mPostImages  ")

        }
        else{
            Toast.makeText(requireContext(), "couldn't locate image.", Toast.LENGTH_SHORT).show()
        }


    }

    private fun uploadFeed(v: View) {

        val postDescription = requireView().post_text.text.toString()


        if (postDescription.isBlank()) {
            mySnackBar(v, "Empty field !!")

        } else {

            if (adapter.getCurrentImageList() == null){

                uploadViewModel.uploadPost(postDescription, null)
            }else{

                uploadViewModel.uploadPost(postDescription, adapter.getCurrentImageList())
            }


            findNavController().navigate(R.id.action_nav_upload_to_nav_home)
            Toast.makeText(requireContext(), "Uploaded Sucessfully", Toast.LENGTH_LONG).show()

        }

    }
}



