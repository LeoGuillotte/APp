package Enstavigation.fr;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Button search_button = (Button) findViewById(R.id.search_button);

        search_button.setOnClickListener(v -> {
            Intent otherActivity = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(otherActivity);
            finish();
        });
    }
}