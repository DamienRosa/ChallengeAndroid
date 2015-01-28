package com.example.damien.challengeandroidwear.searchinstagramtags;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.damien.challengeandroidwear.R;
import com.example.damien.challengeandroidwear.searchinstagramtags.LazyImageLoader.ImageLoader;

import java.util.ArrayList;

public class CustomListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    ImageLoader imageLoader;
    private ArrayList<InstaObject> listObjects;

    public CustomListAdapter(Context context, ArrayList<InstaObject> importedObjects) {
        this.listObjects = importedObjects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(context.getApplicationContext());
    }

    @Override
    public int getCount() {
        return listObjects.size();
    }

    @Override
    public Object getItem(int i) {
        return listObjects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View newView = view;
        if (view == null) {
            newView = inflater.inflate(R.layout.show_object, null);
        }

        ImageView image = (ImageView) newView.findViewById(R.id.image_obj);
        imageLoader.displayImage(listObjects.get(i).getImageURL(), image);

        TextView username = (TextView) newView.findViewById(R.id.user_name_text_view);
        username.setText(listObjects.get(i).getUsername());

        TextView text = (TextView) newView.findViewById(R.id.text_text_view);
        text.setText(listObjects.get(i).getText());

        return newView;
    }
}
