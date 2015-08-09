package com.stpi.campus.util.navigation;

/**
 * Created by Administrator on 2014/8/16.
 */

import android.content.Context;
import android.graphics.Color;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.*;
import com.amap.api.search.core.LatLonPoint;
import com.amap.api.search.route.*;
import com.stpi.campus.R;
import com.stpi.campus.util.navigation.util.AMapUtil;
import com.stpi.campus.util.navigation.util.ChString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RouteOverlay implements OnMarkerClickListener, InfoWindowAdapter {

    private static int zoomLevel = 15;
    private AMap mMap;
    private Route mRoute;
    private LatLng startPoint;
    private LatLng endPoint;
    private Context mContext;
    private Map<Integer, Marker> markerMap;
    private int currentIndex = 0;

    public RouteOverlay(Context context, AMap map, Route route) {
        mContext = context;
        mMap = map;
        mRoute = route;
        mMap.setOnMarkerClickListener(this);
        mMap.setInfoWindowAdapter(this);
        startPoint = SearchPointConvert(route.getStartPos());
        endPoint = SearchPointConvert(route.getTargetPos());
        markerMap = new HashMap<Integer, Marker>();
    }

    /**
     * 绘制节点和线路
     */
    public void addMarkerLine() {
        Marker startMarker = mMap.addMarker((new MarkerOptions())
                .position(startPoint)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start))
                .title("0"));
        startMarker.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(startPoint, zoomLevel));
        markerMap.put(0, startMarker);
        for (int i = 0; i < mRoute.getStepCount(); i++) {
            if (i != 0) {
                Segment segment = mRoute.getStep(i);
                BitmapDescriptor icon = null;

                // if (segment instanceof BusSegment) {
                // icon = BitmapDescriptorFactory.fromResource(R.drawable.bus);
                // } else if (segment instanceof WalkSegment) {
                // icon = BitmapDescriptorFactory.fromResource(R.drawable.man);
                // } else
                if (segment instanceof DriveSegment) {
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.person);
                }
                Marker tempMarker = mMap.addMarker((new MarkerOptions())
                        .position(
                                SearchPointConvert(mRoute.getStep(i)
                                        .getFirstPoint())
                        ).icon(icon)
                        .anchor(0.5f, 0.5f).title("" + i));
                markerMap.put(i, tempMarker);

            }
            mMap.addPolyline((new PolylineOptions())
                    .addAll(convertArrList(mRoute.getStep(i).getShapes()))
                    .color(Color.argb(180, 54, 114, 227)).width(10.9F));
        }
        Marker targerMarker = mMap.addMarker((new MarkerOptions())
                .position(endPoint)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.end))
                .title("" + (mRoute.getStepCount())));
        markerMap.put(mRoute.getStepCount(), targerMarker);

    }

    /**
     * 清除绘制
     */
    public void removeFormMap() {
        currentIndex = 0;
        mMap.clear();
    }

    public boolean showPrePopInfo() {
        if (currentIndex > 0) {
            currentIndex--;
            Marker merker = markerMap.get(currentIndex);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    merker.getPosition(), zoomLevel));
            merker.showInfoWindow();
        }
        if (currentIndex == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean showNextPopInfo() {
        if (currentIndex < mRoute.getStepCount()) {
            currentIndex++;
            Marker merker = markerMap.get(currentIndex);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    merker.getPosition(), zoomLevel));
            merker.showInfoWindow();
        }
        if (currentIndex == mRoute.getStepCount()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        currentIndex = Integer.parseInt(marker.getTitle());
        return false;
    }

    @Override
    public View getInfoContents(Marker arg0) {
        int index = Integer.parseInt(arg0.getTitle());
        return getInfoView(mContext, index);
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }

    /**
     * 根据Segment index 生成popInfo view
     *
     * @param index
     * @return
     */
    public View getInfoView(Context cnt, int index) {
        if (index < 0 || index > mRoute.getStepCount()) {
            return null;
        }

        LinearLayout ll_parents = new LinearLayout(cnt);
        ll_parents.setOrientation(LinearLayout.VERTICAL);
        ll_parents.setBackgroundResource(R.drawable.custom_info_bubble);

        LinearLayout ll_child1 = new LinearLayout(cnt);
        ll_child1.setOrientation(LinearLayout.HORIZONTAL);
        ll_child1.setGravity(Gravity.AXIS_PULL_BEFORE);
        TextView titleVw = new TextView(cnt);
        String spannedInfos[] = getSpannedInfo(index).toString()
                .split("\\n", 2);
        titleVw.setTextColor(Color.BLACK);
        titleVw.setText(AMapUtil.stringToSpan(spannedInfos[0]));
        titleVw.setPadding(3, 0, 0, 3);
        ll_child1.addView(titleVw, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView backgroud = new TextView(cnt);
        backgroud.setBackgroundColor(Color.rgb(165, 166, 165));
        backgroud.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, 1));

        LinearLayout ll_child3 = new LinearLayout(cnt);
        ll_child3.setOrientation(LinearLayout.VERTICAL);

        TextView titleVwdown = new TextView(cnt);
        if (spannedInfos.length == 2) {
            ll_child3.addView(backgroud, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT, 1));

            titleVwdown.setText(AMapUtil.stringToSpan(spannedInfos[1]));
            titleVwdown.setTextColor(Color.rgb(82, 85, 82));
            ll_child3.addView(titleVwdown, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        LinearLayout ll_child2 = new LinearLayout(cnt);
        ll_child2.setOrientation(LinearLayout.HORIZONTAL);
        ll_child2.setGravity(Gravity.CENTER_HORIZONTAL);

        ll_parents.addView(ll_child1, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        ll_parents.addView(ll_child3, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, 1));

        ll_parents.addView(ll_child2, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        return ll_parents;
    }

    /**
     * 获取popInfo描述
     *
     * @param index
     * @return
     */
    public Spanned getSpannedInfo(int index) {
        if (index == mRoute.getStepCount()) {
            StringBuffer buf = new StringBuffer();
            buf.append(ChString.Arrive);
            buf.append(mRoute.getTargetPlace());
            return AMapUtil.stringToSpan(buf.toString());
        }
        if (mRoute.getStep(index) instanceof BusSegment) {
            return getBusSpan(index);
        } else if (mRoute.getStep(index) instanceof DriveSegment) {
            return getCarInfo(index);
        }

        return getFootSpan(index);
    }

    /**
     * 获取公交popInfo描述
     *
     * @param index
     * @return
     */
    private Spanned getBusSpan(int index) {
        BusSegment seg = (BusSegment) mRoute.getStep(index);
        StringBuffer buf = new StringBuffer();

        buf.append(AMapUtil.colorFont(seg.getLineName(), AMapUtil.HtmlBlack));
        buf.append(AMapUtil.makeHtmlSpace(3));
        buf.append(AMapUtil.colorFont(seg.getLastStationName(),
                AMapUtil.HtmlBlack));
        buf.append(ChString.Direction);
        buf.append(AMapUtil.makeHtmlNewLine());

        buf.append(ChString.GetOn + " : ");
        buf.append(AMapUtil.colorFont(seg.getOnStationName(),
                AMapUtil.HtmlBlack));
        buf.append(AMapUtil.makeHtmlSpace(3));
        buf.append(AMapUtil.makeHtmlNewLine());

        buf.append(ChString.GetOff + " : ");
        buf.append(AMapUtil.colorFont(seg.getOffStationName(),
                AMapUtil.HtmlBlack));
        buf.append(AMapUtil.makeHtmlNewLine());
        buf.append(String.format("%s%d%s , ", ChString.Gong,
                seg.getStopNumber() - 1, ChString.Zhan));

        buf.append(ChString.About + AMapUtil.getFriendlyLength(seg.getLength()));

        return AMapUtil.stringToSpan(buf.toString());
    }

    /**
     * 获取驾车popInfo描述
     *
     * @param index
     * @return
     */
    public Spanned getCarInfo(int index) {
        String content = "";
        DriveSegment seg = (DriveSegment) mRoute.getStep(index);
        if (!AMapUtil.IsEmptyOrNullString(seg.getRoadName())
                && !AMapUtil.IsEmptyOrNullString(seg.getActionDescription())) {
            content = seg.getActionDescription() + " --> " + seg.getRoadName();
        } else {
            content = seg.getActionDescription() + seg.getRoadName();
        }

        content = AMapUtil.colorFont(content, AMapUtil.HtmlGray);
        content += AMapUtil.makeHtmlNewLine();
        content += String.format("%s%s", ChString.About,
                AMapUtil.getFriendlyLength(seg.getLength()));

        return AMapUtil.stringToSpan(content);
    }

    /**
     * 获取步行popInfo描述
     *
     * @param index
     * @return
     */
    private Spanned getFootSpan(int index) {
        if (mRoute.getMode() == Route.WalkDefault) {
            String content = "";
            WalkSegment seg = (WalkSegment) mRoute.getStep(index);
            if (!AMapUtil.IsEmptyOrNullString(seg.getRoadName())
                    && !AMapUtil
                    .IsEmptyOrNullString(seg.getActionDescription())) {
                content = seg.getActionDescription() + " --> "
                        + seg.getRoadName();
            } else {
                content = seg.getActionDescription() + seg.getRoadName();
            }

            content = AMapUtil.colorFont(content, AMapUtil.HtmlGray);
            content += AMapUtil.makeHtmlNewLine();
            content += String.format("%s%s", ChString.About,
                    AMapUtil.getFriendlyLength(seg.getLength()));

            return AMapUtil.stringToSpan(content);
        } else {
            StringBuilder result = new StringBuilder();
            result.append(ChString.ByFoot).append(ChString.To);

            if (index == mRoute.getStepCount() - 1) {
                result.append(AMapUtil.colorFont(ChString.TargetPlace,
                        AMapUtil.HtmlGray));
            } else {
                result.append(AMapUtil.colorFont(
                        ((BusSegment) mRoute.getStep(index + 1)).getLineName()
                                + ChString.Station, AMapUtil.HtmlGray
                ));
            }

            result.append(AMapUtil.makeHtmlNewLine());
            result.append(ChString.About
                    + AMapUtil.getFriendlyLength(mRoute.getStep(index)
                    .getLength()));

            return AMapUtil.stringToSpan(result.toString());
        }
    }

    /**
     * 工具方法， 将一个segment shaps 转化成map的LatLng list 方便添加到地图
     *
     * @param shapes
     * @return
     */
    private ArrayList<LatLng> convertArrList(LatLonPoint[] shapes) {
        ArrayList<LatLng> lineShapes = new ArrayList<LatLng>();
        for (LatLonPoint point : shapes) {
            LatLng latLngTemp = SearchPointConvert(point);
            lineShapes.add(latLngTemp);
        }
        return lineShapes;
    }

    /**
     * 工具方法，将搜索得到的LatLonPoint转成latLng 才能添加到地图上
     *
     * @param latLonPoint
     * @return
     */
    private LatLng SearchPointConvert(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }
}

