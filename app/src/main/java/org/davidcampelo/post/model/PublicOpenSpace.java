package org.davidcampelo.post.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import org.davidcampelo.post.R;

import java.io.IOException;
import java.util.List;

import java.util.Locale;

/**
 * Created by davidcampelo on 7/26/16.
 */
public class PublicOpenSpace {

    // All attributes are public for foot-print reasons :)
    public long id = 0;
    public String name;
    public Type type = Type.PARK;
    public long dateCreation;

    public enum Type {
        PARK,
        SQUARE,
        GARDEN,
        OTHER
    }

    ;

    public PublicOpenSpace() {
    }

    // Constructor used by the DAO
    PublicOpenSpace(long id, String name, Type type, long dateCreation) {
        this.id = id;
        this.name = name;
        this.type = type;
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
}