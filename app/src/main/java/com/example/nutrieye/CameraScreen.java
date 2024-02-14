package com.example.nutrieye;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;

import com.example.nutrieye.databinding.ActivityCameraScreenBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class CameraScreen extends AppCompatActivity {

    int cameraFacing = CameraSelector.LENS_FACING_BACK;
    ActivityCameraScreenBinding binding;
    private ProgressDialog cameraProgress;
    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if(result){
                startCamera(cameraFacing);
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        cameraProgress = new ProgressDialog(this);
        cameraProgress.setCancelable(false);
        cameraProgress.setCanceledOnTouchOutside(false);


        if(ContextCompat.checkSelfPermission(CameraScreen.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            activityResultLauncher.launch(Manifest.permission.CAMERA);
        } else{
            startCamera(cameraFacing);
        }

        binding.cameraFlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cameraFacing == CameraSelector.LENS_FACING_BACK) {
                    cameraFacing = CameraSelector.LENS_FACING_FRONT;
                } else {
                    cameraFacing = CameraSelector.LENS_FACING_BACK;
                }
                startCamera(cameraFacing);
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigatetoHomeFragment();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigatetoHomeFragment();
    }

    private void navigatetoHomeFragment() {
        Intent intent = new Intent(this, NavigationScreen.class);
        intent.putExtra("activeFragment", "HomeFragment");
        startActivity(intent);
        finish();
    }

    public void startCamera(int cameraFacing) {
        ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(this);

        listenableFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = listenableFuture.get();

                // Calculate aspect ratio based on camera preview dimensions
                int aspectRatio = aspectRatio(binding.cameraPreview.getWidth(), binding.cameraPreview.getHeight());
                //int aspectRatio = AspectRatio.RATIO_DEFAULT;

                Preview preview = new Preview.Builder().setTargetAspectRatio(aspectRatio).build();

                ImageCapture imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();

                CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(cameraFacing).build();

                cameraProvider.unbindAll();

                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

                binding.cameraShot.setOnClickListener(view -> takePicture(imageCapture));

                binding.flashToggle.setOnClickListener(view -> setFlashIcon(camera));

                binding.cameraGallery.setOnClickListener(view -> openGallery());

                preview.setSurfaceProvider(binding.cameraPreview.getSurfaceProvider());

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            String selectedImagePath = getPathFromUri(selectedImageUri);
            if (selectedImagePath != null) {
                // Pass the selected image URI to the next activity
                Intent intent = new Intent(CameraScreen.this, FoodScreen.class);
                intent.putExtra("selected_image_uri", selectedImageUri.toString());
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to get the image from gallery.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getPathFromUri(Uri selectedImageUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImageUri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return null;
    }

    public void takePicture(ImageCapture imageCapture) {
        // Create a file to store the captured image
        File file = new File(getExternalFilesDir(null), System.currentTimeMillis() + ".jpg");
        // Configure the output options
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();

        cameraProgress.setTitle("Processing Image");
        cameraProgress.setMessage("Please Wait...");
        cameraProgress.show();

        // Capture the image and save it to the file
        imageCapture.takePicture(outputFileOptions, Executors.newCachedThreadPool(), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                // Pass the URI of the captured image to the next activity
                Uri imageUri = Uri.fromFile(file);
                Intent intent = new Intent(CameraScreen.this, FoodScreen.class);
                intent.putExtra("captured_image_uri", imageUri.toString());
                startActivity(intent);
                // Finish the current activity
                finish();

                cameraProgress.dismiss();
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                cameraProgress.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CameraScreen.this, "Failed to save: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                startCamera(cameraFacing);
            }
        });
    }

    private void setFlashIcon(Camera camera){
        if (camera.getCameraInfo().hasFlashUnit()){
            if (camera.getCameraInfo().getTorchState().getValue() == 0){
                camera.getCameraControl().enableTorch(true);
                binding.flashToggle.setImageResource(R.drawable.flash_off);
            } else {
                camera.getCameraControl().enableTorch(false);
                binding.flashToggle.setImageResource(R.drawable.flash_on);
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(CameraScreen.this, "Flash is not currently available.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public int aspectRatio(int width, int height){
        double previewRatio = (double) Math.max(width, height) / Math.min(width, height);
        if(Math.abs(previewRatio - 4.0 / 3.0) <= Math.abs(previewRatio - 16.0 / 9.0)){
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }
}
