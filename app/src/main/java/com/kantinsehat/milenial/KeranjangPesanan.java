package com.kantinsehat.milenial;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KeranjangPesanan extends AppCompatActivity {

    private RecyclerView.LayoutManager layoutManager;
    private KeranjangAdapter keranjangAdapter;
    private RecyclerView rvKeranjang;
    private TextView tvTotal,tvNis;
    private String abcd,peler,nis;
    private int totalHargaBlmDikali;
    private DataHelper mydb=null;
    private Button prosesBt;
    private ProgressDialog progressDialog;
    private ApiInterface apiInterface;
    private List<keranjang> keranjang = new ArrayList<>();

    private List<keranjang> katrin;

    @Override
    /*Airtable revisi *6*/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        setContentView(R.layout.activity_keranjang_pesanan);
        rvKeranjang = findViewById(R.id.keranjangRv);
        tvTotal = findViewById(R.id.total);
        tvNis = findViewById(R.id.tesNis);
        prosesBt = findViewById(R.id.btProses);
        nis = getIntent().getStringExtra("nis");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Tunggu...");
        if(width>1500){
            layoutManager = new GridLayoutManager(this,2);
        }else{
            layoutManager = new LinearLayoutManager(this);
        }
        rvKeranjang.setLayoutManager(layoutManager);
        loadKeranjang();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));
        katrin = new ArrayList<>();

        prosesBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keranjangAdapter.gg(katrin);
                if(katrin.size() == 0){
                    Toast.makeText(KeranjangPesanan.this, "Anda belum memilih makanan, Harap melakukan pemilihan terlebih dahulu", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(KeranjangPesanan.this, MenuPager.class);
                    intent.putExtra("nis", nis);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(KeranjangPesanan.this, PesananInfo.class);
                    intent.putExtra("nis",nis);
                    intent.putExtra("arraylist", (Serializable) katrin);
                    startActivity(intent);
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadKeranjang();
    }

    private void loadKeranjang(){
        progressDialog.show();
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<Value> call = apiInterface.viewKeranjang();
        call.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                progressDialog.dismiss();
                String value = response.body().getValue();
                if(value.equals("1")){
                    keranjang = response.body().getKeranjang();
                    String [] id = new String[keranjang.size()];
                    List<keranjang> p = new ArrayList<>();
                    for(int i=0; i<keranjang.size(); i++){
                        id[i] = keranjang.get(i).getId_device();
                        if(id[i].equals(nis)){
                            p.add(keranjang.get(i));
                            keranjangAdapter = new KeranjangAdapter(KeranjangPesanan.this, p);
                            rvKeranjang.setAdapter(keranjangAdapter);
                            int totals = 0;
                            for(int z=0; z<p.size(); z++){
                                totals += p.get(z).getTotal_harga();
                            }
                            tvTotal.setText(String.valueOf(totals));
                        }
                    }
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(KeranjangPesanan.this, "Not Equals 1", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(KeranjangPesanan.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            peler = intent.getStringExtra("total");
            tvTotal.setText(peler);
        }
    };

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(KeranjangPesanan.this, MenuPager.class);
        intent.putExtra("nis", nis);
        startActivity(intent);
        finish();
    }
}