package com.akshathakadri.knowyourgovernment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PhotoActivity extends AppCompatActivity {
    private static final String TAG = "PhotoActivity";
    private Official official;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Intent intent = getIntent();
        if(intent.hasExtra("location")){
            TextView loc = findViewById(R.id.plocation);
            loc.setText(intent.getStringExtra("location"));
        }
        if(intent.hasExtra("official")){
            official = (Official) intent.getSerializableExtra("official");

            TextView tv = findViewById(R.id.poffice);
            tv.setText(official.getPosition());

            tv = findViewById(R.id.pname);
            tv.setText(official.getName());

            ConstraintLayout mylayout = findViewById(R.id.playout);
            if(official.getParty().equalsIgnoreCase("Republican")) {
                mylayout.setBackgroundColor(Color.RED);
            } else if(official.getParty().equalsIgnoreCase("Democratic")) {
                mylayout.setBackgroundColor(Color.BLUE);
            }

            loadPicassoImage();
        }
    }

    public void loadPicassoImage() {
        final ImageView imageView = findViewById(R.id.pphoto);
        if (official.getPhotoURL() != null) {
            Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) { // Here we try https if the http image attempt failed
                    final String changedUrl = official.getPhotoURL().replace("http:", "https:");
                    picasso.load(changedUrl)
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(imageView);
                }
            }).build();
            picasso.load(official.getPhotoURL())
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
        } else {
            Picasso.with(this).load(official.getPhotoURL())
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.missingimage)
                    .into(imageView);
        }
    }
}
