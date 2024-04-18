package com.berkerkoyuncu3gmail.com.artbook;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.Image;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.berkerkoyuncu3gmail.com.artbook.databinding.ActivityDetailsArtbookBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class detailsArtbook extends AppCompatActivity {

    SQLiteDatabase database;
    Bitmap selectedImage;
    ActivityResultLauncher <Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLuncher;
    private ActivityDetailsArtbookBinding binding ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsArtbookBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        database = openOrCreateDatabase("Arts",MODE_PRIVATE,null);
        registerLauncher();
        Intent intent = getIntent();
        String info = intent.getStringExtra("info");


        if (info.equals("new")){
            binding.button.setVisibility(view.VISIBLE);
        }
        else{
            int artId = intent.getIntExtra("artId",1);
            binding.button.setVisibility(view.INVISIBLE);
            try {
                Cursor cursor = database.rawQuery("SELECT * FROM arts WHERE id = ?",new String[] {String.valueOf(artId)});
                int artNameIndex= cursor.getColumnIndex("name");
                int artistNameIndex = cursor.getColumnIndex("artist");
                int yearIndex = cursor.getColumnIndex("year");
                int imageIndex= cursor.getColumnIndex("image");


                while(cursor.moveToNext()){
                    binding.nameText.setText(cursor.getString(artNameIndex));
                    binding.artistText.setText(cursor.getString(artistNameIndex));
                    binding.yearText.setText(cursor.getString(yearIndex));

                    byte[] bytes = cursor.getBlob(imageIndex);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
                    binding.imageView.setImageBitmap(bitmap);
                }
                cursor.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void save(View view ){
        String name = binding.nameText.getText().toString();
        String artist = binding.artistText.getText().toString();
        String year = binding.yearText.getText().toString();

        Bitmap smallimage = makeSmallerImage(selectedImage, 300);


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallimage.compress(Bitmap.CompressFormat.PNG,75,outputStream);
        byte[] byteArray = outputStream.toByteArray();


        //DATABASE
        try {
            database.execSQL("CREATE TABLE IF NOT EXISTS  arts (id INTEGER PRİMARY KEY , name VARCHAR , artist VARCHAR ,year VARCHAR , image BLOB)" );

           String sqlString = "INSERT INTO arts (name , artist , year ,image) values (? ,? ,?, ?)";
           SQLiteStatement statement = database.compileStatement(sqlString);
           statement.bindString(1,name);
           statement.bindString(2,artist);
           statement.bindString(3,year);
           statement.bindBlob(4,byteArray);
           statement.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
        Intent intent = new Intent(detailsArtbook.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public Bitmap makeSmallerImage(Bitmap bitmap, int maxSize){

        int width = bitmap.getWidth();
        int height =bitmap.getHeight();

        float bitmapRatio = (float)  width / (float)  height;
         if(bitmapRatio>1){
             //Landscape
             width = maxSize;
             height = (int) (width/bitmapRatio);
         }
         else{
             height = maxSize;
             width = (int) (height * bitmapRatio);
         }

        return bitmap.createScaledBitmap(bitmap,width,height,true) ;
    }

    public  void imageView(View view){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
           // SDK version >= 33

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)!= PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"Permision needed for your gallery.",Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //request permission
                            permissionLuncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                }
                else{
                    // request permission
                    permissionLuncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }
            }
            else{
                // Gallery
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }

        }
        else {
            // SDK version <= 32

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Permission needed for your gallery.",Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //request permission
                            permissionLuncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();
                }
                else{
                    // request permission
                    permissionLuncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }
            else{
                // Gallery
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }

        }
    }


    private void registerLauncher(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
              if(result.getResultCode()==RESULT_OK){
                  Intent intentFromResault = result.getData();
                  if (intentFromResault != null){
                      Uri imageData = intentFromResault.getData();
                      //binding.imageView.setImageURI(imageData); -> Artık kullanılmıyor.
                      try {
                          if (Build.VERSION.SDK_INT>=28){
                              ImageDecoder.Source source = ImageDecoder.createSource(detailsArtbook.this.getContentResolver(),imageData);
                              selectedImage = ImageDecoder.decodeBitmap(source);
                              binding.imageView.setImageBitmap(selectedImage);
                          }
                          else{
                              selectedImage =MediaStore.Images.Media.getBitmap(detailsArtbook.this.getContentResolver(),imageData);
                              binding.imageView.setImageBitmap(selectedImage);
                          }
                      }catch (Exception e){
                          e.printStackTrace();
                      }
                  }
              }
            }
        });
        permissionLuncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    // Permission granted
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }
                else{
                    // Permission denied
                    Toast.makeText(detailsArtbook.this, "Permission needed! ", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}