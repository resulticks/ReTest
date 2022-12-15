package io.mob.resu.reandroidsdk;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class ReNotificationActivity extends AppCompatActivity {

    RecyclerView mNotificationView;
    TextView mTvNoDataFound;
    SwipeRefreshLayout mParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_notification);
        loadFragment(new ReNotificationFragment());
    }


    public void loadFragment(Fragment mFragment) {
        try {
            if (mFragment != null) {
                FragmentTransaction fragmentTransactiontv = getSupportFragmentManager().beginTransaction();
                fragmentTransactiontv.replace(R.id.fragment_container, mFragment);
                fragmentTransactiontv.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}