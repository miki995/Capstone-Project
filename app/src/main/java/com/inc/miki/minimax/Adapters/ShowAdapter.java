package com.inc.miki.minimax.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.inc.miki.minimax.R;

import java.util.ArrayList;

import com.inc.miki.minimax.Objects.Show;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowAdapter extends ArrayAdapter<Show> {

    public ShowAdapter(Context context, ArrayList<Show> shows) {
        super(context, 0, shows);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder;

        View listItemView = convertView;
        if (listItemView != null) {
            holder = (ViewHolder) listItemView.getTag();
        } else {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.show_item, parent, false);
            holder = new ViewHolder(listItemView);
            listItemView.setTag(holder);

        }

        Show currentShow = getItem(position);
        if (currentShow != null) {
            Picasso.get().load(currentShow.getImage()).into(holder.showImage);
        }

        return listItemView;
    }

    static final class ViewHolder {
        @BindView(R.id.showLogo)
        ImageView showImage;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
