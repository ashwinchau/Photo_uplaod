package com.calamity_assist.photo_uplaod;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static android.R.attr.bitmap;

public class MainActivity extends ActionBarActivity {

    public static final String UPLOAD_URL="http://192.168.2.100/websservice/photo.php";
    public static final String UPLOAD_KEY = "myfile";
    public static final int Requset_capture=1;
    ImageView im;
     private Bitmap bitmap;
    ProgressDialog progressDialog;

    private Uri filePath;
    private String encode_string,image_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Typeface font=Typeface.createFromAsset(getAssets(),"fontawesome-webfont.ttf");
        Button click=(Button)findViewById(R.id.button2);
        Button select=(Button)findViewById(R.id.button3);
        Button upload=(Button)findViewById(R.id.button4);
        im=(ImageView)findViewById(R.id.imageView);

  //      click.setTypeface(font);

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFileChooser();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });

    }

    public void upload()
    {

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Uploading, please wait...");
        progressDialog.show();


        //converting image to base64 string
        im.setImageBitmap(bitmap);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);


      //  Toast.makeText(this, imageString, Toast.LENGTH_SHORT).show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, UPLOAD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                if(response.equals("true")){
                    Toast.makeText(MainActivity.this, "Uploaded Successful", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();

                }
               // Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_LONG).show();

                    }
                }){
            @Override
            protected Map<String,String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("imageString", imageString);

                return params;
            }
        };

        RequestQueue rQueue = Volley.newRequestQueue(MainActivity.this);
        rQueue.add(stringRequest);
    }
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

    }

    private void openCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);


        filePath = data.getData();
        //Toast.makeText(this, path , Toast.LENGTH_SHORT).show();
        if(requestCode == 0 ) {
                Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                im.setImageBitmap(bp);
                Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        }
        else if(requestCode == 1 )

            {try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                im.setImageBitmap(bitmap);
                Toast.makeText(this, bitmap.toString(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(this,"error hello", Toast.LENGTH_SHORT).show();

            System.out.print("not image");
        }
    }

}

