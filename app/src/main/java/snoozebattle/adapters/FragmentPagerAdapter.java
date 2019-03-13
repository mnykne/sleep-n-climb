/*
 * Author: nykm
 * Notes : This file was derived from the source code I wrote during
 *         Distributed Systems Course Work (Spring 2017)
 */

package snoozebattle.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
public class FragmentPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG =
            FragmentPagerAdapter.class.getSimpleName();

    private final List<Page> mPages = new ArrayList<>();

    private Context mContext;

    public FragmentPagerAdapter(
            @NonNull Context context,
            @NonNull FragmentManager fragmentManager) {
        super(fragmentManager);
        mContext = context;
    }

    public void addPage(Page page) {
        mPages.add(page);
    }

    public Page getPage(int position) {
        return mPages.get(position);
    }

    public Page removePage(int position) {
        return mPages.remove(position);
    }

    public void removeAllPages() {
        mPages.clear();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Page page = mPages.get(position);
        if (page != null && page.getTitle() != 0) {
            return mContext.getString(page.getTitle());
        } else {
            return "Tab " + position;
        }
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        Page page = mPages.get(position);
        if (page != null && page.getFragmentClass() != null) {
            Class<?> cls = page.getFragmentClass();
            try {
                fragment = Fragment.instantiate(mContext, cls.getName(),
                        page.getFragmentArguments());
            } catch (final Exception exception) {
                Log.e(TAG, "Failed to instantiate fragment "
                        + "at position: " + position + " ("
                        + cls.getName() + ")");
            }
        } else {
            Log.e(TAG, "Failed to instantiate fragment "
                    + "at position: " + position);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return mPages.size();
    }

    public static class Page {
        private int mTitle;
        private Class<?> mFragmentClass;
        private Bundle mFragmentArguments;

        public Page setFragmentArguments(Bundle arguments) {
            mFragmentArguments = arguments;
            return this;
        }

        public Bundle getFragmentArguments() {
            return mFragmentArguments;
        }

        public Page setTitle(@StringRes int title) {
            mTitle = title;
            return this;
        }

        public int getTitle() {
            return mTitle;
        }

        public Page setFragmentClass(Class<?> fragmentClass) {
            mFragmentClass = fragmentClass;
            return this;
        }

        public Class<?> getFragmentClass() {
            return mFragmentClass;
        }
    }
}