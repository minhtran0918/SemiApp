package org.semi.object;

public class VerifyStoreAddress {
    private String street;
    private int ward;
    private int district;
    private int city;
    private int country = 0;

    public VerifyStoreAddress() {
    }

    public VerifyStoreAddress(String street, int ward, int district, int city) {
        this.street = street;
        this.ward = ward;
        this.district = district;
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getWard() {
        return ward;
    }

    public void setWard(int ward) {
        this.ward = ward;
    }

    public int getDistrict() {
        return district;
    }

    public void setDistrict(int district) {
        this.district = district;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public int getCountry() {
        return country;
    }

    public void setCountry(int country) {
        this.country = country;
    }
}
