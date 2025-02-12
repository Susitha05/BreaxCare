package com.example.breaxcare.views.usershandler;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.breaxcare.R;
import com.example.breaxcare.model.DBhelper;
import com.example.breaxcare.model.UserSession;  // Import UserSession class
import com.example.breaxcare.views.DashboardActivity;

public class LoginFragment extends Fragment {

    private EditText etUsernameOrEmail, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword;
    private ImageView passView; // Eye icon for toggling password visibility
    private DBhelper dbHelper;
    private UserSession userSession;  // Add instance of UserSession class
    private boolean isPasswordVisible = false; // Track password visibility

    public LoginFragment() {

    }

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userSession = new UserSession(getContext());


        if (userSession.isUserLoggedIn()) {

            Intent intent = new Intent(getContext(), DashboardActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);


        etUsernameOrEmail = view.findViewById(R.id.et_username);
        etPassword = view.findViewById(R.id.et_password);
        btnLogin = view.findViewById(R.id.btn_login);
        tvForgotPassword = view.findViewById(R.id.tv_forgot_password);
        passView = view.findViewById(R.id.passview); // Eye icon for password toggle


        dbHelper = new DBhelper(getContext());

        // Toggle password visibility
        passView.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Hide password
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                passView.setImageResource(R.drawable.ic_passview); // Set to closed-eye icon
            } else {
                // Show password
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                passView.setImageResource(R.drawable.ic_passview); // Set to open-eye icon
            }
            isPasswordVisible = !isPasswordVisible; // Toggle visibility state
            etPassword.setSelection(etPassword.getText().length()); // Move cursor to the end
        });


        btnLogin.setOnClickListener(v -> {
            String usernameOrEmail = etUsernameOrEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();


            if (TextUtils.isEmpty(usernameOrEmail)) {
                etUsernameOrEmail.setError("Username or email is required");
                etUsernameOrEmail.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Password is required");
                etPassword.requestFocus();
                return;
            }


            new LoginTask().execute(usernameOrEmail, password);
        });

        tvForgotPassword.setOnClickListener(v -> {

            ForgotPassFragment forgotPassFragment = new ForgotPassFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, forgotPassFragment);
            transaction.addToBackStack(null);
            transaction.commit();


            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        });

        return view;
    }

    private class LoginTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Toast.makeText(getContext(), "Logging in...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String usernameOrEmail = params[0];
            String password = params[1];

            // Perform database validation
            return dbHelper.checkUser(usernameOrEmail, password);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {

                String usernameOrEmail = etUsernameOrEmail.getText().toString().trim();
                userSession.createUserSession(usernameOrEmail);  // Save session


                Toast.makeText(getContext(), "Login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), DashboardActivity.class);
                startActivity(intent);
                getActivity().finish();
            } else {

                Toast.makeText(getContext(), "Invalid username/email or password", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
