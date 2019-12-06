package com.example.dash.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.dash.R;
import com.example.dash.ui.account.AccountActivity;

public class DashFragment extends Fragment {
    private ImageButton addBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.dash_fragment, container, false);

        addBtn = rootView.findViewById(R.id.addBtn);
        addBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), AccountActivity.class);
            startActivity(intent);
        });

        ViewGroup viewGroup = (ViewGroup) rootView;

//        LinearLayout dashLL = new LinearLayout(getContext());
//        dashLL.setOrientation(LinearLayout.VERTICAL);
//        dashLL.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        dashLL.setGravity(Gravity.CENTER);
//
//        ImageButton imageButton = new ImageButton(getContext());
//        imageButton.setImageResource(R.drawable.ic_add_circle_24px);
//        imageButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
//        imageButton.setBackgroundResource(0);
//
//        dashLL.addView(imageButton);
//
//        viewGroup.addView(dashLL);

        return viewGroup;
    }
}
