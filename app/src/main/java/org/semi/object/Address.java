package org.semi.object;

import android.util.Log;

import java.io.Serializable;

import static org.semi.utils.StringUtils.convertStandardAddress;

public class Address implements Serializable {
    private Country country;
    private City city;
    private District district;
    private Town town;
    private String street;

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public Town getTown() {
        return town;
    }

    public void setTown(Town town) {
        this.town = town;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Override
    public String toString() {
        return convertStandardAddress(street, town.getName(), district.getName(), city.getName());
    }


}
