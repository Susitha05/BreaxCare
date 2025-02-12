package com.example.breaxcare.views;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.breaxcare.R;
import com.example.breaxcare.model.DBhelper;
import com.example.breaxcare.views.usershandler.LoginFragment;

public class NewPassFragment extends Fragment {

    private static final String ARG_EMAIL = "email";
    private String userEmail;

    private EditText etNewPassword, etReNewPassword;
    private Button btnUpdate;
    private ImageView passView;
    private TextView txtCancel;

    public NewPassFragment() {
        // Required empty public constructor
    }

    public static NewPassFragment newInstance(String email) {
        NewPassFragment fragment = new NewPassFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userEmail = getArguments().getString(ARG_EMAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_pass, container, false);

        // Bind UI elements
        etNewPassword = view.findViewById(R.id.nw_password);
        etReNewPassword = view.findViewById(R.id.re_new_password);
        btnUpdate = view.findViewById(R.id.btn_update);
        passView = view.findViewById(R.id.passview);
        txtCancel = view.findViewById(R.id.txt_cancel);

        // Set listeners
        passView.setOnClickListener(v -> togglePasswordVisibility());
        btnUpdate.setOnClickListener(v -> updatePassword());
        txtCancel.setOnClickListener(v -> getActivity().onBackPressed()); // Go back when cancel is clicked

        return view;
    }

    private void togglePasswordVisibility() {
        if (etNewPassword.getTransformationMethod().toString().equals("PasswordTransformationMethod")) {
            etNewPassword.setTransformationMethod(null);  // Show password
            etReNewPassword.setTransformationMethod(null);  // Show password
            passView.setImageResource(R.drawable.ic_passview); // Set the icon to visible
        } else {
            etNewPassword.setTransformationMethod(new android.text.method.PasswordTransformationMethod());  // Hide password
            etReNewPassword.setTransformationMethod(new android.text.method.PasswordTransformationMethod());  // Hide password
            passView.setImageResource(R.drawable.ic_passview); // Set the icon to hide
        }
    }

    private void updatePassword() {
        String newPassword = etNewPassword.getText().toString().trim();
        String reNewPassword = etReNewPassword.getText().toString().trim();

        // Check if passwords match
        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(reNewPassword)) {
            Toast.makeText(getContext(), "Please enter both passwords.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(reNewPassword)) {
            Toast.makeText(getContext(), "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update password in the database
        DBhelper dbHelper = new DBhelper(getContext());
        boolean isUpdated = dbHelper.updatePassword(userEmail, newPassword);

        if (isUpdated) {
            Toast.makeText(getContext(), "Password updated successfully!", Toast.LENGTH_SHORT).show();

            // Navigate to the LoginFragment after successful password update
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment()) // Replace with your LoginFragment
                    .addToBackStack(null)  // Add to back stack to allow navigation back if needed
                    .commit();

        } else {
            Toast.makeText(getContext(), "Failed to update password. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

}
