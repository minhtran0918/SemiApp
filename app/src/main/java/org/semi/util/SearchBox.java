package org.semi.util;

import org.semi.contract.Contract;
import org.semi.object.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.Arrays;

//Box used for draw on GoogleMap and check range.
public class SearchBox {
    public static final double DEFAULT_DIMEN = 0.25;
    public static final int BOX_STROKE_COLOR = 0x400000ff;
    private double dimen;
    private LatLng center;
    private LatLng[] points;
    private Polygon polygon;

    public SearchBox() {
        dimen = 0;
        center = new LatLng(0,0);
        points = MathUtils.getBoxPoints(center, dimen);
    }

    public boolean isContains(@NonNull LatLng point) {
        if(point.longitude > points[0].longitude && point.longitude < points[2].longitude &&
                point.latitude < points[0].latitude && point.latitude > points[2].latitude) {
            return true;
        }
        return false;
    }

    public void setCenter(@NonNull LatLng center) {
        this.center = center;
        points = MathUtils.getBoxPoints(center, dimen);
    }

    public void setCenter(@NonNull Location location) {
        center = new LatLng(location.getLatitude(), location.getLongitude());
        points = MathUtils.getBoxPoints(location, dimen);
    }

    public void setDimen(double dimen) {
        if(dimen <= 0) {
            return;
        }
        this.dimen = dimen;
        points = MathUtils.getBoxPoints(center, dimen);
    }

    public LatLng getSouthWest() {
        return points[1];
    }

    public LatLng getNorthEast() {
        return points[3];
    }

    public double getDimen() {
        return dimen;
    }

    public LatLng getCenter() {
        return center;
    }

    public Location getLocationCenter() {
        return new Location(center.latitude, center.longitude);
    }

    public void draw(@NonNull GoogleMap map) {
        removePolygon();
        LatLng[] hidingBoxPoints = MathUtils.getBoxPoints(center, Contract.HIDING_BOX_DIMEN);
        polygon = map.addPolygon(new PolygonOptions().add(hidingBoxPoints)
                .addHole(Arrays.asList(points))
                .fillColor(Contract.HIDING_BOX_COLOR)
                .strokeColor(Contract.HIDING_BOX_COLOR).strokeWidth(0));
    }

    public void removePolygon() {
        if(polygon != null) {
            polygon.remove();
            polygon = null;
        }
    }
}
