package org.davidcampelo.post;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.davidcampelo.post.model.PublicOpenSpace;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by davidcampelo on 7/26/16.
 */
class PublicOpenSpaceListAdapter extends ArrayAdapter<PublicOpenSpace> {

    Calendar calendar;
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy");
    // Utility class to keep list row values
    public static class ViewHolder{
        TextView name;
        TextView address;
        ImageView image;
    }

    public PublicOpenSpaceListAdapter(Context context, ArrayList<PublicOpenSpace> list) {
        super(context, 0, list);
        calendar = Calendar.getInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PublicOpenSpace object = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null){

            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_public_open_space_list_row, parent, false);

            viewHolder.name = (TextView) convertView.findViewById(R.id.listItemName);
            viewHolder.address = (TextView) convertView.findViewById(R.id.listItemAddress);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.listItemType);

            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(object.getName());
        viewHolder.address.setText("Added on "+ simpleDateFormat.format(new Date(object.getDateCreation())));
        viewHolder.image.setImageResource(object.getTypeResource());


        return convertView;
    }
}
