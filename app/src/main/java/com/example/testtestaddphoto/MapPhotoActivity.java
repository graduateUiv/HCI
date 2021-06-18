package com.example.testtestaddphoto;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MapPhotoActivity extends AppCompatActivity {
//    private ScaleGestureDetector mScaleGestureDetector;
//    private float mScaleFactor = 1.0f;
//    private ImageView mImageView;

//    @Override
//    public boolean onTouchEvent(MotionEvent me){
//        //변수로 선언해 놓은 ScaleGestureDetector
//        mScaleGestureDetector.onTouchEvent(me);
//        return true;
//    }
//
//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
//
//        public boolean OnScale(ScaleGestureDetector sd){
//            //ScaleGestureDetector에서 factor를 받아 변수로 선언한 factor에 넣고
//            mScaleFactor *= sd.getScaleFactor();
//
//            //최대 10배, 최소 10배 줌 한계 설정
//            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
//
//            //이미지뷰 스케일에 적용
//            mImageView.setScaleX(mScaleFactor);
//            mImageView.setScaleY(mScaleFactor);
//            return true;
//        }
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_photo);

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.korea_map_origin);

        //mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener()););
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.activity_map_photo, container,false);
    }

}