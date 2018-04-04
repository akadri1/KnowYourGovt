package com.akshathakadri.knowyourgovernment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.time.Duration;

public class OfficialActivity extends AppCompatActivity {

    private ImageView officialImageView;
    private String location;
    private Official official;
    public enum CHANNELS {Twitter, Facebook, YouTube, GooglePlus};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        officialImageView = findViewById(R.id.officialImage);

        Intent intent = getIntent();
        if(intent.hasExtra("location")){
            TextView loc = findViewById(R.id.offlocation);
            location = intent.getStringExtra("location");
            loc.setText(location);
        }
        if(intent.hasExtra("official")){
            official = (Official) intent.getSerializableExtra("official");
            ConstraintLayout mylayout = findViewById(R.id.mylayout);

            TextView tv = findViewById(R.id.party);
            if(official.getParty() !=null) {
                if (official.getParty().equalsIgnoreCase("Republican")) {
                    mylayout.setBackgroundColor(Color.RED);
                    tv.setText("("+official.getParty()+")");
                } else if (official.getParty().equalsIgnoreCase("Democratic")
                        || official.getParty().equalsIgnoreCase("Democrat")) {
                    mylayout.setBackgroundColor(Color.BLUE);
                    tv.setText("("+official.getParty()+")");
                } else {
                    tv.setVisibility(View.GONE);
                }
            } else {
                tv.setVisibility(View.GONE);
            }
            tv = findViewById(R.id.office);
            tv.setText(official.getPosition());

            tv = findViewById(R.id.fullname);
            tv.setText(official.getName());

            tv = findViewById(R.id.address);
            tv.setText(official.getAddress());

            tv = findViewById(R.id.address);
            tv.setText(official.getAddress());

            tv = findViewById(R.id.phone);
            tv.setText(official.getPhone());

            tv = findViewById(R.id.email);
            tv.setText(official.getEmail());

            tv = findViewById(R.id.web);
            tv.setText(official.getWebsite());

            loadPicassoImage(official);

            officialImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(official.getPhotoURL()==null || official.getPhotoURL().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Candidates photo is not provided!", Toast.LENGTH_SHORT).show();
                    } else {

                        Intent intent = new Intent(getApplicationContext(), PhotoActivity.class);
                        intent.putExtra("official", official);
                        intent.putExtra("location", location);
                        startActivity(intent);
                    }
                }
            });

            Linkify.addLinks(((TextView) findViewById(R.id.web)), Linkify.WEB_URLS);
            Linkify.addLinks(((TextView) findViewById(R.id.phone)), Linkify.PHONE_NUMBERS);
            Linkify.addLinks(((TextView) findViewById(R.id.address)), Linkify.MAP_ADDRESSES);
            Linkify.addLinks(((TextView) findViewById(R.id.email)), Linkify.EMAIL_ADDRESSES);

            ((TextView) findViewById(R.id.web)).setLinkTextColor(Color.WHITE);
            ((TextView) findViewById(R.id.phone)).setLinkTextColor(Color.WHITE);
            ((TextView) findViewById(R.id.address)).setLinkTextColor(Color.WHITE);
            ((TextView) findViewById(R.id.email)).setLinkTextColor(Color.WHITE);

            findViewById(R.id.facebook).setVisibility(View.INVISIBLE);
            findViewById(R.id.youtube).setVisibility(View.INVISIBLE);
            findViewById(R.id.google).setVisibility(View.INVISIBLE);
            findViewById(R.id.twitter).setVisibility(View.INVISIBLE);
            if(official.getChannels()!=null) {
                if (official.getChannels().containsKey(CHANNELS.Facebook.toString())) {
                    findViewById(R.id.facebook).setVisibility(View.VISIBLE);
                }
                if (official.getChannels().containsKey(CHANNELS.YouTube.toString())) {
                    findViewById(R.id.youtube).setVisibility(View.VISIBLE);
                }
                if (official.getChannels().containsKey(CHANNELS.GooglePlus.toString())) {
                    findViewById(R.id.google).setVisibility(View.VISIBLE);
                }
                if (official.getChannels().containsKey(CHANNELS.Twitter.toString())) {
                    findViewById(R.id.twitter).setVisibility(View.VISIBLE);
                }
            }
        }

    }

    public void loadPicassoImage(final Official official) {

        if (official.getPhotoURL() != null) {
            Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) { // Here we try https if the http image attempt failed
                    final String changedUrl = official.getPhotoURL().replace("http:", "https:");
                    picasso.load(changedUrl)
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(officialImageView);
                }
            }).build();
            picasso.load(official.getPhotoURL())
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(officialImageView);
        } else {
            Picasso.with(this).load(official.getPhotoURL())
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.missingimage)
                    .into(officialImageView);
        }
    }
    public void twitterClicked(View v) {
        Intent intent = null;
        String name = official.getChannels().get(CHANNELS.Twitter.toString());
        String twitterAppUrl = "twitter://user?screen_name=" + name;
        String twitterWebUrl = "https://twitter.com/" + name;
        try {
            // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterAppUrl));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterWebUrl));
        }
        startActivity(intent);
    }

    public void facebookClicked(View v) {
        String FACEBOOK_URL = "https://www.facebook.com/" + official.getChannels().get(CHANNELS.Facebook.toString());
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + official.getChannels().get("Facebook");
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url }
            Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
            facebookIntent.setData(Uri.parse(urlToUse));
            startActivity(facebookIntent);
        }
    }

    public void googlePlusClicked(View v) {
        String name = official.getChannels().get(CHANNELS.GooglePlus.toString());
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", name);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/" + name)));
        }
    }
    public void youTubeClicked(View v) {
        String name = official.getChannels().get(CHANNELS.YouTube.toString());
        Intent intent = null; try {
        intent = new Intent(Intent.ACTION_VIEW); intent.setPackage("com.google.android.youtube");
        intent.setData(Uri.parse("https://www.youtube.com/" + name)); startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
        }
    }
}
