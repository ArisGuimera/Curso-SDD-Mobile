package com.aristidevs.cursopremiumandroid.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import coil3.compose.AsyncImage
import com.aristidevs.cursopremiumandroid.ui.theme.BackgroundComponentSelected

/**
 * Imagen del perro. Los creados por el usuario no tienen foto, así que se pinta
 * un placeholder con su inicial. La descripción es distinta en cada caso para
 * que el lector de pantalla no anuncie una foto que no existe.
 */
@Composable
fun DogAvatar(
    name: String,
    image: String,
    shape: Shape,
    initialFontSize: TextUnit,
    modifier: Modifier = Modifier
) {
    if (image.isBlank()) {
        val initial = name.trim().take(1).uppercase()
        Box(
            modifier = modifier
                .clip(shape)
                .background(BackgroundComponentSelected)
                .semantics { contentDescription = "$name, sin foto" },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                color = Color.White,
                fontSize = initialFontSize,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        AsyncImage(
            model = image,
            contentDescription = name,
            modifier = modifier.clip(shape),
            contentScale = ContentScale.Crop
        )
    }
}
