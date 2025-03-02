package com.krm.rentalservices.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize

/*@Composable
fun <T> AutoCompleteTextField(
    items: List<T>,                   // List of objects
    itemToString: (T) -> String,      // Convert object to display string
    itemId: (T) -> String,            // Extract unique ID from object
    selectedId: String?,              // Pre-selected ID
    label: String,                    // Default label
    onItemSelected: (T) -> Unit,      // Callback when an item is selected
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }
    var isDropdownOpen by remember { mutableStateOf(false) }
    val filteredItems = items.filter { itemToString(it).contains(text, ignoreCase = true) }

    // Set initial selection if a valid ID is passed
    LaunchedEffect(selectedId) {
        selectedId?.let { id ->
            items.find { itemId(it) == id }?.let {
                text = itemToString(it) // Set text to selected item's name
            }
        }
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                isDropdownOpen = it.isNotEmpty()
            },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { isDropdownOpen = !isDropdownOpen }) {
                    Icon(
                        imageVector = if (isDropdownOpen) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Dropdown"
                    )
                }
            }
        )

        DropdownMenu(
            expanded = isDropdownOpen,
            onDismissRequest = { isDropdownOpen = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            filteredItems.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = itemToString(item)) },  // New API
                    onClick = {
                        text = itemToString(item)
                        isDropdownOpen = false
                        onItemSelected(item)
                    }
                )
            }
        }
    }
}*/

/*@Composable
fun <T> AutoCompleteTextField(
    items: List<T>,                   // List of objects
    itemToString: (T) -> String,      // Convert object to display string
    itemId: (T) -> String,            // Extract unique ID from object
    selectedId: String?,              // Pre-selected ID
    label: String,                    // Default label
    onItemSelected: (T) -> Unit,      // Callback when an item is selected
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }
    var isDropdownOpen by remember { mutableStateOf(false) }
    val filteredItems = items.filter { itemToString(it).contains(text, ignoreCase = true) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current

    // Set initial selection if a valid ID is passed
    LaunchedEffect(selectedId) {
        selectedId?.let { id ->
            items.find { itemId(it) == id }?.let {
                text = itemToString(it) // Set text to selected item's name
            }
        }
    }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                isDropdownOpen = filteredItems.isNotEmpty()  // Show dropdown only if there are matches
            },
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { isDropdownOpen = it.isFocused }, // Open dropdown when focused
            trailingIcon = {
                IconButton(onClick = { isDropdownOpen = !isDropdownOpen }) {
                    Icon(
                        imageVector = if (isDropdownOpen) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Dropdown"
                    )
                }
            }
        )

        // Overlay Dropdown Menu
        DropdownMenu(
            expanded = isDropdownOpen,
            onDismissRequest = { isDropdownOpen = false },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 250.dp) // Limit height
        ) {
            filteredItems.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = itemToString(item)) },
                    onClick = {
                        text = itemToString(item)
                        isDropdownOpen = false
                        focusManager.clearFocus() // Close keyboard
                        onItemSelected(item)
                    }
                )
            }
        }
    }
}*/




@Composable
fun <T> AutoCompleteTextField(
    items: List<T>,                   // List of objects
    itemToString: (T) -> String,      // Convert object to display string
    itemId: (T) -> String,            // Extract unique ID from object
    selectedId: String?,              // Pre-selected ID
    label: String,                    // Default label
    onItemSelected: (T) -> Unit,      // Callback when an item is selected
    modifier: Modifier = Modifier,
    isEnabled : Boolean
) {
    var text by remember { mutableStateOf("") }
    var isDropdownOpen by remember { mutableStateOf(false) }
    val filteredItems = items.filter { itemToString(it).contains(text, ignoreCase = true) }

    // Set initial selection if a valid ID is passed
    if (selectedId != null) {
        LaunchedEffect(selectedId) {
            selectedId.let { id ->
                items.find { itemId(it) == id }?.let {
                    text = itemToString(it) // Set text to selected item's name
                }
            }
        }
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            enabled = isEnabled,
            value = text,
            onValueChange = {
                text = it
                isDropdownOpen = filteredItems.isNotEmpty()  // Only show if there are matches
            },
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { isDropdownOpen = !isDropdownOpen }) {
                    Icon(
                        imageVector = if (isDropdownOpen) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Dropdown"
                    )
                }
            }
        )

        // Custom dropdown using LazyColumn
        if (isDropdownOpen) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            ) {
                LazyColumn(
//                    modifier = Modifier.heightIn(max = 200.dp)  // Limit height for better UX
                    modifier = Modifier.wrapContentHeight()  // Limit height for better UX
                ) {
                    items(filteredItems) { item ->
                        DropdownMenuItem(
                            text = { Text(text = itemToString(item)) },
                            onClick = {
                                text = itemToString(item)
                                isDropdownOpen = false
                                onItemSelected(item)
                            },
                            enabled = isEnabled
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun <T> AutoCompleteTextView(
    modifier: Modifier = Modifier,
    items: List<T>,
    labelSelector: (T) -> String,  // Function to extract label for display
    onItemSelected: (T) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val filteredItems = items.filter { labelSelector(it).contains(text, ignoreCase = true) }

    Column(modifier) {
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                expanded = it.isNotEmpty()
            },
            label = { Text("Select Item") },
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Dropdown Icon"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded && filteredItems.isNotEmpty(),
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            filteredItems.forEach { item ->
                DropdownMenuItem(
                    text = { Text(labelSelector(item)) },
                    onClick = {
                        text = labelSelector(item)  // Update text field with selected item label
                        expanded = false
                        onItemSelected(item) // Return selected object
                    }
                )
            }
        }
    }
}


