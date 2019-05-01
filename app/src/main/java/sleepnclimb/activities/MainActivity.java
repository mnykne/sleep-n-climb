package sleepnclimb.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import sleepnclimb.R;
import sleepnclimb.adapters.FragmentPagerAdapter;
import sleepnclimb.fragments.StatusFragment;
import sleepnclimb.fragments.AlarmListFragment;
import sleepnclimb.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private FragmentPagerAdapter mFragmentPagerAdapter;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.view_pager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        mFragmentPagerAdapter = new FragmentPagerAdapter(this, fragmentManager);
        mFragmentPagerAdapter.addPage(new FragmentPagerAdapter.Page()
                .setFragmentClass(AlarmListFragment.class));
        mFragmentPagerAdapter.addPage(new FragmentPagerAdapter.Page()
                .setFragmentClass(StatusFragment.class));
        mFragmentPagerAdapter.addPage(new FragmentPagerAdapter.Page()
                .setFragmentClass(SettingsFragment.class));
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        int[] drawables = {
            R.drawable.ic_alarm,
            R.drawable.ic_status,
            R.drawable.ic_settings
        };
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(drawables[i]);
            imageView.setAlpha(0.5f);
            mTabLayout.getTabAt(i).setCustomView(imageView);
            mTabLayout.getTabAt(i).setText("");
        }
        mTabLayout.getTabAt(0).getCustomView().setAlpha(1);
        getSupportActionBar().hide();
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getCustomView() != null) {
                    tab.getCustomView().setAlpha(1);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getCustomView() != null) {
                    tab.getCustomView().setAlpha(0.5f);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getCustomView() != null) {
                    tab.getCustomView().setAlpha(1);
                }
            }
        });
    }
}
