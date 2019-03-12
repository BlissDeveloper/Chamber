package com.example.avery.chamberofwizards.Questions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.avery.chamberofwizards.R;

import java.net.ContentHandler;
import java.util.ArrayList;
import java.util.Locale;

public class CategoryAdapter extends ArrayAdapter<CategoryItem> {

    public CategoryAdapter(Context context, ArrayList<CategoryItem> categoryItems) {
        super(context, 0, categoryItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_spinner_row, parent, false);
        }
        ImageView imageViewFlag = convertView.findViewById(R.id.category_math);
        TextView textViewName = convertView.findViewById(R.id.category_name);

        CategoryItem currentItem = getItem(position);

        if(currentItem != null) {
            imageViewFlag.setImageResource(currentItem.getmFlagImage());
            textViewName.setText(currentItem.getmCategoryName());
        }

        return convertView;
    }
}
