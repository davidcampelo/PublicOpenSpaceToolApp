package org.davidcampelo.post.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;

import java.util.Locale;

/**
 * Created by davidcampelo on 7/26/16.
 */
public class PublicOpenSpace {

    // All attributes are public for foot-print reasons :)
    public long   id = 0;
    public String name;
    public long   dateCreation;

    public Questions questions;

    PublicOpenSpace() {
        this.questions = Questions.parse(null);
    }

    // Constructor used by the DAO
    PublicOpenSpace(long id, String name, long dateCreation) {
        this.id = id;
        this.name = name;
        this.dateCreation = dateCreation;
    }

    public PublicOpenSpace(String name, String address, double latitude, double longitude) {
        this(0, name, 0);
    }


}
