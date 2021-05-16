package Enstavigation.fr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView welcome_button = (TextView) findViewById(R.id.welcome_button);

        welcome_button.setOnClickListener(v -> {
            Intent otherActivity = new Intent(getApplicationContext(), MainActivity2.class);
            startActivity(otherActivity);
            finish();
        });
    }
}

