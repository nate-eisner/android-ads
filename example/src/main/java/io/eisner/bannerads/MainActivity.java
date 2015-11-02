package io.eisner.bannerads;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import io.eisner.ads.AdProvider;
import io.eisner.ads.HeaderFields;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String keyserver = getResources().getString(R.string.server);
        String key = getResources().getString(R.string.key);
        String secret = getResources().getString(R.string.secret);

        HeaderFields keyheaders = new HeaderFields();
        keyheaders.add("X-API-KEY",key);
        keyheaders.add("X-SHHH-ITS-A-SECRET",secret);
        String keyHeaderName = "X-API-KEY";

        View rootView = findViewById(R.id.adContainer);

        final String server = getResources().getString(R.string.xmlserver);

        new AdProvider.Builder(this)
                .imageServer(server) //the location of the xml
                .attachTo(rootView) //root to attach to, the height
                .imageTime(5000) //in ms
                .parseTag("imagefilename") //tag in the xml where the images are wrapped in
                .offlineUse(true) //if there is no connectivity the device will used the local ads
                .useAPIKey(keyserver, keyheaders, keyHeaderName) //setup if an api key is needed
                                                                 // in xml request
                .start();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Button Pressed",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideFab() {
        fab.animate()
                .setDuration(1200)
                .translationY(500)
                .start();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        myAdProvider.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        myAdProvider.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        myAdProvider.restart();
    }
}
