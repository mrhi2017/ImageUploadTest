package com.mrhi2017.gallerytest;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    ImageView iv;

    String imgPath;
    String uploadUri= "http://mrhi2017.dothome.co.kr/uploadFile1.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv= (ImageView) findViewById(R.id.iv);
    }

    public void clickUpload(View v){
        if(imgPath==null) return;

        //Volley매니져
        RequestQueue requestQueue= Volley.newRequestQueue(this);

        //멀티파트 파일 업로드 요청 객체
        SimpleMultiPartRequest smpr= new SimpleMultiPartRequest(Request.Method.POST, uploadUri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        smpr.addFile("upload", imgPath);
        smpr.addStringParam("title", "this is image title");
        requestQueue.add(smpr);

    }

    public void clickBtn(View v){
        Intent intent;
        intent= new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"my chooser"), 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 10:
                if(resultCode==RESULT_OK){
                    Uri imgUri= data.getData();
                    Glide.with(this).load(imgUri).into(iv);

                    //이미지의 셀제 파일 경로
                    imgPath= imgUri.toString();
                    Log.i("ORG PATH", imgPath);

                    if(imgPath.contains("content://")){
                        Cursor cursor= getContentResolver().query(imgUri, null, null, null, null);
                        if(cursor!=null && cursor.getColumnCount()!=0){
                            cursor.moveToFirst();
                            imgPath= cursor.getString(  cursor.getColumnIndex(MediaStore.Images.Media.DATA) );
                        }
                    }

                    Log.i("PATH", imgPath);

                }
                break;
        }
    }
}
