package com.monark.mysangeet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
RecyclerView lst;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lst=findViewById(R.id.list);
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        ArrayList<File> mysongs=fetchsongs(Environment.getExternalStorageDirectory());
                        String[] items=new String[mysongs.size()];
                        for(int i=0;i<mysongs.size();i++){
                            items[i]=mysongs.get(i).getName().replace("mp3","");
                        }
                        ArrayAdapter<String > adapter=new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,items);
                                lst.setAdapter(adapter);
                                lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Intent intent =new Intent(MainActivity.this,PlaySong.class);
                                        String currentSong=lst.getItemAtPosition(position).toString();
                                        intent.putExtra("songlist",mysongs);
                                        intent.putExtra("currentSong",currentSong);
                                        intent.putExtra("position",position);
                                        startActivity(intent);
                                    }
                                });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                            permissionDeniedResponse.getRequestedPermission();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }
    public ArrayList<File> fetchsongs(File file){
        ArrayList arr=new ArrayList();
        File[] songs=file.listFiles();
        if(songs!=null){
            for(File myFile: songs)
              if(!myFile.isHidden() && myFile.isDirectory()){
                  arr.addAll(fetchsongs(myFile));
              }
            else{
                if(myFile.getName().endsWith((".mp3")) && !myFile.getName().startsWith(".")){
                    arr.add(myFile);
                }
              }
        }
        return arr;
    }
}