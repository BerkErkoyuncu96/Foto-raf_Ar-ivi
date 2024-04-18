package com.berkerkoyuncu3gmail.com.artbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.berkerkoyuncu3gmail.com.artbook.databinding.ActivityDetailsArtbookBinding;
import com.berkerkoyuncu3gmail.com.artbook.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    artAdapter artAdapter;
    ArrayList <Art> artArrayList;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        artArrayList = new ArrayList<>();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        artAdapter = new artAdapter(artArrayList);
        binding.recyclerView.setAdapter(artAdapter);
        getData();
    }

    private void getData(){
        try {
            SQLiteDatabase database = this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
            Cursor cursor = database.rawQuery("SELECT * FROM arts",null);
            int nameIndex= cursor.getColumnIndex("name");
            int idIndex = cursor.getColumnIndex("id");

            while(cursor.moveToNext()){
                String name = cursor.getString(nameIndex);
                int id = cursor.getInt(idIndex);
                Art art = new Art(name,id);
                artArrayList.add(art);
            }
            artAdapter.notifyDataSetChanged();
            cursor.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.art_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.addArtItem){
            Intent intent = new Intent(this, detailsArtbook.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


}