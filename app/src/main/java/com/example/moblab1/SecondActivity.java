package com.example.moblab1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // Add any additional code or functionality specific to SecondActivity here

        // Find the "Back" button by its ID
        Button backButton = findViewById(R.id.backButton);

        // Set an OnClickListener to navigate back to the previous activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity (SecondActivity) to go back to MainActivity
                finish();
            }
        });
    }
}
