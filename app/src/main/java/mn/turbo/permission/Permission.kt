package mn.turbo.permission

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Permission(
    permission: String = android.Manifest.permission.CAMERA,
    rationale: String = "We need your permission to access your camera",
    permissionNotAvailableContent: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    val permissionState = rememberPermissionState(permission = permission)

    PermissionRequired(
        permissionState = permissionState,
        permissionNotGrantedContent = {
            Rationale(
                text = rationale,
                onRequestPermission = {
                    permissionState.launchPermissionRequest()
                }
            )
        },
        permissionNotAvailableContent = permissionNotAvailableContent,
        content = content
    )
}

@Composable
fun Rationale(
    text: String,
    onRequestPermission: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(text = "Permission")
        },
        text = {
            Text(text = text)
        },
        confirmButton = {
            Button(onClick = onRequestPermission) {
                Text(text = "Alright..")
            }
        }
    )
}