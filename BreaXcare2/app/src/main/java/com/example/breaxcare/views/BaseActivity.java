package com.example.breaxcare.views;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.breaxcare.R;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Load the TopMenuFragment (if not already loaded)
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Add TopMenuFragment
            Fragment topMenuFragment = new TopMenuFragment();
            transaction.replace(R.id.topMenuContainer, topMenuFragment, "TopMenu");

            // Add BottomMenuFragment
            Fragment bottomMenuFragment = new BottomMenuFragment();
            transaction.replace(R.id.bottomMenuContainer, bottomMenuFragment, "BottomMenu");

            transaction.commit();
        }
    }
}
