package com.stpi.campus.activity.personalService;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.stpi.campus.activity.booking.QueueActivity;
import com.stpi.campus.R;
import com.stpi.campus.activity.RefreshableFragment;

/**
 * Created by Administrator on 13-12-11.
 */
public class CurrentFragment extends RefreshableFragment {

    private View view = null;
    private LinearLayout queue_button = null;
    private LinearLayout cart_button = null;
    private LinearLayout order_button = null;
    private LinearLayout present_button = null;
//    private Button parking_button = null;
//    private Button queue_button = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_current, container, false);

        queue_button = (LinearLayout) view.findViewById(R.id.myQueue);
        cart_button = (LinearLayout) view.findViewById(R.id.myCart);
        order_button = (LinearLayout) view.findViewById(R.id.myOrder);
        present_button = (LinearLayout) view.findViewById(R.id.myPresent);

        queue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CurrentFragment.this.getActivity(), QueueActivity.class);
                startActivity(intent);
            }
        });

        cart_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CurrentFragment.this.getActivity(), MyCartActivity.class);
                startActivity(intent);
            }
        });

        order_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CurrentFragment.this.getActivity(), MyOrderActivity.class);
                startActivity(intent);
            }
        });

        present_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CurrentFragment.this.getActivity(), "该功能尚未开放", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(CurrentFragment.this.getActivity(), ChangePresentActivity.class);
//                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void refresh() {

    }
}