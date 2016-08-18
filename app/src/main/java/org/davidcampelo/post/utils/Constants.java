package org.davidcampelo.post.utils;

/**
 * Created by davidcampelo on 7/26/16.
 */
public class Constants {

    public static final String DATABASE_NAME = "post.db";
    public static final int    DATABASE_VERSION = 35;

    public static final double PORTO_LATITUDE = 41.14553741401715;  // TODO use my location
    public static final double PORTO_LONGITUDE = -8.616052977740765;
    public static final String[] TAB_TITLES = new String[]{ "Location", "General", "Activities", "Environmental quality", "Amenities", "Safety"};
    public static final String MULTIPLE_OPTIONS_SEPARATOR = "&**&";
    public static final String POLYGON_POINTS_SEPARATOR = "*";
    ;

    public enum FragmentAction {
        VIEW,
        EDIT,
        ADD
    };

    public static final String PUBLIC_OPEN_SPACE_EXTRA = "org.davidcampelo.post.PUBLIC_OPEN_SPACE_EXTRA";
    public static final String PROJECT_EXTRA = "org.davidcampelo.post.PROJECT_EXTRA";

}
