package com.plcoding.barcodescanner.presentation.loginScreen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.plcoding.barcodescanner.utils.Constants.BARCODE_SCREEN
import com.plcoding.barcodescanner.utils.Constants.STATIC_PASSWORD
import com.plcoding.barcodescanner.utils.saveTeamNumberString
import com.plcoding.barcodescanner.utils.saveUsername

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun LoginScreen(
    navController: NavController? = null
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var teamNumber by remember { mutableStateOf("") }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.size(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.size(10.dp))

        OutlinedTextField(
            value = teamNumber,
            onValueChange = { teamNumber = it },
            label = { Text("Team Number") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.padding(16.dp)
        )

        Button(onClick = {
            onContinueClicked(
                context,
                password,
                username,
                teamNumber,
                navController
            )
        }) {
            Text("Continue")
        }
    }
}

private fun onContinueClicked(
    context: android.content.Context,
    password: String,
    username: String,
    teamNumber: String,
    navController: NavController?
) {
    when {
        password != STATIC_PASSWORD -> {
            Toast.makeText(context, "Password error", Toast.LENGTH_SHORT).show()
            return
        }

        username.trim().isEmpty() -> {
            Toast.makeText(context, "Please enter a username", Toast.LENGTH_SHORT).show()
        }

        teamNumber.trim().isEmpty() -> {
            Toast.makeText(context, "Please enter a team number", Toast.LENGTH_SHORT).show()
        }

        else -> {
            saveUsername(context, username.trim())
            Log.e("username", username.trim())
            saveTeamNumberString(context, teamNumber.trim())
            Log.e("username", teamNumber.trim())
            navController?.popBackStack()
            navController?.navigate(BARCODE_SCREEN)
        }
    }
}
