package org.davidcampelo.post.model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by davidcampelo on 8/18/16.
 */
public class Project implements Serializable {

    long id = 0;
    String name;
    String desc;
    ArrayList<LatLng> polygonPoints;
    ArrayList<PublicOpenSpace> publicOpenSpaces;
    long dateCreation;

    public Project(long id, String name, String desc, ArrayList<LatLng> polygonPoints, ArrayList<PublicOpenSpace> publicOpenSpaces, long dateCreation) {
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
}
