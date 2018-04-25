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

import com.inc.miki.minimax.Objects.Host;
import com.squareup.picasso.Picasso;

import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HostAdapter extends ArrayAdapter<Host> {

    public HostAdapter(Context context, ArrayList<Host> hosts) {
        super(context, 0, hosts);
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
                    R.layout.host_item, parent, false);
            holder = new ViewHolder(listItemView);
            listItemView.setTag(holder);

        }

        Host currentHost = getItem(position);

        if (currentHost != null) {
            holder.titleTV.setText(currentHost.getName());
            Picasso.get().load(currentHost.getImage()).into(holder.hostImage);
        }
        return listItemView;
    }

    static final class ViewHolder {
        @BindView(R.id.hostName)
        TextView titleTV;
        @BindView(R.id.hostImage)
        ImageView hostImage;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
