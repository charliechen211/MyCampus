package com.stpi.campus.activity.personalService;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.R;
import com.stpi.campus.activity.RefreshableFragment;

/**
 * Created by Administrator on 13-12-11.
 */
public class ShoppingFragment extends RefreshableFragment {

    private View view = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_shopping, container, false);

        TextView circleName = (TextView) view.findViewById(R.id.circleName);
        circleName.setText(UserInfo.circleName);
        LinearLayout hotelButton = (LinearLayout) view.findViewById(R.id.button_hotel);
        hotelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShoppingFragment.this.getActivity(),
                        SearchMerchantActivity.class);
                intent.putExtra("search", "*");
                intent.putExtra("typeId", "2");
                startActivity(intent);
            }
        });

        LinearLayout buyButton = (LinearLayout) view.findViewById(R.id.button_buy);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShoppingFragment.this.getActivity(),
                        SearchMerchantActivity.class);
                intent.putExtra("search", "*");
                intent.putExtra("typeId", "4");
                startActivity(intent);
            }
        });

        LinearLayout entertainButton = (LinearLayout) view.findViewById(R.id.button_entertain);
        entertainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShoppingFragment.this.getActivity(),
                        SearchMerchantActivity.class);
                intent.putExtra("search", "*");
                intent.putExtra("typeId", "3");
                startActivity(intent);
            }
        });

        LinearLayout foodButton = (LinearLayout) view.findViewById(R.id.button_food);
        foodButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShoppingFragment.this.getActivity(),
                        SearchMerchantActivity.class);
                intent.putExtra("search", "*");
                intent.putExtra("typeId", "1");
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void refresh() {
        super.refresh();
    }
}