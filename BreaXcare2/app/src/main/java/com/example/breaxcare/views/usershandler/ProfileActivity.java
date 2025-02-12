package com.example.breaxcare.views.usershandler;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.breaxcare.R;
import com.example.breaxcare.model.DBhelper;
import com.example.breaxcare.model.UserSession;
import com.example.breaxcare.views.BaseActivity;
import com.example.breaxcare.views.StartActivity;

import java.io.IOException;

public class ProfileActivity extends BaseActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 101;
    private static final int GALLERY_PERMISSION_REQUEST = 102;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    private EditText etFirstName, etLastName, etUsername, etEmail;
    private Button btnEdit, btnSave, btnDelete, btnUploadImg;
    private Spinner etUserType;
    private ImageView icProfileImg;
    private DBhelper dbHelper;
    private UserSession userSession;
    private String userEmail;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views and session
        etFirstName = findViewById(R.id.fName);
        etLastName = findViewById(R.id.lName);
        etUsername = findViewById(R.id.username);
        etEmail = findViewById(R.id.email);
        etUserType = findViewById(R.id.UserTypeSpinner);
        btnEdit = findViewById(R.id.btn_edit);
        btnSave = findViewById(R.id.btn_save);
        btnDelete = findViewById(R.id.btn_delete);
        btnUploadImg = findViewById(R.id.btn_uploadimg);
        icProfileImg = findViewById(R.id.ic_profileimg);

        dbHelper = new DBhelper(this);
        userSession = new UserSession(this);

        // Get user email from session
        userEmail = userSession.getUserEmail();

        // Set up Spinner adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.UserType_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etUserType.setAdapter(adapter);

        // Load user data into fields
        loadUserData();

        // Set buttons functionality
        setButtonListeners();
    }

    private void loadUserData() {
        String[] userData = dbHelper.getUserDataByEmail(userEmail);
        if (userData != null && userData.length >= 5) {
            etFirstName.setText(userData[0]);
            etLastName.setText(userData[1]);
            etUsername.setText(userData[2]);
            etEmail.setText(userData[3]);
            etUserType.setSelection(((ArrayAdapter) etUserType.getAdapter()).getPosition(userData[4]));

            int userId = dbHelper.getUserIdByEmail(userEmail);
            Bitmap profileImage = dbHelper.getUserImage(userId);
            if (profileImage != null) {
                icProfileImg.setImageBitmap(getCircularBitmap(profileImage));
            }
        }
    }

    private void setButtonListeners() {
        btnUploadImg.setOnClickListener(v -> {
            // Check if permissions are granted before showing the image source dialog
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions();
            } else {
                showImageSourceDialog();
            }
        });

        btnEdit.setOnClickListener(v -> setEditableFields(true));

        btnSave.setOnClickListener(v -> updateUserData());

        btnDelete.setOnClickListener(v -> confirmDeleteAccount());
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                CAMERA_PERMISSION_REQUEST);
    }

    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image Source")
                .setItems(new String[]{"Camera", "Gallery"}, (dialog, which) -> {
                    if (which == 0) {
                        openCamera();
                    } else {
                        openGallery();
                    }
                })
                .show();
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST || requestCode == GALLERY_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageSourceDialog();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap photo = null;
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                imageUri = data.getData();
                try {
                    photo = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == CAMERA_REQUEST && data != null) {
                photo = (Bitmap) data.getExtras().get("data");
            }

            if (photo != null) {
                icProfileImg.setImageBitmap(getCircularBitmap(photo));

                int userId = dbHelper.getUserIdByEmail(userEmail);
                // Save to database
                boolean isSaved = dbHelper.saveUserImage(userId, photo);
                if (isSaved) {
                    Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int width = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap output = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        canvas.drawCircle(width / 2, width / 2, width / 2, paint);

        return output;
    }

    private void updateUserData() {
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String username = etUsername.getText().toString();
        String userType = etUserType.getSelectedItem().toString();

        if (!firstName.isEmpty() && !lastName.isEmpty() && !username.isEmpty()) {
            if (dbHelper.updateUserProfile(userEmail, firstName, lastName, username, userType)) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                setEditableFields(false);
            } else {
                Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDeleteAccount() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (dbHelper.deleteUser(userEmail)) {
                        userSession.clearSession();
                        startActivity(new Intent(ProfileActivity.this, StartActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Error deleting account", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void setEditableFields(boolean editable) {
        etFirstName.setEnabled(editable);
        etLastName.setEnabled(editable);
        etUsername.setEnabled(editable);
        etUserType.setEnabled(editable);
    }


}
