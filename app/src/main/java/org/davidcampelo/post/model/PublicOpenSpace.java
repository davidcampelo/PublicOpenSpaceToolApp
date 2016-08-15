package org.davidcampelo.post.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import org.davidcampelo.post.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.Locale;

/**
 * Created by davidcampelo on 7/26/16.
 */
public class PublicOpenSpace {

    // All attributes are public for foot-print reasons :)
    long id = 0;
    String name;
    Type type = Type.PARK;
    ArrayList<LatLng> polygonPoints;
    long dateCreation;

    public enum Type {
        PARK,
        SQUARE,
        GARDEN,
        OTHER
    }

    ;

    public PublicOpenSpace() {
    }

    public ArrayList<LatLng> getPolygonPoints() {
        return polygonPoints;
    }

    // Constructor used by the DAO
    PublicOpenSpace(long id, String name, Type type, ArrayList<LatLng> polygonPoints, long dateCreation) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.polygonPoints = polygonPoints;

        this.dateCreation = dateCreation;
    }

    public int getTypeResource() {
        switch (this.type) {
            case PARK:
                return R.drawable.icon_park;
            case SQUARE:
                return R.drawable.icon_square;
            case GARDEN:
                return R.drawable.icon_garden;
            default:
                return R.drawable.icon_other;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPolygonPoints(ArrayList<LatLng> polygonPoints) {
        this.polygonPoints = polygonPoints;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public long getDateCreation() {
        return dateCreation;
    }
}