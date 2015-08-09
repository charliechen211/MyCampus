package com.stpi.campus.activity.campusNearby;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.stpi.campus.activity.campusNearby.transportation.TransportationActivity;
import com.stpi.campus.R;
import com.stpi.campus.activity.RefreshableFragment;

/**
 * Created by cyc on 2014/11/7.
 */
public class CampusNearbyFragment extends RefreshableFragment {

    private View view = null;

    private Menu menu = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_campus_nearby, container, false);
        setHasOptionsMenu(true);
        getActivity().getResources().getColorStateList(android.R.color.holo_blue_bright);
        createBtnAndSetOnClickListener(R.id.button_subway, 1, 0);
        createBtnAndSetOnClickListener(R.id.button_bus, 1, 1);
        createBtnAndSetOnClickListener(R.id.button_campus_bus, 1, 2);
        createBtnAndSetOnClickListener(R.id.button_campus_turn_bus, 1, 3);
/*        createBtnAndSetOnClickListener(R.id.button_market, 2, 0);
        createBtnAndSetOnClickListener(R.id.button_hospital, 2, 1);
        createBtnAndSetOnClickListener(R.id.button_bank, 2, 2);
        createBtnAndSetOnClickListener(R.id.button_atm, 2, 3);
        createBtnAndSetOnClickListener(R.id.button_mail, 2, 4);
        createBtnAndSetOnClickListener(R.id.button_repair, 2, 5);*/
        createBtnAndSetOnClickListener(R.id.button_campus_restaurant, 3, 1);
        createBtnAndSetOnClickListener(R.id.button_campus_out_restaurant, 3, 2);
        createBtnAndSetOnClickListener(R.id.button_ktv, 4, 1);
        createBtnAndSetOnClickListener(R.id.button_cinema, 4, 2);
        createBtnAndSetOnClickListener(R.id.button_cafe, 4, 3);
        createBtnAndSetOnClickListener(R.id.button_supermarket, 4, 4);
        createBtnAndSetOnClickListener(R.id.button_hair, 4, 5);
        createBtnAndSetOnClickListener(R.id.button_scene, 4, 6);
        createBtnAndSetOnClickListener(R.id.button_fast_hotel, 5, 1);
        createBtnAndSetOnClickListener(R.id.button_normal_hotel, 5, 2);

        return view;
    }

    private void createBtnAndSetOnClickListener(int rId, final int typeId, final int objectId) {
        LinearLayout button_campus_out_res = (LinearLayout) view.findViewById(rId);
        button_campus_out_res.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(typeId == 1 || typeId == 2) {
                    if(objectId == 1){
                        Toast.makeText(getActivity(), "公交功能暂未开放", Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent = new Intent(CampusNearbyFragment.this.getActivity(),
                                TransportationActivity.class);
                        intent.putExtra("typeId",typeId);
                        intent.putExtra("objectId", objectId);
                        startActivity(intent);
                    }

                }else {
                    Intent intent = new Intent(CampusNearbyFragment.this.getActivity(),
                            ItemListActivity.class);
                    intent.putExtra("typeId", typeId);
                    intent.putExtra("objectId", objectId);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
    }

    private void setActionBarMenu() {
/*        menu.add(0, R.integer.common_search, 0, "查找").setIcon(R.drawable.actionbar_search_icon)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);*/
    }

    @Override
    public void refresh() {
        super.refresh();
        getActivity().getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable
                .actionbar_color_campusnearby));

        setActionBarMenu();
    }
}