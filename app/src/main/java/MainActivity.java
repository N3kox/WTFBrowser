package com.example.wtfbrowser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wtfbrowser.page.browser.BrowserActivity;

public class MainActivity extends AppCompatActivity {
    Button tabButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabButton = findViewById(R.id.new_tab_button);
        tabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, BrowserActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
