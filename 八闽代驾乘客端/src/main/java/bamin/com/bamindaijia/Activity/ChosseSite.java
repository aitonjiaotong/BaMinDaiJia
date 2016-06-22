package bamin.com.bamindaijia.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Tip;

import java.util.ArrayList;
import java.util.List;

import bamin.com.bamindaijia.R;
import bamin.com.bamindaijia.constant.Constant;

public class ChosseSite extends AppCompatActivity implements View.OnClickListener {

    private ListView mListView_site;
    private MyAdapter mAdapter;
    private View mSite_head;
    private LatLng mStart_site;
    private List<PoiItem> mPois = new ArrayList<>();
    private TextView mEditText_where;
    private Inputtips mInputtips;
    private TextView mTextView_city;
    private String mCity;
    private String mCityCode;
    private ListView mListView_search;
    private List<Tip> listSearch = new ArrayList<>();
    private MySearchAdapter mAdapterSearch;
    private String mChosseType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosse_site);
        initIntent();
        findID();
        initUI();
        setListener();
    }

    private void initIntent() {
        Intent intent = getIntent();
        mStart_site = intent.getParcelableExtra(Constant.INTENT_KEY.START_SITE);
        mCity = intent.getStringExtra(Constant.INTENT_KEY.CITY);
        mCityCode = intent.getStringExtra(Constant.INTENT_KEY.CITY_CODE);
        mChosseType = intent.getStringExtra(Constant.INTENT_KEY.CHOSSE_TYPE);
        Log.e("initIntent", "选择方式" + mChosseType);
    }

    private void findID() {
        mListView_site = (ListView) findViewById(R.id.listView_site);
        mSite_head = getLayoutInflater().inflate(R.layout.site_head, null);
        mEditText_where = (TextView) findViewById(R.id.editText_where);
        mTextView_city = (TextView) findViewById(R.id.textView_city);
        mListView_search = (ListView) findViewById(R.id.listView_search);
    }

    private void initUI() {
        mAdapter = new MyAdapter();
        mListView_site.addHeaderView(mSite_head);
        mListView_site.setAdapter(mAdapter);
        mTextView_city.setText(mCity);
        mAdapterSearch = new MySearchAdapter();
        mListView_search.setAdapter(mAdapterSearch);
        fanBianMa();
    }

    private void setListener() {
        mEditText_where.addTextChangedListener(new MyTextWatcher());
        mInputtips = new Inputtips(ChosseSite.this, new Inputtips.InputtipsListener() {
            @Override
            public void onGetInputtips(List<Tip> list, int i) {
                listSearch.addAll(list);
                mAdapterSearch.notifyDataSetChanged();
                for (int j = 0; j < list.size(); j++) {
                    Log.e("onGetInputtips", "提示返回结果" + list.get(j).getName());
                }
            }
        });
        findViewById(R.id.textView_cancle).setOnClickListener(this);
        mListView_site.setOnItemClickListener(new MySiteOnItemClickListener());
        mListView_search.setOnItemClickListener(new MySearchOnItemClickListener());
    }

    class MySiteOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            position = position - 1;
            Intent intent = new Intent();
            if (Constant.INTENT_KEY.START.equals(mChosseType)) {
                intent.putExtra(Constant.INTENT_KEY.START_SITE_BACK, mPois.get(position).getTitle());
                intent.putExtra(Constant.INTENT_KEY.START_LATLNG_BACK, mPois.get(position).getLatLonPoint());
                setResult(Constant.REQUEST_RESULT_CODE.CHOSSE_START_SITE, intent);
            } else if (Constant.INTENT_KEY.END.equals(mChosseType)) {
                intent.putExtra(Constant.INTENT_KEY.END_SITE_BACK, mPois.get(position).getTitle());
                setResult(Constant.REQUEST_RESULT_CODE.CHOSSE_END_SITE, intent);
            }
            finish();
        }
    }

    class MySearchOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent();
            if (Constant.INTENT_KEY.START.equals(mChosseType)) {
                intent.putExtra(Constant.INTENT_KEY.START_SITE_BACK, listSearch.get(position).getName());
                intent.putExtra(Constant.INTENT_KEY.START_LATLNG_BACK, listSearch.get(position).getPoint());
                setResult(Constant.REQUEST_RESULT_CODE.CHOSSE_START_SITE, intent);
            } else if (Constant.INTENT_KEY.END.equals(mChosseType)) {
                intent.putExtra(Constant.INTENT_KEY.END_SITE_BACK, listSearch.get(position).getName());
                setResult(Constant.REQUEST_RESULT_CODE.CHOSSE_END_SITE, intent);
            }
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_cancle:
                finish();
                break;
        }
    }

    class MySearchAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listSearch.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View inflate = getLayoutInflater().inflate(R.layout.site_item, null);
            TextView textView_poi = (TextView) inflate.findViewById(R.id.textView_poi);
            TextView textView_detail = (TextView) inflate.findViewById(R.id.textView_detail);
            textView_poi.setText(listSearch.get(position).getName());
            textView_detail.setText(listSearch.get(position).getDistrict());
            return inflate;
        }
    }

    private void fanBianMa() {
        GeocodeSearch geocodeSearch = new GeocodeSearch(ChosseSite.this);
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
                mPois.clear();
                mPois.addAll(regeocodeAddress.getPois());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });
        //latLonPoint参数表示一个Latlng，第二参数表示范围多少米，GeocodeSearch.AMAP表示是国测局坐标系还是GPS原生坐标系
        LatLonPoint latLonPoint = new LatLonPoint(mStart_site.latitude, mStart_site.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 500, GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(query);
    }

    class MyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.e("onTextChanged", "" + s);
            try {
                listSearch.clear();
                mAdapterSearch.notifyDataSetChanged();
                if (!"".equals(s)) {
                    mInputtips.requestInputtips(s + "", "" + mCityCode);
                }
            } catch (AMapException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mPois.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View inflate = getLayoutInflater().inflate(R.layout.site_item, null);
            TextView textView_poi = (TextView) inflate.findViewById(R.id.textView_poi);
            TextView textView_detail = (TextView) inflate.findViewById(R.id.textView_detail);
            if (mPois.size() > 0) {
                textView_poi.setText(mPois.get(position).getTitle());
                textView_detail.setText(mPois.get(position).getSnippet());
            }
            return inflate;
        }
    }
}
