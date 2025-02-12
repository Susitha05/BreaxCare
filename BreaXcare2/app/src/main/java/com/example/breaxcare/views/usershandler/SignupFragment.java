package com.example.breaxcare.views.usershandler;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.breaxcare.R;
import com.example.breaxcare.model.DBhelper;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupFragment extends Fragment {

    private EditText firstNameEditText, lastNameEditText, usernameEditText, emailEditText, dobEditText, passwordEditText, confirmPasswordEditText;
    private Spinner userTypeSpinner;
    private CheckBox termsCheckbox;
    private Button signupButton;
    private ImageView passviewIcon;
    private boolean isPasswordVisible = false;

    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_signup, container, false);

        // Initialize views
        firstNameEditText = rootView.findViewById(R.id.lName);
        lastNameEditText = rootView.findViewById(R.id.fName);
        usernameEditText = rootView.findViewById(R.id.username);
        emailEditText = rootView.findViewById(R.id.email);
        dobEditText = rootView.findViewById(R.id.dob);
        passwordEditText = rootView.findViewById(R.id.password);
        confirmPasswordEditText = rootView.findViewById(R.id.cPassword);
        userTypeSpinner = rootView.findViewById(R.id.UserTypeSpinner);
        termsCheckbox = rootView.findViewById(R.id.termsCheckbox);
        signupButton = rootView.findViewById(R.id.btn_signup);
        passviewIcon = rootView.findViewById(R.id.passview);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.UserType_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter);

        dobEditText.setOnClickListener(v -> showDatePickerDialog());


        passviewIcon.setOnClickListener(v -> togglePasswordVisibility());


        signupButton.setOnClickListener(v -> new SignupTask().execute());

        return rootView;
    }

    private void showDatePickerDialog() {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view, selectedYear, selectedMonth, selectedDay) -> {

            dobEditText.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passviewIcon.setImageResource(R.drawable.ic_passview);
        } else {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passviewIcon.setImageResource(R.drawable.ic_passview);
        }
        isPasswordVisible = !isPasswordVisible;
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }

    private boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private class SignupTask {
        public void execute() {
            if (isEmpty(firstNameEditText) || isEmpty(lastNameEditText) || isEmpty(usernameEditText) ||
                    isEmpty(emailEditText) || isEmpty(dobEditText) || isEmpty(passwordEditText) ||
                    isEmpty(confirmPasswordEditText)) {
                showToast("Please fill in all fields.");
                return;
            }

            if (!passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
                showToast("Passwords do not match.");
                return;
            }

            if (!isValidEmail(emailEditText.getText().toString())) {
                showToast("Please enter a valid email.");
                return;
            }

            if (!termsCheckbox.isChecked()) {
                showToast("You must accept the terms and conditions.");
                return;
            }

            DBhelper dbHelper = new DBhelper(getContext());
            try {
                boolean isInserted = dbHelper.insertUser(
                        firstNameEditText.getText().toString(),
                        lastNameEditText.getText().toString(),
                        usernameEditText.getText().toString(),
                        emailEditText.getText().toString(),
                        dobEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        userTypeSpinner.getSelectedItem().toString()
                );

                if (isInserted) {
                    showToast("Signup Successful!");
                    navigateToLoginFragment();
                } else {
                    showToast("Username or Email already exists.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast("An error occurred while signing up. Please try again.");
            }
        }
    }

    private void navigateToLoginFragment() {
        LoginFragment loginFragment = new LoginFragment();

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, loginFragment)
                .addToBackStack(null)
                .commit();
    }
}
