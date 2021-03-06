package org.davidcampelo.post;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.content.res.AppCompatResources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

    int bgColorOn;
    int bgColorOff;

    // Date format to show date on list
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy");
    // Utility class to keep list row values
    public static class ViewHolder {
        TextView name;
        TextView date;
        ImageView image;
        RelativeLayout container;
    }

    public ProjectListAdapter(Context context, ArrayList<Project> list) {
        super(context, 0, list);

        bgColorOn = ContextCompat.getColor(getContext(), R.color.colorPrimaryLight);
        bgColorOff= ContextCompat.getColor(getContext(), R.color.colorWhite);
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
            viewHolder.name = (TextView) convertView.findViewById(R.id.listItemName);
            if (nameSize != -1) {
                viewHolder.name.setTextAppearance(getContext(), nameSize);
            }
            viewHolder.date = (TextView) convertView.findViewById(R.id.listItemDate);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.listItemType);
            viewHolder.container = (RelativeLayout) convertView.findViewById(R.id.listAdapterContainer);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (position % 2 == 0) {
            viewHolder.container.setBackgroundColor(bgColorOn);
        }
        else
        {
            viewHolder.container.setBackgroundColor(bgColorOff);
        }

        viewHolder.name.setText(object.getName());
        if (showDate)
            viewHolder.date.setText("Added on "+ simpleDateFormat.format(new Date(object.getDateCreation())));
        else {
            viewHolder.name.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            viewHolder.date.setVisibility(View.INVISIBLE);
        }


        return convertView;
    }
}
