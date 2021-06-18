package com.example.testtestaddphoto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MapPhotoActivity_c extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_photo_color);

        ImageView imageView = findViewById(R.id.imageView_color);
        imageView.setImageResource(R.drawable.korea_map_color);

        //mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener()););
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.activity_map_photo_color, container,false);
    }
}
