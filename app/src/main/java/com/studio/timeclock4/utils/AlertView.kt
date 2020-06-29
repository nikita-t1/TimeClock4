package com.studio.timeclock4.utils

import androidx.fragment.app.FragmentManager

enum class BottomSheetTheme { LIGHT, DARK }
enum class BottomSheetStyle { BOTTOM_SHEET, DIALOG, IOS }
enum class BottomSheetActionStyle { POSITIVE, NEGATIVE, DEFAULT }

// create simple alerts easily with some customization.
class AlertView(private var title: String, private var message: String, private var style: BottomSheetStyle) {

    private var theme: BottomSheetTheme = BottomSheetTheme.LIGHT
    private var actions: ArrayList<AlertAction> = ArrayList()

    fun addAction(action: AlertAction) {
        actions.add(action)
    }

    fun show(fragmentManager: FragmentManager) {
        when (style) {
            BottomSheetStyle.BOTTOM_SHEET -> {
                val bottomSheet = BottomSheetFragment(title, message, actions, style, theme)
                bottomSheet.show(fragmentManager, bottomSheet.tag)
                bottomSheet.isCancelable = false
            }
            BottomSheetStyle.IOS -> {
                val bottomSheet = BottomSheetFragment(title, message, actions, style, theme)
                bottomSheet.show(fragmentManager, bottomSheet.tag)
                bottomSheet.isCancelable = false
            }
//            BottomSheetStyle.DIALOG -> {
//                val bottomSheet = DialogFragment(title, message, actions, theme)
//                bottomSheet.show(fragmentManager, bottomSheet.tag)
//                bottomSheet.isCancelable = false
//            }
        }
    }

    fun setTheme(theme: BottomSheetTheme) {
        this.theme = theme
    }
}

class AlertAction {
    var title: String
    var style: BottomSheetActionStyle
    var action: ((AlertAction) -> Unit)?
    var actionListener: AlertActionListener?

    constructor(title: String, style: BottomSheetActionStyle, action: (AlertAction) -> Unit) {
        this.title = title
        this.style = style
        this.action = action
        this.actionListener = null
    }

    constructor(title: String, style: BottomSheetActionStyle, actionListener: AlertActionListener) {
        this.title = title
        this.style = style
        this.actionListener = actionListener
        this.action = null
    }
}

interface AlertActionListener {
    fun onActionClick(action: AlertAction)
}
