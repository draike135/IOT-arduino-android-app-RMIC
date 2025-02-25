package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import android.widget.SeekBar;

import me.tankery.lib.circularseekbar.CircularSeekBar;

public class MainActivity extends AppCompatActivity {
    SeekBar slider;
    TextView A_hum, Status,Air_tmp,soil_hum,pump_th,c_v;
    CircularSeekBar circular_slider;
    FirebaseDatabase fdatabase;
    DatabaseReference Dref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        A_hum= findViewById(R.id.Air_hum);
        Status= findViewById(R.id.Status);
        Air_tmp= findViewById(R.id.Air_tmp);
        soil_hum= findViewById(R.id.soil_hum);
        slider= findViewById(R.id.slider);
        pump_th=findViewById(R.id.pump_the);
        circular_slider=findViewById(R.id.circular_slider);
        c_v=findViewById(R.id.cir);
        fdatabase=FirebaseDatabase.getInstance();
        Dref=fdatabase.getReference();


        Dref.child("A_hum").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double value = snapshot.getValue(Double.class);
                if (value != null) {
                    String A_hum_text = String.valueOf(value); // Corrected conversion to String
                    // Update TextView with the retrieved value
                    if (A_hum != null) {
                        A_hum.setText("Air Humidity: " + A_hum_text);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        Dref.child("S_hum").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double value = snapshot.getValue(Double.class);
                if (value != null) {
                    double per=value*0.1;
                    String formattedPer = String.format("%.3f", per);
                    String S_hum_text = String.valueOf(per); // Corrected conversion to String
                    // Update TextView with the retrieved value
                    if (soil_hum != null) {
                        soil_hum.setText("Soil Humidity: " + per +"%");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        Dref.child("temp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double value = snapshot.getValue(Double.class);
                if (value != null) {
                    String Temp_text = String.valueOf(value); // Corrected conversion to String
                    // Update TextView with the retrieved value
                    if (Air_tmp != null) {
                        Air_tmp.setText("Air Temp: " + Temp_text);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        Dref.child("Pump").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean value = snapshot.getValue(Boolean.class);
                if (value != null) {
                    String pump_text = value ? "ON" : "OFF"; // Corrected conversion to String
                    // Update TextView with the retrieved value
                    if (Status != null) {
                        Status.setText(pump_text);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Read the current progress value from the slider
                // 'progress' represents the current value of the slider
                // Do whatever you need to do with this value
                double doubleValue = progress *0.1;
                pump_th.setText("Pump humidity treshhold: " + doubleValue+"%");
                // Send the value to Firebase
                Dref.child("th").setValue(doubleValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Invoked when the user starts touching the seek bar
                // No need to set a fixed value here
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Invoked when the user stops touching the seek bar
                // No need to set a fixed value here
            }
        });
        circular_slider.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                // 'progress' represents the current progress of the CircularSeekBar
                // Now you can use this 'progress' value as needed, such as sending it to Firebase
                double s_value=Math.round(progress);
                Dref.child("motor").setValue((double) s_value);

                String Temp_text = String.valueOf(s_value);
                c_v.setText(Temp_text);
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar circularSeekBar) {
                // This method is called when the user stops touching the CircularSeekBar
                // You can perform any necessary actions here, if needed
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar circularSeekBar) {
                // This method is called when the user starts touching the CircularSeekBar
                // You can perform any necessary actions here, if needed
            }
        });

    }
}