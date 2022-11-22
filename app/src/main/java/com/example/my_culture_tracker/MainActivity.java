package com.example.my_culture_tracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.my_culture_tracker.Adapter.Adapter;
import com.example.my_culture_tracker.Database.DbHelper;
import com.example.my_culture_tracker.Model.ListItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<ListItem> arrayList;
    Adapter adapter;
    DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DbHelper(this);

        // fetch data from database if it exist

        arrayList = dbHelper.getAllInformations();

        if(arrayList.size()>0){
            // data is available
            adapter = new Adapter(arrayList,this);
            recyclerView.setAdapter(adapter);
        }else {
            Toast.makeText(getApplicationContext(),"Not data found", Toast.LENGTH_LONG).show();
        }

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return 0;
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                ListItem listItem = arrayList.get(position);

                // remove data
                dbHelper.deleteRow(listItem.getId());
                arrayList.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position,arrayList.size());


            }
        }).attachToRecyclerView(recyclerView);


        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setCameraId(0);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentIntegrator.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result!=null){
            if(result.getContents() == null){
                Toast.makeText(getApplicationContext(),"Not result found",Toast.LENGTH_LONG).show();
            }else {
                boolean inserted = dbHelper.insertData(result.getFormatName(),result.getContents());

                if(inserted){
                    arrayList.clear();
                    arrayList = dbHelper.getAllInformations();
                    adapter = new Adapter(arrayList,this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
