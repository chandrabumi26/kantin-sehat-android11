package com.kantinsehat.milenial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

public class ScanUser extends AppCompatActivity {
    CodeScanner cdScannerUser;
    CodeScannerView cdScannerViewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_user);
        cdScannerViewUser = findViewById(R.id.scannerUser);
        cdScannerUser = new CodeScanner(this, cdScannerViewUser);
        cdScannerUser.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent nis = new Intent(ScanUser.this, MenuPager.class);
                        nis.putExtra("nis",result.getText().toString());
                        startActivity(nis);
                        finish();
                    }
                });
            }
        });

        cdScannerViewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cdScannerUser.startPreview();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        cdScannerUser.startPreview();
    }

    @Override
    protected void onPause() {
        cdScannerUser.releaseResources();
        super.onPause();
    }
}