package com.tugasbesarkotlin5.pointofsales.Model;

public class Store {
    private String logo;
    private String nama_toko;
    private String telp_toko;
    private String alamat;

    public Store() {
    }

    public Store(String logo, String nama_toko, String telp_toko, String alamat) {
        this.logo = logo;
        this.nama_toko = nama_toko;
        this.telp_toko = telp_toko;
        this.alamat = alamat;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getNama_toko() {
        return nama_toko;
    }

    public void setNama_toko(String nama_toko) {
        this.nama_toko = nama_toko;
    }

    public String getTelp_toko() {
        return telp_toko;
    }

    public void setTelp_toko(String telp_toko) {
        this.telp_toko = telp_toko;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
}
