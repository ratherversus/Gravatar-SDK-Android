package com.gravatar.quickeditor.ui

import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.gravatar.quickeditor.ui.editor.AuthenticationMethod
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import com.gravatar.quickeditor.ui.editor.bottomsheet.GravatarQuickEditorBottomSheet
import kotlinx.parcelize.Parcelize

/**
 * Activity that hosts the [GravatarQuickEditorBottomSheet] composable.
 * This activity is used to show the Gravatar Quick Editor in a bottom sheet.
 *
 * @see GravatarEditorActivityArguments
 */
public class GravatarQuickEditorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = requireNotNull(intent.parcelable<GravatarEditorActivityArguments>(EXTRA_QE_ARGUMENTS))
        setContent {
            GravatarQuickEditorBottomSheet(
                gravatarQuickEditorParams = arguments.gravatarQuickEditorParams,
                authenticationMethod = arguments.authenticationMethod,
                onAvatarSelected = {
                    // Do nothing for the moment
                },
                onDismiss = { finish() },
            )
        }
    }

    /**
     * Arguments for the [GravatarQuickEditorActivity].
     *
     * [gravatarQuickEditorParams] The parameters to configure the Quick Editor.
     * [authenticationMethod] The method used for authentication with the Gravatar REST API.
     */
    @Parcelize
    public class GravatarEditorActivityArguments(
        public val gravatarQuickEditorParams: GravatarQuickEditorParams,
        public val authenticationMethod: AuthenticationMethod,
    ) : Parcelable {
        /**
         * Starts the [GravatarQuickEditorActivity] with the provided arguments.
         */
        public fun startGravatarQuickEditor(context: Context) {
            val intent = Intent(context, GravatarQuickEditorActivity::class.java)
            intent.putExtra(EXTRA_QE_ARGUMENTS, this)
            context.startActivity(intent)
        }
    }

    private companion object {
        private const val EXTRA_QE_ARGUMENTS = "qeArguments"
    }
}

private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= TIRAMISU -> getParcelableExtra(key, T::class.java)
    else -> {
        @Suppress("DEPRECATION")
        getParcelableExtra(key) as? T
    }
}
