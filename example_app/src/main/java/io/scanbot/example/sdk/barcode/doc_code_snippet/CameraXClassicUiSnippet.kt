package io.scanbot.example.sdk.barcode.doc_code_snippet

/*
    NOTE: this snippet of code is to be used only as a part of the website documentation.
    This code is not intended for any use outside of the support of documentation by Scanbot SDK GmbH employees.
*/

// NOTE for maintainers: whenever changing this code,
// ensure that links using it are still pointing to valid lines!
// Pay attention to imports adding/removal/sorting!
// Page URLs using this code:
// TODO: add URLs here

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.scanbot.example.sdk.barcode.R
import io.scanbot.sdk.ui.camera.ScanbotCameraXView


class CameraXClassicUiSnippetActivity : AppCompatActivity() {

    private lateinit var cameraView: ScanbotCameraXView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doc_snippet_activity_classic_ui_camera_x)

        // @Tag("Barcode Classic UI orientation lock snippet")
        cameraView = findViewById<ScanbotCameraXView>(R.id.camera)!!

        // Lock the orientation of the Activity as well as the orientation of the taken picture to portrait:
        cameraView.lockToPortrait(true);
        // @EndTag("Barcode Classic UI orientation lock snippet")

        // @Tag("Barcode Classic UI configure shutter sound snippet")
        cameraView.setCameraOpenCallback {
            cameraView.postDelayed({
                cameraView.setShutterSound(false)
            }, 700)
        }
        // @EndTag("Barcode Classic UI configure shutter sound snippet")

        // @Tag("Barcode Classic UI configure focusing snippet")
        cameraView.setCameraOpenCallback {
            cameraView.postDelayed({
                cameraView.continuousFocus()
            }, 700)
        }
        // @EndTag("Barcode Classic UI configure focusing snippet")
    }
}
