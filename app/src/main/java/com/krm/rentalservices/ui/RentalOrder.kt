package com.krm.rentalservices.ui


import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue
import com.krm.rentalservices.Constants
import com.krm.rentalservices.R
import com.krm.rentalservices.model.Customer
import com.krm.rentalservices.model.OrderItem
import com.krm.rentalservices.model.Product
import com.krm.rentalservices.model.RentalOrder
import com.krm.rentalservices.ui.theme.buttonColors
import com.krm.rentalservices.utils.AutoCompleteTextField
import com.krm.rentalservices.utils.Utils
import com.krm.rentalservices.viewmodel.RentalOrderViewModel
import java.io.File


@Composable
fun RentalOrder(
    rentalOrderViewModel: RentalOrderViewModel,
    navController: NavController,
    rentalOrder: RentalOrder?
) {

    Log.d(TAG, "RentalOrder: init called")
    // Collect state values using collectAsState to observe the data in UI

    val orderItems by rentalOrderViewModel.orderItemsDTO.collectAsStateWithLifecycle()
    val totalAmount by rentalOrderViewModel.totalAmount.collectAsStateWithLifecycle()
    val chargesAmt by rentalOrderViewModel.otherChargesTotalAmount.collectAsStateWithLifecycle()
    val paidAmount by rentalOrderViewModel.paidAmount.collectAsStateWithLifecycle()
    val discountAmt by rentalOrderViewModel.discountAmount.collectAsStateWithLifecycle()
    val isUpdateOrder by rentalOrderViewModel.isUpdateOrder.collectAsStateWithLifecycle()
    val selectedCustomer = rentalOrderViewModel.selectedCustomer.collectAsStateWithLifecycle()
    val customerSpinner by rentalOrderViewModel.customerState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) } // 🔹 Manage dialog state

    //Prevents setting the order value one more time for not resets the current order editing during recomposition
    if (rentalOrder != null && !isUpdateOrder) {
        Log.d(TAG, "RentalOrder: is not null")
        rentalOrderViewModel.setRentalOrder(rentalOrder)
        rentalOrderViewModel.setOtherChargesDTO(rentalOrder.otherChargesList)
        rentalOrderViewModel.setOrderItemDTO(rentalOrder.orderItemList)
        rentalOrderViewModel.setPaymentsDTO(rentalOrder.paymentList)
        rentalOrderViewModel.setDiscountAmt(rentalOrder.discountAmt.toString())
        rentalOrderViewModel.setIsUpdateOrder(true)
    }

    Scaffold { innerPadding ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {

            Log.d(TAG, "RentalOrder: ConstraintLayout init called")
            val (firstRow, discountRow, balanceInfo, totalRow, allOptions, chargesRow, paymentRow) = createRefs()

            if (showDialog) {
                AddItemRentalOrderDialog(
                    rentalOrderViewModel = rentalOrderViewModel,
                    context = context,
                    onDismiss = {
                        showDialog = false
                    },
                )
            }

            Column(
                modifier = Modifier
                    .constrainAs(firstRow) {
                        top.linkTo(parent.top) //, margin = 56.dp
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(chargesRow.top)
                        height = Dimension.fillToConstraints
                    }
                    .fillMaxWidth(),
//                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Log.d(TAG, "RentalOrder: orderItems size" + orderItems.size)

                AutoCompleteTextField(
                    items = customerSpinner.data,
                    itemToString = { it.name + " - " + it.mobNo },
                    itemId = { it.id },
                    selectedId = selectedCustomer.value?.id,
                    label = "Search / Choose Customer",
                    onItemSelected = { rentalOrderViewModel.selectCustomer(it) },
                    modifier = Modifier
//                        .weight(1f)
                        .wrapContentHeight(),
                    isEnabled = if (isUpdateOrder) false else true
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 4.dp)
                ) {
                    Text(
                        "ITEMS",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentSize(Alignment.CenterStart)
                            .align(Alignment.CenterVertically)
                    )

                    if (orderItems.isNotEmpty()) {
                        TextButton(
                            onClick = { showDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .wrapContentSize(Alignment.CenterEnd),
                        ) {
                            Text(
                                "+ Item",
                                fontSize = 17.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (orderItems.isEmpty()) {
                    Button(
                        onClick = { showDialog = true },
                        colors = MaterialTheme.colorScheme.buttonColors,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(),
                        shape = RectangleShape
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.add),
                                contentDescription = "Add",
//                            modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))  // Space between icon and text
                            Text(
                                "Add Items", style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(0.dp)
                            )
                        }

//                        Text("Add Items", style = MaterialTheme.typography.titleMedium)
                    }
                }

                // LazyColumn for Order Items
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
//                        .wrapContentHeight()
//                        .padding(vertical = 4.dp)
                ) {
                    items(orderItems) { orderItem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    showDialog = true
                                    val product = Product(
                                        id = orderItem.productId,
                                        name = orderItem.productName,
                                        rentalPrice = orderItem.rentalPrice,
                                        description = "",
                                        timestamp = null
                                    )
                                    rentalOrderViewModel.selectProduct(product)
                                },
//                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            ItemNQty(
                                orderItem.productName,
                                Modifier
                                    .weight(1.5f)
                                    .align(Alignment.Top)
                            )
                            TableCell(
                                "${orderItem.quantity} PCS × ${orderItem.days}D × ₹${orderItem.rentalPrice}",
                                Modifier
                                    .weight(1.5f)
                                    .align(Alignment.Bottom)
                            )
                            if (isUpdateOrder && rentalOrder!!.orderStatus == Constants.RETURNED_ORDER) {
                                TableCell(
                                    "${orderItem.rtnQty} PCS Rtn",
                                    Modifier
                                        .weight(1f)
                                        .align(Alignment.Bottom)
                                )
                            }
                            TableAmtCell(
                                "₹${orderItem.rentalPrice * orderItem.days * orderItem.quantity}",
                                Modifier
                                    .weight(1f)
                                    .align(Alignment.Bottom)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.constrainAs(chargesRow) {
                    bottom.linkTo(discountRow.top)
                }
            ) {
                Text(
                    "Charges: ₹$chargesAmt",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .weight(1f)
//                        .padding(horizontal = 4.dp)
                        .wrapContentSize(Alignment.CenterEnd)
                        .align(Alignment.CenterVertically)
                )
            }

            OutlinedTextField(
                value = discountAmt.toString(),
                onValueChange = { newValue ->
                    rentalOrderViewModel.setDiscountAmt(newValue)
                    Log.d(TAG, "discountAmt newValue called newValue$newValue")
                },
                label = { Text("Discount ") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier
                    .constrainAs(discountRow) {
                        bottom.linkTo(totalRow.top, margin = 5.dp)
                        end.linkTo(parent.end)
                    }
//                    .padding(horizontal = 4.dp)
                    .wrapContentSize(Alignment.CenterEnd)
            )

            // Total Amount
            Text(
                "Total: ₹$totalAmount",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .constrainAs(totalRow) {
                        bottom.linkTo(paymentRow.top, margin = 5.dp)
                        end.linkTo(parent.end)
                    }
//                    .padding(horizontal = 4.dp)
                    .wrapContentSize(Alignment.CenterEnd)
            )

            Text(
                "Paid Amount: ₹$paidAmount",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .constrainAs(paymentRow) {
                        bottom.linkTo(balanceInfo.top)
                        end.linkTo(parent.end)
                    }
//                    .padding(horizontal = 4.dp)
                    .wrapContentSize(Alignment.CenterEnd)
            )

            Text(
                text = "Balance : " + (totalAmount - paidAmount),
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .constrainAs(balanceInfo) {
                        bottom.linkTo(allOptions.top)
                        end.linkTo(parent.end)
                    }
//                    .padding(horizontal = 4.dp)
                    .wrapContentSize(Alignment.CenterEnd)
//                .align(Alignment.CenterVertically)
            )

            Row(
                modifier = Modifier
                    .constrainAs(allOptions) {
                        bottom.linkTo(parent.bottom)
                    }
//                    .background(Color.LightGray)
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = { navController.navigate(Constants.ADD_CHARGES_ROUTE) },
                    colors = MaterialTheme.colorScheme.buttonColors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(5.dp),
                    shape = RectangleShape,
                    contentPadding = PaddingValues(2.dp)
                ) {
                    /*Image(
                        painter = painterResource(id = R.drawable.charges),  // Replace with your PNG image resource
                        contentDescription = "Choose Customer",  // Optional: For accessibility
                        modifier = Modifier.size(40.dp)  // Modify the size as needed
                    )*/
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.charges),
                            contentDescription = "Add",
//                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))  // Space between icon and text
                        Text(
                            "Add Charges", style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(0.dp)
                        )
                    }
                }

                Button(
                    onClick = { navController.navigate(Constants.ADD_PAYMENT_ROUTE) },
                    colors = MaterialTheme.colorScheme.buttonColors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(5.dp),
                    shape = RectangleShape,
                    contentPadding = PaddingValues(2.dp)

                ) {
                    /*Image(
                        painter = painterResource(id = R.drawable.payment),  // Replace with your PNG image resource
                        contentDescription = "Payment",  // Optional: For accessibility
                        modifier = Modifier.size(40.dp)  // Modify the size as needed
                    )*/
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.money),
                            contentDescription = "Add",
//                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))  // Space between icon and text
                        Text(
                            "Add Payment", style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(0.dp)
                        )
                    }

                }

                Button(
                    onClick = {
                        clickPreviewInvoice(
                            rentalOrderViewModel = rentalOrderViewModel,
                            context = context,
                            rentalOrder = rentalOrder,
                            orderItems = orderItems,
                            navController = navController
                        )
                    },
                    colors = MaterialTheme.colorScheme.buttonColors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(5.dp),
                    shape = RectangleShape,
                    contentPadding = PaddingValues(2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.invoice),  // Example: Add Icon
                            contentDescription = "Add",
//                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))  // Space between icon and text
                        Text(
                            "Preview Invoice", style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(0.dp)
                        )
                    }
                }
                /*{
                    *//*Image(
                        painter = painterResource(id = R.drawable.next),  // Replace with your PNG image resource
                        contentDescription = "Save order",  // Optional: For accessibility
                        modifier = Modifier.size(40.dp)  // Modify the size as needed
                    )*//*

                    Text("Preview Invoice")
                }*/
            }
        }
    }
}

fun clickPreviewInvoice(
    rentalOrderViewModel: RentalOrderViewModel, context: Context, rentalOrder: RentalOrder?,
    orderItems: List<OrderItem>, navController: NavController
) {
    rentalOrderViewModel.selectedCustomer.value?.let { customer ->
        generateInvoicePdfFile(
            businessName = "KRM Rental Services \n Mimisal",
            businessContact = "9841949487",
            invoiceNo = if (rentalOrderViewModel.isUpdateOrder.value) {
                rentalOrderViewModel.selectedRentalOrder.value!!.orderId
            } else {
                rentalOrderViewModel.generateInvoiceNo()
            },
            invoiceDate = Utils.getCurrentDateTime(),
            customer = customer,
            orderItems = rentalOrderViewModel.orderItemsDTO.value.map { orderItem ->
                mapOf(
                    "productName" to orderItem.productName,
                    "quantity" to orderItem.quantity,
                    "rtnQty" to orderItem.rtnQty,
                    "days" to orderItem.days,
                    "rentalPrice" to orderItem.rentalPrice,
                    "amount" to orderItem.amount
                )
            },
            otherCharges = rentalOrderViewModel.otherChargeItems.value.map { otherCharges ->
                mapOf(
                    "chargeType" to otherCharges.chargeType,
                    "amount" to otherCharges.amount,
                    "remarks" to otherCharges.remarks
                )

            },
            payments = rentalOrderViewModel.paymentItems.value.map { payment ->
                mapOf(
                    "date" to payment.date,
                    "payMode" to payment.payMode,
                    "amount" to payment.amount,
                    "remarks" to payment.remarks
                )
            },
            totalAmt = rentalOrderViewModel.totalAmount.value,
            discountAmt =
            rentalOrderViewModel.discountAmount.value,
            paidAmt = rentalOrderViewModel.paidAmount.value,
            balanceAmt = rentalOrderViewModel.totalAmount.value - rentalOrderViewModel.paidAmount.value,
            context = context,
            isReturnOrder = rentalOrder?.orderStatus == Constants.RETURNED_ORDER
        )

    } ?: run {
        Toast.makeText(context, "Please choose customer to proceed", Toast.LENGTH_SHORT)
            .show()
        return
    }

    if (orderItems.isNotEmpty()) {
        navController.navigate(Constants.PREVIEW_PDF)
    } else {
        Toast.makeText(context, "Please add items to proceed", Toast.LENGTH_SHORT)
            .show()
    }
}


@Composable
fun TableHeaderCell(text: String, modifier: Modifier) {
    Text(
        text = text,
        modifier = modifier.padding(3.dp),
        color = Color.White,
        fontSize = 16.sp,
        textAlign = TextAlign.Left
    )
}

@Composable
fun TableCell(text: String, modifier: Modifier) {
    Text(
        text = text,
        modifier = modifier.padding(3.dp),
        fontSize = 14.sp,
        textAlign = TextAlign.Left
    )
}

@Composable
fun TableCellBig(text: String, modifier: Modifier) {
//    Column(modifier = modifier) {
    Text(
        text = "$text\nQty * Days * Rate",
        modifier = modifier.padding(3.dp),
        fontSize = 12.sp,
//            fontFamily = FontFamily.SansSerif,
        textAlign = TextAlign.Left
    )

}

@Composable
fun ItemNQty(text: String, modifier: Modifier) {
    Column(modifier) {
        Text(
            text = text,
//            modifier = modifier.padding(3.dp),
            fontSize = 17.sp,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Left
        )

        Text(
            text = "Qty × Days × Rate",
//            modifier = modifier.padding(3.dp),
//            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            textAlign = TextAlign.Left
        )
    }
}

@Composable
fun TableHeadAmtCell(text: String, modifier: Modifier) {
    Text(
        text = text,
        modifier = modifier.padding(3.dp),
        color = Color.White,
        fontSize = 16.sp,
        textAlign = TextAlign.Right
    )
}

@Composable
fun TableAmtCell(text: String, modifier: Modifier) {
    Text(
        text = text,
        modifier = modifier.padding(3.dp),
        fontSize = 14.sp,
        textAlign = TextAlign.Right
    )
}

fun generateInvoicePdfFile(
    context: Context,
    businessName: String,
    businessContact: String,
    invoiceNo: String,
    invoiceDate: String,
    customer: Customer,
    orderItems: List<Map<String, Any>>,
    otherCharges: List<Map<String, Any>>,
    payments: List<Map<String, Any>>,
    totalAmt: Long,
    discountAmt: Long,
    paidAmt: Long,
    balanceAmt: Long,
    isReturnOrder: Boolean
) {
    try {
        val pdfFile =
            File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Invoice.pdf")
        val pdfWriter = PdfWriter(pdfFile)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)

        // ** Business Info **
        document.add(Paragraph(businessName).setBold().setFontSize(18f))
        document.add(Paragraph("Mobile: $businessContact").setFontSize(12f))

        // ** Invoice Header **

        // Create a table with two columns
        val invoiceTable =
            Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f))).useAllAvailableWidth()

// Add Invoice No. (Left Aligned)
        invoiceTable.addCell(
            Cell().add(Paragraph("Invoice No: $invoiceNo"))
                .setTextAlignment(TextAlignment.LEFT)
                .setBorder(Border.NO_BORDER)
        )

// Add Invoice Date (Right Aligned)
        invoiceTable.addCell(
            Cell().add(Paragraph("Invoice Date: $invoiceDate"))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER)
        )

// Add the table to the document
        document.add(invoiceTable)

        // ** Customer Details **
        document.add(
            Paragraph(
                "\nBILL TO\n${customer.name}\nMobile Number: ${customer.mobNo}" +
                        "\nAddress: ${customer.address}"
            )
                .setFontSize(12f)
                .setBold()
        )

        // ** Order Table **
        val table: Table? = if (isReturnOrder) {
            Table(floatArrayOf(3f, 1f, 1f, 1f, 2f, 2f)).useAllAvailableWidth()
        } else {
            Table(floatArrayOf(3f, 1f, 1f, 2f, 2f)).useAllAvailableWidth()
        }

        table?.addCell(
            Cell().add(Paragraph("ITEMS").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY)
        )
        table?.addCell(
            Cell().add(Paragraph("QTY").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY)
        )

        if (isReturnOrder) {
            table?.addCell(
                Cell().add(Paragraph("Rtn QTY").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            )
        }
        table?.addCell(
            Cell().add(Paragraph("DAYS").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY)
        )
        table?.addCell(
            Cell().add(Paragraph("RATE").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY)
        )
        table?.addCell(
            Cell().add(Paragraph("AMOUNT").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY)
        )

        // ** Dynamically Add Order Items **
        for (item in orderItems) {
            table?.addCell(item["productName"].toString())
            table?.addCell(item["quantity"].toString())
            if (isReturnOrder) {
                table?.addCell(item["rtnQty"].toString())
            }
            table?.addCell(item["days"].toString())
            table?.addCell(item["rentalPrice"].toString())
            table?.addCell(item["amount"].toString())
        }
        document.add(table)

        // ** Other Charges Table **
        if (otherCharges.isNotEmpty()) {
            document.add(Paragraph("\nCharges").setBold().setFontSize(14f))
            val chargesTable = Table(floatArrayOf(3f, 2f, 3f)).useAllAvailableWidth()
            chargesTable.addCell(
                Cell().add(Paragraph("Charge").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            )
            chargesTable.addCell(
                Cell().add(Paragraph("Amount").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            )
            chargesTable.addCell(
                Cell().add(Paragraph("Remarks").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            )

            for (charge in otherCharges) {
                chargesTable.addCell(charge["chargeType"].toString())
                chargesTable.addCell("₹${charge["amount"]}")
                chargesTable.addCell(charge["remarks"].toString())
            }
            document.add(chargesTable)
        }

        if (payments.isNotEmpty()) {
            // ** Payment Details Table **
            document.add(Paragraph("\nPayment Details").setBold().setFontSize(14f))
            val paymentTable = Table(floatArrayOf(2f, 3f, 2f, 3f)).useAllAvailableWidth()
            paymentTable.addCell(
                Cell().add(Paragraph("Date").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            )
            paymentTable.addCell(
                Cell().add(Paragraph("Mode").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            )
            paymentTable.addCell(
                Cell().add(Paragraph("Amount").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            )
            paymentTable.addCell(
                Cell().add(Paragraph("Remarks").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            )

            for (payment in payments) {
                paymentTable.addCell(payment["date"].toString())
                paymentTable.addCell(payment["payMode"].toString())
                paymentTable.addCell("₹${payment["amount"]}")
                paymentTable.addCell(payment["remarks"].toString())
            }
            document.add(paymentTable)
        }

        // ** Total, Paid & Balance Amount **
        document.add(
            Paragraph("\nTotal Amount: ₹ $totalAmt").setTextAlignment(TextAlignment.RIGHT).setBold()
                .setFontSize(14f)
        )
        if (discountAmt != 0L) {
            document.add(
                Paragraph("\nDiscount Amount: ₹ $discountAmt").setTextAlignment(TextAlignment.RIGHT)
//                    .setBold()
                    .setFontSize(14f)
            )
        }
        document.add(
            Paragraph("Paid Amount: ₹ $paidAmt").setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(14f)
        )
        document.add(
            Paragraph("Balance Amount: ₹ $balanceAmt").setTextAlignment(TextAlignment.RIGHT)
                .setBold()
                .setFontSize(16f)
        )

        // ** Terms and Conditions **
        document.add(Paragraph("\nTERMS AND CONDITIONS").setBold().setFontSize(12f))
        document.add(Paragraph("1.Payment and Deposit: Full payment is due before or on the specified due date. A refundable security deposit may be required, subject to the condition of the returned materials."))
        document.add(Paragraph("2.Responsibility and Returns: The renter is responsible for the proper use and care of the materials. Damaged or unreturned items will be charged for repair or replacement costs."))

        document.close()
        println("✅ PDF generated at: ${pdfFile.absolutePath}")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}









