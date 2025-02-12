package com.example.breaxcare.views.checkuphandler;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.breaxcare.R;
import com.example.breaxcare.model.apiconnection.ApiService;
import com.example.breaxcare.model.DBhelper;
import com.example.breaxcare.model.apiconnection.RetrofitClient;
import com.example.breaxcare.model.UserSession;
import com.example.breaxcare.views.BaseActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckupActivity extends BaseActivity {

    private ProgressBar progressBar;
    private TextView progressText, txtRisk, txtDescription, analyzingText;
    private Button btnUploadImg, btnanalyze;
    private ImageView imageView;
    private CardView resultCard;

    private FrameLayout loadingOverlay;

    private final int CAMERA_PERMISSION_REQUEST = 101;
    private final int GALLERY_PERMISSION_REQUEST = 102;
    Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkup);

        // Initialize views
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        txtRisk = findViewById(R.id.txt_risk);
        txtDescription = findViewById(R.id.txt_description);
        btnUploadImg = findViewById(R.id.btn_uploadimg);
        imageView = findViewById(R.id.ic_mammogram_img);
        btnanalyze = findViewById(R.id.btn_analyze);
        resultCard = findViewById(R.id.resultCard);

        int progress = 0;
        updateProgressBar(progress);
        resultCard.setVisibility(View.GONE);
        createOverlay();

        btnUploadImg.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions();
            } else {
                showPermissionPopup();
            }
        });
        btnanalyze.setOnClickListener(v -> {
            if (imageUri != null) {
                showAnalyzingOverlay();
                // Convert the image URI to a File object
                String imagePath = getImagePathFromUri(imageUri);
                File imageFile = new File(imagePath);
                Log.d("image path",imagePath);
                Log.d("image", String.valueOf(imageFile));


                uploadImage(imageUri);
            } else {
                Toast.makeText(CheckupActivity.this, "Please select an image first.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] imageData = new byte[inputStream.available()];
            inputStream.read(imageData);

            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageData);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", "image.jpg", requestBody);

            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
            Call<ResponseBody> call = apiService.uploadImage(body);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            resultCard.setVisibility(View.VISIBLE);
                            hideAnalyzingOverlay();
                            String responseString = response.body().string();
                            Log.d("API Response", "Success: " + responseString);


                            JSONObject jsonResponse = new JSONObject(responseString);
                            String roundnessColor = jsonResponse.optString("roundness_color", "N/A");
                            double whiteDotsPercentage = jsonResponse.optDouble("white_dots_percentage", -1);
                            int whiteDotsCount = jsonResponse.optInt("white_dots_count", -1);
                            String result = jsonResponse.optString("result", "N/A");

                            Log.d("Analysis Result", "Roundness Color: " + roundnessColor);
                            Log.d("Analysis Result", "White Dots Percentage: " + whiteDotsPercentage + "%");
                            Log.d("Analysis Result", "White Dots Count: " + whiteDotsCount);
                            Log.d("Analysis Result", "Final Result: " + result);

                            int present = 0;

                            if(whiteDotsPercentage<1){
                                present = 40;
                            } else if (whiteDotsPercentage > 1 && whiteDotsPercentage < 2) {
                                present = 70;
                            }else{
                                present = 90;
                            }

                            // Get user ID from UserSession
                            DBhelper dBhelper = new DBhelper(CheckupActivity.this);
                            UserSession user = new UserSession(CheckupActivity.this);
                            LocalDate currentDate = LocalDate.now();
                            String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            String userId = user.getUserEmail();
                            int uid = dBhelper.getUserIdByEmail(userId); // Get user ID as integer

// Insert checkup result into medical history table
                            updateProgressBar(present);
                            boolean isInserted = dBhelper.insertMedicalHistory(
                                    uid,                    // User ID
                                    result,                 // Final result from API response
                                    roundnessColor,         // Roundness color from API response
                                    whiteDotsCount,         // White dots count from API response
                                    whiteDotsPercentage,    // White dots percentage from API response
                                    txtRisk.getText().toString(), // Risk level determined from progress
                                    formattedDate           // Current date
                            );

                            if (isInserted) {
                                Log.d("db", "Medical history insert successful");
                            } else {
                                Log.d("db", "Medical history insert failed");
                            }

                        } catch (Exception e) {
                            Log.e("Parse Error", "Failed to parse response: " + e.getMessage());
                        }
                    } else {
                        hideAnalyzingOverlay();
                        Log.e("API Response Error", "Response Code: " + response.code() + ", Message: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("API Failure", "Error: " + t.getMessage());
                    hideAnalyzingOverlay();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Image Read Error", "Failed to read image: " + e.getMessage());
            hideAnalyzingOverlay();
        }
    }




    private void updateProgressBar(int progress) {
        progressBar.setProgress(progress);
        String status;

        if (progress == 0) {
            progressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.green));
            txtRisk.setText("");
            txtDescription.setText("You are not upload the image  ");
            progressText.setTextColor(Color.DKGRAY);
            status = txtRisk.getText().toString();
        } else if (progress > 0 && progress <= 40) {
            progressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.green));
            txtRisk.setText("Low Risk");
            txtRisk.setTextColor(Color.GREEN);
            txtDescription.setText("You are at a low risk level. Continue maintaining a healthy lifestyle.");
            progressText.setTextColor(Color.GREEN);
            status = txtRisk.getText().toString();
        } else if (progress <= 70) {
            progressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.yellow));
            txtRisk.setText("Moderate Risk");
            txtRisk.setTextColor(Color.YELLOW);
            txtDescription.setText("You are at a moderate risk level. Consider monitoring your health closely.");
            progressText.setTextColor(Color.YELLOW);
            status = txtRisk.getText().toString();
        } else {
            progressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.red));
            txtRisk.setText("High Risk");
            txtRisk.setTextColor(Color.RED);
            txtDescription.setText("You are at high risk. Please consult a healthcare professional immediately.");
            progressText.setTextColor(Color.RED);
            status = txtRisk.getText().toString();
        }

        progressText.setText(progress + "%");
        DBhelper dBhelper = new DBhelper(this);
        UserSession user = new UserSession(this);
        LocalDate currentDate = LocalDate.now();
        String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String userid = user.getUserEmail();
        String uid = String.valueOf(dBhelper.getUserIdByEmail(userid));
        boolean isdeleted = dBhelper.detelecheckup(uid);
        if(isdeleted = true){
            Log.d("db","deteted");
            Log.d("db",status);
            Log.d("db",uid);
        }else{
            Log.d("db","Delete Fail");
        }
        boolean isinsert = dBhelper.insertcheckup(uid,status,formattedDate);
        if(isinsert = true){
            Log.d("db","Successful");
            Log.d("db",status);
        }else{
            Log.d("db","Fail");
        }
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                CAMERA_PERMISSION_REQUEST);
    }


    private void showPermissionPopup() {
        new AlertDialog.Builder(this)
                .setTitle("Image Upload")
                .setMessage("Please provide a clear mammogram image. We do not collect or store your images. " +
                        "We only keep image data for better tracking and symptom analysis.")
                .setPositiveButton("Yes", (dialog, which) -> selectImageOption())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }


    private void selectImageOption() {
        new AlertDialog.Builder(this)
                .setTitle("Select Image Source")
                .setMessage("Do you want to take a picture or select an image from the gallery?")
                .setPositiveButton("Take Picture", (dialog, which) -> launchCamera())
                .setNegativeButton("Select from Gallery", (dialog, which) -> launchGallery())
                .show();
    }


    private void launchCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_PERMISSION_REQUEST);
    }


    private void launchGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showPermissionPopup();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == CAMERA_PERMISSION_REQUEST || requestCode == GALLERY_PERMISSION_REQUEST) {
                imageUri = data.getData();


                imageView.setImageURI(imageUri);


                Log.d("Selected Image", "Image URI: " + imageUri.toString());
            }
        }
    }
    private String getImagePathFromUri(Uri uri) {
        if (uri == null) return null;

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    return cursor.getString(columnIndex);
                }
            } catch (Exception e) {
                Log.e("File Path Error", "Error retrieving file path: " + e.getMessage());
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private void createOverlay() {
        // Create FrameLayout (overlay)
        loadingOverlay = new FrameLayout(this);
        loadingOverlay.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        loadingOverlay.setBackgroundColor(Color.parseColor("#AAFFFFFF"));  // Semi-transparent white

        // Create TextView for "Analyzing..."
        analyzingText = new TextView(this);
        analyzingText.setText("Analyzing...");
        analyzingText.setTextSize(18);
        analyzingText.setTextColor(Color.BLACK);
        analyzingText.setGravity(Gravity.CENTER);
        loadingOverlay.addView(analyzingText);

        // Add the overlay to the root view
        ViewGroup rootView = findViewById(android.R.id.content);
        rootView.addView(loadingOverlay);

        // Initially hide the overlay
        loadingOverlay.setVisibility(View.GONE);
    }

    private void showAnalyzingOverlay() {
        // Show the overlay when analyzing
        loadingOverlay.setVisibility(View.VISIBLE);
    }

    private void hideAnalyzingOverlay() {
        // Hide the overlay when analysis is complete
        loadingOverlay.setVisibility(View.GONE);
    }

}
