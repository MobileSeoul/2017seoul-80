package com.easysubway;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import kr.go.seoul.trafficsubway.Common.BaseActivity;

public class MainActivity extends BaseActivity {

    static  final int GET_STRING = 1;
    TextView text;
    public static Context mContext;

    public MainActivity()
    {
        openAPIKey = "70575677706d696333327152507752";
        subwayLocationAPIKey = "70575677706d696333327152507752";
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        Button button = (Button)findViewById(R.id.Search);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivityForResult(intent, GET_STRING);
            }
        });

        if(getIntent() != null && getIntent().getStringExtra("OpenAPIKey") != null)
            openAPIKey = getIntent().getStringExtra("OpenAPIKey");
        if(getIntent() != null && getIntent().getStringExtra("SubwayLocationAPIKey") != null)
            subwayLocationAPIKey = getIntent().getStringExtra("SubwayLocationAPIKey");
        initView();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == GET_STRING){
            if(resultCode == RESULT_OK){
                text.setText(data.getStringExtra("INPUT_TEXT"));
            }
        }
    }

    private void initView()
    {
        btnBackSubway = (ImageView)findViewById(R.id.btn_back_subway);
        btnBackSubway.setOnClickListener(new android.view.View.OnClickListener()
                                         {

                                             public void onClick(View view)
                                             {
                                                 finish();
                                             }

                                             final MainActivity this$0;

                                             {
                                                 this.this$0 = MainActivity.this;
                                                 // super();
                                             }
                                         }
        );
        lineMapWebview = (WebView)findViewById(R.id.line_map_webview);
        lineMapWebview.setWebViewClient(new WebViewClient());
        lineMapWebview.getSettings().setJavaScriptEnabled(true);
        lineMapWebview.getSettings().setBuiltInZoomControls(true);
        lineMapWebview.getSettings().setSupportZoom(true);
        lineMapWebview.getSettings().setDisplayZoomControls(false);
        lineMapWebview.getSettings().setDefaultTextEncodingName("UTF-8");
        mWebViewInterface = new WebViewInterface(this, lineMapWebview, openAPIKey, subwayLocationAPIKey);
        lineMapWebview.addJavascriptInterface(mWebViewInterface, "Android");
        lineMapWebview.loadUrl("file:///android_asset/mSeoul_Subway.html");
    }

    private String openAPIKey;
    private String subwayLocationAPIKey;
    private ImageView btnBackSubway;
    private WebView lineMapWebview;
    private WebViewInterface mWebViewInterface;
}
