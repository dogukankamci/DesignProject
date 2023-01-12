package com.dogukan.hizolcer;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TrackSpeed extends AppCompatActivity {
    TextView twspeed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_speed);
        getWindow(). addFlags (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        twspeed = (TextView) findViewById(R.id.textView4);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://designproject-adec5-default-rtdb.firebaseio.com");
        DatabaseReference myRef = database.getReference("Anlık Hız");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int value = dataSnapshot.getValue(int.class);
                Log.i("Hız","Hedef kişinin anlık hızı: " +value);
                twspeed.setText("Anlık Hız = " +value + " km/h");
                if(value>80 && value<130)
                {
                    twspeed.setTextColor(Color.rgb(238, 210, 2));

                }
                else if (value<80)
                {
                    twspeed.setTextColor(Color.rgb(255, 255, 255));
                }
                else if (value>=130)
                {
                    Toast.makeText(TrackSpeed.this, "Aşırı Hız Uyarısı!", Toast.LENGTH_SHORT).show();
                    MediaPlayer mp = MediaPlayer.create(TrackSpeed.this,R.raw.warning);
                    mp.start();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.i("Hız", "Bulut sisteminden hız verisi okunamadı." +
                        "Bir sorun oluştu.", error.toException());
            }
        });

    }
}