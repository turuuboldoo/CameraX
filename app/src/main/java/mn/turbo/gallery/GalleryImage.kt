package mn.turbo.gallery

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import mn.turbo.Constant.EMPTY_IMAGE_URI
import mn.turbo.permission.Permission

@Composable
fun GalleryImage(
    modifier: Modifier = Modifier,
    onImageUri: (Uri) -> Unit = {}
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            onImageUri.invoke(uri ?: EMPTY_IMAGE_URI)
        }
    )

    @Composable
    fun LaunchGallery() {
        SideEffect {
            launcher.launch("image/*")
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        Permission(
            permission = Manifest.permission.ACCESS_MEDIA_LOCATION,
            rationale = "Give me your images from gallery!",
            permissionNotAvailableContent = {
                Column(modifier) {
                    Text(text = "No Gallery?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Button(
                            modifier = Modifier.padding(4.dp),
                            onClick = {
                                context.startActivity(
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .apply {
                                            data = Uri.fromParts("package", context.packageName, null)
                                        }
                                )
                            }
                        ) {
                            Text(text = "Go to Settings NOW!")
                        }
                        Button(
                            modifier = Modifier.padding(4.dp),
                            onClick = {
                                onImageUri.invoke(EMPTY_IMAGE_URI)
                            }
                        ) {
                            Text(text = "Go back to Camera.")
                        }
                    }
                }
            }
        ) {
            LaunchGallery()
        }
    } else {
        LaunchGallery()
    }
}