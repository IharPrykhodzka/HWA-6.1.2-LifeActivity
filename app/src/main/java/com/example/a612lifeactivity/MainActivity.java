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
    private ArrayList<Integer> intPosition;
    private static final String LOG = "MyLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = getSharedPreferences("MyText", MODE_PRIVATE);

        SharedPreferences.Editor myEditor = sharedPref.edit();
        myEditor.putString(LARGE_TEXT, getString(R.string.large_text));
        myEditor.apply();

        initList();

        initToolbar();

        onRefresh();

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
                intPosition = new ArrayList<>();
                intPosition.add(position);
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

                intPosition.clear();

                initList();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList("Удалённые позиции", intPosition);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<Integer> positionList = savedInstanceState.getIntegerArrayList("Удалённые позиции");
        try {
            for (int i : positionList){
                contentList.remove(i);
            }
        }catch (NullPointerException e) {
            Log.d(LOG, "нету индексов");
        }

    }
}
