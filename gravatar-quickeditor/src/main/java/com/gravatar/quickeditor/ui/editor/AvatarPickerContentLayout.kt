package com.gravatar.quickeditor.ui.editor

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * The layout direction of the Avatar picker in the Quick Editor.
 */
@Parcelize
public sealed class AvatarPickerContentLayout : Parcelable {
    /**
     * Horizontal layout.
     */
    @Parcelize
    public data object Horizontal : AvatarPickerContentLayout()

    /**
     * Vertical layout.
     */
    @Parcelize
    public data object Vertical : AvatarPickerContentLayout()
}
