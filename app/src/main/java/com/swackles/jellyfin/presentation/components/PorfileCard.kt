package com.swackles.jellyfin.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.swackles.jellyfin.R
import com.swackles.jellyfin.session.Session
import java.util.UUID

@Composable
fun ProfileCard(session: Session, active: Boolean, onClick: (UUID) -> Unit) {
    OutlinedCard(
        border = if (active) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else CardDefaults.outlinedCardBorder(),
        modifier = Modifier
            .size(ProfileCardProps.SIZE)
            .clickable { onClick(session.id) }
    ) {
        if (session.profileImageUrl == null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Outlined.AccountCircle,
                    contentDescription = session.username,
                    modifier = Modifier
                        .width(48.dp)
                        .height(48.dp)
                )
            }
        } else {
            Image(
                painter =
                    if (LocalInspectionMode.current) painterResource(R.drawable.preview_logo)
                    else rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(session.profileImageUrl).size(Size.ORIGINAL).build()
                    ),
                contentDescription = "${session.username}'s profile image",
                contentScale = ContentScale.Crop
            )
        }
    }
}


object ProfileCardProps  {
    val SIZE = 120.dp
}