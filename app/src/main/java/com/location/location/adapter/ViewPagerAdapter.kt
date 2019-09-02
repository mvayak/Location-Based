package com.location.location.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.location.location.R

internal class ViewPagerAdapter(private val ctx: Context) : PagerAdapter() {

    override fun getCount(): Int {
        return 3
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(view: ViewGroup, position: Int, `object`: Any) {
        view.removeView(`object` as View)
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {

        val inflater = LayoutInflater.from(ctx)
        val layout = inflater.inflate(
            R.layout.viewpager_first_layout,
            view, false
        ) as ViewGroup

        view.addView(layout)
        return layout
    }

}