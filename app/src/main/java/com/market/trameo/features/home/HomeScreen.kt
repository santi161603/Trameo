package com.market.trameo.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.market.trameo.R
import com.market.trameo.core.component.BottomNavItem
import com.market.trameo.core.component.TrameoBottomNavigation
import com.market.trameo.core.navigation.Routes
import com.market.trameo.core.theme.Marfil
import com.market.trameo.core.theme.MarfilVariant
import com.market.trameo.core.theme.Terracota
import com.market.trameo.domain.model.ModerationStatus
import com.market.trameo.domain.model.ObjectCategory
import com.market.trameo.domain.model.SwapObject
import com.market.trameo.domain.model.User

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onTruequesClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onMisObjetosClick: () -> Unit = {},
    onObjectClick: (String) -> Unit = {},
    onPublicarClick: () -> Unit = {},
    onChatsClick: () -> Unit = {},
    onMapaClick: () -> Unit = {}
) {
    val objects by viewModel.items.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    Scaffold(
        containerColor = Marfil,
        bottomBar = {
            TrameoBottomNavigation(
                items = listOf(
                    BottomNavItem(
                        route = Routes.HOME,
                        title = stringResource(id = R.string.home_bottom_home),
                        icon = Icons.Default.Home,
                        contentDescription = stringResource(id = R.string.home_bottom_home)
                    ),
                    BottomNavItem(
                        route = Routes.MIS_OBJETOS,
                        title = stringResource(id = R.string.home_bottom_mis_objetos),
                        icon = Icons.AutoMirrored.Filled.List,
                        contentDescription = stringResource(id = R.string.home_bottom_mis_objetos)
                    ),
                    BottomNavItem(
                        route = Routes.TRUEQUES,
                        title = stringResource(id = R.string.home_bottom_trueques),
                        icon = Icons.Default.Autorenew,
                        contentDescription = stringResource(id = R.string.home_bottom_trueques)
                    ),
                    BottomNavItem(
                        route = Routes.PERFIL,
                        title = stringResource(id = R.string.home_bottom_perfil),
                        icon = Icons.Default.Person,
                        contentDescription = stringResource(id = R.string.home_bottom_perfil)
                    )
                ),
                currentRoute = Routes.HOME,
                onItemClick = { item ->
                    if (item.route == Routes.TRUEQUES) {
                        onTruequesClick()
                    }
                    if (item.route == Routes.PERFIL) {
                        onPerfilClick()
                    }
                    if (item.route == Routes.MIS_OBJETOS) {
                        onMisObjetosClick()
                    }
                },
                onCenterClick = onPublicarClick,
                centerIcon = Icons.Default.Add,
                centerContentDescription = stringResource(id = R.string.home_bottom_publicar)
            )
        }
    ) { padding ->
        HomeContent(
            padding = padding,
            items = objects,
            query = query,
            selectedCategory = selectedCategory,
            onQueryChange = viewModel::onSearchQueryChange,
            onCategoryChange = viewModel::onCategoryFilterChange,
            currentUser = currentUser,
            onObjectClick = onObjectClick,
            onChatsClick = onChatsClick,
            onMapaClick = onMapaClick
        )
    }
}

@Composable
private fun HomeContent(
    padding: PaddingValues,
    items: List<SwapObject>,
    query: String,
    selectedCategory: ObjectCategory?,
    onQueryChange: (String) -> Unit,
    onCategoryChange: (ObjectCategory?) -> Unit,
    currentUser: User?,
    onObjectClick: (String) -> Unit,
    onChatsClick: () -> Unit,
    onMapaClick: () -> Unit
) {
    val favorites = remember { mutableStateMapOf<String, Boolean>() }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = rememberLazyGridState(),
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Marfil),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(modifier = Modifier.fillMaxWidth()) {
                HomeHeader(
                    onChatsClick = onChatsClick,
                    currentUser = currentUser
                )
                Spacer(modifier = Modifier.height(12.dp))
                HomeSearchBox(
                    query = query,
                    onQueryChange = onQueryChange
                )
                Spacer(modifier = Modifier.height(12.dp))
                CategoryFilterRow(
                    selectedCategory = selectedCategory,
                    onCategoryChange = onCategoryChange
                )
                Spacer(modifier = Modifier.height(14.dp))
                HomeBanner(onClick = onMapaClick)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.home_nearby_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        gridItems(items, key = { it.id }) { item ->
            val isFavorite = favorites[item.id] == true
            HomeObjectCard(
                item = item,
                isFavorite = isFavorite,
                onFavoriteClick = { favorites[item.id] = !isFavorite },
                onClick = { onObjectClick(item.id) }
            )
        }

        if (items.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    tonalElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.home_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    onChatsClick: () -> Unit,
    currentUser: User?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val context = LocalContext.current
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(currentUser?.profilePhotoUri ?: "https://picsum.photos/seed/trameo-profile/200/200")
                .crossfade(true)
                .build(),
            contentDescription = stringResource(id = R.string.home_profile_content_description),
            placeholder = painterResource(id = R.drawable.ic_launcher_background),
            error = painterResource(id = R.drawable.ic_launcher_foreground),
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .border(1.dp, Color.White, CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(
                    id = R.string.home_greeting,
                    currentUser?.name ?: stringResource(id = R.string.home_default_name)
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(id = R.string.home_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = stringResource(id = R.string.home_notifications)
                )
            }
            IconButton(onClick = onChatsClick) {
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = stringResource(id = R.string.home_chats)
                )
            }
        }
    }
}

@Composable
private fun HomeSearchBox(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text(stringResource(id = R.string.home_search_label)) },
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = stringResource(id = R.string.home_search_content_description)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = true
    )
}

@Composable
private fun CategoryFilterRow(
    selectedCategory: ObjectCategory?,
    onCategoryChange: (ObjectCategory?) -> Unit
) {
    val categories = listOf<ObjectCategory?>(null) + ObjectCategory.entries

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories, key = { it?.name ?: "TODOS" }) { category ->
            val selected = selectedCategory == category
            FilterChip(
                selected = selected,
                onClick = { onCategoryChange(category) },
                label = {
                    Text(
                        text = category?.let { stringResource(id = it.labelRes()) }
                            ?: stringResource(id = R.string.home_category_all)
                    )
                }
            )
        }
    }
}

@Composable
private fun HomeBanner(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(185.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        val context = LocalContext.current
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data("https://picsum.photos/seed/trameo-home-banner/1000/600")
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(id = R.string.home_banner_content_description),
                placeholder = painterResource(id = R.drawable.ic_launcher_background),
                error = painterResource(id = R.drawable.ic_launcher_foreground),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.68f)
                    .background(Color.Black.copy(alpha = 0.28f))
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.home_banner_title),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = R.string.home_banner_subtitle),
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun HomeObjectCard(
    item: SwapObject,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    val statusLabel = when (item.moderationStatus) {
        ModerationStatus.PUBLICADO -> stringResource(id = R.string.common_status_publicado)
        ModerationStatus.PENDIENTE_VERIFICACION -> stringResource(id = R.string.common_status_pendiente)
    }

    val statusColor = when (item.moderationStatus) {
        ModerationStatus.PUBLICADO -> Terracota
        ModerationStatus.PENDIENTE_VERIFICACION -> Color(0xFFD38A1F)
    }

    val conditionColor = when (item.condition) {
        com.market.trameo.domain.model.ObjectCondition.NUEVO -> Color(0xFF2E7D32)
        com.market.trameo.domain.model.ObjectCondition.COMO_NUEVO -> Color(0xFF1976D2)
        com.market.trameo.domain.model.ObjectCondition.BUENO -> Color(0xFFF9A825)
        com.market.trameo.domain.model.ObjectCondition.REGULAR -> Color(0xFF8D6E63)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        val context = LocalContext.current
        Column {
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(item.photos.firstOrNull())
                        .crossfade(true)
                        .build(),
                    contentDescription = item.name,
                    placeholder = painterResource(id = R.drawable.ic_launcher_background),
                    error = painterResource(id = R.drawable.ic_launcher_foreground),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop
                )

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = conditionColor.copy(alpha = 0.9f),
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = stringResource(id = item.condition.labelRes()),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name.compactTitle(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    IconButton(onClick = onFavoriteClick, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = stringResource(id = R.string.home_favorite),
                            tint = if (isFavorite) Color(0xFFE53935) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = stringResource(id = item.category.labelRes()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = stringResource(id = R.string.home_by_owner, item.ownerDisplayName()),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = statusColor.copy(alpha = 0.14f),
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Text(
                        text = statusLabel,
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

private fun com.market.trameo.domain.model.ObjectCategory.labelRes(): Int = when (this) {
    com.market.trameo.domain.model.ObjectCategory.TECNOLOGIA -> R.string.object_category_tecnologia
    com.market.trameo.domain.model.ObjectCategory.LIBROS -> R.string.object_category_libros
    com.market.trameo.domain.model.ObjectCategory.ROPA -> R.string.object_category_ropa
    com.market.trameo.domain.model.ObjectCategory.HOGAR -> R.string.object_category_hogar
    com.market.trameo.domain.model.ObjectCategory.DEPORTES -> R.string.object_category_deportes
}

private fun com.market.trameo.domain.model.ObjectCondition.labelRes(): Int = when (this) {
    com.market.trameo.domain.model.ObjectCondition.NUEVO -> R.string.object_condition_nuevo
    com.market.trameo.domain.model.ObjectCondition.COMO_NUEVO -> R.string.object_condition_como_nuevo
    com.market.trameo.domain.model.ObjectCondition.BUENO -> R.string.object_condition_bueno
    com.market.trameo.domain.model.ObjectCondition.REGULAR -> R.string.object_condition_regular
}

private fun SwapObject.ownerDisplayName(): String = when (ownerId) {
    "seed-user-1" -> "Santiago"
    else -> ownerId
}

private fun String.compactTitle(): String {
    return if (length > 9) {
        take(7) + "..."
    } else {
        this
    }
}
