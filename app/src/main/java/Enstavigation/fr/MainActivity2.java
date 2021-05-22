package Enstavigation.fr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class MainActivity2 extends AppCompatActivity {


    SwitchCompat simpleSwitch1, simpleSwitch2, simpleSwitch3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Button search_button = (Button) findViewById(R.id.search_button);
        Button buttonT = (Button) findViewById(R.id.buttonT);
        Button buttonV = (Button) findViewById(R.id.buttonV);
        search_button.setOnClickListener(v -> {
            Intent myIntent= new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(myIntent);
            finish();
        });

        buttonT.setOnClickListener(v -> {
            Intent myIntent= new Intent(getApplicationContext(), MapsActivity1.class);
            startActivity(myIntent);
            finish();
        });

        buttonV.setOnClickListener(v -> {
            Intent myIntent= new Intent(getApplicationContext(), MapsActivity2.class);
            startActivity(myIntent);
            finish();
        });



            Button buttonr = (Button) findViewById(R.id.buttonr);
            buttonr.setOnClickListener(v -> {
            Intent otherActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(otherActivity);
            finish();
        });
    }
}
