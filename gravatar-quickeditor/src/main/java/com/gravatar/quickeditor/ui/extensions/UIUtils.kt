package com.gravatar.quickeditor.ui.extensions

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

internal fun Rect.getPaddedBounds(
    paddingValues: PaddingValues,
    density: Density,
    layoutDirection: LayoutDirection,
): Rect {
    // Convert PaddingValues to Px
    val startPaddingPx = with(density) { paddingValues.calculateLeftPadding(layoutDirection).toPx() }
    val topPaddingPx = with(density) { paddingValues.calculateTopPadding().toPx() }
    val endPaddingPx = with(density) { paddingValues.calculateRightPadding(layoutDirection).toPx() }
    val bottomPaddingPx = with(density) { paddingValues.calculateBottomPadding().toPx() }

    return Rect(
        left = this.left + startPaddingPx,
        top = this.top + topPaddingPx,
        right = this.right - endPaddingPx,
        bottom = this.bottom - bottomPaddingPx,
    )
}
