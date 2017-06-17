package com.jerry.dbtest.Activities;

import java.util.List;

import com.jerry.dbtest.R;
import com.jerry.dbtest.adapter.ProvinceAdapter;
import com.jerry.dbtest.entity.CityDaoImpl;
import com.jerry.dbtest.entity.Province;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class CityMainActivity extends AppCompatActivity {

    public static final String INTENT_TAG_SCODE = "scode";
    private CityDaoImpl mCityDaoImpl;
    private List<Province> mAllProvinces;
    private ListView mListView;
    private ProvinceAdapter mProvinceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_main);
        initDatas();
        initListView();
    }

    private void initDatas() {
        mCityDaoImpl = new CityDaoImpl(this);
        mAllProvinces = mCityDaoImpl.queryProvince();
    }

    private void initListView() {
        mListView = (ListView) findViewById(R.id.listview);
        mProvinceAdapter = new ProvinceAdapter(mAllProvinces, this);
        mListView.setAdapter(mProvinceAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Province pro = (Province) parent.getAdapter().getItem(position);
                int sCode = pro.getsCode();
                Intent intent = new Intent(CityMainActivity.this, CityActivity.class);
                intent.putExtra(INTENT_TAG_SCODE, sCode);
                startActivity(intent);
            }
        });
    }
}
