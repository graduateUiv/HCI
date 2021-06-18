package com.example.testtestaddphoto;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


public class PhotoAdapter extends BaseAdapter {
    private Context mContext;
    private String[] aString;

    public PhotoAdapter(Context c, String[] newString) {
        mContext = c;
        aString = newString;
    }

    @Override
    public int getCount() {
        return aString.length;
    }

    @Override
    public Object getItem(int position) {
        return aString[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
//            imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setPadding(2, 2, 2, 2);

        } else {
            imageView = (ImageView) convertView;
        }
        Log.d("why111", aString[position]);
        Log.d("why222", String.valueOf(position));
        imageView.setImageURI(Uri.parse(aString[position]));

        return imageView;

    }
}
