package org.davidcampelo.post;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.davidcampelo.post.model.Project;
import org.davidcampelo.post.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Inner class for handling list adapter rows
 */
class ProjectListAdapter extends ArrayAdapter<Project> {

    // Date format to show date on list
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy");
    // Utility class to keep list row values
    public static class ViewHolder {
        TextView name;
        TextView date;
        ImageView image;
    }

    public ProjectListAdapter(Context context, ArrayList<Project> list) {
        super(context, 0, list);
    }

    private int nameSize = -1;
    private boolean showDate = true;

    public void setShowDate(boolean showDate) {
        this.showDate = showDate;
    }

    public void setNameSize(int nameSize) {
        this.nameSize = nameSize;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }


    public View getCustomView(int position, View convertView, ViewGroup parent) {
        Project object = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null){

            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_project_list_row, parent, false);
            if (position % 2 == 0) {
                convertView.setBackgroundColor(Constants.LIST_ROW_COLOR);
            }
            viewHolder.name = (TextView) convertView.findViewById(R.id.listItemName);
            if (nameSize != -1)
                viewHolder.name.setTextAppearance(getContext(), nameSize);

            viewHolder.date = (TextView) convertView.findViewById(R.id.listItemDate);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.listItemType);


            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(object.getName());
        if (showDate)
            viewHolder.date.setText("Added on "+ simpleDateFormat.format(new Date(object.getDateCreation())));


        return convertView;
    }
}
