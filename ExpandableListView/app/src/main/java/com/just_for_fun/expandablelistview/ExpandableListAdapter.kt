package com.just_for_fun.expandablelistview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView

class ExpandableListAdapter(private val context: Context, private val awardsMap: Map<String, List<Awards>>): BaseExpandableListAdapter() {

    private val categories: List<String> = awardsMap.keys.toList()

    override fun getGroupCount(): Int = categories.size

    override fun getChildrenCount(groupPosition: Int): Int {
        val category = categories[groupPosition]
        return awardsMap[category]?.size ?: 0
    }

    override fun getGroup(groupPosition: Int): Any? = categories[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): Any? {
        val category = categories[groupPosition]
        return awardsMap[category]?.get(childPosition) ?: Awards("", "", 0, "", "", "")
    }

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = false

    override fun getGroupView(
        groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?
    ): View? {
        val categoryName = getGroup(groupPosition) as String
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.custom_expandable_group, parent, false)

        val textView = view.findViewById<TextView>(R.id.groupTitle)
        textView.text = categoryName

        val arrowIcon = view.findViewById<ImageView>(R.id.arrowIcon)
        arrowIcon.rotation = if (isExpanded) 180f else 0f

        return view
    }

    override fun getChildView(
        groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?
    ): View? {
        val award = getChild(groupPosition, childPosition) as Awards
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.expandable_child, parent, false)

        val awardNameYearTextView = view.findViewById<TextView>(R.id.awardNameYear)
        val awardDetailsTextView = view.findViewById<TextView>(R.id.awardDetails)

        awardNameYearTextView.text = "${award.name} (${award.year})"
        awardDetailsTextView.text = "${award.category}\n${award.awardName}\n${award.description}"

        val slideIn = TranslateAnimation(0f, 0f, 50f, 0f)
        slideIn.duration = 300
        view.startAnimation(slideIn)


        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true

}