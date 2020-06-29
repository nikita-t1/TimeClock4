package com.studio.timeclock4.utils

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.studio.timeclock4.R
import kotlinx.android.synthetic.main.action_layout_light.view.*
import kotlinx.android.synthetic.main.alert_layout_dark.view.*

@SuppressLint("ValidFragment")
open class BottomSheetFragment(
    private val title: String = "",
    private val message: String = "",
    private val actions: ArrayList<AlertAction>,
    private val style: BottomSheetStyle,
    private val theme: BottomSheetTheme
) : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate view according to theme selected. Default is AlertTheme.LIGHT
        var view: View? = null
        if (theme == BottomSheetTheme.LIGHT)
            view = inflater.inflate(R.layout.alert_layout_light, container, false)
        else if (theme == BottomSheetTheme.DARK)
            view = inflater.inflate(R.layout.alert_layout_dark, container, false)

        // Set up view
        initView(view)

        return view
    }

    private fun initView(view: View?) {
        view!!.tvTitle.text = title
        view.tvMessage.text = message

        // In case of title or message is empty
        if (title.isEmpty()) view.tvTitle.visibility = View.GONE
        if (message.isEmpty()) view.tvMessage.visibility = View.GONE

        // Change view according to selected Style
        if (style == BottomSheetStyle.BOTTOM_SHEET)
            view.tvCancel.visibility = View.GONE
        else if (style == BottomSheetStyle.IOS)
            view.tvCancel.visibility = View.VISIBLE
        view.tvCancel.setOnClickListener({ dismiss() })

        // Inflate action views
        inflateActionsView(view.actionsLayout, actions)
    }

    /**
     * Inflate action views
     */
    private fun inflateActionsView(actionsLayout: LinearLayout, actions: ArrayList<AlertAction>) {
        for (action in actions) {

            // Inflate action view according to theme selected
            var view: View? = null
            if (theme == BottomSheetTheme.LIGHT)
                view = LayoutInflater.from(context).inflate(R.layout.action_layout_light, null)
            else if (theme == BottomSheetTheme.DARK)
                view = LayoutInflater.from(context).inflate(R.layout.action_layout_dark, null)

            view!!.tvAction.text = action.title

            // Click listener for action.
            view.tvAction.setOnClickListener {
                dismiss()

                // For Kotlin
                action.action?.invoke(action)

                // For Java
                action.actionListener?.onActionClick(action)
            }

            // Action text color according to AlertActionStyle
            if (context != null) {
                when (action.style) {
                    BottomSheetActionStyle.POSITIVE -> {
                        view.tvAction.setTextColor(ContextCompat.getColor(context!!, R.color.green))
                    }
                    BottomSheetActionStyle.NEGATIVE ->
                        view.tvAction.setTextColor(ContextCompat.getColor(context!!, R.color.red))

                    BottomSheetActionStyle.DEFAULT -> {
                        if (theme == BottomSheetTheme.LIGHT)
                            view.tvAction.setTextColor(ContextCompat.getColor(context!!, R.color.darkGray))
                        else if (theme == BottomSheetTheme.DARK)
                            view.tvAction.setTextColor(ContextCompat.getColor(context!!, R.color.lightWhite))
                    }
                }
            }

            // Add view to layout
            actionsLayout.addView(view)
        }
    }
}
