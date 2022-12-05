package mn.turbo.camera

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch
import mn.turbo.permission.Permission
import java.io.File

@Composable
fun CameraCapture(
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    onImageFile: (File) -> Unit = { }
) {
    val context = LocalContext.current

    Permission(
        permissionNotAvailableContent = {
            Column(modifier = modifier) {
                Text(text = "We need your permission to access your camera")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    context.startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                    )
                }) {
                    Text(text = "Open Settings")
                }
            }
        }
    ) {
        Box(modifier = modifier) {
            val lifecycleOwner = LocalLifecycleOwner.current
            val coroutineScope = rememberCoroutineScope()
            var previewUseCase by remember { mutableStateOf<UseCase>(Preview.Builder().build()) }
            val imageCaptureUseCase by remember {
                mutableStateOf(
                    ImageCapture.Builder()
                        .setIoExecutor(Dispatchers.IO.asExecutor())
                        .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setJpegQuality(50)
                        .build()
                )
            }
            Box {
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    onUseCase = {
                        previewUseCase = it
                    }
                )
                Button(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(bottom = 16.dp)
                        .align(Alignment.BottomCenter),
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            onImageFile(imageCaptureUseCase.takePicture(ioExecutor))
                        }
                    }
                ) {
                    Text(text = "Take picture")
                }
            }

            LaunchedEffect(previewUseCase) {
                val cameraProvider = context.getCameraProvider()
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, previewUseCase, imageCaptureUseCase
                    )
                } catch (e: Exception) {
                    Log.w("123123", "CaptureImage - LaunchedEffect -> $e")
                }
            }
        }
    }
}
