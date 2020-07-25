package com.testapp.fitspirational.FeatureActivities.Diet.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.testapp.fitspirational.Executors.DietContentExecutor;
import com.testapp.fitspirational.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        int dietType = getArguments().getInt(ARG_SECTION_NUMBER);
        View root = inflater.inflate(R.layout.fragment_diet, container, false);
        final RecyclerView recyclerView = root.findViewById(R.id.diet_recyclerView);
        final ProgressBar progressBar = root.findViewById(R.id.diet_progressBar);
        int[] layout1 = new int[4];
        int[] layout2 = new int[3];
        layout1[0] = R.layout.diet_intro_item;
        layout1[1] = R.id.diet_introImage;
        layout1[2] = R.id.diet_intro_titleText;
        layout1[3] = R.id.diet_intro_bodyText;
        layout2[0] = R.layout.diet_item;
        layout2[1] = R.id.diet_cardTitle;
        layout2[2] = R.id.diet_cardBody;
        DietContentExecutor dietContentExecutor = new DietContentExecutor(
                dietType , getContext(), progressBar, recyclerView, layout1, layout2
        );
        dietContentExecutor.run();

        return root;
    }
}