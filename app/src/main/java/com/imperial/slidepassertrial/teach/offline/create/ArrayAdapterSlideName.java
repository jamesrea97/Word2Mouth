package com.imperial.slidepassertrial.teach.offline.create;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.imperial.slidepassertrial.R;

import java.io.File;
import java.util.ArrayList;

public class ArrayAdapterSlideName extends ArrayAdapter<String> {

    private static ArrayList<String> slideNames;

    private int layout;
    private Context context;
    public ArrayAdapterSlideName(@NonNull Context context, int resource, @NonNull ArrayList<String> objects) {
        super(context, resource, objects);

        layout = resource;
        slideNames = objects;
        this.context = context;
    }



    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        ArrayAdapterSlideName.ViewHolder mainViewHolder = null;

        File thumbnailFile;
        final File audioFile;
        Uri audioUri = null;

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layout, parent, false);

            ArrayAdapterSlideName.ViewHolder viewhHolder = new ArrayAdapterSlideName.ViewHolder();

            viewhHolder.slideName = convertView.findViewById(R.id.list_item_text);
            viewhHolder.slideName.setText(position + ". " + slideNames.get(position));

            convertView.setTag(viewhHolder);
        }

        mainViewHolder = (ArrayAdapterSlideName.ViewHolder) convertView.getTag();

        return convertView;
    }

    public class ViewHolder {
        TextView slideName;
    }

}
