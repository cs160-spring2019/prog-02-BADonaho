package com.example.photoannotation;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.graphics.Bitmap.Config.ARGB_8888;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;
    File photoFile;
    DrawView drawView;
    ImageView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button takePhoto = findViewById(R.id.takePhoto);
        final Button setPhoto = findViewById(R.id.setPhoto);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        setPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                grabImage();
            }
        });
        drawView = findViewById(R.id.drawView);
        final Button bigBrush = findViewById(R.id.bigBrush);
        final Button smallBrush = findViewById(R.id.smallBrush);
        final Button draw = findViewById(R.id.draw);
        final Button erase = findViewById(R.id.erase);
        final Button clear = findViewById(R.id.clear);
        bigBrush.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drawView.bigBrush();
            }
        });
        smallBrush.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drawView.smallBrush();
            }
        });
        draw.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drawView.draw();
            }
        });
        erase.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drawView.erase();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drawView.clearStrokes();
            }
        });
        photoView = findViewById(R.id.photoView);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                System.out.println("Error occurred while creating the File");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void grabImage()
    {
        if (photoFile != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            bitmap = bitmap.copy(ARGB_8888, true);
            photoView.setImageBitmap(bitmap);
        }
    }
}
