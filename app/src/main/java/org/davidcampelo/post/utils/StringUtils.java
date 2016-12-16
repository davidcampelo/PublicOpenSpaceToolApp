package org.davidcampelo.post.utils;

import android.util.Log;

import org.davidcampelo.post.model.PublicOpenSpace;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created by davidcampelo on 12/15/16.
 */
public class StringUtils {

    public static void writeToFile(FileWriter out, String string) throws IOException {
        out.write(string + "\n");
        Log.e("[EXPORT]", string);
    }

    /**
     * Takes a String with MULTIPLE_CHOICE Question's Opition ID answers
     * and return an array of Option IDs
     */
    public static ArrayList<Long> splitIntoOptionIds(String questionAnswers) {
        StringTokenizer tokenizer = new StringTokenizer(questionAnswers, Constants.DEFAULT_SEPARATOR);
        ArrayList<Long> selectedIds = new ArrayList<>();

        while ( tokenizer.hasMoreElements() ) {
            String selectedId = (String) tokenizer.nextElement();
            if (selectedId == null || selectedId.length() == 0)
                continue;
            selectedIds.add(Long.valueOf( selectedId ));
        }

        return selectedIds;
    }

    /**
     * Remove the last character if it's a comma
     */
    public static String toStringWithNoEndingCommas(StringBuilder stringBuilder) {
        String str = stringBuilder.toString();
        if (str.length() > 0 && str.endsWith(",")){
            str = str.substring(0, str.length()-1);
        }

        return str;
    }


    public static String toString(String[] array) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            b.append(array[i]);
            if (i < array.length - 1)
                b.append(", ");
        }

        return b.toString();
    }

    public static String toString2(Set<String> strings) {
        StringBuilder b = new StringBuilder();
        for (String str : strings){
            b.append(str + ", ");
        }
        return b.toString();
    }

    public static String toString(Set<Map.Entry<String, Object>> entries) {
        StringBuilder b = new StringBuilder();
        Iterator<Map.Entry<String, Object>> i = entries.iterator();
        while (i.hasNext()){
            Map.Entry<String, Object> entry = i.next();
            b.append(entry.getValue()+ ", ");
        }
        return b.toString();
    }


}
