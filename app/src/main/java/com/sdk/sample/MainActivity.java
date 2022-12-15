package com.sdk.sample;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.text1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("resu.io.NOTIFICATION");
                intent.setComponent(new ComponentName(getPackageName(),"io.mob.resu.reandroidsdk.ReNotificationBroadcastReceiver"));
                getApplicationContext().sendBroadcast(intent);
            }
        });


    }


}
