package com.kantinsehat.milenial;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuPager extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    MenuAdapter menuAdapter;
    ExtendedFloatingActionButton fabPesananSaya;
    private DataHelper mydb=null;
//    private ArrayList totalsList;
//    private int totalHargaBlmDikali=0;
    private List<keranjang> keranjangList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private ApiInterface apiInterface;
    private String nis_tes;
    MakananFragment mkn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_pager);
        mkn = new MakananFragment();
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager2);
        progressDialog = new ProgressDialog(MenuPager.this);
        progressDialog.setMessage("Tunggu...");
        /*Set Nis*/
        Intent getNis = getIntent();
        Bundle bb = getNis.getExtras();
        nis_tes = (String) bb.get("nis");
        /*/SetNis*/
        FragmentManager fm = getSupportFragmentManager();
        menuAdapter = new MenuAdapter(fm, getLifecycle());
        viewPager2.setAdapter(menuAdapter);
        tabLayout.addTab(tabLayout.newTab().setText("Makanan"));
        tabLayout.addTab(tabLayout.newTab().setText("Minuman"));
        tabLayout.addTab(tabLayout.newTab().setText("Jajanan"));
        loadKeranjang();
        fabPesananSaya = findViewById(R.id.pesananSaya);
        fabPesananSaya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuPager.this, KeranjangPesanan.class);
                intent.putExtra("nis", nis_tes);
                startActivity(intent);
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadKeranjang();
    }

    public String sendData(){
        return nis_tes;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(MenuPager.this, MainActivity.class);
                        deleteAll(nis_tes);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void deleteAll(String id_device){
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<keranjang> call = apiInterface.deleteAll(id_device);
        call.enqueue(new Callback<keranjang>() {
            @Override
            public void onResponse(Call<keranjang> call, Response<keranjang> response) {
                Boolean success = response.body().getSuccess();
                if(success){

                }else{
                    Toast.makeText(MenuPager.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<keranjang> call, Throwable t) {
                Toast.makeText(MenuPager.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                    keranjangList = response.body().getKeranjang();
                    String [] id = new String[keranjangList.size()];
                    List<keranjang> untukSize = new ArrayList<>();
                    for(int i=0; i<keranjangList.size(); i++){
                        id[i] = keranjangList.get(i).getId_device();
                        if(id[i].equals(nis_tes)){
                            untukSize.add(keranjangList.get(i));
                            String countKeranjang = String.valueOf(untukSize.size());
                            int untukHarga = 0;
                            for(int z=0; z<untukSize.size(); z++){
                                untukHarga += untukSize.get(z).getTotal_harga();
                            }
                            String buttonText = "Keranjang " + countKeranjang + " Pesanan " +"Rp."+String.valueOf(untukHarga);
                            fabPesananSaya.setText(buttonText);
                        }else{
                            String buttonText = "Pesanan";
                            fabPesananSaya.setText(buttonText);
                        }
                    }
                }else{
                    Toast.makeText(MenuPager.this, "Not 1", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MenuPager.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void deleteData(){
//        mydb = new DataHelper(this);
//        Boolean res = mydb.deleteData();
//        if(res = true){
//            Toast.makeText(MenuPager.this, "Berhasil delete", Toast.LENGTH_SHORT).show();
//        }else{
//            Toast.makeText(MenuPager.this, "Gagal", Toast.LENGTH_SHORT).show();
//        }
//    }

//    private void getData(){
//        mydb = new DataHelper(this);
//        Cursor res = mydb.getAllData();
//        res.moveToFirst();
//        ArrayList<Integer> dd = new ArrayList<>();
//        for(int i=0; i<res.getCount(); i++){
//            res.moveToPosition(i);
//            totalsList.add(res.getString(6));
//            dd.add(res.getInt(6));
//        }
//
//        int [] cat = new int[totalsList.size()];
//        int d=0;
//        for(int i=0; i<cat.length; i++){
//            cat[i] = dd.get(i);
//            d+=cat[i];
//        }
//        totalHargaBlmDikali = d;
//    }

}