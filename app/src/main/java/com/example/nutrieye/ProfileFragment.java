package com.example.nutrieye;

import static android.content.Context.MODE_PRIVATE;
import static com.example.nutrieye.NavigationScreen.USER_UID_KEY;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private String userUID;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CROP_IMAGE_REQUEST = 2;
    CircleImageView profilePic;
    private AlertDialog alertDialog;
    TextView profileGender, profileName, profileEmail, profileAge, profileWeight, profileHeight, profileContactNumber, profileHealthCond, profileFoodAllergens, profileActivityLvl;

    private static final int TAKE_PHOTO_REQUEST = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        MaterialButton logout = view.findViewById(R.id.logoutButton);
        MaterialButton editProfie = view.findViewById(R.id.editProfileButton);
        MaterialButton activityLogs = view.findViewById(R.id.activityLogsButton);

        profilePic = view.findViewById(R.id.pictureProfile);
        profileName = view.findViewById(R.id.profileName);
        profileEmail = view.findViewById(R.id.profileEmail);
        profileGender = view.findViewById(R.id.profileGender);
        profileAge = view.findViewById(R.id.profileAge);
        profileWeight = view.findViewById(R.id.profileWeight);
        profileHeight = view.findViewById(R.id.profileHeight);
        profileFoodAllergens = view.findViewById(R.id.profileFoodAllergens);
        profileHealthCond = view.findViewById(R.id.profileHealthCond);
        profileActivityLvl = view.findViewById(R.id.profileActivityLevel);

        String text = profileFoodAllergens.getText().toString();

        String[] allergens = text.split(",");
        if (allergens.length > 2) {
            // If there are 3 or more elements after splitting by comma
            String truncatedText = allergens[0] + "," + allergens[1] + ",...";
            profileFoodAllergens.setText(truncatedText);
        } else {
            // If there are less than 3 elements
            profileFoodAllergens.setText(text);
        }

        String text1 = profileHealthCond.getText().toString();

        String[] hconditions = text.split(",");
        if (hconditions.length > 2) {
            // If there are 3 or more elements after splitting by comma
            String truncatedText = hconditions[0] + "," + hconditions[1] + ",...";
            profileHealthCond.setText(truncatedText);
        } else {
            // If there are less than 3 elements
            profileHealthCond.setText(text1);
        }

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Logging Out");
                alertDialog.setMessage("Are you sure you want to logout?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int option) {
                        // Log the activity here
                        logLogoutActivity();

                        // Clear userUID from SharedPreferences
                        clearUserUIDfromSharedPreferences();

                        // logout logic
                        Intent intent = new Intent(getActivity(), LoginScreen.class);
                        startActivity(intent);
                        requireActivity().finishAffinity();
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int option) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        editProfie.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), EditProfileScreen.class);

            // Pass the userUID as an extra to the EditProfileScreen activity
            intent.putExtra("USER_UID", userUID);
            startActivity(intent);
        });

        profilePic.setOnClickListener(v -> {
            selectImage();
        });

        activityLogs.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), RecentLogsScreen.class);

            startActivity(intent);
            //To be modified
            if (getActivity() != null) {
                getActivity().finishAffinity();
            }
        });

        loadUserData();

        return view;

    }

    private void clearUserUIDfromSharedPreferences() {
        // Clear userUID from SharedPreferences
        SharedPreferences preferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(USER_UID_KEY); // USER_UID_KEY is the key used to store userUID
        editor.apply();

        // Clear "Remember Me" preferences
        editor.remove("isUserRemembered");
        editor.remove("savedEmail");
        editor.remove("savedPassword");
        editor.apply();
    }

    private void logLogoutActivity() {
        // Create ActivityLogs structure
        DatabaseReference userRootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);
        DatabaseReference activityLogsRef = userRootRef.child("ActivityLogs");

        // Get current time
        String currentTime = new SimpleDateFormat("HH:mm:ss a", Locale.US).format(Calendar.getInstance().getTime());
        String currentDay = new SimpleDateFormat("MMM dd yyyy", Locale.US).format(Calendar.getInstance().getTime());

        // Generate a unique ID for the log entry
        String logID = "LogID_" + System.currentTimeMillis();

        // Create the log entry structure
        DatabaseReference logEntryRef = activityLogsRef.child(currentDay).child(logID);
        logEntryRef.child("action").setValue("Logged Out");
        logEntryRef.child("category").setValue("Authentication");
        logEntryRef.child("timestamp").setValue(currentDay + " " + currentTime);
    }

    private void loadUserData() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userUID = preferences.getString(USER_UID_KEY, null);
        if (userUID != null) {
            fetchUserDataFromFirebase();
        } else {
            // Handle case where userUID is not available
        }
    }

    private void fetchUserDataFromFirebase() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userUID).child("Profile");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String image = snapshot.child("profilePhoto").getValue(String.class);
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);
                    String sex = snapshot.child("sex").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String dob = snapshot.child("dob").getValue(String.class);
                    Double weight = snapshot.child("weight").getValue(Double.class);
                    Double height = snapshot.child("height").getValue(Double.class);
                    String foodAllergens = snapshot.child("foodAllergens").getValue(String.class);
                    String healthConditions = snapshot.child("healthConditions").getValue(String.class);
                    String phyActivity = snapshot.child("phyActivity").getValue(String.class);

                    profileName.setText(firstName + " " + lastName);
                    profileEmail.setText(email);

                    int age = calculateAgeFromDOB(dob);
                    profileAge.setText(String.valueOf(age));

                    profileWeight.setText(String.valueOf(weight));
                    profileHeight.setText(String.valueOf(height));
//                    profileHeight.setText(height);

                    profileGender.setText(sex);
                    // Set profile picture based on gender
                    // Update profilePic with the new image URL
                    if (image != null && !image.isEmpty()) {
                        Picasso.get().load(image).into(profilePic, new Callback() {
                            @Override
                            public void onSuccess() {
                                // Image loaded successfully, do nothing here
                            }

                            @Override
                            public void onError(Exception e) {
                                // Error occurred while loading image
                                // Log the error or handle it as needed
                            }
                        });
                    } else {
                        // If the image URL is empty or null, load a default image
                        Picasso.get().load("male".equalsIgnoreCase(sex) ? R.drawable.malepic : R.drawable.femalepic)
                                .into(profilePic);
                    }

                    profileFoodAllergens.setText(foodAllergens);
                    profileHealthCond.setText(healthConditions);
                    profileActivityLvl.setText(phyActivity);

                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private int calculateAgeFromDOB(String dob) {
        // Parse the date string from DOB
        LocalDate dateOfBirth = LocalDate.parse(dob, DateTimeFormatter.ofPattern("MM/dd/yyyy"));

        // Get current date
        LocalDate currentDate = LocalDate.now();

        // Calculate the difference in years
        return Period.between(dateOfBirth, currentDate).getYears();
    }

    private void selectImage() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
        dialog.setTitle("Select Image");
        String[] options = {"Take Photo", "Choose from Gallery"}; // Add Take Photo option

        dialog.setItems(options, (dialogInterface, i) -> {
            if (i == 0) {
                // Take Photo option selected
                Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST);
                } else {
                    Toast.makeText(requireContext(), "No camera app found", Toast.LENGTH_SHORT).show();
                }
            } else if (i == 1) {
                // Choose from Gallery option selected
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, PICK_IMAGE_REQUEST);
            }
            alertDialog.dismiss();
        });

        dialog.setNegativeButton("Close", (dialogInterface, which) -> alertDialog.dismiss());
        alertDialog = dialog.create();
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            performCrop(imageUri);
        } else if (requestCode == CROP_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            // Get the cropped image URI
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap croppedImage = extras.getParcelable("data");
                if (croppedImage != null) {
                    // Upload the cropped image to Firebase Storage
                    uploadImageToFirebaseStorage(croppedImage);
                }
            }
        } else if (requestCode == TAKE_PHOTO_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getExtras() != null) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            if (imageBitmap != null) {
                // Upload the captured image to Firebase Storage
                uploadImageToFirebaseStorage(imageBitmap);
            }
        }
    }

    private void uploadImageToFirebaseStorage(Bitmap bitmap) {
        // Convert the Bitmap to a byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        // Use userUID as the filename
        String filename = userUID + ".jpg";

        // Get a reference to the Firebase Storage location where the image will be saved
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("profile_images").child(filename);

        // Upload the image data to Firebase Storage
        imageRef.putBytes(imageData)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image upload success, get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Save the download URL to Firebase Realtime Database
                        saveImageDownloadUrlToDatabase(uri.toString());

                        // Load the image into profilePic using Picasso
                        Picasso.get()
                                .load(uri.toString())
                                .into(profilePic);
                    }).addOnFailureListener(e -> {
                        // Handle failure to get download URL
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle failure to upload image
                });
    }


    private void saveImageDownloadUrlToDatabase(String imageUrl) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userUID).child("Profile");

        userRef.child("profilePhoto").setValue(imageUrl)
                .addOnSuccessListener(aVoid -> {
                    // Image URL saved successfully
                    // Refresh the profile to display the updated image
                    fetchUserDataFromFirebase();
                })
                .addOnFailureListener(e -> {
                    // Handle failure to save image URL
                });
    }


    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");

            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256); // Set desired output dimensions
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("return-data", true); // Ensure cropped image is returned in the result

            startActivityForResult(cropIntent, CROP_IMAGE_REQUEST);
        } catch (ActivityNotFoundException e) {
            Log.e("ProfileFragment", "Device doesn't support image cropping", e);
            Toast.makeText(requireContext(), "Image cropping not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }

}