package io.mob.resu.reandroidsdk;

import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReClassificationViewHolder extends ReBaseViewHolder<ClassificationModel> implements View.OnClickListener {

    ReClassifications listener;
    TextView textView;
    LinearLayout buttonPanel;

    public ReClassificationViewHolder(View itemView) {
        super(itemView);
        bindHolder();
    }

    public ReClassificationViewHolder(View itemView, ReClassifications listener) {
        super(itemView);
        this.listener = listener;
        bindHolder();
    }

    private void bindHolder() {
        try {
            textView = itemView.findViewById(R.id.tv_classification);
            buttonPanel = itemView.findViewById(R.id.buttonPanel);
            buttonPanel.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        listener.itemClick(data);
    }

    @Override
    void populateData() {
        try {
            if (data.isSelected()) {
                textView.setTextColor(Color.parseColor("#FFFFFF"));
                buttonPanel.setBackgroundColor(Color.parseColor("#5B7ADF"));
            } else {
                textView.setTextColor(Color.parseColor("#5B7ADF"));
                buttonPanel.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
            textView.setText(data.getChannelName());
        } catch (Exception e) {

        }

    }
}
