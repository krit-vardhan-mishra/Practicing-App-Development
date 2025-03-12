package com.just_for_fun.expandablelistview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
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
            .inflate(android.R.layout.simple_expandable_list_item_1, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = categoryName
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

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true

}