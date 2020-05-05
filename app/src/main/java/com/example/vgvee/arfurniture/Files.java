package com.example.vgvee.arfurniture;

import android.net.Uri;

public class Files {
    public Integer its_img;
    public String its_name,its_provider,its_price;
    public Uri its_sfb;

    public Files(Integer its_img, String its_name, String its_provider, String its_price, Uri its_sfb) {
        this.its_img = its_img;
        this.its_name = its_name;
        this.its_provider = its_provider;
        this.its_price = its_price;
        this.its_sfb = its_sfb;
    }
}
