package com.tsp.android.hilibrary.utils

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources.Theme
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

object HiRes {
    fun getString(@StringRes id: Int): String {
        return context().getString(id)
    }

    fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String {
        return context().getString(resId, *formatArgs)
    }

    fun getColor(@ColorRes id: Int): Int {
        return ContextCompat.getColor(context(), id)
    }

    fun getColorStateList(@ColorRes id: Int): ColorStateList? {
        return ContextCompat.getColorStateList(context(), id)
    }

    private fun context(): Context {
        return AppGlobals.get() as Context
    }

    fun getDrawable(@DrawableRes drawableId: Int): Drawable? {
        return ContextCompat.getDrawable(context(), drawableId)
    }
}