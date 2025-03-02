package com.aaditya.inv.dialogs;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.aaditya.inv.R;
import com.aaditya.inv.utils.Commons;
import com.aaditya.inv.utils.Constants;
import com.aaditya.inv.utils.CustomListener;
import com.aaditya.inv.utils.InMemoryInfo;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BottomSheetPhotoUploadDialog extends BottomSheetDialogFragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1888;
    private static final int STORAGE_REQUEST = 1999;
    private final String[] REQUIRED_PERMISSIONS = new String[]{ "android.permission.CAMERA"};
    private final String[] REQUIRED_PERMISSIONS_PHOTO = new String[]{ "android.permission.READ_EXTERNAL_STORAGE"};
    private ImageView customerPhoto;
    private Context context;
    private Activity activity;
    private String mCurrentPhotoPath = "";

    private CustomListener customListener;
    public BottomSheetPhotoUploadDialog(ImageView customerPhoto, Context context, Activity activity) {
       super();
       this.customerPhoto = customerPhoto;
       this.context = context;
       this.activity = activity;
    }

    public BottomSheetPhotoUploadDialog(ImageView customerPhoto, Context context, Activity activity, CustomListener customListener) {
        super();
        this.customerPhoto = customerPhoto;
        this.context = context;
        this.activity = activity;
        this.customListener = customListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dj_loan_application_start_upload_options, container, false);

        v.findViewById(R.id.loan_application_upload_camera).setOnClickListener(view -> {
            InMemoryInfo.loanPhotoUploadedFrom = "camera";
            if(allPermissionsGranted(REQUIRED_PERMISSIONS)) {
                try {
                    dispatchTakePictureIntent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                ActivityCompat.requestPermissions(this.activity, REQUIRED_PERMISSIONS, REQUEST_IMAGE_CAPTURE);
            }
        });

        v.findViewById(R.id.loan_application_upload_gallary).setOnClickListener(view -> {
            InMemoryInfo.loanPhotoUploadedFrom = "gallary";
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), STORAGE_REQUEST);
        });
        return v;
    }
    private boolean allPermissionsGranted(String[] permissions){
        for(String permission : permissions) {
            if(ContextCompat.checkSelfPermission(this.context, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if(!allPermissionsGranted(REQUIRED_PERMISSIONS)) {
                Toast.makeText(this.context, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.activity.finish();
            } else {
                try {
                    dispatchTakePictureIntent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        if (requestCode == STORAGE_REQUEST) {
            if(!allPermissionsGranted(REQUIRED_PERMISSIONS_PHOTO)) {
                Toast.makeText(this.context, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.activity.finish();
            } else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), STORAGE_REQUEST);
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            customerPhoto.setVisibility(View.VISIBLE);
            Uri imageUri = Uri.parse(mCurrentPhotoPath);
            customerPhoto.setImageURI(imageUri);
        }
        if (requestCode == STORAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            customerPhoto.setImageURI(data.getData());
            customerPhoto.setVisibility(View.VISIBLE);
        }
        if (this.customListener != null && resultCode == Activity.RESULT_OK)
            this.customListener.listen(InMemoryInfo.loanPhotoUploadedFrom.contentEquals("camera") ? mCurrentPhotoPath : data.getData().toString());

        this.dismiss();
    }

    private void dispatchTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = Commons.createImageFile(getContext());
            } catch (IOException ex) {
                // Error occurred while creating the File
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mCurrentPhotoPath = "file:" + photoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(activity, getContext().getPackageName() + ".provider", photoFile);;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public String getmCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }
    public void setmCurrentPhotoPath(String mCurrentPhotoPath) {
        this.mCurrentPhotoPath = mCurrentPhotoPath;
    }

}
