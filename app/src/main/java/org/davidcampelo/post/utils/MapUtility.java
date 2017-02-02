package org.davidcampelo.post.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by davidcampelo on 8/15/16.
 */
public class MapUtility {

    /** Default padding for map camera */
    private static final int DEFAULT_PADDING = 50;
    private static final double SQUAREMETERS_TO_RADIUS_FACTOR = 10000;
    private static String LOG_TAG = "[MAP UTILITY] ";

    private static final double EARTH_RADIUS = 6371000;// meters

    public String resolveAddress(Context context, double latitude, double longitude) {
        if (latitude == Double.MAX_VALUE || longitude == Double.MAX_VALUE)
            return "";

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());

        // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            StringBuilder stringBuilder = new StringBuilder();
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            if (address != null)
                stringBuilder.append(address);
            String city = addresses.get(0).getLocality();
            if (city != null)
                stringBuilder.append(", " + city);
            String postalCode = addresses.get(0).getPostalCode();
            if (postalCode != null)
                stringBuilder.append(" - " + postalCode);

            return stringBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Address not found!";
    }
    /**
     * Calculate area according to a List of points and formats to 2 decimal places
     */
    public static String calculateAreaAndFormat(final List<LatLng> locations) {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator(Constants.NUMBER_DECIMAL_SEPARATOR);
        otherSymbols.setGroupingSeparator(Constants.NUMBER_GROUP_SEPARATOR);
        DecimalFormat decimalFormat = new DecimalFormat(Constants.NUMBER_DECIMAL_FORMAT, otherSymbols);

        return decimalFormat.format(calculateAreaOfGPSPolygonOnSphereInHectares(locations, EARTH_RADIUS));
    }

    private static double calculateAreaOfGPSPolygonOnSphereInHectares(List<LatLng> locations, double earthRadius) {
        return (calculateAreaOfGPSPolygonOnSphereInSquareMeters(locations, earthRadius)/SQUAREMETERS_TO_RADIUS_FACTOR);
    }


    private static double calculateAreaOfGPSPolygonOnSphereInSquareMeters(final List<LatLng> locations, final double radius) {
        if (locations.size() < 3) {
            return 0;
        }

        final double diameter = radius * 2;
        final double circumference = diameter * Math.PI;
        final List<Double> listY = new ArrayList<Double>();
        final List<Double> listX = new ArrayList<Double>();
        final List<Double> listArea = new ArrayList<Double>();
        // calculate segment x and y in degrees for each point
        final double latitudeRef = locations.get(0).latitude;
        final double longitudeRef = locations.get(0).longitude;
        for (int i = 1; i < locations.size(); i++) {
            final double latitude = locations.get(i).latitude;
            final double longitude = locations.get(i).longitude;
            listY.add(calculateYSegment(latitudeRef, latitude, circumference));
            Log.d(LOG_TAG, String.format("Y %s: %s", listY.size() - 1, listY.get(listY.size() - 1)));
            listX.add(calculateXSegment(longitudeRef, longitude, latitude, circumference));
            Log.d(LOG_TAG, String.format("X %s: %s", listX.size() - 1, listX.get(listX.size() - 1)));
        }

        // calculate areas for each triangle segment
        for (int i = 1; i < listX.size(); i++) {
            final double x1 = listX.get(i - 1);
            final double y1 = listY.get(i - 1);
            final double x2 = listX.get(i);
            final double y2 = listY.get(i);
            listArea.add(calculateAreaInSquareMeters(x1, x2, y1, y2));
            Log.d(LOG_TAG, String.format("area %s: %s", listArea.size() - 1, listArea.get(listArea.size() - 1)));
        }

        // sum areas of all triangle segments
        double areasSum = 0;
        for (final Double area : listArea) {
            areasSum = areasSum + area;
        }

        // get abolute value of area, it can't be negative
        return Math.abs(areasSum);// Math.sqrt(areasSum * areasSum);
    }

    private static Double calculateAreaInSquareMeters(final double x1, final double x2, final double y1, final double y2) {
        return (y1 * x2 - x1 * y2) / 2;
    }

    private static double calculateYSegment(final double latitudeRef, final double latitude, final double circumference) {
        return (latitude - latitudeRef) * circumference / 360.0;
    }

    private static double calculateXSegment(final double longitudeRef, final double longitude, final double latitude,
                                            final double circumference) {
        return (longitude - longitudeRef) * circumference * Math.cos(Math.toRadians(latitude)) / 360.0;
    }

    /**
     * Return an ArrayList of LatLng points on a String separated by {@Constants.POLYGON_POINTS_SEPARATOR}
     */
    public static ArrayList<LatLng> parsePoints(String pointsStr) {
        ArrayList<LatLng> points = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(pointsStr, Constants.POLYGON_POINTS_SEPARATOR);

        while(tokenizer.hasMoreElements()) {
            String latlng = (String) tokenizer.nextElement();

            if (latlng.length() > 0) {
                points.add(parse(latlng));
            }
        }

        return points;
    }

    /**
     * Return a String with all points of an ArrayList<LatLng> separated by {@Constants.POLYGON_POINTS_SEPARATOR}
     */
    public static String parsePoints(ArrayList<LatLng> points) {
        StringBuilder strBuilder = new StringBuilder();

        for (LatLng point : points) {
            strBuilder.append(point.latitude + ","+ point.longitude + Constants.POLYGON_POINTS_SEPARATOR);
        }

        return strBuilder.toString();
    }


    private static LatLng parse(String latlng) {
        int pos = latlng.indexOf(",");
        double lat = Double.valueOf(latlng.substring(0, pos));
        double lng = Double.valueOf(latlng.substring(pos+1));
        return new LatLng(lat, lng);
    }

    public static LatLng calculateCentroid(ArrayList<LatLng> points) {
        double x = 0, y = 0;
        int count = points.size();

        for (LatLng point : points) {
            x += point.latitude;
            y += point.longitude;
        }

        return new LatLng(x/count, y/count);
    }

    public static void moveMapCamera(GoogleMap googleMap, ArrayList<LatLng> polygonPoints) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : polygonPoints) {
            builder.include(point);
        }
        LatLngBounds bounds = builder.build();

        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, DEFAULT_PADDING));
    }
}
