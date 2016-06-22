package bamin.com.bamindaijia.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.github.lguipeng.library.animcheckbox.AnimCheckBox;

import java.util.List;

import bamin.com.bamindaijia.R;
import bamin.com.bamindaijia.constant.Constant;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    MapView mMapView = null;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {

        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    double latitude = amapLocation.getLatitude();//获取纬度
                    double longitude = amapLocation.getLongitude();//获取经度
//                    amapLocation.getAccuracy();//获取精度信息
//                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    Date date = new Date(amapLocation.getTime());
//                    df.format(date);//定位时间
//                    amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
//                    amapLocation.getCountry();//国家信息
//                    amapLocation.getProvince();//省信息
//                    amapLocation.getCity();//城市信息
//                    amapLocation.getDistrict();//城区信息
//                    amapLocation.getStreet();//街道信息
//                    amapLocation.getStreetNum();//街道门牌号信息
                    mProgressDialog.dismiss();//定位后取消progressDialog
                    mCityCode = amapLocation.getCityCode();
                    mCity = amapLocation.getCity();
//                    amapLocation.getAdCode();//地区编码
//                    amapLocation.getAoiName();//获取当前定位点的AOI信息
                    //设置上车地址
                    mAmapLocationPoiName = amapLocation.getPoiName();
                    mTextView_start.setText(mAmapLocationPoiName);
                    Log.e("onLocationChanged", "" + amapLocation.toString());
                    mLocationLatLng = new LatLng(latitude, longitude);
                    moveCenterMarker(mLocationLatLng);
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }

    };
    private TextView mTextView_end;
    private ProgressDialog mProgressDialog;
    private RelativeLayout mRela_xiaofei;
    private RelativeLayout mRela_daijiafei;
    private TextView mTextView_xiaofei;
    private TextView mTextView_daijiafei;
    private Button mButton_call;
    private AnimCheckBox mAnimCheckBox_pay_forMe;
    private TextView mTextView_contacts;
    private DrawerLayout drawer_layout;
    private RelativeLayout list_left_drawer;
    private void moveCenterMarker(LatLng latLng) {
        mMarkerOptions = new MarkerOptions();
        mMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_marker));
        mMarkerOptions.position(latLng);
        if (mMarker != null) {
            mMarker.destroy();
            Log.e("onLocationChanged", "销毁marker");
        }
        mMarker = AMap.addMarker(mMarkerOptions);
        AMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
    }

    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private com.amap.api.maps.AMap AMap;
    private MarkerOptions mMarkerOptions = null;
    private int mBasic_color;
    private int mText_gray;
    private TextView mTextView_forMe;
    private TextView mTextView_forOther;
    private TextView mTextView_line_forMe;
    private TextView mTextView_line_forOther;
    private RelativeLayout mRela_lianxiren;
    private RelativeLayout mRela_parForMe;
    private TextView mTextView_lianxiren;
    private TextView mTextView_payforme_line;
    private Marker mMarker;
    private Marker mCenterMarker;
    private TextView mTextView_start;
    private Marker[] mMarkersPois = new Marker[5];
    private List<PoiItem> mPois;
    private LatLng mLocationLatLng;
    private String mAmapLocationPoiName;
    private String mCityCode;
    private String mCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPermission();
        ButterKnife.bind(this);
        initColor();
        findID();
        setListener();
        initUI();
        initMap(savedInstanceState);
    }

    private void findID() {
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        list_left_drawer = (RelativeLayout) findViewById(R.id.list_left_drawer);
        mMapView = (MapView) findViewById(R.id.map);
        mTextView_forMe = (TextView) findViewById(R.id.textView_forMe);
        mTextView_forOther = (TextView) findViewById(R.id.textView_forOther);
        mTextView_line_forMe = (TextView) findViewById(R.id.textView_line_forMe);
        mTextView_line_forOther = (TextView) findViewById(R.id.textView_line_forOther);
        mRela_lianxiren = (RelativeLayout) findViewById(R.id.rela_lianxiren);
        mRela_parForMe = (RelativeLayout) findViewById(R.id.rela_parForMe);
        mTextView_lianxiren = (TextView) findViewById(R.id.textView_lianxiren);
        mTextView_payforme_line = (TextView) findViewById(R.id.textView_payforme_line);
        mTextView_start = (TextView) findViewById(R.id.textView_start);
        mTextView_end = (TextView) findViewById(R.id.textView_end);
        mRela_xiaofei = (RelativeLayout) findViewById(R.id.rela_xiaofei);
        mRela_daijiafei = (RelativeLayout) findViewById(R.id.rela_daijiafei);
        mTextView_xiaofei = (TextView) findViewById(R.id.textView_xiaofei);
        mTextView_daijiafei = (TextView) findViewById(R.id.textView_daijiafei);
        mButton_call = (Button) findViewById(R.id.button_call);
        mAnimCheckBox_pay_forMe = (AnimCheckBox) findViewById(R.id.AnimCheckBox_pay_forMe);
        mTextView_contacts = (TextView) findViewById(R.id.textView_contacts);
    }

    private void initUI() {
        forMeChangText();
        setProgressDialog();
        mRela_xiaofei.setVisibility(View.GONE);
        mRela_daijiafei.setVisibility(View.GONE);
        mTextView_xiaofei.setVisibility(View.GONE);
        mTextView_daijiafei.setVisibility(View.GONE);
        mButton_call.setVisibility(View.GONE);
    }

    private void setProgressDialog() {
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setMessage("正在定位……");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    finish();
                }
                return false;
            }
        });
    }

    //为自己叫车
    private void forMeChangText() {
        mTextView_forMe.setText("为自己叫代驾");
        mTextView_forOther.setText("为别人叫");
        mTextView_forMe.setTextColor(mBasic_color);
        mTextView_line_forMe.setVisibility(View.VISIBLE);
        mTextView_forOther.setTextColor(mText_gray);
        mTextView_line_forOther.setVisibility(View.INVISIBLE);
        mRela_lianxiren.setVisibility(View.GONE);
        mRela_parForMe.setVisibility(View.GONE);
        mTextView_lianxiren.setVisibility(View.GONE);
        mTextView_payforme_line.setVisibility(View.GONE);
    }

    //为别人叫车
    private void forOtherChangText() {
        mTextView_forMe.setText("为自己叫");
        mTextView_forOther.setText("为别人叫代驾");
        mTextView_forMe.setTextColor(mText_gray);
        mTextView_line_forMe.setVisibility(View.INVISIBLE);
        mTextView_forOther.setTextColor(mBasic_color);
        mTextView_line_forOther.setVisibility(View.VISIBLE);
        mRela_lianxiren.setVisibility(View.VISIBLE);
        mRela_parForMe.setVisibility(View.VISIBLE);
        mTextView_lianxiren.setVisibility(View.VISIBLE);
        mTextView_payforme_line.setVisibility(View.VISIBLE);
    }

    private void initColor() {
        mBasic_color = getResources().getColor(R.color.basic_color);
        mText_gray = getResources().getColor(R.color.text_gray);
    }

    private void setListener() {
        findViewById(R.id.rela_forMe).setOnClickListener(this);
        findViewById(R.id.rela_forOther).setOnClickListener(this);
        findViewById(R.id.imageView_location).setOnClickListener(this);
        findViewById(R.id.rela_start).setOnClickListener(this);
        findViewById(R.id.rela_endSite).setOnClickListener(this);
        findViewById(R.id.rela_lianxiren).setOnClickListener(this);
        findViewById(R.id.rela_parForMe).setOnClickListener(this);
        findViewById(R.id.imageView_mine).setOnClickListener(this);
    }

    private void initMap(Bundle savedInstanceState) {
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mMapView.onCreate(savedInstanceState);
        AMap = mMapView.getMap();
        //设置默认定位按钮是否显示
        UiSettings uiSettings = AMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        AMap.setMyLocationEnabled(true);
        AMap.moveCamera(CameraUpdateFactory.zoomBy(6));

        ViewTreeObserver vto = mMapView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //没有这句话，里面会被执行三次
                mMapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.bts_location_fixed));
                mCenterMarker = AMap.addMarker(markerOptions);
                mCenterMarker.setTitle("从这里上车……");
                mCenterMarker.showInfoWindow();
                mCenterMarker.setPositionByPixels(mMapView.getMeasuredWidth() / 2, mMapView.getMeasuredHeight() / 2);
            }
        });
        AMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                LatLng position = mCenterMarker.getPosition();
                float distance = AMapUtils.calculateLineDistance(mLocationLatLng, position);

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (distance > 50) {
                        Log.e("onTouch", "拖动位置改变" + position);
                        GeocodeSearch geocodeSearch = new GeocodeSearch(MainActivity.this);
                        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                            @Override
                            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                                RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
                                Log.e("onRegeocodeSearched",
                                        regeocodeAddress.getBusinessAreas().get(0).getName()
                                                + "\n" + regeocodeAddress.getAois().get(0).getAoiName()
                                                + "\n" + regeocodeAddress.getCrossroads().get(0).getFirstRoadName()
                                                + "\n" + regeocodeAddress.getRoads().size()
                                );
                                mPois = regeocodeAddress.getPois();
                                Log.e("onRegeocodeSearched", "pois长度" + mPois.size());
                                mCenterMarker.setTitle(regeocodeAddress.getAois().get(0).getAoiName());
                                mCenterMarker.showInfoWindow();
                                mTextView_start.setText(regeocodeAddress.getAois().get(0).getAoiName());
                            }

                            @Override
                            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                            }
                        });
                        //latLonPoint参数表示一个Latlng，第二参数表示范围多少米，GeocodeSearch.AMAP表示是国测局坐标系还是GPS原生坐标系
                        LatLonPoint latLonPoint = new LatLonPoint(position.latitude, position.longitude);
                        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 100, GeocodeSearch.AMAP);
                        geocodeSearch.getFromLocationAsyn(query);
                    } else {
                        mCenterMarker.setTitle(mAmapLocationPoiName);
                        mCenterMarker.showInfoWindow();
                        mTextView_start.setText(mAmapLocationPoiName);
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mCenterMarker.hideInfoWindow();
                    mTextView_start.setText("正在获取上车地点……");
                }

            }
        });
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    private void addPoiMarker(int i) {
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng latLng = new LatLng(mPois.get(i).getLatLonPoint().getLatitude(), mPois.get(i).getLatLonPoint().getLongitude());
        Log.e("addPoiMarker", "pois坐标" + latLng.toString());
        markerOptions.position(latLng);
        mMarkersPois[i] = AMap.addMarker(markerOptions);
        mMarkersPois[i].setTitle(mPois.get(i).getTitle());
        mMarkersPois[i].showInfoWindow();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationClient.stopLocation();//停止定位
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mLocationClient.onDestroy();//销毁定位客户端。
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mMapView.onSaveInstanceState(outState);
    }

    private void initPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constant.PERMISSION.ACCESS_COARSE_LOCATION);//自定义的code
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constant.PERMISSION.ACCESS_FINE_LOCATION);//自定义的code
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constant.PERMISSION.WRITE_EXTERNAL_STORAGE);//自定义的code
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    Constant.PERMISSION.READ_EXTERNAL_STORAGE);//自定义的code
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    Constant.PERMISSION.READ_PHONE_STATE);//自定义的code
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //可在此继续其他操作。
        if (requestCode == Constant.PERMISSION.ACCESS_COARSE_LOCATION || requestCode == Constant.PERMISSION.ACCESS_FINE_LOCATION || requestCode == Constant.PERMISSION.WRITE_EXTERNAL_STORAGE || requestCode == Constant.PERMISSION.READ_EXTERNAL_STORAGE || requestCode == Constant.PERMISSION.READ_PHONE_STATE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initPermission();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_RESULT_CODE.CHOSSE_START_SITE && resultCode == Constant.REQUEST_RESULT_CODE.CHOSSE_START_SITE) {
            Log.e("onActivityResult", "出发地返回值");
            String startSiteBack = data.getStringExtra(Constant.INTENT_KEY.START_SITE_BACK);
            LatLonPoint startLatlngBack = data.getParcelableExtra(Constant.INTENT_KEY.START_LATLNG_BACK);
            mTextView_start.setText(startSiteBack);
            LatLng latLng = new LatLng(startLatlngBack.getLatitude(), startLatlngBack.getLongitude());
            AMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
            mCenterMarker.setTitle(startSiteBack);
            mCenterMarker.showInfoWindow();
        } else if (requestCode == Constant.REQUEST_RESULT_CODE.CHOSSE_END_SITE && resultCode == Constant.REQUEST_RESULT_CODE.CHOSSE_END_SITE) {
            mTextView_end.setText(data.getStringExtra(Constant.INTENT_KEY.END_SITE_BACK));
            mRela_xiaofei.setVisibility(View.VISIBLE);
            mRela_daijiafei.setVisibility(View.VISIBLE);
            mTextView_xiaofei.setVisibility(View.VISIBLE);
            mTextView_daijiafei.setVisibility(View.VISIBLE);
            mButton_call.setVisibility(View.VISIBLE);
        }else if (requestCode==Constant.REQUEST_RESULT_CODE.CHOSSE_CONTACTS&&resultCode==Constant.REQUEST_RESULT_CODE.CHOSSE_CONTACTS){
            String theContacts = data.getStringExtra(Constant.INTENT_KEY.THE_CONTACTS);
            mTextView_contacts.setText(theContacts);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.imageView_mine:
                drawer_layout.openDrawer(list_left_drawer);
                break;
            case R.id.rela_lianxiren:
                intent.setClass(MainActivity.this,GetContactsActivity.class);
                startActivityForResult(intent, Constant.REQUEST_RESULT_CODE.CHOSSE_CONTACTS);
                break;
            case R.id.rela_parForMe:
                if (mAnimCheckBox_pay_forMe.isChecked()){
                    mAnimCheckBox_pay_forMe.setChecked(false);
                }else {
                    mAnimCheckBox_pay_forMe.setChecked(true);
                }
                break;
            case R.id.rela_endSite:
                intent.setClass(MainActivity.this, ChosseSite.class);
                intent.putExtra(Constant.INTENT_KEY.CHOSSE_TYPE, Constant.INTENT_KEY.END);
                intent.putExtra(Constant.INTENT_KEY.START_SITE, mLocationLatLng);
                intent.putExtra(Constant.INTENT_KEY.CITY, mCity);
                intent.putExtra(Constant.INTENT_KEY.CITY_CODE, mCityCode);
                startActivityForResult(intent, Constant.REQUEST_RESULT_CODE.CHOSSE_END_SITE);
                break;
            case R.id.rela_start:
                intent.setClass(MainActivity.this, ChosseSite.class);
                intent.putExtra(Constant.INTENT_KEY.CHOSSE_TYPE, Constant.INTENT_KEY.START);
                intent.putExtra(Constant.INTENT_KEY.START_SITE, mLocationLatLng);
                intent.putExtra(Constant.INTENT_KEY.CITY, mCity);
                intent.putExtra(Constant.INTENT_KEY.CITY_CODE, mCityCode);
                startActivityForResult(intent, Constant.REQUEST_RESULT_CODE.CHOSSE_START_SITE);
                break;
            case R.id.rela_forMe:
                forMeChangText();
                break;
            case R.id.rela_forOther:
                forOtherChangText();
                break;
            case R.id.imageView_location:
                mLocationClient.startLocation();
                break;
            default:
                break;
        }
    }
}
