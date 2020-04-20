package com.hama.crdl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.view.KeyEvent;

public class BrowserActivity extends AppCompatActivity {
    TextView URL;
    WebView Browser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        Browser =(WebView) findViewById(R.id.Browser_Browser);
        Browser.setWebViewClient(new WebViewClient() {
            // do your stuff here
            @Override
            public void onPageFinished(WebView view, String url)
            {
                //Browser.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                // Toast.makeText(MainActivity.this, "Loaded!", Toast.LENGTH_SHORT).show();

                //Toast.makeText(MainActivity.this,HTMLString , Toast.LENGTH_LONG).show();

            }
        });
        URL = (TextView) findViewById(R.id.urlTextBox) ;
        URL.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    Browser.loadUrl(URL.getText().toString().trim());
                    //Toast.makeText(HelloFormStuff.this, edittext.getText(), Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

    }
}
