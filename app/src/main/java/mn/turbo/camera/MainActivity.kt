package mn.turbo.camera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import mn.turbo.camera.Constant.EMPTY_IMAGE_URI
import mn.turbo.camera.camera.CameraCapture
import mn.turbo.camera.gallery.GalleryImage
import mn.turbo.camera.ui.theme.CameraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainContent(Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
fun MainContent(modifier: Modifier = Modifier) {
    var imageUri by remember {
        mutableStateOf(EMPTY_IMAGE_URI)
    }

    if (imageUri != EMPTY_IMAGE_URI) {
        Box(modifier = modifier) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
            Button(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = {
                    imageUri = EMPTY_IMAGE_URI
                }
            ) {
                Text(text = "Delete this!")
            }
        }
    } else {
        var showGallery by remember {
            mutableStateOf(false)
        }
        if (showGallery) {
            GalleryImage(
                modifier = modifier,
                onImageUri = { uri ->
                    showGallery = false
                    imageUri = uri
                }
            )
        } else {
            Box(modifier = modifier) {
                CameraCapture(
                    modifier = modifier,
                    onImageFile = { file ->
                        imageUri = file.toUri()
                    }
                )
                Button(
                    onClick = { showGallery = true },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(4.dp)
                ) {
                    Text(text = "Select from Gallery")
                }
            }
        }
    }
}
