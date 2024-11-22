package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.ui.avatarpicker.AvatarUi
import com.gravatar.quickeditor.ui.extensions.getPaddedBounds
import com.gravatar.restapi.models.Avatar
import java.net.URL

@Composable
internal fun LazyAvatarRow(
    avatars: List<AvatarUi>,
    onAvatarSelected: (AvatarUi) -> Unit,
    onAvatarOptionClicked: (Avatar, AvatarOption) -> Unit,
    horizontalArrangement: Arrangement.Horizontal,
    state: LazyListState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    var parentBounds by remember { mutableStateOf(Rect(Offset.Zero, Size.Zero)) }

    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    LazyRow(
        horizontalArrangement = horizontalArrangement,
        modifier = modifier.onGloballyPositioned { coordinates ->
            parentBounds = coordinates.boundsInRoot().getPaddedBounds(contentPadding, density, layoutDirection)
        },
        state = state,
        contentPadding = contentPadding,
    ) {
        items(items = avatars, key = { it.avatarId }) { avatarModel ->
            Avatar(
                avatar = avatarModel,
                parentBounds = parentBounds,
                onAvatarSelected = { onAvatarSelected(avatarModel) },
                onAvatarOptionClicked = { avatar, option -> onAvatarOptionClicked(avatar, option) },
                size = avatarSize,
                modifier = Modifier
                    .animateItem()
                    .size(avatarSize),
            )
        }
    }
}

internal val avatarSize = 96.dp

@Composable
internal fun Avatar(
    avatar: AvatarUi,
    size: Dp,
    parentBounds: Rect? = null,
    onAvatarSelected: () -> Unit,
    onAvatarOptionClicked: (Avatar, AvatarOption) -> Unit,
    modifier: Modifier,
) {
    when (avatar) {
        is AvatarUi.Uploaded -> {
            val sizePx = with(LocalDensity.current) { size.roundToPx() }
            SelectableAvatar(
                imageUrl = avatar.imageUrlWithSize(sizePx),
                isSelected = avatar.isSelected,
                loadingState = avatar.loadingState,
                parentBounds = parentBounds,
                onAvatarClicked = { onAvatarSelected() },
                onAvatarOptionClicked = { onAvatarOptionClicked(avatar.avatar, it) },
                modifier = modifier,
            )
        }

        is AvatarUi.Local -> SelectableAvatar(
            imageUrl = avatar.uri.toString(),
            isSelected = false,
            loadingState = avatar.loadingState,
            parentBounds = parentBounds,
            onAvatarClicked = { onAvatarSelected() },
            modifier = modifier,
        )
    }
}

private fun AvatarUi.Uploaded.imageUrlWithSize(sizePx: Int) = avatar.imageUrl.toURL()?.let { url ->
    URL(url.protocol, url.host, url.path.plus("?size=$sizePx"))
}.toString()

private val AvatarUi.Uploaded.loadingState: AvatarLoadingState
    get() = if (isLoading) AvatarLoadingState.Loading else AvatarLoadingState.None

private val AvatarUi.Local.loadingState: AvatarLoadingState
    get() = when {
        isLoading -> AvatarLoadingState.Loading
        else -> AvatarLoadingState.Failure
    }
