package org.davidcampelo.post.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by davidcampelo on 8/18/16.
 */
public class Project implements Parcelable {

    long id = 0;
    String name;
    String desc;
    ArrayList<LatLng> polygonPoints;
    ArrayList<PublicOpenSpace> publicOpenSpaces;
    long dateCreation;

    public Project(String name, String desc, ArrayList<LatLng> polygonPoints) {
        this(0, name, desc, polygonPoints, null, 0);
    }

    Project(long id, String name, String desc, ArrayList<LatLng> polygonPoints, ArrayList<PublicOpenSpace> publicOpenSpaces, long dateCreation) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.polygonPoints = polygonPoints;
        this.publicOpenSpaces = publicOpenSpaces;
        this.dateCreation = dateCreation;
    }

    public Project(long id) {
        this(id, null, null, null, null, 0);
    }

    public Project() {

    }

    protected Project(Parcel in) {
        id = in.readLong();
        name = in.readString();
        desc = in.readString();
        polygonPoints = in.createTypedArrayList(LatLng.CREATOR);
        publicOpenSpaces = in.createTypedArrayList(PublicOpenSpace.CREATOR);
        dateCreation = in.readLong();
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public ArrayList<LatLng> getPolygonPoints() {
        return polygonPoints;
    }

    public ArrayList<PublicOpenSpace> getPublicOpenSpaces() {
        return publicOpenSpaces;
    }

    public long getDateCreation() {
        return dateCreation;
    }

    public void setPolygonPoints(ArrayList<LatLng> polygonPoints) {
        this.polygonPoints = polygonPoints;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeString(desc);
        parcel.writeTypedList(polygonPoints);
        parcel.writeTypedList(publicOpenSpaces);
        parcel.writeLong(dateCreation);
    }
}
