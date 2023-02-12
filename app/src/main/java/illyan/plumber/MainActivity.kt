package illyan.plumber

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import illyan.plumber.ui.theme.PlumberTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlumberTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalculatorScreen(modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Dataset
        val floats = remember { mutableStateListOf<Float>() }
        var average by remember { mutableStateOf(0f) }
        var mean by remember { mutableStateOf(0f) }

        // Building pipe
        val pipe = remember {
            Pipe.Builder<List<Float>, List<Float>>()
                .setFitting { it }
                .addFilter {
                    average = it.average().toFloat()
                    it
                }
                .addFilter {
                    if (it.isEmpty()) return@addFilter it
                    val sorted = it.sorted()
                    mean = if (sorted.size.mod(2) == 1) {
                        sorted[sorted.size / 2]
                    } else {
                        (sorted[sorted.size / 2] + sorted[(sorted.size / 2 - 1).coerceIn(0, sorted.size)]) / 2
                    }
                    it
                }
                .addOutputPipe {
                    Log.d("Calculator", "Average = $average, Mean = $mean")
                }
                .build()
        }
        LaunchedEffect(floats.size) {
            pipe.processData(floats)
        }

        // UI
        Text(text = "Average of ${floats.size} floats = $average")
        Text(text = "Mean = $mean")
        Text(text = "Add this float to the others")
        var numberAsString by remember { mutableStateOf("") }
        val addNumber = {
            numberAsString.toFloatOrNull()?.let { floats.add(it) }
            numberAsString = ""
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TextField(
                value = numberAsString,
                onValueChange = { numberAsString = it },
                label = { Text(text = "float value") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                ),
                maxLines = 1,
                singleLine = true,
                keyboardActions = KeyboardActions(
                    onDone = { addNumber() }
                )
            )
            Button(
                onClick = addNumber
            ) {
                Text(text = "Add")
            }
        }
        LazyColumn {
            items(floats) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "$it"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorScreenPreview() {
    PlumberTheme {
        CalculatorScreen()
    }
}