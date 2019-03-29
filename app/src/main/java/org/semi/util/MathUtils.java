package org.semi.util;

import com.google.android.gms.maps.model.LatLng;

import org.semi.object.Location;

public final class MathUtils {
    /*
    Mean Equatorial: 6,378.1370km
    Mean Polar: 6,356.7523
    Authalic/Volumetric: 6,371km
    Meridional: 6367km
    */
    public static final double KILO_PER_LAT = 111.111;
    public static final double KILO_RADIUS = 6378.137;

    private MathUtils() {}

    /**
     * Convert distance in kilometer to latitude.
     * @param kilometer Distance in kilometer.
     * @return Distance in latitude.
     */
    public static double kmToLat(double kilometer) {
        return kilometer / KILO_PER_LAT;
    }

    /**
     * Convert distance in kilometer to longitude.
     * @param kilometer Distance in kilometer.
     * @return Distance in longitude.
     */
    public static double kmToLng(double kilometer, double lat) {
        return kilometer / (KILO_PER_LAT * Math.cos(lat * Math.PI / 180));
    }
    public static LatLng[] getBoxPoints(LatLng center, double dimen) {
        double halfDimen = dimen / 2;
        double extraLat = kmToLat(halfDimen);
        double extraLng = kmToLng(halfDimen, center.latitude);
        LatLng[] points = {
                new LatLng(center.latitude + extraLat, center.longitude - extraLng),
                new LatLng(center.latitude - extraLat, center.longitude - extraLng),
                new LatLng(center.latitude - extraLat, center.longitude + extraLng),
                new LatLng(center.latitude + extraLat, center.longitude + extraLng)
        };
        return points;
    }

    public static LatLng[] getBoxPoints(Location location, double dimen) {
        double halfDimen = dimen / 2;
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        double extraLat = kmToLat(halfDimen);
        double extraLng = kmToLng(halfDimen, latitude);
        LatLng[] points = {
                new LatLng(latitude + extraLat, longitude - extraLng),
                new LatLng(latitude - extraLat, longitude - extraLng),
                new LatLng(latitude - extraLat, longitude + extraLng),
                new LatLng(latitude + extraLat, longitude + extraLng)
        };
        return points;
    }

    public static double haversine(Location from,
                                      Location to) {
        double fromLat = Math.toRadians(from.getLatitude());
        double fromLng = Math.toRadians(from.getLongitude());
        double toLat = Math.toRadians(to.getLatitude());
        double toLng = Math.toRadians(to.getLongitude());
        double havLat = Math.sin((toLat - fromLat) / 2);
        double havLng = Math.sin((toLng - fromLng) / 2);
        return 2 * KILO_RADIUS * Math.asin(Math.sqrt(havLat * havLat + Math.cos(fromLat) * Math.cos(toLat) * havLng * havLng));
    }

    public static int getSample(int desiredWidth, int oldWidth) {
        if(desiredWidth >= oldWidth) {
            return 0;
        }
        double value = oldWidth * 1.0 / desiredWidth;
        return (int) Math.ceil(Math.log(value) / Math.log(2));
    }
}
