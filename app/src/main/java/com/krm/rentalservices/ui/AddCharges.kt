package com.krm.rentalservices.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.krm.rentalservices.viewmodel.RentalOrderViewModel

@Composable
fun AddCharges(
    rentalOrderViewModel: RentalOrderViewModel, navController: NavHostController
) {
    var chargeAmt by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }
    var selectedChargeType by remember { mutableStateOf("") }
//    var amount by remember { mutableStateOf("0") }
//    val selectedPayMode = rentalOrderViewModel.selectedPayMode.collectAsState().value
    var formIsValid by remember { mutableStateOf(true) }
    var validationMessage by remember { mutableStateOf("") }

    val otherChargesList: List<String> = listOf("Cleaning Charge", "Damage Charges")
    val otherChargesItems by rentalOrderViewModel.otherChargeItems.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        OtherChargesSpinner(
            otherChargesList, selectedPayMode = selectedChargeType, onChargeTypeSelected = {
                selectedChargeType = it
            }, modifier = Modifier.weight(1f)
        )

        OutlinedTextField(
            value = chargeAmt,
            onValueChange = { chargeAmt = it },
            label = { Text("Charges amount") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            isError = chargeAmt.isEmpty() && !formIsValid,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = remarks,
            onValueChange = { remarks = it },
            label = { Text("Enter Remarks") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            isError = remarks.isEmpty() && !formIsValid,
            modifier = Modifier.fillMaxWidth()
        )


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                rentalOrderViewModel.addOtherChargesItem(
                    otherChargeType = selectedChargeType,
                    remarks = remarks,
                    amount = chargeAmt.toLong()
                )
            }
            ) {
                Text("Add Charges")
            }

            /*Button(
            onClick = {
                navController.popBackStack()
            }
//                        , modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }*/
        }

        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 8.dp)
        ) {
            TableHeaderCell("Charge Type", Modifier.weight(0.8f))
            TableHeaderCell("Remarks", Modifier.weight(1f))
            TableHeadAmtCell("Amount", Modifier.weight(0.8f))
        }


        // LazyColumn for Payment Items
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 8.dp)
        ) {
            items(otherChargesItems) { chargeItem ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TableCell(chargeItem.chargeType, Modifier.weight(0.8f))
                    TableCell(chargeItem.remarks, Modifier.weight(1f))
                    TableAmtCell(
                        "₹${chargeItem.amount}", Modifier.weight(0.8f)
                    )
                }
            }
        }

        Text(
            text = "Total Charges Amount : ₹ " + rentalOrderViewModel.otherChargesTotalAmount.collectAsState().value.toString(),
            color = Color.Black,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
                .wrapContentSize(Alignment.BottomEnd)
//                .align(Alignment.CenterHorizontally)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherChargesSpinner(
    modes: List<String>, selectedPayMode: String?, // Pass the default product if available
    onChargeTypeSelected: (String) -> Unit, modifier: Modifier
) {
    var expanded by remember { mutableStateOf(false) } // Controls dropdown visibility
    var selectedItem by remember { mutableStateOf(selectedPayMode ?: modes.firstOrNull()) }

    ExposedDropdownMenuBox(expanded = expanded,
        onExpandedChange = { expanded = !expanded } // Toggle dropdown visibility
    ) {
        OutlinedTextField(
            value = selectedItem ?: "Select", // Show default text
            onValueChange = {},
            readOnly = true, // Makes the field uneditable
            label = { Text("Select Charge Type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = modifier
//            /*modifier = Modifier
                .menuAnchor() // Ensures dropdown aligns correctly
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded, onDismissRequest = { expanded = false }, modifier = modifier
        ) {
            modes.forEach { chargeType ->
                DropdownMenuItem(text = { Text(chargeType) }, onClick = {
                    selectedItem = chargeType // Update selected item
                    expanded = false // Close dropdown
                    onChargeTypeSelected(chargeType) // Notify parent about the selection
                })
            }
        }
    }
}