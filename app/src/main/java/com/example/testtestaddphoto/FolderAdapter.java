package com.example.testtestaddphoto;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.util.ArrayList;

public class FolderAdapter extends BaseAdapter {
    ArrayList<Folder> items = new ArrayList<>();

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(Folder folder) {
        this.items.add(folder);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FolderView folderView;
        if(convertView==null){
            folderView = new FolderView(parent.getContext());
        }
        else{
            folderView = (FolderView)convertView;
        }
        Folder folder = items.get(position);
        folderView.setFolderName(folder.getFolderName());
        folderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), position+"번째 선택", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(), ShowPhotoListActivity.class);
                intent.putExtra("폴더", position);
                v.getContext().startActivity(intent);
            }
        });
        return folderView;
    }


}
