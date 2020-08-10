package com.parse.starter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class userListActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        setTitle("Feed");

        listView=findViewById(R.id.listView);
        final ArrayList<String> usersList=new ArrayList<>();
        final ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,usersList);

        ParseQuery<ParseUser> users=ParseUser.getQuery();
        users.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername());
        users.orderByAscending("username");

        users.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e==null && objects.size()>0) {
                    for (ParseUser object:objects) {
                        usersList.add(object.getUsername());
                    }
                    listView.setAdapter(adapter);
                } else {
                    e.printStackTrace();
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(getApplicationContext(),userFeedActivity.class);
                intent.putExtra("username",usersList.get(i));
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.share) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            } else {
                getPhoto();
            }

        } else if(item.getItemId()==R.id.logout) {
            ParseUser.logOut();

            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void getPhoto() {
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImage=data.getData();

        if (requestCode==1 && resultCode==RESULT_OK && data!=null) {
            try{
                Bitmap bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                ByteArrayOutputStream stream=new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
                byte[] byteArray=stream.toByteArray();

                ParseFile file=new ParseFile("image.png",byteArray);
                ParseObject object=new ParseObject("Image");

                object.put("image",file);
                object.put("username",ParseUser.getCurrentUser().getUsername());

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e==null) {
                            Toast.makeText(userListActivity.this, "Photo Uploaded!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(userListActivity.this, "An error occurred! Try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==1) {
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
    }
}