package com.example.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.item.ItemRelated;
import com.example.util.Constant;
import com.squareup.picasso.Picasso;
import com.viaviapp.allinonevideosapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RelatedGridAdapter extends ArrayAdapter<ItemRelated> {


    private Activity activity;
    private List<ItemRelated> itemsLatest;
    private ItemRelated objLatestBean;
    private int row;
    private ArrayList<ItemRelated> arraylist;

    public RelatedGridAdapter(Activity act, int resource, List<ItemRelated> arrayList, int columnWidth) {
        super(act, resource, arrayList);
        this.activity = act;
        this.row = resource;
        this.itemsLatest = arrayList;
        this.arraylist = new ArrayList<ItemRelated>();
        this.arraylist.addAll(itemsLatest);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(row, null);

            holder = new ViewHolder();
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if ((itemsLatest == null) || ((position + 1) > itemsLatest.size()))
            return view;

        objLatestBean = itemsLatest.get(position);

        holder.imgv_latetst = (ImageView) view.findViewById(R.id.image);
        holder.name = (TextView) view.findViewById(R.id.text);
        holder.txt_time = (TextView) view.findViewById(R.id.textView2);
        holder.txt_category = (TextView) view.findViewById(R.id.textcatname);

        if (objLatestBean.getRVideoType().equals("local")) {
            Picasso.with(activity).load(objLatestBean.getRImageUrl()).placeholder(R.drawable.placeholder).into(holder.imgv_latetst);
        } else if (objLatestBean.getRVideoType().equals("server_url")) {
            Picasso.with(activity).load(objLatestBean.getRImageUrl()).placeholder(R.drawable.placeholder).into(holder.imgv_latetst);
        } else if (objLatestBean.getRVideoType().equals("youtube")) {
            Picasso.with(activity).load(Constant.YOUTUBE_IMAGE_FRONT + objLatestBean.getRVideoId() + Constant.YOUTUBE_SMALL_IMAGE_BACK).placeholder(R.drawable.placeholder).into(holder.imgv_latetst);
        } else if (objLatestBean.getRVideoType().equals("dailymotion")) {
            Picasso.with(activity).load(Constant.DAILYMOTION_IMAGE_PATH + objLatestBean.getRVideoId()).placeholder(R.drawable.placeholder).into(holder.imgv_latetst);
        } else if (objLatestBean.getRVideoType().equals("vimeo")) {
            Picasso.with(activity).load("" + objLatestBean.getRImageUrl().toString()).placeholder(R.drawable.placeholder).into(holder.imgv_latetst);
        }

        holder.name.setText(objLatestBean.getRVideoName().toString());
        holder.txt_time.setText("(" + objLatestBean.getRDuration().toString() + ")");
        holder.txt_category.setText(objLatestBean.getRCategoryName().toString());
         return view;

    }

    public class ViewHolder {

        public ImageView imgv_latetst;
        public TextView name, txt_time, txt_category;

    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        itemsLatest.clear();
        if (charText.length() == 0) {
            itemsLatest.addAll(arraylist);
        } else {
            for (ItemRelated wp : arraylist) {
                if (wp.getRVideoName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    itemsLatest.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
