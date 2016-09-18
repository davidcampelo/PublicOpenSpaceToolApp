package org.davidcampelo.post.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import org.davidcampelo.post.R;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import java.util.Locale;

/**
 * Created by davidcampelo on 7/26/16.
 */
public class PublicOpenSpace implements Parcelable {

    // All attributes are public for foot-print reasons :)
    long id = 0;
    String name;
    Type type = Type.PARK;
    ArrayList<LatLng> polygonPoints;
    Project project;
    long dateCreation;

    public enum Type {
        PARK,
        SQUARE,
        GARDEN,
        OTHER
    }

    public PublicOpenSpace() {
    }

    protected PublicOpenSpace(Parcel in) {
        id = in.readLong();
        name = in.readString();
        polygonPoints = in.createTypedArrayList(LatLng.CREATOR);
        project = in.readParcelable(Project.class.getClassLoader());
        dateCreation = in.readLong();
    }

    public static final Creator<PublicOpenSpace> CREATOR = new Creator<PublicOpenSpace>() {
        @Override
        public PublicOpenSpace createFromParcel(Parcel in) {
            return new PublicOpenSpace(in);
        }

        @Override
        public PublicOpenSpace[] newArray(int size) {
            return new PublicOpenSpace[size];
        }
    };

    public ArrayList<LatLng> getPolygonPoints() {
        return polygonPoints;
    }

    // Constructor used by the DAO
    PublicOpenSpace(long id, String name, Type type, ArrayList<LatLng> polygonPoints, Project project, long dateCreation) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.polygonPoints = polygonPoints;
        this.project = project;
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

    public void setProject(Project project) {
        this.project = project;
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

    public Project getProject() {
        return project;
    }

    public long getDateCreation() {
        return dateCreation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeInt(type.ordinal());
        parcel.writeTypedList(polygonPoints);
        parcel.writeValue(project);
        parcel.writeLong(dateCreation);
    }

}