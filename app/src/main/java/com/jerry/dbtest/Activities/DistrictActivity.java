package com.jerry.dbtest.Activities;

import java.util.List;

import com.jerry.dbtest.R;
import com.jerry.dbtest.adapter.DiscAdapter;
import com.jerry.dbtest.entity.CityDaoImpl;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class DistrictActivity extends Activity {

    private List<String> mAllDistrict;
    private ListView mListView;
    private DiscAdapter mDiscAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_main);
        initDatas();
        initListView();
    }

    private void initDatas() {
        int sCode = getIntent().getIntExtra(CityMainActivity.INTENT_TAG_SCODE, 0);
        mAllDistrict = new CityDaoImpl(this).queryDistrict(sCode);
    }

    private void initListView() {
        mListView = (ListView) findViewById(R.id.listview);
        mDiscAdapter = new DiscAdapter(mAllDistrict, this);
        mListView.setAdapter(mDiscAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String disc = (String) parent.getAdapter().getItem(position);
                Toast.makeText(getApplicationContext(), disc, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
