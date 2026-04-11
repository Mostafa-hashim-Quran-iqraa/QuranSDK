package com.blacksmith.quranApp.data.providers

import android.content.res.AssetManager
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import com.blacksmith.quranApp.data.local.MyPreferences
import java.io.File

/**
 * Resolves application's resources.
 */
interface ResourceProviderInterface {
    fun getMyPreferences(): MyPreferences

    /**
     * Resolves text's id to String.
     *
     * @param id to be fetched from the resources
     * @return String representation of the {@param id}
     */
    fun getString(@StringRes id: Int): String
    fun getExternalFilesDir(): File?

    /**
     * Resolves text's id to String and formats it.
     *
     * @param resId      to be fetched from the resources
     * @param formatArgs format arguments
     * @return String representation of the {@param resId}
     */
    fun getString(@StringRes resId: Int, vararg formatArgs: String?): String

    /**
     * Resolves drawablw resource.
     *
     * @param drawId      to be fetched from the resources
     * @return String representation of the {@param resId}
     */
    fun getDrawable(@StringRes drawId: Int): Drawable

    /**
     * Resolves Asset resource.
     */
    fun getAsset(): AssetManager

    /**
     * Resolves Dimension resource.
     */
    fun getDimension(@DimenRes id: Int): Float

    /**
     * Resolves drawablw resource.
     *
     * @param id      to be fetched from the resources
     * @return int representation of the {@param resId}
     */
    fun getColorFromRes(@ColorRes id: Int): Int
}