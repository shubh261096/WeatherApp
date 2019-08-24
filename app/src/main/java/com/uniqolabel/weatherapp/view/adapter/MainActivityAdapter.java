package com.uniqolabel.weatherapp.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uniqolabel.weatherapp.R;
import com.uniqolabel.weatherapp.service.model.ForecastdayItem;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.uniqolabel.weatherapp.utils.CommonUtils.getDayFromDate;


public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ViewHolder> {

    private final List<ForecastdayItem> forecastdayItemList;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.conditionIcon)
        ImageView conditionIcon;
        @BindView(R.id.conditionText)
        TextView conditionText;
        @BindView(R.id.forecastDate)
        TextView forecastDate;
        @BindView(R.id.avgTempC)
        TextView avgTempC;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public MainActivityAdapter(List<ForecastdayItem> forecastdayItemList, Context context) {
        this.forecastdayItemList = forecastdayItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_forecast, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        ForecastdayItem forecastdayItem = getItem(position);
        if (!TextUtils.isEmpty(forecastdayItem.getDate())) {
            viewHolder.forecastDate.setText(getDayFromDate(forecastdayItem.getDate()));
        }
        viewHolder.avgTempC.setText(String.format("%s%s", forecastdayItem.getDay().getAvgtempC(), context.getString(R.string.degree_celcius)));

        if (!TextUtils.isEmpty(forecastdayItem.getDay().getCondition().getIcon())) {
            Picasso.get()
                    .load("http:" + forecastdayItem.getDay().getCondition().getIcon())
                    .into(viewHolder.conditionIcon);
        }
        if (!TextUtils.isEmpty(forecastdayItem.getDay().getCondition().getText())) {
            viewHolder.conditionText.setText(forecastdayItem.getDay().getCondition().getText());
        }
    }

    private ForecastdayItem getItem(int position) {
        return forecastdayItemList.get(position);
    }

    @Override
    public int getItemCount() {
        return forecastdayItemList.size();
    }

    public void clearData() {
        forecastdayItemList.clear();
        notifyDataSetChanged();
    }

}