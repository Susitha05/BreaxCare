package com.example.breaxcare.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.breaxcare.R;
import com.example.breaxcare.views.taskmaneger.EventsActivity;
import com.example.breaxcare.views.usershandler.ProfileActivity;

public class BottomMenuFragment extends Fragment {

    private ImageView menuItem1, menuItem2, menuItem3, menuItem4;
    private TextView menuText1, menuText2, menuText3, menuText4;

    public BottomMenuFragment() {
        // Required empty public constructor
    }

    public static BottomMenuFragment newInstance(String param1, String param2) {
        BottomMenuFragment fragment = new BottomMenuFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottom_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize all menu item ImageViews
        menuItem1 = view.findViewById(R.id.menuItem1);
        menuItem2 = view.findViewById(R.id.menuItem2);
        menuItem3 = view.findViewById(R.id.menuItem3);
        menuItem4 = view.findViewById(R.id.menuItem4);

        // Initialize TextViews
        menuText1 = view.findViewById(R.id.menutxt1);
        menuText2 = view.findViewById(R.id.menutxt2);
        menuText3 = view.findViewById(R.id.menutxt3);
        menuText4 = view.findViewById(R.id.menutxt4);

        // Retrieve the selected menu item from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MenuState", Context.MODE_PRIVATE);
        int selectedMenuItem = sharedPreferences.getInt("selectedMenuItem", -1); // Default to -1 (no item selected)

        // Restore the active state of the selected menu item
        restoreMenuState(selectedMenuItem);

        // Set up click listeners for each item
        setUpClickListener(view.findViewById(R.id.Item1), menuItem1, menuText1, DashboardActivity.class, 1);
        setUpClickListener(view.findViewById(R.id.Item2), menuItem2, menuText2, EventsActivity.class, 2); // No navigation for Item 2
        setUpClickListener(view.findViewById(R.id.Item3), menuItem3, menuText3, GeminiAIActivity.class, 3); // No navigation for Item 3
        setUpClickListener(view.findViewById(R.id.Item4), menuItem4, menuText4, ProfileActivity.class, 4);
    }

    private void setUpClickListener(LinearLayout item, ImageView menuItem, TextView menuText, Class<?> activityClass, int itemId) {
        item.setOnClickListener(v -> {
            // Save the selected menu item index in SharedPreferences
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MenuState", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("selectedMenuItem", itemId); // Save the selected item ID
            editor.apply();

            resetMenuIcons(); // Reset all icons and text to default color
            int primaryColor = getResources().getColor(R.color.primaryColor, null);
            menuItem.setColorFilter(primaryColor, PorterDuff.Mode.SRC_IN); // Set active color for ImageView
            menuText.setTextColor(primaryColor); // Set active color for TextView

            if (activityClass != null) {
                Intent intent = new Intent(getActivity(), activityClass);
                startActivity(intent); // Start the desired activity
            }
        });
    }

    private void resetMenuIcons() {
        // Reset all menu icons and texts to default color
        int defaultColor = getResources().getColor(R.color.defaultIconColor, null);

        menuItem1.setColorFilter(defaultColor, PorterDuff.Mode.SRC_IN);
        menuItem2.setColorFilter(defaultColor, PorterDuff.Mode.SRC_IN);
        menuItem3.setColorFilter(defaultColor, PorterDuff.Mode.SRC_IN);
        menuItem4.setColorFilter(defaultColor, PorterDuff.Mode.SRC_IN);

        menuText1.setTextColor(defaultColor);
        menuText2.setTextColor(defaultColor);
        menuText3.setTextColor(defaultColor);
        menuText4.setTextColor(defaultColor);
    }

    private void restoreMenuState(int selectedMenuItem) {
        // Restore the active color for the previously selected menu item
        int primaryColor = getResources().getColor(R.color.primaryColor, null);

        switch (selectedMenuItem) {
            case 1:
                menuItem1.setColorFilter(primaryColor, PorterDuff.Mode.SRC_IN);
                menuText1.setTextColor(primaryColor);
                break;
            case 2:
                menuItem2.setColorFilter(primaryColor, PorterDuff.Mode.SRC_IN);
                menuText2.setTextColor(primaryColor);
                break;
            case 3:
                menuItem3.setColorFilter(primaryColor, PorterDuff.Mode.SRC_IN);
                menuText3.setTextColor(primaryColor);
                break;
            case 4:
                menuItem4.setColorFilter(primaryColor, PorterDuff.Mode.SRC_IN);
                menuText4.setTextColor(primaryColor);
                break;
            default:
                break;
        }
    }
}
