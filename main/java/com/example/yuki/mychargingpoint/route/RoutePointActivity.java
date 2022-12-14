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

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
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
import com.example.yuki.mychargingpoint.location.Utils;
import com.example.yuki.mychargingpoint.util.AddMarker;
import com.example.yuki.mychargingpoint.util.DialogFragmentSetting;
import com.example.yuki.mychargingpoint.util.SharedHelper;
import com.example.yuki.mychargingpoint.util.ToastUtil;
import com.example.yuki.mychargingpoint.weather.WeatherSearchActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoutePointActivity extends AppCompatActivity implements AMapLocationListener, AMapNaviListener, OnClickListener, AMap.OnInfoWindowClickListener,
        AMap.InfoWindowAdapter, OnCheckedChangeListener, AMap.OnMarkerClickListener {
    private boolean congestion, cost, hightspeed, avoidhightspeed;
    /***
     * ????????????(??????)
     */
    private AMapNavi mAMapNavi;
    private AMap aMap;
    private AddMarker addMarker = new AddMarker();
    private Context context;
    /***
     * ????????????
     */
    private MapView mRouteMapView;
    private Marker mStartMarker;
    private Marker mWayMarker;
    private Marker mEndMarker;
    /***
     * ????????????Action?????????
     */
    private boolean mapClickwayReady;
    /***
     * ????????????Aciton?????????
     */
    private boolean mapClickEndReady;
    private boolean mymarkerbool02 = false;
    private NaviLatLng endLatlng = new NaviLatLng(39.955846, 116.352765);
    private NaviLatLng wayLatlng = new NaviLatLng(39.955846, 116.352765);
    private NaviLatLng startLatlng = new NaviLatLng(39.925041, 116.437901);
    private List<NaviLatLng> startList = new ArrayList<NaviLatLng>();
    /***
     * ?????????????????????
     */
    private List<NaviLatLng> wayList = new ArrayList<NaviLatLng>();
    /***
     * ?????????????????????????????????????????????
     */
    private List<NaviLatLng> endList = new ArrayList<NaviLatLng>();
    /***
     * ???????????????????????????
     */
    private SparseArray<RouteOverLay> routeOverlays = new SparseArray<RouteOverLay>();
    /***
     * ?????????????????????????????????????????????????????????
     */
    private int routeIndex;
    /***
     * ???????????????????????????????????????????????????????????????????????????????????????
     */
    private int zindex = 1;
    /**
     * ???????????????????????????
     */
    private boolean calculateSuccess = false;
    //    private boolean chooseRouteSuccess = false;
    private EditText editText;
    private AMapLocation mCurrentLocation;
    private AMapLocationClient mlocationClient = null;
    private AMapLocationClientOption mLocationOption = null;
    //    private int length[] = new int[10], length1 = 0, length2 = 0, length3 = 0;
    final private int BasicMap = 110;
    final private int TwoMap = 111;
    final private int RestRoute = 112;
    final private int RoutePoint = 113;
    final private int WeatherSearch = 114;
    final private int Setting = 115;
    private SharedHelper sh;
    private Map map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rest_calculatepoint);
        editText = (EditText) findViewById(R.id.car_number);
        CheckBox congestion = (CheckBox) findViewById(R.id.congestion);
        CheckBox cost = (CheckBox) findViewById(R.id.cost);
        CheckBox hightspeed = (CheckBox) findViewById(R.id.hightspeed);
        CheckBox avoidhightspeed = (CheckBox) findViewById(R.id.avoidhightspeed);
        Button calculate = (Button) findViewById(R.id.calculate);
        Button wayPoint = (Button) findViewById(R.id.waypoint);
        Button endPoint = (Button) findViewById(R.id.endpoint);
        Button selectroute = (Button) findViewById(R.id.selectroute);
        Button gpsnavi = (Button) findViewById(R.id.gpsnavi);
        Button emulatornavi = (Button) findViewById(R.id.emulatornavi);
        Button mypoint2 = (Button) findViewById(R.id.mypoint2);
        Button mypointClear = (Button) findViewById(R.id.mypointClear);
        calculate.setOnClickListener(this);
        wayPoint.setOnClickListener(this);
        endPoint.setOnClickListener(this);
        selectroute.setOnClickListener(this);
        gpsnavi.setOnClickListener(this);
        emulatornavi.setOnClickListener(this);
        mypoint2.setOnClickListener(this);
        mypointClear.setOnClickListener(this);
        congestion.setOnCheckedChangeListener(this);
        cost.setOnCheckedChangeListener(this);
        hightspeed.setOnCheckedChangeListener(this);
        avoidhightspeed.setOnCheckedChangeListener(this);

        mRouteMapView = (MapView) findViewById(R.id.navi_view);
        mRouteMapView.onCreate(savedInstanceState);
        context = getApplicationContext();
        aMap = mRouteMapView.getMap();
        aMap.setOnMarkerClickListener(this);
        aMap.setOnInfoWindowClickListener(this);
        aMap.setInfoWindowAdapter(this);
        initLocationbest();
        initLocation();
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //????????????
                if (mapClickwayReady) {
                    wayLatlng = new NaviLatLng(latLng.latitude, latLng.longitude);
                    mWayMarker.setPosition(latLng);
                    wayList.add(wayLatlng);
                    mapClickwayReady = false;
                }
                if (mapClickEndReady) {
                    startLatlng = new NaviLatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
//                    mStartMarker.setPosition(latLng);
//                    wayList.clear();
                    startList.clear();
                    startList.add(startLatlng);
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
        getNaviParam();
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
                startActivity(new Intent(RoutePointActivity.this, BasicMapActivity.class));
                finish();
                break;
            case TwoMap:
                startActivity(new Intent(RoutePointActivity.this, TwoMapActivity.class));
                finish();
                break;
            case RestRoute:
                startActivity(new Intent(RoutePointActivity.this, RestRouteShowActivity.class));
                finish();
                break;
            case RoutePoint:
                startActivity(new Intent(RoutePointActivity.this, RoutePointActivity.class));
                finish();
                break;
            case WeatherSearch:
                startActivity(new Intent(RoutePointActivity.this, WeatherSearchActivity.class));
                break;
            case Setting:
                DialogFragmentSetting dialog = new DialogFragmentSetting();
                dialog.DialogFragmentSetting(context);
                dialog.show(getFragmentManager(), "loginDialog");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUp(AMap amap) {

        UiSettings uiSettings = amap.getUiSettings();
        amap.showIndoorMap(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setScaleControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
    }

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

    public void initLocationbest() {
        mlocationClient = new AMapLocationClient(this);
        //?????????????????????
        mLocationOption = new AMapLocationClientOption();
        //??????????????????
        mlocationClient.setLocationListener(this);
        //???????????????????????????????????????Battery_Saving?????????????????????Device_Sensors??????????????????
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //??????????????????,????????????,?????????2000ms
        mLocationOption.setInterval(2000);
        //???????????????????????????,?????????false
        mLocationOption.setOnceLocation(true);
        //??????setOnceLocationLatest(boolean b)?????????true??????????????????SDK???????????????3s???????????????????????????????????????
        //??????????????????true???setOnceLocation(boolean b)????????????????????????true??????????????????
        mLocationOption.setOnceLocationLatest(true);
        //??????????????????
        mlocationClient.setLocationOption(mLocationOption);
        // ????????????????????????????????????????????????????????????????????????????????????????????????????????????
        // ??????????????????????????????????????????????????????????????????2000ms?????????????????????????????????stopLocation()???????????????????????????
        // ???????????????????????????????????????????????????onDestroy()??????
        // ?????????????????????????????????????????????????????????????????????stopLocation()???????????????????????????sdk???????????????
        //????????????
        mlocationClient.startLocation();
    }

    private void getNaviParam() {
        Intent intent = getIntent();
        boolean bool = intent.getBooleanExtra("getNavi", false);
        if (intent == null || bool == false) {
            return;
        } else {
            endLatlng = intent.getParcelableExtra("end");
//                    mStartMarker.setPosition(latLng);
            endList.clear();
            endList.add(endLatlng);
            mapClickEndReady = false;
            Timer timer = new Timer();//?????????Timer???
            timer.schedule(new TimerTask() {
                public void run() {
                    calculate();
                    this.cancel();
                }
            }, 2000);//??????
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
//        if (aMapLocation == null || aMapLocation.getErrorCode() != AMapLocation.LOCATION_SUCCESS) {
//            return;
//        }
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                mCurrentLocation = aMapLocation;
                startLatlng = new NaviLatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                startList.clear();
                startList.add(startLatlng);
            } else {
                //???????????????????????????ErrCode????????????????????????????????????????????????errInfo???????????????????????????????????????
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        } else {
        }
//        double m1=mCurrentLocation.getLatitude();
//        double m2=mCurrentLocation.getLongitude();
//        ToastUtil.show(RestRouteShowActivity.this, getString(R.string.welcometxt));
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
    /**????????????*/
    public void onCalculateMultipleRoutesSuccess(int[] ints) {
//        ????????????????????????????????????
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
        /**
         * ????????????????????????????????????
         */
        routeOverlays.clear();
        AMapNaviPath path = mAMapNavi.getNaviPath();
        /**
         * ??????????????????????????????????????????????????????1??????
         */
        drawRoutes(-1, path);
    }

    @Override
    public void onCalculateRouteFailure(int arg0) {
        calculateSuccess = false;
        Toast.makeText(getApplicationContext(), "?????????????????????errorcode???" + arg0, Toast.LENGTH_SHORT).show();
    }

    /**
     * ?????????
     */
    private void drawRoutes(int routeId, AMapNaviPath path) {
        calculateSuccess = true;
        aMap.moveCamera(CameraUpdateFactory.changeTilt(0));
        RouteOverLay routeOverLay = new RouteOverLay(aMap, path, this);
        routeOverLay.setTrafficLine(false);
        routeOverLay.addToMap();
        routeOverlays.put(routeId, routeOverLay);
    }

    /**
     * ?????????
     */
    public void changeRoute() {
        if (!calculateSuccess) {
            Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
            return;
        }
        /**?????????????????????????????????*/
        if (routeOverlays.size() == 1) {
            //????????????AMapNavi ???????????????????????????
            mAMapNavi.selectRouteId(routeOverlays.keyAt(0));
            Toast.makeText(this, "????????????:" + (mAMapNavi.getNaviPath()).getAllLength() + "m" + "\n" + "????????????:" + (mAMapNavi.getNaviPath()).getAllTime() + "s", Toast.LENGTH_SHORT).show();
            return;
        }

        if (routeIndex >= routeOverlays.size())
            routeIndex = 0;
        int routeID = routeOverlays.keyAt(routeIndex);
        /**????????????????????????*/
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
        mapClickwayReady = false;
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
            strategyFlag = mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, false);//?????????????????????true??????????????????????????????????????????
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
//            String regex=".*[a-zA-Z]+.*";
//            Matcher m= Pattern.compile(regex).matcher(carNumber);
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
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calculate:/**?????????*/
                calculate();
                break;
            case R.id.waypoint:
                Toast.makeText(this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
                mapClickwayReady = true;
                break;
            case R.id.endpoint:
                Toast.makeText(this, "???????????????????????????", Toast.LENGTH_SHORT).show();
                mapClickEndReady = true;
                break;
            case R.id.selectroute:/**?????????*/
                changeRoute();
                break;
            case R.id.gpsnavi:
                Intent gpsintent = new Intent(getApplicationContext(), RouteNaviActivity.class);
                gpsintent.putExtra("gps", true);
                gpsintent.putExtra("getNavi", false);
//                gpsintent.putExtra("start", new NaviLatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
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
            case R.id.mypointClear:
                startList.clear();
                wayList.clear();
                endList.clear();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTitle().startsWith("?????????")) {
            int markerId = Integer.parseInt(marker.getTitle().toString().subSequence(3, 5).toString());
//            ToastUtil.show(this, ""+markerId);
            addMarker.marker3[markerId].showInfoWindow();
            return false;
        }
        return false;
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
    public View getInfoContents(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker arg0) {
        // TODO Auto-generated method stub

    }
}
