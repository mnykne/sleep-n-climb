package snoozebattle.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import snoozebattle.R;
import snoozebattle.adapters.FragmentPagerAdapter;
import snoozebattle.fragments.AchievementsFragment;
import snoozebattle.fragments.AlarmListFragment;
import snoozebattle.fragments.StatisticsFragment;

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
                .setFragmentClass(AchievementsFragment.class));
        mFragmentPagerAdapter.addPage(new FragmentPagerAdapter.Page()
                .setFragmentClass(StatisticsFragment.class));

        mViewPager.setAdapter(mFragmentPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        // TODO CLEANUP
        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_alarm);
        mTabLayout.getTabAt(0).getIcon().setTint(getColor(R.color.colorActionBarButtonText));
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_achievements);
        mTabLayout.getTabAt(1).getIcon().setTint(getColor(R.color.colorActionBarButtonText));
        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_statistics);
        mTabLayout.getTabAt(2).getIcon().setTint(getColor(R.color.colorActionBarButtonText));
        mTabLayout.getTabAt(0).setText("");
        mTabLayout.getTabAt(1).setText("");
        mTabLayout.getTabAt(2).setText("");

        getSupportActionBar().hide();
    }
}
