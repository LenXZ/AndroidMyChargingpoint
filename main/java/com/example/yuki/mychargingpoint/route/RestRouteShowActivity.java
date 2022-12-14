package com.example.yuki.mychargingpoint.route;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapCarInfo;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapRestrictionInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.example.yuki.mychargingpoint.R;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.example.yuki.mychargingpoint.basic.BasicMapActivity;
import com.example.yuki.mychargingpoint.basic.TwoMapActivity;
import com.example.yuki.mychargingpoint.util.AddMarker;
import com.example.yuki.mychargingpoint.util.Constants;
import com.example.yuki.mychargingpoint.util.DialogFragmentSetting;
import com.example.yuki.mychargingpoint.util.SharedHelper;
import com.example.yuki.mychargingpoint.weather.WeatherSearchActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RestRouteShowActivity extends AppCompatActivity implements AMapNaviListener, OnClickListener, OnCheckedChangeListener, AMap.InfoWindowAdapter {
    private boolean congestion, cost, hightspeed, avoidhightspeed;
    /**
     * ????????????(??????)
     */
    private AMapNavi mAMapNavi;
    private AMap aMap;
    private AddMarker addMarker = new AddMarker();
    private Context context;
    /**
     * ????????????
     */
    private MapView mRouteMapView;
    private Marker mStartMarker;
    private Marker mEndMarker;
    /**
     * ????????????Action?????????
     */
    private boolean mapClickStartReady;
    private boolean mapClickwayReady;
    /**
     * ????????????Aciton?????????
     */
    private boolean mapClickEndReady;
    private boolean mymarkerbool02 = false;
    private NaviLatLng startLatlng = new NaviLatLng(39.925041, 116.437901);
    private NaviLatLng endLatlng = new NaviLatLng(39.955846, 116.352765);
    private NaviLatLng wayLatlng = new NaviLatLng(39.955846, 116.352765);
    private List<NaviLatLng> startList = new ArrayList<NaviLatLng>();
    /**
     * ?????????????????????
     */
    private List<NaviLatLng> wayList = new ArrayList<NaviLatLng>();
    /**
     * ?????????????????????????????????????????????
     */
    private List<NaviLatLng> endList = new ArrayList<NaviLatLng>();
    /**
     * ???????????????????????????
     */
    private SparseArray<RouteOverLay> routeOverlays = new SparseArray<RouteOverLay>();
    private Marker mWayMarker;
    /**
     * ?????????????????????????????????????????????????????????
     */
    private int routeIndex;
    /**
     * ???????????????????????????????????????????????????????????????????????????????????????
     **/
    private int zindex = 1;
    /**
     * ???????????????????????????
     */
    private boolean calculateSuccess = false;
    private boolean chooseRouteSuccess = false;
    final private int BasicMap = 110;
    final private int TwoMap = 111;
    final private int RestRoute = 112;
    final private int RoutePoint = 113;
    final private int WeatherSearch = 114;
    final private int Setting = 115;
    private SharedHelper sh;
    private Map map;

    //    RouteChose routeChose = new RouteChose();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rest_calculate);
        CheckBox congestion = (CheckBox) findViewById(R.id.congestion);
        CheckBox cost = (CheckBox) findViewById(R.id.cost);
        CheckBox hightspeed = (CheckBox) findViewById(R.id.hightspeed);
        CheckBox avoidhightspeed = (CheckBox) findViewById(R.id.avoidhightspeed);
        Button calculate = (Button) findViewById(R.id.calculate);
        Button startPoint = (Button) findViewById(R.id.startpoint);
        Button wayPoint = (Button) findViewById(R.id.waypoint);
        Button endPoint = (Button) findViewById(R.id.endpoint);
        Button selectroute = (Button) findViewById(R.id.selectroute);
        Button gpsnavi = (Button) findViewById(R.id.gpsnavi);
        Button emulatornavi = (Button) findViewById(R.id.emulatornavi);
        Button mypoint2 = (Button) findViewById(R.id.mypoint2);
        calculate.setOnClickListener(this);
        startPoint.setOnClickListener(this);
        wayPoint.setOnClickListener(this);
        endPoint.setOnClickListener(this);
        selectroute.setOnClickListener(this);
        gpsnavi.setOnClickListener(this);
        emulatornavi.setOnClickListener(this);
        mypoint2.setOnClickListener(this);
        congestion.setOnCheckedChangeListener(this);
        cost.setOnCheckedChangeListener(this);
        hightspeed.setOnCheckedChangeListener(this);
        avoidhightspeed.setOnCheckedChangeListener(this);

        mRouteMapView = (MapView) findViewById(R.id.navi_view);
        mRouteMapView.onCreate(savedInstanceState);
        context = getApplicationContext();
        aMap = mRouteMapView.getMap();
//        aMap.setOnMarkerClickListener(this);
//        aMap.setOnInfoWindowClickListener(this);
        aMap.setInfoWindowAdapter(this);
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //???????????????
                if (mapClickStartReady) {
                    startLatlng = new NaviLatLng(latLng.latitude, latLng.longitude);
                    mStartMarker.setPosition(latLng);
                    startList.clear();
                    wayList.clear();
                    startList.add(startLatlng);
                    mapClickStartReady = false;
                }
                //??????????????????
                if (mapClickwayReady) {
                    wayLatlng = new NaviLatLng(latLng.latitude, latLng.longitude);
                    mWayMarker.setPosition(latLng);
//                    wayList.clear();
                    wayList.add(wayLatlng);
                    mapClickwayReady = false;
                }
                //???????????????
                if (mapClickEndReady) {
                    endLatlng = new NaviLatLng(latLng.latitude, latLng.longitude);
                    mEndMarker.setPosition(latLng);
                    endList.clear();
                    endList.add(endLatlng);
                    mapClickEndReady = false;
                }

            }
        });
        // ?????????Marker???????????????
        mStartMarker = aMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.start))));
        mWayMarker = aMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.through))));
        mEndMarker = aMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.end))));

        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        mAMapNavi.addAMapNaviListener(this);
        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                setUp(aMap);
            }
        });
//        routeChose.RouteChoseContextAMap(context,aMap);
        initLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(1, BasicMap, 1, R.string.action_BasicMap);
        menu.add(1, TwoMap, 2, R.string.action_TwoMap);
        menu.add(1, RestRoute, 3, R.string.action_RestRoute);
        menu.add(1, RoutePoint, 4, R.string.action_RoutePoint);
        menu.add(1, WeatherSearch, 5, R.string.action_WeatherSearch);
        menu.add(1, Setting, 6, R.string.action_settings);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case BasicMap:
                BasicMapActivity.instance.finish();
                startActivity(new Intent(RestRouteShowActivity.this, BasicMapActivity.class));
                finish();
                break;
            case TwoMap:
                startActivity(new Intent(RestRouteShowActivity.this, TwoMapActivity.class));
                finish();
                break;
            case RestRoute:
                startActivity(new Intent(RestRouteShowActivity.this, RestRouteShowActivity.class));
                finish();
                break;
            case RoutePoint:
                startActivity(new Intent(RestRouteShowActivity.this, RoutePointActivity.class));
                finish();
                break;
            case WeatherSearch:
                startActivity(new Intent(RestRouteShowActivity.this, WeatherSearchActivity.class));
                break;
            case Setting:
                DialogFragmentSetting dialog = new DialogFragmentSetting();
                dialog.DialogFragmentSetting(context);
                dialog.show(getFragmentManager(), "loginDialog");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * ??????????????????
     */
    private void setUp(AMap amap) {

        UiSettings uiSettings = amap.getUiSettings();
        amap.showIndoorMap(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setScaleControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
    }

    /**
     * ????????????
     */
    private void initLocation() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();//??????????????????????????????
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//??????????????????????????????????????????????????????
        // ???????????????????????????
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                fromResource(R.drawable.gps_point));
        // ??????????????????????????????????????????
        myLocationStyle.strokeColor(Color.argb(180, 3, 145, 255));
        //??????????????????????????????????????????
        myLocationStyle.strokeWidth(5);
        // ???????????????????????????
        myLocationStyle.radiusFillColor(Color.argb(10, 0, 0, 180));
        aMap.setMyLocationStyle(myLocationStyle);//?????????????????????Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);?????????????????????????????????????????????????????????
        aMap.setMyLocationEnabled(true);// ?????????true?????????????????????????????????false??????????????????????????????????????????????????????false???
    }

    /**
     * ??????????????????
     */
    @Override
    protected void onResume() {
        super.onResume();
        mRouteMapView.onResume();
    }

    /**
     * ??????????????????
     */
    @Override
    protected void onPause() {
        super.onPause();
        mRouteMapView.onPause();
    }

    /**
     * ??????????????????
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mRouteMapView.onSaveInstanceState(outState);
    }

    /**
     * ??????????????????
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        startList.clear();
        wayList.clear();
        endList.clear();
        routeOverlays.clear();
        mRouteMapView.onDestroy();
        /**
         * ?????????????????????????????????activity??????????????????????????????????????????
         */
        mAMapNavi.removeAMapNaviListener(this);
        mAMapNavi.destroy();

    }

    /**
     * ????????????
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        switch (id) {
            case R.id.congestion:
                congestion = isChecked;
                break;
            case R.id.avoidhightspeed:
                avoidhightspeed = isChecked;
                break;
            case R.id.cost:
                cost = isChecked;
                break;
            case R.id.hightspeed:
                hightspeed = isChecked;
                break;
            default:
                break;
        }
    }

    @Override
    public void onInitNaviSuccess() {
    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {
        //????????????????????????????????????
        routeOverlays.clear();
        HashMap<Integer, AMapNaviPath> paths = mAMapNavi.getNaviPaths();
        for (int i = 0; i < ints.length; i++) {
            AMapNaviPath path = paths.get(ints[i]);
            if (path != null) {
                drawRoutes(ints[i], path);
            }
        }
    }

    @Override
    public void onCalculateRouteSuccess() {
        /**?????????????????????????????????**/
        routeOverlays.clear();
        AMapNaviPath path = mAMapNavi.getNaviPath();
        /**??????????????????????????????????????????????????????1??????**/
        drawRoutes(-1, path);
    }

    @Override
    public void onCalculateRouteFailure(int arg0) {
        calculateSuccess = false;
        Toast.makeText(getApplicationContext(), "?????????????????????errorcode???" + arg0, Toast.LENGTH_SHORT).show();
    }

    private void drawRoutes(int routeId, AMapNaviPath path) {
        calculateSuccess = true;
        aMap.moveCamera(CameraUpdateFactory.changeTilt(0));
        RouteOverLay routeOverLay = new RouteOverLay(aMap, path, this);
        routeOverLay.setTrafficLine(false);
        routeOverLay.addToMap();
        routeOverlays.put(routeId, routeOverLay);
    }

    public void changeRoute() {
        if (!calculateSuccess) {
            Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
            return;
        }
        /**
         * ?????????????????????????????????
         */
        if (routeOverlays.size() == 1) {
            chooseRouteSuccess = true;
            //????????????AMapNavi ???????????????????????????
            mAMapNavi.selectRouteId(routeOverlays.keyAt(0));
            Toast.makeText(this, "????????????:" + (mAMapNavi.getNaviPath()).getAllLength() + "m" + "\n" + "????????????:" + (mAMapNavi.getNaviPath()).getAllTime() + "s", Toast.LENGTH_SHORT).show();
            return;
        }

        if (routeIndex >= routeOverlays.size())
            routeIndex = 0;
        int routeID = routeOverlays.keyAt(routeIndex);
        //????????????????????????
        for (int i = 0; i < routeOverlays.size(); i++) {
            int key = routeOverlays.keyAt(i);
            routeOverlays.get(key).setTransparency(0.4f);
        }
        routeOverlays.get(routeID).setTransparency(1);
        /**????????????????????????????????????????????????????????????????????????????????????????????????????????????**/
        routeOverlays.get(routeID).setZindex(zindex++);

        //????????????AMapNavi ???????????????????????????
        mAMapNavi.selectRouteId(routeID);
        Toast.makeText(this, "????????????:" + mAMapNavi.getNaviPath().getLabels(), Toast.LENGTH_SHORT).show();
        routeIndex++;
        chooseRouteSuccess = true;

        /**????????????????????????????????????????????????**/
        AMapRestrictionInfo info = mAMapNavi.getNaviPath().getRestrictionInfo();
        if (!TextUtils.isEmpty(info.getRestrictionTitle())) {
            Toast.makeText(this, info.getRestrictionTitle(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * ????????????????????????????????????
     */
    private void clearRoute() {
        for (int i = 0; i < routeOverlays.size(); i++) {
            RouteOverLay routeOverlay = routeOverlays.valueAt(i);
            routeOverlay.removeFromMap();
        }
        routeOverlays.clear();
    }

    /**
     * ?????????
     */
    private void calculate() {
        clearRoute();
        mapClickStartReady = false;
        mapClickEndReady = false;
        if (avoidhightspeed && hightspeed) {
            Toast.makeText(getApplicationContext(), "??????????????????????????????????????????true.", Toast.LENGTH_LONG).show();
        }
        if (cost && hightspeed) {
            Toast.makeText(getApplicationContext(), "??????????????????????????????????????????true.", Toast.LENGTH_LONG).show();
        }
            /*
             * strategyFlag???????????????????????????PathPlanningStrategy????????????????????????????????????PathPlanningStrategy?????????????????????
			 * ???:mAMapNavi.calculateDriveRoute(mStartList, mEndList, mWayPointList,PathPlanningStrategy.DRIVING_DEFAULT);
			 */
        int strategyFlag = 0;
        try {
            strategyFlag = mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (strategyFlag >= 0) {
            String carNumber = null;
            AMapCarInfo carInfo = new AMapCarInfo();
            try {
                sh = new SharedHelper(context);
                map = sh.read();
                carNumber = map.get("carnumber").toString();
            } catch (Exception e) {

            }
//            String regex = ".*[a-zA-Z]+.*";
//            Matcher m = Pattern.compile(regex).matcher(carNumber);
            if (carNumber!=null) {
                //????????????
                carInfo.setCarNumber(carNumber);
                //????????????????????????????????????
                carInfo.setRestriction(true);
            } else {
                carInfo.setRestriction(false);
            }
            mAMapNavi.setCarInfo(carInfo);
            mAMapNavi.calculateDriveRoute(startList, endList, wayList, strategyFlag);
//            Toast.makeText(getApplicationContext(), "??????:" + strategyFlag, Toast.LENGTH_LONG).show();
//                    wayList.add(new NaviLatLng(Constants.chargingpointbaicheng03.latitude, Constants.chargingpointbaicheng03.longitude));
//                    for (int i = 1; i < 3; i++) {
//                        length3 = i;
//                        Log.w("??????0000", "" + i);
//                        int re1 = 0;
//                        try {
//                            Intent intent1 = new Intent();
//                            intent1.setClassName(this, "RouteChose.class");
//                            startActivity(intent1);
//                            Log.w("??????0001", "OK");
//                        } catch (Exception e) {
//                            Log.w("??????0001", "WRONG01");
//                        }
//                        try {
//                            wayList2.clear();
//                            wayList2.add(addMarker.getNaviLatLng(i));
//                            routeChose.RouteChose0(startList, endList, wayList2);
//                            routeChose.DriveRoute();
//                            Timer timer = new Timer();//?????????Timer???
//                            timer.schedule(new TimerTask() {
//                                public void run() {
//                                    this.cancel();
//                                }
//                            }, 1000);
//                            Thread thread=new Thread();
//                            thread.sleep(1000);
//                            re1 = routeChose.DriveRoute();
//                            routeOverlays.clear();
//                            Log.w("??????0002", "OK");
//                        } catch (Exception e) {
//                            Log.w("??????0002", "WRONG02");
//                        }

//                        try {
//                            int re;
//                            RouteChose routeChose = new RouteChose();
//                            re = routeChose.RouteChose(startList, endList, wayList);
//                            if (length1 == 0) {
//                                length1 = re;
//                            } else {
//                                if (length1 >= re) {
//                                    length1 = re;
//                                    length2 = i;
//                                }
//                            }
//                        } catch (Exception e) {
//                            Toast.makeText(getApplicationContext(), "wrong3", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                    wayList.clear();
//                    wayList.add(new NaviLatLng(marker3[length2].getPosition().latitude, marker3[length2].getPosition().longitude));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calculate:
                calculate();
                break;
            case R.id.startpoint:
                Toast.makeText(this, "???????????????????????????", Toast.LENGTH_SHORT).show();
                mapClickStartReady = true;
                break;
            case R.id.waypoint:
                Toast.makeText(this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
                mapClickwayReady = true;
                break;
            case R.id.endpoint:
                Toast.makeText(this, "???????????????????????????", Toast.LENGTH_SHORT).show();
                mapClickEndReady = true;
                break;
            case R.id.selectroute:
                changeRoute();
                break;
            case R.id.gpsnavi:
                Intent gpsintent = new Intent(getApplicationContext(), RouteNaviActivity.class);
                gpsintent.putExtra("gps", true);
                gpsintent.putExtra("getNavi", false);
                startActivity(gpsintent);
                break;
            case R.id.emulatornavi:
                Intent intent = new Intent(getApplicationContext(), RouteNaviActivity.class);
                intent.putExtra("gps", false);
                intent.putExtra("getNavi", false);
                startActivity(intent);
                break;
            case R.id.mypoint2:
                if (mymarkerbool02) {
                    mymarkerbool02 = false;
                    addMarker.MarkerRemove();
                } else {
                    mymarkerbool02 = true;
                    addMarker.addMarkersToMap(aMap);// ??????????????????marker
                }
                break;
            default:
                break;
        }
    }

    //?????????marker??????????????????
    @Override
    public View getInfoWindow(final Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.poikeywordsearch_uri02,
                null);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(marker.getTitle());
        TextView snippet = (TextView) view.findViewById(R.id.snippet);
        snippet.setText("" + marker.getSnippet());
        ImageButton button = (ImageButton) view
                .findViewById(R.id.start_amap_app);
        //
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wayLatlng = new NaviLatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                mWayMarker.setPosition(marker.getPosition());
                wayList.add(wayLatlng);
                Toast.makeText(context, "?????????"+marker.getTitle()+"????????????", Toast.LENGTH_SHORT).show();
            }
        });
        ImageButton button02 = (ImageButton) view
                .findViewById(R.id.start_amap_app02);
        //
        button02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endLatlng = new NaviLatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                mEndMarker.setPosition(marker.getPosition());
                endList.clear();
                endList.add(endLatlng);
                Toast.makeText(context, "?????????"+marker.getTitle()+"????????????", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    /**
     * ************************************************** ?????????????????????????????????????????????????????????????????????????????????????????????***********************************************************************************************
     **/

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo arg0) {


    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo arg0) {


    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] arg0) {


    }

    @Override
    public void hideCross() {


    }

    @Override
    public void hideLaneInfo() {


    }

    @Override
    public void notifyParallelRoad(int arg0) {


    }

    @Override
    public void onArriveDestination() {


    }

    @Override
    public void onArrivedWayPoint(int arg0) {


    }

    @Override
    public void onEndEmulatorNavi() {


    }

    @Override
    public void onGetNavigationText(int arg0, String arg1) {


    }

    @Override
    public void onGpsOpenStatus(boolean arg0) {


    }

    @Override
    public void onInitNaviFailure() {


    }

    @Override
    public void onLocationChange(AMapNaviLocation arg0) {


    }

    @Override
    public void onNaviInfoUpdate(NaviInfo arg0) {


    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo arg0) {


    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapCameraInfos) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] amapServiceAreaInfos) {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {


    }

    @Override
    public void onReCalculateRouteForYaw() {


    }

    @Override
    public void onStartNavi(int arg0) {


    }

    @Override
    public void onTrafficStatusUpdate() {


    }

    @Override
    public void showCross(AMapNaviCross arg0) {


    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] arg0, byte[] arg1, byte[] arg2) {


    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo arg0) {


    }

    @Override
    public void onPlayRing(int i) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat arg0) {


    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
