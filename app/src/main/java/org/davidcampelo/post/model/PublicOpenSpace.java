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
    public String address;
    public double latitude = Double.MAX_VALUE;
    public double longitude = Double.MAX_VALUE;
    public long   dateCreation;

    public Questions questions;

    PublicOpenSpace() {
        this.questions = Questions.parse(null);
    }

    // Constructor used by the DAO
    PublicOpenSpace(long id, String name, String address, double latitude, double longitude, String strQuestions, long dateCreation) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dateCreation = dateCreation;

        this.questions = Questions.parse(strQuestions);
    }

    public PublicOpenSpace(String name, String address, double latitude, double longitude) {
        this(0, name, address, latitude, longitude, null, 0);
    }

    public String getAnswers(){
        return questions.getAnswers();
    }

    public String getAnswer(Questions.QuestionIdentifier identifier){
        return questions.getAnswer(identifier);
    }

    public void putAnswer(Questions.QuestionIdentifier identifier, String value){
        questions.putAnswer(identifier, value);
    }

    public String resolveAddress(Context ctx) {
        if (latitude == Double.MAX_VALUE || longitude == Double.MAX_VALUE)
            return "";

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(ctx, Locale.getDefault());

        // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            StringBuilder stringBuilder = new StringBuilder();
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            if (address != null)
                stringBuilder.append(address);
            String city = addresses.get(0).getLocality();
            if (city != null)
                stringBuilder.append(", "+ city);
            String postalCode = addresses.get(0).getPostalCode();
            if (postalCode != null)
                stringBuilder.append(" - "+ postalCode);

            return stringBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Address not found!";
    }


}
