package com.example.a612lifeactivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private List<Map<String, String>> contentList;
    private SharedPreferences sharedPref;
    private final static String LARGE_TEXT = "large_text";
    private final String TEXT = "text";
    private final String SIZE = "length";
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<Integer> intPosition = new ArrayList<>();
    private ArrayList<Integer> positionListOld = new ArrayList<>();
    private static final String LOG = "MyLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            positionListOld = savedInstanceState.getIntegerArrayList("Delete points");
            assert positionListOld != null;
            Log.d(LOG, "в onCreate " + positionListOld.toString());
        } catch (NullPointerException e) {
            Log.d(LOG, "нету индексов: в onCreate");
        }


        sharedPref = getSharedPreferences("MyText", MODE_PRIVATE);

        SharedPreferences.Editor myEditor = sharedPref.edit();
        myEditor.putString(LARGE_TEXT, getString(R.string.large_text));
        myEditor.apply();

        initList();

        initToolbar();

        onRefresh();
        try {
            Log.d(LOG, "значения бандла: " + savedInstanceState.toString());
        } catch (NullPointerException e) {
            Log.d(LOG, "значения бандла null");
        }

    }

    private void initList() {

        ListView list = findViewById(R.id.list);

        String[] values = prepareContent();

        final BaseAdapter listContentAdapter = createAdapter(values);

        list.setAdapter(listContentAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                contentList.remove(position);
                Integer integer = position;
                intPosition.add(integer);
                listContentAdapter.notifyDataSetChanged();
            }
        });

    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

    }

    @NonNull
    private SimpleAdapter createAdapter(String[] stringsTxt) {
        contentList = new ArrayList<>(stringsTxt.length);
        prepareAdapterContent(stringsTxt);

        return new SimpleAdapter(this, contentList,
                R.layout.list_with_content,
                new String[]{TEXT, SIZE},
                new int[]{R.id.firstText, R.id.secondText});
    }

    @NonNull
    private void prepareAdapterContent(String[] stringsTxt) {
        Map<String, String> mapForList;
        for (String value : stringsTxt) {
            mapForList = new HashMap<>();
            mapForList.put(TEXT, value);
            mapForList.put(SIZE, Integer.toString(value.length()));
            contentList.add(mapForList);
        }
    }

    @NonNull
    private String[] prepareContent() {

        return sharedPref.getString(LARGE_TEXT, "").split("\n\n");
    }

    public void onRefresh() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                swipeRefreshLayout.setOnRefreshListener(this);
                swipeRefreshLayout.setRefreshing(false);

                positionListOld.clear();

                initList();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (positionListOld != null) {
            Log.d(LOG, "positionListOld в onSaveInstanceState: " + positionListOld.toString());
            try {

                Log.d(LOG, "intPosition в onSaveInstanceState: " + intPosition.toString());
                positionListOld.addAll(intPosition);
                outState.putIntegerArrayList("Delete points", positionListOld);
                Log.d(LOG, "outState.putIntegerArrayList(\"Delete points\", positionListOld); в onSaveInstanceState: " + positionListOld.toString());

            } catch (NullPointerException e) {
                outState.putIntegerArrayList("Delete points", positionListOld);
                Log.d(LOG, "нету индексов в intPosition в onSaveInstanceState " + positionListOld.toString());
            }

        } else {
            outState.putIntegerArrayList("Delete points", intPosition);
            Log.d(LOG, "outState.putIntegerArrayList(\"Delete points\", intPosition); в onSaveInstanceState " + intPosition.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<Integer> positionList = savedInstanceState.getIntegerArrayList("Delete points");


        try {
            assert positionList != null;
            for (Integer i : positionList) {
                contentList.remove(i.intValue());
                Log.d(LOG, "positionList в onRestoreInstanceState: " + positionList.toString());
            }
        } catch (NullPointerException e) {
            Log.d(LOG, "нету индексов");
        }

    }
}
