import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.krm.rentalservices.BottomNavItem
import com.krm.rentalservices.viewmodel.ERROR_INTERNET
import com.krm.rentalservices.viewmodel.RentalOrderViewModel
import com.krm.rentalservices.viewmodel.SUCCESS
import java.io.File
import java.io.FileOutputStream

@Composable
fun OrderPDFViewerScreen(
    rentalOrderViewModel: RentalOrderViewModel,
    navController: NavHostController
) {
    val orderState = rentalOrderViewModel.isUpdateOrder.collectAsState()
    val context = LocalContext.current
    var pdfBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val pdfFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Invoice.pdf")

    // Zoom and Pan states
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    // Load PDF page
    LaunchedEffect(pdfFile) {
        pdfBitmap = renderFirstPage(context, pdfFile)
    }

    val state = rentalOrderViewModel.addRentalOrderState.value
//    val isOrderUpdate = rentalOrderViewModel.isUpdateOrder.value

    if (!state.isEventHandled) {
        when (state.success) {
            SUCCESS -> { //, ERROR_INTERNET
                Toast.makeText(
                    LocalContext.current,
                    state.data + if (orderState.value) " Order updated successfully" else " Order placed successfully",
                    Toast.LENGTH_LONG
                ).show()

                rentalOrderViewModel.clearData()
//                rentalOrderViewModel.markEventHandled()
                rentalOrderViewModel.fetchInventory()
                navController.navigate(BottomNavItem.OrderList.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }

            ERROR_INTERNET -> {
                Toast.makeText(
                    LocalContext.current,
                    state.data + " No internet connection",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Invoice PDF", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale *= zoom
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                }
        ) {
            pdfBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "PDF Preview",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY
                        )
                )
            } ?: Text("Loading PDF...", modifier = Modifier.align(Alignment.Center))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { printPdf(context, pdfFile) }) {
                Text("Print")
            }
            Button(onClick = { sharePdf(context, pdfFile) }) {
                Text("Share")
            }
            Button(
                onClick = {
                    /* var orderStatus = Constants.OPEN_ORDER
                     if (rentalOrderViewModel.isUpdateOrder.value) {
                         if (rentalOrderViewModel.rentalOrder.value!!.orderStatus == Constants.RETURNED_ORDER) {
                             orderStatus = Constants.RETURNED_ORDER
                         }

                     }*/
                    rentalOrderViewModel.saveRentalOrder()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green,  // Set the background color
                    contentColor = Color.White    // Set the text color
                )
            ) {
                Text("Done")
            }

            /* Button(onClick = { downloadPdf(context, pdfFile) }) {
                 Text("Download")
             }*/
        }
    }
}

// Function to render the first page of PDF as Bitmap
fun renderFirstPage(context: Context, pdfFile: File): Bitmap? {
    return try {
        val fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfRenderer = PdfRenderer(fileDescriptor)
        val page = pdfRenderer.openPage(0)

        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        page.close()
        pdfRenderer.close()
        bitmap
    } catch (e: Exception) {
        Log.e("PDF_VIEWER", "Error loading PDF", e)
        null
    }
}

// Function to Print PDF
fun printPdf(context: Context, file: File) {
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    val printAdapter: PrintDocumentAdapter = PdfPrintAdapter(context, file)
    printManager.print("Invoice", printAdapter, PrintAttributes.Builder().build())
}
/*
// Function to Share PDF
fun sharePdf(context: Context, file: File) {
    val uri = Uri.fromFile(file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
    }
    context.startActivity(Intent.createChooser(intent, "Share Invoice PDF"))
}*/

fun sharePdf(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Important!
    }

    context.startActivity(Intent.createChooser(shareIntent, "Share Invoice PDF"))
}


// Function to Download PDF (Copy to Downloads Folder)
/*fun downloadPdf(context: Context, file: File) {
    val downloadsDir =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val newFile = File(downloadsDir, "Invoice" + Utils.getCurrentDateTime() + ".pdf")
    file.copyTo(newFile, overwrite = true)
    Log.d("PDF_VIEWER", "File copied to Downloads: ${newFile.absolutePath}")
}*/

// Print Adapter Class
class PdfPrintAdapter(private val context: Context, private val file: File) :
    PrintDocumentAdapter() {
    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes?,
        cancellationSignal: android.os.CancellationSignal?,
        callback: LayoutResultCallback?,
        extras: android.os.Bundle?
    ) {
        callback?.onLayoutFinished(
            android.print.PrintDocumentInfo.Builder("invoice.pdf").build(),
            true
        )
    }

    override fun onWrite(
        pages: Array<out PageRange>?,
        destination: ParcelFileDescriptor?,
        cancellationSignal: android.os.CancellationSignal?,
        callback: WriteResultCallback?
    ) {
        try {
            file.inputStream().use { input ->
                FileOutputStream(destination?.fileDescriptor).use { output ->
                    input.copyTo(output)
                }
            }
            callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
        } catch (e: Exception) {
            Log.e("PDF_PRINT", "Error writing PDF", e)
        }
    }

}
