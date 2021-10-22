package com.kantinsehat.milenial;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MakananFragment extends Fragment {
    private RecyclerView.LayoutManager mLayoutManager;
    private List<makanan> makanan= new ArrayList<>();
    private RecyclerView makananRv;
    private ProgressDialog progressDialog;
    private ApiInterface apiInterface;
    private MakananAdapter pesanMakananAdapter;
    EditText searchView;
    CharSequence search="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    View v =inflater.inflate(R.layout.fragment_makanan,container,false);
    DisplayMetrics displayMetrics = new DisplayMetrics();
    getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    int width = displayMetrics.widthPixels;
    progressDialog = new ProgressDialog(this.getContext());
    progressDialog.setMessage("Tunggu...");
    searchView = v.findViewById(R.id.searchbar);
    makananRv = v.findViewById(R.id.rvMakanan);
    pesanMakananAdapter = new MakananAdapter(this.getContext(), makanan);
    if(width > 1500){
        mLayoutManager = new GridLayoutManager(this.getContext(), 3);
    }else{
        mLayoutManager = new LinearLayoutManager(this.getContext());
    }
    makananRv.setLayoutManager(mLayoutManager);
    makananRv.setAdapter(pesanMakananAdapter);
    loadData();
    searchView.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            pesanMakananAdapter.getFilter().filter(charSequence);
            search = charSequence;
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    });
    return v;
    }

    private void loadData(){
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
                    String [] status = new String[makanan.size()];
                    List<makanan> p = new ArrayList<>();
                    for(int i =0; i<makanan.size(); i++){
                        status [i] = makanan.get(i).getStatus_makanan();
                        if(status[i].equals("on")){
                            p.add(makanan.get(i));
                            pesanMakananAdapter = new MakananAdapter(MakananFragment.this.getContext(),p);
                            makananRv.setAdapter(pesanMakananAdapter);
                        }
                    }
                }else{
                    Toast.makeText(MakananFragment.this.getContext(), "Error not equals 1", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MakananFragment.this.getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}