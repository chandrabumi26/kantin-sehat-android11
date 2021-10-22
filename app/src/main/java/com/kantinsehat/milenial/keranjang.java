package com.kantinsehat.milenial;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class keranjang implements java.io.Serializable{

    @Expose
    @SerializedName("id_keranjang") private String id_keranjang;
    @Expose
    @SerializedName("id_device") private String id_device;
    @Expose
    @SerializedName("nama") private String nama;
    @Expose
    @SerializedName("harga") private int harga;
    @Expose
    @SerializedName("jumlah") private int jumlah;
    @Expose
    @SerializedName("notes") private String notes;
    @Expose
    @SerializedName("total_harga") private int total_harga;
    @Expose
    @SerializedName("gambar") private String gambar;
    @Expose
    @SerializedName("success") private Boolean success;
    @Expose
    @SerializedName("message") private String message;

    public String getId_keranjang() {
        return id_keranjang;
    }

    public void setId_keranjang(String id_keranjang) {
        this.id_keranjang = id_keranjang;
    }

    public String getId_device() {
        return id_device;
    }

    public void setId_device(String id_device) {
        this.id_device = id_device;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getTotal_harga() {
        return total_harga;
    }

    public void setTotal_harga(int total_harga) {
        this.total_harga = total_harga;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
