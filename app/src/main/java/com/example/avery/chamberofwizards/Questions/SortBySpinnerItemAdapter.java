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

import java.util.ArrayList;

public class SortBySpinnerItemAdapter  extends ArrayAdapter<SortBySpinnerItem> {

    public SortBySpinnerItemAdapter(Context context, ArrayList<SortBySpinnerItem> spinnerItems) {
        super(context, 0, spinnerItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView,  @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
           convertView = LayoutInflater.from(getContext()).inflate(R.layout.click_question_sort_by_layout, parent, false);
        }

        ImageView imageView= convertView.findViewById(R.id.sort_by_category_icon);
        TextView textView = convertView.findViewById(R.id.sort_by_category_name);

        SortBySpinnerItem sortBySpinnerItem = getItem(position);

        if(sortBySpinnerItem != null) {
            imageView.setImageResource(sortBySpinnerItem.getmFlagImage());
            textView.setText(sortBySpinnerItem.getmCategoryName());
        }

        return convertView;
    }
}
