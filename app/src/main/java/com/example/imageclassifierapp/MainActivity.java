package com.example.imageclassifierapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
// stackoverflow - camera activity returning null android

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView picImageView;
    String picPath;
    Uri pic2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create Button and ImageView objects
        Button takePicButton = findViewById(R.id.takePicBtn);
        picImageView = findViewById(R.id.picImageView);

        // Disable button if user has no camera
        if (!hasCamera()) {
            takePicButton.setEnabled(false);
        }
    }

    // Check if user has a camera
    private boolean hasCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    // When button is clicked, start intent to take picture, and open camera
    public void takePic(View v) {
        Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Take a picture and pass results to onActivityResult
        if (takePicIntent.resolveActivity(getPackageManager()) != null) {
            File imageFile = null;
            try {
                imageFile = createImageFile();
            } catch (IOException ex) {
                System.out.println("Error occurred while creating File.");
            }
            if (imageFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        imageFile);

                pic2 = photoURI;
                takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePicIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    // After picture taken, return pic to imageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                // Get the photo data
//                Bundle extras = data.getExtras();
//                // Returns photo info and converts to bitmap
//                Bitmap imageBitmap = (Bitmap) extras.get("data");
//                picImageView.setImageBitmap(imageBitmap);
            }
        } catch(Exception ex) {
            // Handle Exception
            ex.printStackTrace();
        }
    }

    // Create path/file for pic
    private File createImageFile() throws IOException {
        // Create an image file name & instantiate File
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        picPath = image.getAbsolutePath();
        return image;
    }

    // Add pic to gallery on user's device
    private void saveToPhone() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(picPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}
