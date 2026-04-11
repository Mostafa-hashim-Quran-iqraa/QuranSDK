package com.blacksmith.quranApp.data.resources

import android.content.ContentResolver
import android.content.Context
import android.content.res.AssetManager
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.AnyRes
import androidx.annotation.BoolRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.blacksmith.quranApp.data.local.LocaleHelper
import com.blacksmith.quranApp.data.local.MyPreferences
import com.blacksmith.quranApp.data.providers.ResourceProviderInterface
import dagger.internal.Preconditions
import java.io.File
import javax.inject.Inject


class ResourceProviderImp @Inject constructor(
    context: Context,
    var appPreferences: MyPreferences
) : ResourceProviderInterface {

    private val mContext: Context = Preconditions.checkNotNull(
        context,
        "context cannot be null"
    )

    override fun getMyPreferences(): MyPreferences = appPreferences

    override fun getString(@StringRes id: Int): String {
        val newContext: Context = LocaleHelper.updateLocale(
            mContext,
            appPreferences
        )
        return newContext.resources.getString(id)
    }

    override fun getExternalFilesDir(): File? {
        return mContext.getExternalFilesDir(null)
    }


    override fun getString(@StringRes id: Int, vararg formatArgs: String?): String {
        val newContext: Context = LocaleHelper.updateLocale(
            mContext,
            appPreferences
        )
        return newContext.resources.getString(id, *formatArgs)
    }

    fun getUriToDrawable(
        @AnyRes drawableId: Int
    ): Uri? {
        return (ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + mContext.resources.getResourcePackageName(drawableId)
                + '/' + mContext.resources.getResourceTypeName(drawableId)
                + '/' + mContext.resources.getResourceEntryName(drawableId)).toUri()
    }

    override fun getDrawable(@DrawableRes id: Int): Drawable {
        return mContext.getDrawable(id)!!
    }

    fun getBoolean(@BoolRes id: Int): Boolean {
        return mContext.resources.getBoolean(id)
    }

    fun getInt(@IntegerRes id: Int): Int {
        return mContext.resources.getInteger(id)
    }


    override fun getColorFromRes(@ColorRes id: Int): Int {
        return ContextCompat.getColor(mContext, id)
    }

    override fun getAsset(): AssetManager {
        return mContext.resources.assets
    }


    override fun getDimension(@DimenRes id: Int): Float {
        return mContext.resources.getDimension(id)
    }

}