package com.example.linkviewer;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private EditText edtUrl;
    private EditText edtFilter;
    private ListView listRates;
    private Button btnLoad;
    private Button btnRefresh;

    private ArrayAdapter<String> adapter;
    private List<String> allRates = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtUrl = findViewById(R.id.edtUrl);
        edtFilter = findViewById(R.id.edtFilter);
        listRates = findViewById(R.id.listRates);
        btnLoad = findViewById(R.id.btnLoad);
        btnRefresh = findViewById(R.id.btnRefresh);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allRates);
        listRates.setAdapter(adapter);

        edtFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i(TAG, "filter.beforeTextChanged()");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG, "filter.onTextChanged() -> '" + s + "'");
                filterRates(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(TAG, "filter.afterTextChanged()");
            }
        });
        btnLoad.setOnClickListener(new View.OnClickListener() {  //load btn
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btnLoad.onClick() called");
                loadRatesFromInput();
            }
        });
        btnRefresh.setOnClickListener(new View.OnClickListener() {  //refresh btn
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btnRefresh.onClick() called");
                loadRatesFromInput();
            }
        });
        loadRatesFromInput();  //pradinis
    }
    private void loadRatesFromInput() {
        Log.i(TAG, "loadRatesFromInput() called");
        String url = edtUrl.getText().toString().trim();
        if (url.isEmpty()) {
            Toast.makeText(this, "Ivesk tinkama URL", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "loadRatesFromInput(): URL empty");
            return;
        }

        try {
            new DataLoader(new DataLoader.ResultCallback() {
                @Override
                public void onSuccess(String rawData) {

                    try {
                        List<String> parsed = Parser.parse(rawData);
                        adapter.clear();
                        adapter.addAll(parsed);
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e("Parser", "Parse stringa", e);
                    }
                }
                @Override
                public void onError(Exception e) {
                    Log.e("MainActivity", "Load stringa", e);
                }
            }).execute(url);


        } catch (Exception e) {
            Log.e(TAG, "loadRatesFromInput() catch", e);
            Toast.makeText(this, "Klaida: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private void filterRates(String filter) {
        Log.i(TAG, "filterRates() called with filter='" + filter + "'");

        List<String> filtered = new ArrayList<>();

        if (filter == null || filter.isEmpty()) {
            filtered.addAll(allRates);
        } else {
            String f = filter.toLowerCase();
            for (String r : allRates) {
                if (r.toLowerCase().contains(f)) {
                    filtered.add(r);
                }
            }
        }

        adapter.clear();
        adapter.addAll(filtered);
        adapter.notifyDataSetChanged();

        Log.i(TAG, "filterRates() applied, adapter count=" + adapter.getCount());
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy() called");
        super.onDestroy();
    }
}
