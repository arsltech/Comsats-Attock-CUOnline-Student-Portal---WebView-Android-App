package com.developer.arsltech.cuonlineportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    WebView webView;
    TextView txtConnection;
    ProgressDialog progressDialog;
    Button btnConnection;
    ImageView imageViewNoC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_main);

        imageViewNoC = (ImageView) findViewById(R.id.imagViewNoInternet);
        txtConnection = (TextView) findViewById(R.id.textC);
        webView = (WebView) findViewById(R.id.webView);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        btnConnection = (Button) findViewById(R.id.btnReferesh);
        checkInternet();



        //final Activity MyActivity = this;
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                getSupportActionBar().setTitle(title);
            }

            public void onProgressChanged(WebView view, int progress)
            {
                // MyActivity.setTitle("Loading...");
                progressDialog.show();
                //MyActivity.setProgress(progress * 100);

                if(progress == 100) progressDialog.dismiss();
                //MyActivity.setTitle(R.string.app_name);




            }
        });

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.d("Failure Url :" , failingUrl);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Log.d("Ssl Error:",handler.toString() + "error:" +  error);
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDomStorageEnabled(true);


        webView.setDownloadListener(new DownloadListener()
        {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });


        btnConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInternet();
            }
        });



    }
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to Exit?")
                    .setNegativeButton("No",null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MainActivity.this.onBackPressed();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                finishAffinity();
                            }
                        }
                    }).show();
        }

    }
    public void checkInternet(){

        ConnectivityManager connMgr = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi =
                connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile =
                connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if( wifi.isAvailable() )
        {
            webView.loadUrl("https://cuonline.ciit-attock.edu.pk:8089/");
            webView.setVisibility(View.VISIBLE);
            txtConnection.setVisibility(View.GONE);
            imageViewNoC.setVisibility(View.GONE);
            btnConnection.setVisibility(View.INVISIBLE);
        }
        else if( mobile.isAvailable() )
        {
            webView.loadUrl("https://cuonline.ciit-attock.edu.pk:8089/");
            webView.setVisibility(View.VISIBLE);
            txtConnection.setVisibility(View.GONE);
            btnConnection.setVisibility(View.INVISIBLE);
            imageViewNoC.setVisibility(View.GONE);
        }
        else
        {
            webView.setVisibility(View.GONE);
            txtConnection.setVisibility(View.VISIBLE);
            btnConnection.setVisibility(View.VISIBLE);
            imageViewNoC.setVisibility(View.VISIBLE);
            Toast.makeText(this, "No Network " , Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menucustom,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.menuRefresh){
            checkInternet();
        }
        if(item.getItemId() == R.id.menuInformation){
            startActivity(new Intent(getApplicationContext(),AboutActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
