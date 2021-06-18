package com.example.testtestaddphoto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;

public class ShowPhotoListActivity extends AppCompatActivity {
    private String projectFolderName = "/HCI_TravelPhoto/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo_list);

        File storageDir = new File(Environment.getExternalStorageDirectory() + projectFolderName);
        if (!storageDir.exists()) storageDir.mkdirs();
        File[] locationFoldersNames = storageDir.listFiles();

        Intent intent = getIntent();
        int order = intent.getExtras().getInt("폴더");

        File locationPhotoDir = new File(Environment.getExternalStorageDirectory() + projectFolderName + locationFoldersNames[order].getName());
        File[] photoSets = locationPhotoDir.listFiles();
        String[] photoSetsPath = new String[photoSets.length];
        GridView gridView = (GridView) findViewById((R.id.photoGrid));
        for (int i = 0; i < photoSets.length; i++) {
            photoSetsPath[i] = photoSets[i].getAbsolutePath();
            Log.d("why", photoSetsPath[i]);
        }
        gridView.setAdapter(new PhotoAdapter(this, photoSetsPath));

    }

}