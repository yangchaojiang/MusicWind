package com.data.user.apapter


/**
 * Created by yangc on 2017/6/10.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */
class MyFragmentPagerAdapter(context: android.content.Context, mData: MutableList<android.support.v4.app.Fragment>, fa: android.support.v4.app.FragmentManager) : android.support.v4.app.FragmentPagerAdapter(fa) {
    var list = mData
    var mContext = context
    override fun getItem(position: Int): android.support.v4.app.Fragment {
        return  list[position]
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return mContext.resources.getStringArray(com.data.user.R.array.dataList)[position]
    }

}