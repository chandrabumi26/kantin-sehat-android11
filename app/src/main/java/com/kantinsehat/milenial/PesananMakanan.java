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

public class PesananMakanan extends AppCompatActivity {
    private CircularProgressDrawable circularProgressDrawable;
    private TextView tvNamaPesanan,tvHargaPesanan,tvDeskripsi;
    private EditText etNotes;
    private Button btTambah;
    private ImageView img,ivBack;
    private ApiInterface apiInterface;
    private ProgressDialog progressDialog;
    String nama,harga,gambar, abc, deskripsi, nisUser;
    private List<makanan> makanan = new ArrayList<>();
    private DataHelper dt = null;
    ElegantNumberButton enb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        if(width > 1500){
            setContentView(R.layout.activity_pesanan);
        }else{
            setContentView(R.layout.activity_pesanan_phone);
        }
        tvNamaPesanan = findViewById(R.id.namaPesanan);
        tvHargaPesanan = findViewById(R.id.hargaPesanan);
        tvDeskripsi = findViewById(R.id.makananDeskripsi);
        img = findViewById(R.id.gambarPesanan);
        etNotes = findViewById(R.id.notes);
        btTambah = findViewById(R.id.tambahBt);
        enb = findViewById(R.id.btElegan);
        Bundle extras = getIntent().getExtras();
        abc = extras.getString("id");
        nisUser = extras.getString("nisUser");
        Toast.makeText(PesananMakanan.this, nisUser, Toast.LENGTH_SHORT).show();
        progressDialog = new ProgressDialog(PesananMakanan.this);
        progressDialog.setMessage("Tunggu...");
        circularProgressDrawable = new CircularProgressDrawable(PesananMakanan.this);
        circularProgressDrawable.setCenterRadius(10f);
        circularProgressDrawable.start();
        loadData(abc);

        ivBack = findViewById(R.id.makananBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PesananMakanan.this, MenuPager.class);
                startActivity(intent);
                finish();
            }
        });

        enb.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                String count = enb.getNumber();
                int hharga = Integer.parseInt(harga);
                int qty = Integer.parseInt(count);
                String fixHarga = String.valueOf(hharga*qty);
                tvHargaPesanan.setText(fixHarga);
            }
        });

        btTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                addData();
                String ccount = enb.getNumber();
                int qty = Integer.parseInt(ccount);
                int hharga = Integer.parseInt(harga);
                String notes = etNotes.getText().toString();
                String tots = tvHargaPesanan.getText().toString();
                int totals = Integer.parseInt(tots);
                addKeranjang(nisUser,nama,qty,hharga,notes,totals,gambar);
            }
        });
    }

    private void addData(){
        dt = new DataHelper(PesananMakanan.this);
        String ccount = enb.getNumber();
        int qty = Integer.parseInt(ccount);
        int hharga = Integer.parseInt(harga);
        String notes = etNotes.getText().toString();
        String tots = tvHargaPesanan.getText().toString();
        int totals = Integer.parseInt(tots);
        boolean isInserted = dt.insertData(nama,qty,notes,hharga,gambar,totals);
        if(isInserted = true){
            /*Terima kasih telah memilih menu makanan*/
            Toast.makeText(PesananMakanan.this, "Terima kasih sudah memilih menus pesanan "+nama, Toast.LENGTH_SHORT).show();
            Intent i=new Intent(this,MenuPager.class);
            startActivity(i);
            finish();
        }else{
            Toast.makeText(PesananMakanan.this, "Gagal ditambahkan", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadData(String a){
        progressDialog.show();
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<Value> call = apiInterface.viewMakanan();
        call.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                progressDialog.dismiss();
                String value = response.body().getValue();
                if(value.equals("1")){
                    makanan = response.body().getMakanan();
                    String [] id = new String[makanan.size()];
                    for(int i=0; i<makanan.size(); i++){
                        id[i] = String.valueOf(makanan.get(i).getIdMakanan());
                        if(id[i].equals(a)){

                            nama = makanan.get(i).getNama_makanan();
                            harga = String.valueOf(makanan.get(i).getHarga_makanan());
                            gambar = makanan.get(i).getGambar();
                            deskripsi = makanan.get(i).getDeskripsi_makanan();
                        }
                    }
                    tvNamaPesanan.setText(nama);
                    tvHargaPesanan.setText(harga);
                    tvDeskripsi.setText(deskripsi);
                    Glide.with(PesananMakanan.this).load("https://bikinlanding.com/kantinsehat/upload/"+gambar).apply(new RequestOptions().placeholder(circularProgressDrawable)).into(img);
                }else{
                    Toast.makeText(PesananMakanan.this, "not equal 1", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(PesananMakanan.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addKeranjang(final String id_device, final String nama, final int jumlah, final int harga, final String notes, final int total_harga, final String gambar){
        progressDialog.show();
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<keranjang> call = apiInterface.saveKeranjang(id_device, nama, jumlah, harga,notes, total_harga, gambar);
        call.enqueue(new Callback<keranjang>() {
            @Override
            public void onResponse(Call<keranjang> call, Response<keranjang> response) {
                progressDialog.dismiss();
                Boolean success = response.body().getSuccess();
                if(success){
//                    Toast.makeText(PesananMakanan.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(PesananMakanan.this, "Terima kasih sudah memilih menus pesanan "+nama, Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(PesananMakanan.this,MenuPager.class);
                    i.putExtra("nis",nisUser);
                    startActivity(i);
                    finish();
                }else{
                    Toast.makeText(PesananMakanan.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<keranjang> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(PesananMakanan.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}