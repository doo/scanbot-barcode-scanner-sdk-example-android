package io.scanbot.example.sdk.barcode.ui.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.scanbot.example.sdk.barcode.ui.compose.theme.ScanbotSdkAndroidTheme
import io.scanbot.example.sdk.barcode.ui.compose.theme.sbBrandColor
import io.scanbot.example.sdk.barcode.ui.usecases.UseCase
import io.scanbot.example.sdk.barcode.ui.usecases.ViewType
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.licensing.LicenseStatus
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class ComposeExampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainApp()
        }
    }

    @Composable
    fun MainApp() {
        ScanbotSdkAndroidTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val navController = rememberNavController()
                AppNavHost(navController)
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Menu : Screen("menu")
    object BarcodeScannerSingle : Screen("BarcodeScannerSingle")
    object BarcodeScannerMulti : Screen("BarcodeScannerMulti")
    object BarcodeScannerBatch : Screen("BarcodeScannerBatch")
    object BarcodeScannerMicro : Screen("BarcodeScannerMicro")
    object BarcodeScannerDistant : Screen("BarcodeScannerDistant")
    object BarcodeFindAndPick : Screen("BarcodeFindAndPick")

    data class BarcodeDetail(val data: String, val format: String) :
        Screen("barcodeDetail/{data}/{format}") {
        companion object {
            fun createRoute(data: String, format: String): String {
                val encodedData = URLEncoder.encode(data, StandardCharsets.UTF_8.toString())
                val encodedFormat = URLEncoder.encode(format, StandardCharsets.UTF_8.toString())
                return "barcodeDetail/$encodedData/$encodedFormat"
            }
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Menu.route) {
        composable(Screen.Menu.route) { MenuScreen(navController) }
        composable(Screen.BarcodeScannerSingle.route) { BarcodeScannerSingleScan(navController) }
        composable(Screen.BarcodeScannerMulti.route) { BarcodeScannerMultiScan(navController) }
        composable(Screen.BarcodeScannerBatch.route) { BarcodeScannerBatchScan(navController) }
        composable(Screen.BarcodeScannerMicro.route) { BarcodeScannerMicroScan(navController) }
        composable(Screen.BarcodeScannerDistant.route) { BarcodeScannerDistantScan(navController) }
        composable(Screen.BarcodeFindAndPick.route) { BarcodeFindAndPick(navController) }
        composable(
            route = "barcodeDetail/{data}/{format}",
            arguments = listOf(
                navArgument("data") { type = NavType.StringType },
                navArgument("format") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val data =
                backStackEntry.arguments?.getString("data")
                    ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
                    ?: ""
            val format =
                backStackEntry.arguments?.getString("format")
                    ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
                    ?: ""
            BarcodeDetailScreen(data, format)
        }
    }
}

@Composable
fun MenuScreen(navController: NavHostController) {
    val context = navController.context
    val scanbotSdk: ScanbotBarcodeScannerSDK = remember { ScanbotBarcodeScannerSDK(context) }

    val menuItems = listOf(
        Triple(
            "Scanning Single Barcode",
            Screen.BarcodeScannerSingle.route,
            "Barcode scanner with AR overlay"
        ),
        Triple(
            "Scanning Multiple Barcodes",
            Screen.BarcodeScannerMulti.route,
            "Barcode multi scan mode without finder"
        ),
        Triple(
            "Batch Scanning",
            Screen.BarcodeScannerBatch.route,
            "Barcode batch scan mode"
        ),
        Triple(
            "Scanning Tiny Barcodes",
            Screen.BarcodeScannerMicro.route,
            "Barcode micro barcode scan mode"
        ),
        Triple(
            "Scanning Distant Barcodes",
            Screen.BarcodeScannerDistant.route,
            "Barcode distant barcode scan mode"
        ),
        Triple(
            "Find and Pick Barcodes",
            Screen.BarcodeFindAndPick.route,
            "Find Specific barcode and pick it"
        ),
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .systemBarsPadding(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        item() {
            Text(
                "Scanbot SDK Compose Custom UI Demo",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
        if (scanbotSdk.licenseInfo.status != LicenseStatus.OKAY) {
            item() {
                Surface(
                    modifier = Modifier.padding(bottom = 24.dp),
                    color = sbBrandColor,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        "Warning: Scanbot SDK License is not valid! Current status: ${scanbotSdk.licenseInfo.status}",
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        items(menuItems) { (title, route, description) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(route) }
                    .semantics {
                        this.contentDescription = description
                    },
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    color = Color.LightGray.copy(alpha = 0.3f)
                )
            }
        }
    }
}

