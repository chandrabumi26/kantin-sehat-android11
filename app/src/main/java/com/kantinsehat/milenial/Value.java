package com.kantinsehat.milenial;

import java.util.List;

public class Value {
    String value;
    String message;
    List<users> users;
    List<makanan> makanan;
    List<minuman> minuman;
    List<jajanan> jajanan;
    List<keranjang> keranjang;

    public String getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public List<com.kantinsehat.milenial.users> getUsers() {
        return users;
    }

    public List<com.kantinsehat.milenial.makanan> getMakanan() {
        return makanan;
    }

    public List<com.kantinsehat.milenial.minuman> getMinuman() {
        return minuman;
    }

    public List<com.kantinsehat.milenial.jajanan> getJajanan() {
        return jajanan;
    }

    public List<com.kantinsehat.milenial.keranjang> getKeranjang(){return keranjang;}
}
