package com.staceybellerose.simplewidgets;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Display the About activity
 */
public class AboutActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.about_header);
        setContentView(R.layout.activity_about);
        findViewById(R.id.button1).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View view) {
                finish();
            }
        });
    }

}
