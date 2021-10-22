package com.kantinsehat.milenial;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KeranjangAdapter extends RecyclerView.Adapter<KeranjangAdapter.ViewHolder> {
    private Context context;
//    private ArrayList idKeranjangList;
//    private ArrayList<String> quantity,totaal_harga;
    private List<keranjang> keranjangList;
    private ApiInterface apiInterface;
    ArrayList<Integer> forSize;
    ArrayList<Integer> forQuantity;
    List<keranjang> krn = new ArrayList<>();
    DisplayMetrics displayMetrics = new DisplayMetrics();


//    public KeranjangAdapter(ArrayList nameList, ArrayList jumlahList,ArrayList notesList, ArrayList total_hargaList, ArrayList gambarList, ArrayList totalsList, ArrayList forSize, ArrayList idKeranjangList, Context context){
//        this.nameList = nameList;
//        this.jumlahList = jumlahList;
//        this.notesList = notesList;
//        this.total_hargaList = total_hargaList;
//        this.gambarList = gambarList;
//        this.totalsList = totalsList;
//        this.forSize = forSize;
//        this.idKeranjangList = idKeranjangList;
//        quantity = new ArrayList<>();
//        totaal_harga = new ArrayList<>();
//    }

    public KeranjangAdapter(Context context, List<keranjang> keranjangList){
        this.context = context;
        this.keranjangList = keranjangList;
        forSize = new ArrayList<>();
        forQuantity = new ArrayList<>();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.keranjang_items,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        /*Initation*/
        keranjang keranjangs = keranjangList.get(position);
        int jumlahHarga = keranjangs.getJumlah() * keranjangs.getHarga();
        holder.tvKeranjangName.setText(keranjangs.getNama());
        holder.tvKeranjangNotes.setText(keranjangs.getNotes());
        holder.enb.setNumber(String.valueOf(keranjangs.getJumlah()));
        holder.tvKeranjangHarga.setText(String.valueOf(jumlahHarga));
        Glide.with(holder.ivKeranjangImg.getContext()).load("https://bikinlanding.com/kantinsehat/upload/"+keranjangs.getGambar()).apply(new RequestOptions().placeholder(holder.circularProgressDrawable)).into(holder.ivKeranjangImg);
        /*End of Initiation*/

        if(position < forSize.size()){
            forSize.set(position,jumlahHarga);
            forQuantity.set(position,keranjangs.getJumlah());
        }else if(position >= forSize.size()){
            forSize.add(position,jumlahHarga);
            forQuantity.add(position,keranjangs.getJumlah());
        }
        holder.btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.progressDialog.show();
                int id = Integer.parseInt(keranjangs.getId_keranjang());
                apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                Call<keranjang> call = apiInterface.deleteItem(id);
                call.enqueue(new Callback<keranjang>() {
                    @Override
                    public void onResponse(Call<keranjang> call, Response<keranjang> response) {
                        holder.progressDialog.dismiss();
                        Boolean success = response.body().getSuccess();
                        if(success){
                            Toast.makeText(holder.ivKeranjangImg.getContext(), "Berhasil didelete", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(holder.ivKeranjangImg.getContext(), KeranjangPesanan.class);
                            i.putExtra("nis", keranjangs.getId_device());
                            holder.ivKeranjangImg.getContext().startActivity(i);
                            ((Activity) holder.ivKeranjangImg.getContext()).finish();
                        }else{
                            Toast.makeText(holder.ivKeranjangImg.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<keranjang> call, Throwable t) {
                        holder.progressDialog.dismiss();
                        Toast.makeText(holder.ivKeranjangImg.getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        /*when enb value change*/
        String [] cut = new String[keranjangList.size()];
        holder.enb.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                int bb = holder.getAdapterPosition();
                if(position == bb){
                    cut[position] = holder.enb.getNumber();
                    int jumlah = Integer.parseInt(cut[position]);
                    int total_hargaa = keranjangs.getHarga() * jumlah;
                    holder.tvKeranjangHarga.setText(String.valueOf(total_hargaa));
                    forSize.set(position,total_hargaa);
                    forQuantity.set(position,jumlah);
//                    if(jumlah==0){
//                        holder.progressDialog.show();
//                        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
//                        int id = Integer.parseInt(keranjangs.getId_keranjang());
//                        Call<keranjang> call = apiInterface.deleteItem(id);
//                        call.enqueue(new Callback<keranjang>() {
//                            @Override
//                            public void onResponse(Call<keranjang> call, Response<keranjang> response) {
//                                holder.progressDialog.dismiss();
//                                Boolean success = response.body().getSuccess();
//                                if(success){
//                                    Toast.makeText(holder.ivKeranjangImg.getContext(), "Berhasil didelete", Toast.LENGTH_SHORT).show();
//                                    Intent i = new Intent(holder.ivKeranjangImg.getContext(), KeranjangPesanan.class);
//                                    i.putExtra("nis", keranjangs.getId_device());
//                                    holder.ivKeranjangImg.getContext().startActivity(i);
//                                    ((Activity) holder.ivKeranjangImg.getContext()).finish();
//
//                                }else{
//                                    Toast.makeText(holder.ivKeranjangImg.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<keranjang> call, Throwable t) {
//                                holder.progressDialog.dismiss();
//                                Toast.makeText(holder.ivKeranjangImg.getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
                }
                int[] duts =new int[forSize.size()];
                int qq = 0;
                for(int i=0; i<forSize.size(); i++){
                    duts[i] =(int)forSize.get(i);
                    qq += duts[i];
                }
                /*Tinggal Broadcast*/
                String kontol = String.valueOf(qq);
                Intent intent = new Intent("custom-message");
                intent.putExtra("total",kontol);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//                Toast.makeText(holder.ivKeranjangImg.getContext(), String.valueOf(qq), Toast.LENGTH_SHORT).show();

            }
        });


    }

//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        final String nama = (String) nameList.get(position);
//        final String jumlah = (String) jumlahList.get(position);
//        final String notes = (String) notesList.get(position);
//        final String harga = (String) total_hargaList.get(position);
//        final String gambar = (String) gambarList.get(position);
//        final int qty = (Integer) forSize.get(position);
//        final String idPrimary = (String) idKeranjangList.get(position);
//        holder.idKeranjang = idPrimary;
//        holder.tvKeranjangName.setText(nama);
//        /*set harga*/
//        int jumlahHarga = Integer.parseInt(jumlah) * Integer.parseInt(harga);
//        holder.tvKeranjangHarga.setText(String.valueOf(jumlahHarga));
//        holder.enb.setNumber(jumlah);
//
//        holder.tvKeranjangNotes.setText(notes);
//        Glide.with(holder.ivKeranjangImg.getContext()).load("https://bikinlanding.com/kantinsehat/upload/"+gambar).apply(new RequestOptions().placeholder(holder.circularProgressDrawable)).into(holder.ivKeranjangImg);
//
//        /*Begin*/
//        String [] cut =  new String[idKeranjangList.size()];
//        String [] cit = new String[idKeranjangList.size()];
//        int [] cat = new int[forSize.size()];
//        if(position < quantity.size()){
//            quantity.set(position, jumlah);
//            totaal_harga.set(position, String.valueOf(jumlahHarga));
//        }else if(position >= quantity.size()){
//            quantity.add(position, jumlah);
//            totaal_harga.add(position, String.valueOf(jumlahHarga));
//        }
//        holder.enb.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
//            @Override
//            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
//                int bb =holder.getAdapterPosition();
//                    if(position == bb){
//                        cut[position] = holder.enb.getNumber();
//                        int hargaKali = Integer.parseInt(harga);
//                        int qty = Integer.parseInt(cut[position]);
//                        String totalHarga = String.valueOf(hargaKali*qty);
//                        holder.tvKeranjangHarga.setText(totalHarga);
//                        quantity.set(position, cut[position]);
//                        String abcde = holder.tvKeranjangHarga.getText().toString();
//                        totaal_harga.set(position, abcde);
//                        cit[position] = holder.tvKeranjangHarga.getText().toString();
//                        cat[position] =Integer.parseInt(cit[position]);
//                        int df = 0;
//                        for(int i =0; i<cat.length; i++){
//                            df +=cat[i];
//                        }
//                        holder.totalBeli = df;
//                        forSize.set(position,df);
//                        /*delete kalo 0*/
//                        if(qty==0){
//                            holder.mydb = new DataHelper(holder.ivKeranjangImg.getContext());
//                            Boolean res = holder.mydb.deleteData(holder.idKeranjang);
//                            if(res = true){
//                                Toast.makeText(holder.ivKeranjangImg.getContext(), holder.tvKeranjangName.getText().toString()+" berhasil dihapus", Toast.LENGTH_SHORT).show();
//                                Intent intent = ((Activity) holder.ivKeranjangImg.getContext()).getIntent();
//                                ((Activity) holder.ivKeranjangImg.getContext()).finish();
//                                ((Activity) holder.ivKeranjangImg.getContext()).startActivity(intent);
//
//                            }
//                            else{
//                                Toast.makeText(holder.ivKeranjangImg.getContext(), "gagal", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                        /*/delete kalo 0*/
//                    }
//                    int[] duts =new int[forSize.size()];
//                    int qq = 0;
//                    for(int i=0; i<forSize.size(); i++){
//                        duts[i] =(int)forSize.get(i);
//                        qq += duts[i];
//                    }
//                    /*Tinggal di broadcast*/
//                    String kontol = String.valueOf(qq);
//                    Intent intent = new Intent("custom-message");
//                    intent.putExtra("total",kontol);
//                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//
////                    holder.tvKeranjangName.setText(kontol);
//            }
//        });
//
//    }

    public List<keranjang> gg(List<keranjang> katrin){
        for(int i=0; i<keranjangList.size(); i++){
            keranjang katrins = new keranjang();
            katrins.setNama(keranjangList.get(i).getNama());
            katrins.setHarga(keranjangList.get(i).getHarga());
            katrins.setJumlah(forQuantity.get(i));
            katrins.setNotes(keranjangList.get(i).getNotes());
            katrins.setTotal_harga(forSize.get(i));
            katrin.add(i, katrins);
        }
        return katrin;
    }

    @Override
    public int getItemCount() {
        return keranjangList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircularProgressDrawable circularProgressDrawable;
        private TextView tvKeranjangName, tvKeranjangHarga, tvKeranjangJumlah, tvKeranjangNotes, tvKeranjangId;
        private ImageView ivKeranjangImg;
        private ElegantNumberButton enb;
        private Button btDelete;
        private ProgressDialog progressDialog;
        private DataHelper mydb=null;
        private int totalBeli;
        String img,qtyYeah,idKeranjang;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circularProgressDrawable = new CircularProgressDrawable(itemView.getContext());
            circularProgressDrawable.setCenterRadius(10f);
            circularProgressDrawable.start();
            tvKeranjangName = itemView.findViewById(R.id.keranjangName);
            tvKeranjangHarga = itemView.findViewById(R.id.keranjangHarga);
            ivKeranjangImg = itemView.findViewById(R.id.keranjangImg);
            enb = itemView.findViewById(R.id.keranjangJumlah);
//            tvKeranjangJumlah = itemView.findViewById(R.id.keranjangJumlah);
            tvKeranjangNotes = itemView.findViewById(R.id.keranjangNotes);
            tvKeranjangId = itemView.findViewById(R.id.keranjangId);
            btDelete = itemView.findViewById(R.id.deleteById);
            progressDialog = new ProgressDialog(itemView.getContext());
            progressDialog.setMessage("Tunggu...");
//            btDelete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    String id = tvKeranjangId.getText().toString();
//                    mydb = new DataHelper(itemView.getContext());
//                    Boolean res = mydb.deleteData(idKeranjang);
//                    if(res = true){
//                        Toast.makeText(itemView.getContext(), tvKeranjangName.getText().toString()+" berhasil dihapus", Toast.LENGTH_SHORT).show();
//                        Intent intent = ((Activity) itemView.getContext()).getIntent();
//                        ((Activity) itemView.getContext()).finish();
//                        ((Activity) itemView.getContext()).startActivity(intent);
//
//                    }
//                    else{
//                        Toast.makeText(itemView.getContext(), "gagal", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
        }


    }



}
