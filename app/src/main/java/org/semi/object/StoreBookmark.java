package org.semi.object;

import java.util.Date;

public class StoreBookmark {
    private String id;
    private String name;
    private String address;
    private String urlImg;
    private String date;

    public StoreBookmark(String id, String name, String address, String urlImg, String date) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.urlImg = urlImg;
        this.date = date;
    }

    @Override
    public String toString() {
        return "StoreBookmark{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", urlImg='" + urlImg + '\'' +
                ", date=" + date +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }
}
