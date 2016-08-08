package org.davidcampelo.post.utils;

/**
 * Created by davidcampelo on 7/26/16.
 */
public class Constants {

    public static final String DATABASE_NAME = "post.db";
    public static final int    DATABASE_VERSION = 23;

    public static final double PORTO_LATITUDE = 41.14553741401715;
    public static final double PORTO_LONGITUDE = -8.616052977740765;
    public static final String[] TAB_TITLES = new String[]{"General", "Localization", "Activities", "Environmental quality", "Amenities", "Safety"};
    public static final String QUESTIONS_SEPARATOR = ",";
    ;

    public enum FragmentAction {
        VIEW,
        EDIT,
        ADD
    };

    public static final String INTENT_ID_EXTRA = "org.davidcampelo.post.INTENT_ID_EXTRA";
    public static final String INTENT_ACTION_EXTRA = "org.davidcampelo.post.INTENT_ACTION_EXTRA";

}
