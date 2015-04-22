package com.totoro.commons.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import in.srain.cube.util.CLog;

/**
 * 抽象适配器
 * Created by Chen on 14-11-15.
 */
public abstract class AbstractAdapter<T, Holder extends BaseViewHolder> extends BaseAdapter {

    private static final String TAG = AbstractAdapter.class.getSimpleName();

    private List<T> data;
    private LayoutInflater mInflater;
    private int itemLayoutId;
    private Constructor<? extends BaseViewHolder> constructor;

    public AbstractAdapter(Context mContext, Class<? extends BaseViewHolder> clazz, int itemLayoutId) {
        data = new ArrayList<T>();
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.itemLayoutId = itemLayoutId;
        try {
            constructor = clazz.getConstructor(View.class);
        } catch (NoSuchMethodException e) {
            CLog.e(TAG, e.getMessage(), e.fillInStackTrace());
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(itemLayoutId, null);
            try {
                holder = (Holder) constructor.newInstance(convertView);
                convertView.setTag(holder);
            } catch (Exception e) {
                CLog.e(TAG, e.getMessage(), e.fillInStackTrace());
            }
        } else {
            holder = (Holder) convertView.getTag();
        }

        getItemView(position, holder);

        return convertView;
    }

    public void setData(List<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return data;
    }

    public void clearData() {
        this.data.clear();
        notifyDataSetChanged();
    }

    public abstract void getItemView(int position, Holder viewHolder);

}
