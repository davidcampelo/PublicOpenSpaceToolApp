package org.davidcampelo.post.utils;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by davidcampelo on 7/26/16.
 */
public class Constants {

    public static final String DATABASE_NAME = "post.db";
    public static final int    DATABASE_VERSION = 51;

    public static final String[] TAB_TITLES = new String[]{
            "Location",
            "General",
            "Activities",
            "Environmental quality",
            "Amenities",
            "Safety"
    };
    // At least 70% questions must be answered
    public static final double MINIMUM_QUESTIONS_TO_BE_ANSWERED = 0.5f;
    // If a given Question is not enabled (as a previous Option was marked)
    public static final String DEFAULT_NOT_ENABLED_QUESTION = "-1";
    // Date format used to show date in the app
    public static SimpleDateFormat APPLICATION_DATE_FORMAT = new SimpleDateFormat("dd/MMM/yyyy");

    // Decimal configurations used in the Question 3 (POS area)
    public static final char NUMBER_DECIMAL_SEPARATOR = '.';
    public static final String NUMBER_DECIMAL_FORMAT = "#,###,###,##0.00";
    public static final char NUMBER_GROUP_SEPARATOR = ',';

    public static final String DEFAULT_SEPARATOR = "|";
    public static final String CSV_SEPARATOR = ";";
    public static final String POLYGON_COORDINATES_SEPARATOR = ",";
    public static final String POLYGON_POINTS_SEPARATOR = " ";
    public static final ArrayList<LatLng> PORTUGAL_BOUNDING_POINTS;
    // The "other option" (an Option created by the user) item must always have this substring (e.g. 8.other_act)
    public static final String MULTIPLE_QUESTION_OTHER_START_STRING = ".other";
    // Custom Option objects added by user (using the "other" option) will have the same alias
    public static String OPTION_ALIAS_ADDED_BY_USER = "999.added_by_user";
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
