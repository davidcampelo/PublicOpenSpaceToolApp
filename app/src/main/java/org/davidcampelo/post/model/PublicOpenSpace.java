package org.davidcampelo.post.model;

/**
 * Created by davidcampelo on 7/26/16.
 */
public class PublicOpenSpace {

    // All attributes are public for foot-print reasons :)
    public long   id = 0;
    public String name;
    public String address;

    public long dateCreation;

    PublicOpenSpace() {
    }

    // Constructor used by the DAO
    PublicOpenSpace(long id, String name, String address, long dateCreation) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.dateCreation = dateCreation;

    }
    public PublicOpenSpace(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public long getDateCreation() {
        return dateCreation;
    }
}
