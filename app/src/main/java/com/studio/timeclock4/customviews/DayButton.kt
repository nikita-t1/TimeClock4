package com.studio.timeclock4.customviews

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import com.studio.timeclock4.R

class DayButton(context: Context, attributeSet: AttributeSet): LinearLayout(context, attributeSet){

    var dayOfMonth = 1
        set(value) {
            field = value
            val textView = layout.findViewById<TextView>(R.id.textView)
            textView.text = value.toString()
        }

    var dotColor = Color.GREEN
        set(value) {
            val dot = layout.findViewById<View>(R.id.dot)
            ViewCompat.setBackgroundTintList(dot, ColorStateList.valueOf(value))
        }

    private val layout: View = LayoutInflater.from(context).inflate(R.layout.view_day_button, this, true)

    init {

        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.DayButton)
        dotColor = typedArray.getColor(R.styleable.DayButton_dotColor, Color.GRAY)
        if (typedArray.hasValue(R.styleable.DayButton_dayOfMonth))
            dayOfMonth = typedArray.getInt(R.styleable.DayButton_dayOfMonth, 0)

        typedArray.recycle()
        invalidate()
    }
}