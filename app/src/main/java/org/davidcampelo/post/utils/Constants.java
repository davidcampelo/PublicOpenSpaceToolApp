package org.davidcampelo.post.utils;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by davidcampelo on 7/26/16.
 */
public class Constants {

    public static final String DATABASE_NAME = "post.db";
    public static final int    DATABASE_VERSION = 49;

    public static final String[] TAB_TITLES = new String[]{
            "Location",
            "General",
            "Activities",
            "Environmental quality",
            "Amenities",
            "Safety"
    };
    public static final String DEFAULT_SEPARATOR = "|";
    public static final String CSV_SEPARATOR = ";";
    public static final String POLYGON_COORDINATES_SEPARATOR = ",";
    public static final String POLYGON_POINTS_SEPARATOR = " ";
    public static final ArrayList<LatLng> PORTUGAL_BOUNDING_POINTS;
    public static final int LIST_ROW_COLOR = Color.argb(150, 240,240,240);
    // The "other option" (an Option created by the user) item must always have this substring (e.g. 8.other_act)
    public static final String MULTIPLE_QUESTION_OTHER_START_STRING = ".other";
    // In the CSV header file very "other column" has a correspondent "other column" with the suffix below
    public static final String MULTIPLE_QUESTION_OTHER_CSV_SUFFIX = "_l";
    public static final  int NUMBER_OF_QUESTIONS = 53;
    public static final  int NUMBER_OF_OPTIONS = 169;

    static {
        PORTUGAL_BOUNDING_POINTS = new ArrayList<LatLng>();
        PORTUGAL_BOUNDING_POINTS.add(new LatLng(42.076823, -8.958980));
        PORTUGAL_BOUNDING_POINTS.add(new LatLng(36.995029, -7.364849));
    }
    ;

    public enum FragmentAction {
        VIEW,
        EDIT,
        ADD
    };

    public static final String PUBLIC_OPEN_SPACE_EXTRA = "org.davidcampelo.post.PUBLIC_OPEN_SPACE_EXTRA";
    public static final String PROJECT_EXTRA = "org.davidcampelo.post.PROJECT_EXTRA";

}
