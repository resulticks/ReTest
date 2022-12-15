package io.mob.resu.reandroidsdk;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Buvaneswaran on 2/23/2017.
 */

public abstract class ReBaseViewHolder<T> extends RecyclerView.ViewHolder {

    public T data;
    String TAG = getClass().getSimpleName();

    ReBaseViewHolder(View itemView) {
        super(itemView);
    }

    public void setData(T data) {
        this.data = data;
        populateData();
    }

    abstract void populateData();
}
