package com.market.trameo.core.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.market.trameo.ui.theme.Terracota

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val contentDescription: String
)

@Composable
fun TrameoBottomNavigation(
    items: List<BottomNavItem>,
    currentRoute: String,
    onItemClick: (BottomNavItem) -> Unit,
    onCenterClick: () -> Unit,
    centerIcon: ImageVector,
    centerContentDescription: String,
    modifier: Modifier = Modifier
) {
    val leftItems = items.take(items.size / 2)
    val rightItems = items.drop(items.size / 2)

    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                leftItems.forEach { item ->
                    BottomItem(
                        item = item,
                        selected = item.route == currentRoute,
                        onClick = { onItemClick(item) }
                    )
                }

                Spacer(modifier = Modifier.width(64.dp))

                rightItems.forEach { item ->
                    BottomItem(
                        item = item,
                        selected = item.route == currentRoute,
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onCenterClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-14).dp)
                .size(62.dp),
            containerColor = Terracota,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(
                imageVector = centerIcon,
                contentDescription = centerContentDescription
            )
        }
    }
}

@Composable
private fun BottomItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (selected) Terracota else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.contentDescription,
            tint = tint
        )
        Text(
            text = item.title,
            style = MaterialTheme.typography.labelSmall,
            color = tint,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}
