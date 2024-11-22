package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.R
import com.gravatar.quickeditor.ui.avatarpicker.AvatarUi
import com.gravatar.quickeditor.ui.avatarpicker.AvatarsSectionUiState
import com.gravatar.quickeditor.ui.editor.AvatarPickerContentLayout
import com.gravatar.restapi.models.Avatar
import com.gravatar.ui.GravatarTheme
import java.net.URI

@Composable
internal fun VerticalAvatarsSection(
    state: AvatarsSectionUiState,
    onAvatarSelected: (AvatarUi) -> Unit,
    onAvatarOptionClicked: (Avatar, AvatarOption) -> Unit,
    onChoosePhotoClick: () -> Unit,
    onTakePhotoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var popupVisible by remember { mutableStateOf(false) }
    var popupAnchorBounds: Rect by remember { mutableStateOf(Rect(Offset.Zero, Size.Zero)) }
    var parentBounds by remember { mutableStateOf(Rect(Offset.Zero, Size.Zero)) }
    val gridState = rememberLazyGridState()

    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    val contentPadding = PaddingValues(16.dp)
    val itemSpacing = 8.dp
    Surface(modifier = modifier) {
        Box {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = avatarSize),
                modifier = Modifier.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = RoundedCornerShape(8.dp),
                ).onGloballyPositioned { coordinates ->
                    parentBounds = coordinates.boundsInRoot().getPaddedBounds(contentPadding, density, layoutDirection)
                },
                state = gridState,
                contentPadding = contentPadding,
                horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                verticalArrangement = Arrangement.spacedBy(itemSpacing),
            ) {
                item(
                    span = { GridItemSpan((maxLineSpan)) },
                ) {
                    QESectionTitle(
                        title = stringResource(id = state.titleRes),
                        modifier = Modifier,
                    )
                }
                item(
                    span = { GridItemSpan(maxLineSpan) },
                ) {
                    QESectionMessage(
                        message = stringResource(R.string.gravatar_qe_avatar_picker_description),
                        modifier = Modifier
                            .padding(top = 4.dp),
                    )
                }
                if (state.avatars.isEmpty()) {
                    item(
                        span = { GridItemSpan(maxLineSpan) },
                    ) {
                        ListEmptyStateBox()
                    }
                    item(
                        span = { GridItemSpan(maxLineSpan) },
                    ) {
                        QEButton(
                            buttonText = stringResource(id = R.string.gravatar_qe_avatar_picker_upload_image),
                            onClick = { popupVisible = true },
                            enabled = state.uploadButtonEnabled,
                            modifier = Modifier
                                .onGloballyPositioned { layoutCoordinates ->
                                    popupAnchorBounds = layoutCoordinates
                                        .boundsInRoot()
                                },
                        )
                    }
                } else {
                    item(
                        span = { GridItemSpan(1) },
                    ) {
                        UploadImageGridButton(
                            onClick = { popupVisible = true },
                            enabled = state.uploadButtonEnabled,
                            modifier = Modifier
                                .onGloballyPositioned { layoutCoordinates ->
                                    popupAnchorBounds = layoutCoordinates
                                        .boundsInRoot()
                                },
                        )
                    }
                    items(items = state.avatars, key = { it.avatarId }) { avatarModel ->
                        Avatar(
                            avatar = avatarModel,
                            parentBounds = parentBounds,
                            onAvatarSelected = { onAvatarSelected(avatarModel) },
                            onAvatarOptionClicked = { avatar, option -> onAvatarOptionClicked(avatar, option) },
                            size = avatarSize,
                            modifier = Modifier,
                        )
                    }
                }
            }
            if (popupVisible) {
                val isGridButton = state.avatars.isNotEmpty()
                MediaPickerPopup(
                    anchorAlignment = if (isGridButton) Alignment.Start else Alignment.CenterHorizontally,
                    onDismissRequest = { popupVisible = false },
                    anchorBounds = popupAnchorBounds,
                    onChoosePhotoClick = {
                        popupVisible = false
                        onChoosePhotoClick()
                    },
                    onTakePhotoClick = {
                        popupVisible = false
                        onTakePhotoClick()
                    },
                )
            }
        }
    }
}

private fun Rect.getPaddedBounds(
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

@Composable
@Preview(showBackground = true)
private fun VerticalAvatarSectionPreview() {
    GravatarTheme {
        VerticalAvatarsSection(
            state = AvatarsSectionUiState(
                avatars = List(6) {
                    AvatarUi.Uploaded(
                        avatar = Avatar {
                            imageUrl = URI.create("https://gravatar.com/avatar/test")
                            imageId = it.toString()
                            rating = Avatar.Rating.G
                            altText = "alt"
                            updatedDate = ""
                        },
                        isSelected = it == 0,
                        isLoading = false,
                    )
                },
                scrollToIndex = null,
                uploadButtonEnabled = true,
                avatarPickerContentLayout = AvatarPickerContentLayout.Vertical,
            ),
            onTakePhotoClick = { },
            onChoosePhotoClick = { },
            onAvatarSelected = { },
            onAvatarOptionClicked = { _, _ -> },
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun VerticalAvatarSectionEmptyPreview() {
    GravatarTheme {
        VerticalAvatarsSection(
            state = AvatarsSectionUiState(
                avatars = emptyList(),
                scrollToIndex = null,
                uploadButtonEnabled = true,
                avatarPickerContentLayout = AvatarPickerContentLayout.Vertical,
            ),
            onTakePhotoClick = { },
            onChoosePhotoClick = { },
            onAvatarSelected = { },
            onAvatarOptionClicked = { _, _ -> },
        )
    }
}
