package com.digitalentertainmentteam.fmradyonudinle.model;

/**
 * Created by huseyin on 10/01/18.
 */
public class ItemRadio {
    private String RadioCategoryName;
    private String RadioId;
    private String RadioImageUrl;
    private String RadioName;
    private String RadioUrl;
    private int id;

    public ItemRadio()
    {}

    public ItemRadio(String Radioid) {
        this.RadioId = Radioid;
    }

    public ItemRadio(String Radioid, String Radioname, String Radiocategoryname, String Radiourl, String image) {
        this.RadioId = Radioid;
        this.RadioName = Radioname;
        this.RadioCategoryName = Radiocategoryname;
        this.RadioUrl = Radiourl;
        this.RadioImageUrl = image;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRadioId() {
        return this.RadioId;
    }

    public void setRadioId(String Radioid) {
        this.RadioId = Radioid;
    }

    public String getRadioName() {
        return this.RadioName;
    }

    public void setRadioName(String Radioname) {
        this.RadioName = Radioname;
    }

    public String getRadioCategoryName() {
        return this.RadioCategoryName;
    }

    public void setRadioCategoryName(String Radiocategoryname) {
        this.RadioCategoryName = Radiocategoryname;
    }

    public String getRadioImageurl() {
        return this.RadioImageUrl;
    }

    public void setRadioImageurl(String radioimage) {
        this.RadioImageUrl = radioimage;
    }

    public String getRadiourl() {
        return this.RadioUrl;
    }

    public void setRadiourl(String radiourl) {
        this.RadioUrl = radiourl;
    }
}
