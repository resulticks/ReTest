package io.mob.resu.reandroidsdk;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ReClassificationAdapter extends ReBaseRecyclerAdapter<ClassificationModel, ReClassificationViewHolder> {

    // Caution: we are not supposed to bring context inside the adapter at any cause;
    public ReClassifications listener;

    public ReClassificationAdapter(ArrayList<ClassificationModel> data, ReClassifications listener) {
        super(data);
        this.listener = listener;
    }

    @Override
    public ReClassificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReClassificationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.re_classifications, parent, false), listener);
    }
}
