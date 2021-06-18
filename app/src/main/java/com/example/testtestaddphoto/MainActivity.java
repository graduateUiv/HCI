package com.example.testtestaddphoto;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.media.ExifInterface;


import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

//import uk.co.senab.photoview.PhotoViewAttacher;



public class MainActivity extends AppCompatActivity {
    private Boolean isPermission = true;
    private static final int PICK_FROM_ALBUM = 1;
    private String photoDate;
    private String projectFolderName = "/HCI_TravelPhoto/";
    showLocationFolderFragment fragment_showFolder = new showLocationFolderFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tedPermission();


        findViewById(R.id.btnGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 권한 허용에 동의하지 않았을 경우 토스트를 띄웁니다.
                if (isPermission) goToAlbum();
                else
                    Toast.makeText(view.getContext(), getResources().getString(R.string.permission_2), Toast.LENGTH_LONG).show();
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment_showFolder); // fragment삽입할 위치, fragment

        Button mapButton = (Button) findViewById(R.id.btnMap);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), MapPhotoActivity.class);
                startActivity(intent);
            }
        });
        mapButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(getApplication(), MapPhotoActivity_c.class);
                startActivity(intent);
                return false;
            }
        });

        
        //상단 툴바 관련 코드
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //액션바를 툴바로 바꿔줌
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false); //기본 제목 없애기
        actionBar.setDisplayHomeAsUpEnabled(true); //뒤로가기 버튼 생성

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.toolbar_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //상단 툴바의 옵션 메뉴들
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.edit:
                break;
            case R.id.setting:
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String imagePath = null;
        String photoLocation = null;


        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (requestCode == PICK_FROM_ALBUM) {
            if (data == null) { //데이터가 없을때 생기는 오류 방지
            } else {
                if (data.getClipData() == null) {
                    Toast.makeText(this, "다중선택이 불가한 기기입니다.", Toast.LENGTH_LONG).show();
                } else { //사진 선택
                    ClipData clipData = data.getClipData();
                    Log.i("clipData", String.valueOf(clipData.getItemCount())); // 사용자가 몇개 골랐는지

                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        imagePath = getRealPathFromURI(clipData.getItemAt(i).getUri());

                        if(imagePath == null){
                            Toast.makeText(this, "잘못된 접근입니다.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        else{
                            File photoFile = new File(imagePath);

                            try {

                                checkDate(photoFile);
                                createDirectory();
                                photoLocation = checkLocation(photoFile); //위치 받아오기
                                saveInLocation(photoFile, photoLocation);
                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.detach(fragment_showFolder).attach(fragment_showFolder).commit();


                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }
        }
    }

    // 사진 URI 얻어오는 함수 : 2016년부터 ? 변경
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    // 앨범에서 사진 선택
    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK); //앨범 호출
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE); //이미지 파일
        intent.putExtra(intent.EXTRA_ALLOW_MULTIPLE, true); // 다중 선택 가능
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    // 저장폴더 생성
    private void createDirectory() throws IOException {
        File storageDir = new File(Environment.getExternalStorageDirectory() + projectFolderName);
        if (!storageDir.exists())
            storageDir.mkdirs(); // 폴더가 존재하지 않는다면 생성
    }

    // 파일 복사
    private static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    // 권한 설정
    private void tedPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
                isPermission = true;

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
                isPermission = false;

            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }

    private String checkLocation(File photoFile) {
        String photoLocation = null;

        String fileName = photoFile.getPath();
        Log.d("fileName", fileName);
        try {
            ExifInterface exif = new ExifInterface(fileName);
            photoLocation = showExif(exif);
//            saveInLocation(photoFile);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "위치가져오기오류!", Toast.LENGTH_LONG).show();
        }
        return photoLocation;
    }

    private String showExif(ExifInterface exif) throws IOException {

        String photoLocation = null;

        Geocoder geocoder = new Geocoder(this);
        List<Address> list = null;
        String latitude = getTagString(ExifInterface.TAG_GPS_LATITUDE, exif); // 위도
        latitude = latitude.substring(14); // GPSLatitude : null -> null
        String longitude = getTagString(ExifInterface.TAG_GPS_LONGITUDE, exif); // 경도
        longitude = longitude.substring(15); // GPSLongitude : null -> null
        Log.d("latitude", latitude);
        Log.d("longitude", longitude);
        if (latitude.contains("null") || longitude.contains("null")) {
            photoLocation = "noLocation";
        } else {
            double d1 = changeGPS(latitude);
            double d2 = changeGPS(longitude);
            try {
                list = geocoder.getFromLocation(d1, d2, 10);
            } catch (IOException e) {
                e.printStackTrace();
                list = geocoder.getFromLocation(d1, d2, 10);
                Toast.makeText(this, "위치변환오류!", Toast.LENGTH_LONG).show();
            }

            if (list != null) {
                if (list.size() == 0) {
                    Toast.makeText(this, "해당주소없음!", Toast.LENGTH_LONG).show();
                } else {
                    photoLocation = list.get(0).getLocality();
                    if (photoLocation == null) {
                        photoLocation = list.get(0).getAdminArea();
                        if (photoLocation == null) {
                            photoLocation = "noLocation";
                        }
                    }
                    Log.d("listAdminArea", list.get(0).getAdminArea());
                    Log.d("list", list.get(0).toString());
                    Log.d("listA", list.get(0).getAddressLine(0));
                    Toast.makeText(this, "주소있음!", Toast.LENGTH_LONG).show();
                }
            }
        }
        return photoLocation;
    }

    private double changeGPS(String gpsInfo) {
        String splitGPSInfo[] = gpsInfo.split(",");
        Log.d("splitGPSInfo0", splitGPSInfo[0]);
        Log.d("splitGPSInfo1", splitGPSInfo[1]);
        Log.d("splitGPSInfo2", splitGPSInfo[2]);
        Double changeGPSInfo[] = new Double[3];
        for (int i = 0; i < splitGPSInfo.length; i++) {
            int idx = splitGPSInfo[i].indexOf("/");
            double num1 = Double.parseDouble(splitGPSInfo[i].substring(0, idx));
            double num2 = Double.parseDouble(splitGPSInfo[i].substring(idx + 1));
            changeGPSInfo[i] = num1 / num2;
        }
        double d1 = changeGPSInfo[0];
        double d2 = changeGPSInfo[1];
        double d3 = changeGPSInfo[2];
        double changedGPSInfo = d1 + d2 / 60 + d3 / 3600;
        return changedGPSInfo;
    }

    private String getTagString(String tag, ExifInterface exif) {
        return (tag + " : " + exif.getAttribute(tag) + "\n");
    }

    private void saveInLocation(File photoFile, String photoLocation) throws IOException {
        File storageDir = new File(Environment.getExternalStorageDirectory() + projectFolderName + photoLocation + "/");
        if (!storageDir.exists()) storageDir.mkdirs(); // 폴더가 존재하지 않는다면 생성
        File savedImageFile = new File(storageDir, photoFile.getName());

        Log.d("photoFile", photoFile.getName());
        Log.d("storage", storageDir.getAbsolutePath());
        Log.d("savedImageFile", savedImageFile.getPath());
        copy(photoFile, savedImageFile);
    }

    private void checkDate(File photoFile) throws IOException {
        String fileName = photoFile.getPath();
        ExifInterface exif = new ExifInterface(fileName);
        photoDate = getTagString(ExifInterface.TAG_DATETIME, exif);
        Log.d("photoDate", photoDate);
    }


}