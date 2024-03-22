package com.swackles.jellyfin.presentation.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import com.swackles.jellyfin.data.room.models.User
import com.swackles.jellyfin.presentation.common.components.Image

@Composable
internal fun ProfileCard(user: User, active: Boolean, onClick: (userId: Long) -> Unit) {
    OutlinedCard(
        border = if (active) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else CardDefaults.outlinedCardBorder(),
        modifier = Modifier
            .padding(8.dp)
            .size(120.dp)
            .clickable { onClick(user.id) }
    ) {
        if (user.profileImageUrl == null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Outlined.AccountCircle,
                    contentDescription = user.username,
                    modifier = Modifier
                        .width(48.dp)
                        .height(48.dp)
                )
            }
        } else {
            Image(
                url = user.profileImageUrl!!,
                description = "${user.username}'s profile image",
            )
        }
    }
    Spacer(Modifier.width(64.dp))
}
