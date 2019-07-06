package org.semi.object;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class VerifyStore {

    private String user_id;
    private String store_id;
    private String title;
    private int type;
    private VerifyStoreAddress address;
    private GeoPoint geo;
    private String contact;
    private String startEnd;   //07:00-20:00
    private String description;
    private String imageURL;
    private @ServerTimestamp Date timeStamp;

    public VerifyStore() {
    }

    public VerifyStore(String user_id, String store_id, String title, int type, VerifyStoreAddress address, GeoPoint geo, String contact, String startEnd, String description, String imageURL, Date timeStamp) {
        this.user_id = user_id;
        this.store_id = store_id;
        this.title = title;
        this.type = type;
        this.address = address;
        this.geo = geo;
        this.contact = contact;
        this.startEnd = startEnd;
        this.description = description;
        this.imageURL = imageURL;
        this.timeStamp = timeStamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public VerifyStoreAddress getAddress() {
        return address;
    }

    public void setAddress(VerifyStoreAddress address) {
        this.address = address;
    }

    public GeoPoint getGeo() {
        return geo;
    }

    public void setGeo(GeoPoint geo) {
        this.geo = geo;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getStartEnd() {
        return startEnd;
    }

    public void setStartEnd(String startEnd) {
        this.startEnd = startEnd;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
