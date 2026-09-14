package com.aristidevs.cursopremiumandroid.presentation.detail

import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aristidevs.cursopremiumandroid.R
import com.aristidevs.cursopremiumandroid.domain.model.DogDetailModel
import com.aristidevs.cursopremiumandroid.presentation.common.DogAvatar
import com.aristidevs.cursopremiumandroid.ui.theme.BackgroundApp
import com.aristidevs.cursopremiumandroid.ui.theme.BackgroundComponent
import com.aristidevs.cursopremiumandroid.ui.theme.ControlColor
import com.aristidevs.cursopremiumandroid.ui.theme.PrimaryButton
import com.aristidevs.cursopremiumandroid.ui.theme.SecondaryText

@Composable
fun DogDetailScreen(
    id: Int, onBackSelected: () -> Unit, viewModel: DogDetailViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(id) {
        viewModel.loadDog(id)
    }

    DogDetailContent(uiState, onBackSelected, onRetry = { viewModel.onRetry() })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DogDetailContent(
    uiState: DogDetailUiState,
    onBackSelected: () -> Unit,
    onRetry: () -> Unit
) {

    val title = if (uiState is DogDetailUiState.Success) {
        uiState.dogDetail.name
    } else {
        "Detalle"
    }

    Scaffold(
        containerColor = BackgroundApp, topBar = {
            TopAppBar(
                title = { Text(title) }, navigationIcon = {
                    IconButton(onBackSelected) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow),
                            contentDescription = "back"
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundApp,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is DogDetailUiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(uiState.message, color = SecondaryText)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = onRetry,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryButton)
                        ) {
                            Text("Reintentar", color = Color.White)
                        }
                    }
                }

                DogDetailUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is DogDetailUiState.Success -> {
                    DogDetailSuccessContent(uiState.dogDetail)
                }
            }
        }
    }

}

@Composable
fun DogDetailSuccessContent(dogDetail: DogDetailModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        DogAvatar(
            name = dogDetail.name,
            image = dogDetail.image,
            shape = RoundedCornerShape(16.dp),
            initialFontSize = 96.sp,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = dogDetail.name,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = dogDetail.breed,
            fontSize = 18.sp,
            color = ControlColor
        )

        Spacer(Modifier.height(24.dp))

        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundComponent)
        ) {
            Column(Modifier.padding(20.dp)) {
                DetailRow("Edad", dogDetail.age.toString() )
                DetailRow("Peso", dogDetail.weight )
                DetailRow("Origen", dogDetail.origin )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(dogDetail.description, fontSize = 16.sp, color = Color.White)
        Spacer(Modifier.height(8.dp))
        Text(dogDetail.temperament, fontSize = 16.sp, color = Color.White)
    }
}

@Composable
fun DetailRow(title:String, value:String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(title, modifier = Modifier.weight(1f), color = SecondaryText)
        Text(value, color = Color.White, fontWeight = FontWeight.Medium)
    }
}