package com.example.dash.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dash.R;

public class Dash extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.dash_fragment, container, false);

        LinearLayout dashLL = new LinearLayout(getContext());

        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.ic_add_circle_24px);

        dashLL.addView(imageView);

        return rootView;
    }
}
