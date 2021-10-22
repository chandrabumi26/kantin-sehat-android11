package com.kantinsehat.milenial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PesananMinuman extends AppCompatActivity {
    private CircularProgressDrawable circularProgressDrawable;
    private TextView tvNamaPesananMinuman, tvHargaPesananMinuman, tvDeskripsiMinuman;
    private EditText etNotesMinuman;
    private Button btTambahMinuman;
    private ImageView img,ivBack;
    private ApiInterface apiInterface;
    private ProgressDialog progressDialog;
    String namaMinuman, hargaMinuman, gambarMinuman, abc, deskripsi, nis;
    private List<minuman> minuman = new ArrayList<>();
    private DataHelper dt = null;
    ElegantNumberButton enbMinuman;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        if(width > 1500){
            setContentView(R.layout.activity_pesanan_minuman);
        }else{
            setContentView(R.layout.activity_pesanan_minuman_phone);
        }
        tvNamaPesananMinuman = findViewById(R.id.namaPesananMinuman);
        tvHargaPesananMinuman = findViewById(R.id.hargaPesananMinuman);
        tvDeskripsiMinuman = findViewById(R.id.minumanDeskripsi);
        img = findViewById(R.id.gambarPesananMinuman);
        ivBack = findViewById(R.id.minumanBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PesananMinuman.this, MenuPager.class);
                startActivity(intent);
                finish();
            }
        });
        etNotesMinuman = findViewById(R.id.notesMinuman);
        btTambahMinuman = findViewById(R.id.tambahBtMinuman);
        enbMinuman = findViewById(R.id.btEleganMinuman);
        Bundle bundle = getIntent().getExtras();
        abc = bundle.getString("id_minuman");
        nis = bundle.getString("nisUser");
        Toast.makeText(PesananMinuman.this, nis, Toast.LENGTH_SHORT).show();
//        abc = getIntent().getStringExtra("id_minuman");

        progressDialog = new ProgressDialog(PesananMinuman.this);
        progressDialog.setMessage("Tunggu...");
        circularProgressDrawable = new CircularProgressDrawable(PesananMinuman.this);
        circularProgressDrawable.setCenterRadius(10f);
        circularProgressDrawable.start();
        loadData(abc);
        enbMinuman.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                String count = enbMinuman.getNumber();
                int hharga = Integer.parseInt(hargaMinuman);
                int qty = Integer.parseInt(count);
                String fixHarga = String.valueOf(hharga*qty);
                tvHargaPesananMinuman.setText(fixHarga);
            }
        });
        btTambahMinuman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                addData();
                String ccount = enbMinuman.getNumber();
                int qty = Integer.parseInt(ccount);
                int hharga = Integer.parseInt(hargaMinuman);
                String notes = etNotesMinuman.getText().toString();
                String tots = tvHargaPesananMinuman.getText().toString();
                int totals = Integer.parseInt(tots);
                addKeranjang(nis,namaMinuman,qty,hharga,notes,totals,gambarMinuman);
            }
        });
    }

    private void addData(){
        dt = new DataHelper(PesananMinuman.this);
        String ccount = enbMinuman.getNumber();
        int qty = Integer.parseInt(ccount);
        int hharga = Integer.parseInt(hargaMinuman);
        String notes = etNotesMinuman.getText().toString();
        String tots = tvHargaPesananMinuman.getText().toString();
        int totals = Integer.parseInt(tots);
        boolean isInserted = dt.insertData(namaMinuman,qty,notes,hharga,gambarMinuman,totals);
        if(isInserted = true){
            Toast.makeText(PesananMinuman.this, "Terima kasih sudah memilih menus pesanan "+namaMinuman, Toast.LENGTH_SHORT).show();
            Intent i=new Intent(this,MenuPager.class);
            startActivity(i);
            finish();
        }else{
            Toast.makeText(PesananMinuman.this, "Gagal ditambahkan", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadData(String a){
        progressDialog.show();
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<Value> call = apiInterface.viewMinuman();
        call.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                progressDialog.dismiss();
                String value = response.body().getValue();
                if(value.equals("1")){
                    minuman = response.body().getMinuman();
                    String [] id = new String[minuman.size()];
                    for(int i=0; i<minuman.size(); i++){
                        id[i] = String.valueOf(minuman.get(i).getIdMinuman());
                        if(id[i].equals(a)){
                            namaMinuman = minuman.get(i).getNama_minuman();
                            hargaMinuman = String.valueOf(minuman.get(i).getHarga_minuman());
                            gambarMinuman = minuman.get(i).getGambar_minuman();
                            deskripsi = minuman.get(i).getDeskripsi_minuman();
                        }
                    }
                    tvNamaPesananMinuman.setText(namaMinuman);
                    tvHargaPesananMinuman.setText(hargaMinuman);
                    tvDeskripsiMinuman.setText(deskripsi);
                    Glide.with(PesananMinuman.this).load("https://bikinlanding.com/kantinsehat/upload/"+gambarMinuman).apply(new RequestOptions().placeholder(circularProgressDrawable)).into(img);
                }else{
                    Toast.makeText(PesananMinuman.this, "Not equals 1", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(PesananMinuman.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addKeranjang(final String id_device, final String nama, final int jumlah, final int harga, final String notes, final int total_harga, final String gambar){
        progressDialog.show();
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<keranjang> call = apiInterface.saveKeranjang(id_device, nama, jumlah, harga, notes, total_harga, gambar);
        call.enqueue(new Callback<keranjang>() {
            @Override
            public void onResponse(Call<keranjang> call, Response<keranjang> response) {
                progressDialog.dismiss();
                Boolean success = response.body().getSuccess();
                if(success){
                    Toast.makeText(PesananMinuman.this, "Terima kasih sudah memilih menus pesanan "+namaMinuman, Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(PesananMinuman.this,MenuPager.class);
                    i.putExtra("nis",nis);
                    startActivity(i);
                    finish();
                }else{
                    Toast.makeText(PesananMinuman.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<keranjang> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(PesananMinuman.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}