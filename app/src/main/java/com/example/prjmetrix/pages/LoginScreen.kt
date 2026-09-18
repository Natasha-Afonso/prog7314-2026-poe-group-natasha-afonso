package com.example.prjmetrix.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.prjmetrix.AuthViewModel
import com.example.prjmetrix.R

// Colors matching the XML
val BgBlue = Color(0xFFC1DBE8)
val BrandBrown = Color(0xFF43302E)
val BottomBarYellow = Color(0xFFFFF1B5)
val DividerGray = Color(0xFFDADFE1)
val LinkTeal = Color(0xFF0F6E63)

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var webClientId = stringResource(R.string.default_web_client_id)

    val uiState by authViewModel.uiState.collectAsState()

    //when user successfully logs in the user is taken to the onboarding/home
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated)
        {
            navController.navigate("settings") {
                popUpTo("login") {inclusive = true}
            }
        }
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomBar(onSignUpClick = { navController.navigate("signup") })
        },
        containerColor = BgBlue
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon placeholder
            Box(
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth(0.4f)
                    .background(BrandBrown)
                .clip(RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = "App icon",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Sign in to MeTrix",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = BrandBrown
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "MeTrix is you.",
                fontSize = 14.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email label + field
            Text(
                text = "Email address",
                fontWeight = FontWeight.Bold,
                color = BrandBrown,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("angela@gmail.com") },
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedBorderColor = BrandBrown,
                    cursorColor = BrandBrown
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Password label + field
            Text(
                text = "Password",
                fontWeight = FontWeight.Bold,
                color = BrandBrown,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("********") },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedBorderColor = BrandBrown,
                    cursorColor = BrandBrown
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            //ui design for error message displaying
            uiState.errorMessage?.let {message ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = message,
                    color = Color(0xFFB00020),
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Sign in button
            Button(
                onClick = { authViewModel.signIn(email, password)  },
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBrown),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp).width(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Sign in",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Forgot password?",
                color = BrandBrown,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // OR divider
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Divider(color = DividerGray, thickness = 1.dp)
                Text(
                    text = "OR",
                    fontSize = 13.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .background(BgBlue)
                        .padding(horizontal = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Google sign in (image placeholder)
            Image(
                //onClick = {  },
                painter = painterResource(id = R.drawable.gsignin),
                contentDescription = "Sign in with Google",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .padding(top = 20.dp)
                    .clickable(enabled = !uiState.isLoading) {
                        authViewModel.signInWithGoogle(webClientId)}
            )
        }
    }
}

@Composable
fun BottomBar(onSignUpClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BottomBarYellow)
            .padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Don't have an account?",
            color = Color(0xFF1B1F22)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Sign up",
            fontWeight = FontWeight.Bold,
            color = LinkTeal,
            modifier = Modifier.padding(4.dp).clickable{onSignUpClick()}
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun LoginScreenPreview() {
//    LoginScreen()
//}