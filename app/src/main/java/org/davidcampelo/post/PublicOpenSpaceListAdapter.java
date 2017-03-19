package org.davidcampelo.post;

/**
 * Created by davidcampelo on 3/11/17.
 */

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.davidcampelo.post.model.PublicOpenSpace;
import org.davidcampelo.post.utils.Constants;

import java.util.ArrayList;
import java.util.Date;

/**
 * Inner class for handling list adapter rows
 */
class   PublicOpenSpaceListAdapter extends ArrayAdapter<PublicOpenSpace> {

    int bgColorOn;
    int bgColorOff;

    // Date format to show date on list
    // Utility class to keep list row values
    public static class ViewHolder{
        TextView name;
        TextView date;
        ImageView image;
        RelativeLayout container;
    }

    public PublicOpenSpaceListAdapter(Context context, ArrayList<PublicOpenSpace> list) {
        super(context, 0, list);

        bgColorOn = ContextCompat.getColor(getContext(), R.color.colorPrimaryLight);
        bgColorOff= ContextCompat.getColor(getContext(), R.color.colorWhite);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PublicOpenSpace object = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null){

            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_public_open_space_list_row, parent, false);

            viewHolder.name = (TextView) convertView.findViewById(R.id.listItemName);
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
        viewHolder.date.setText("Added on "+ Constants.APPLICATION_DATE_FORMAT.format(new Date(object.getDateCreation())));
        //viewHolder.image.setImageResource(object.getTypeResource());


        return convertView;
    }
}
