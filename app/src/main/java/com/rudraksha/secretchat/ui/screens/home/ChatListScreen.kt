package com.rudraksha.secretchat.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rudraksha.secretchat.R
import com.rudraksha.secretchat.data.model.ChatItem
import com.rudraksha.secretchat.ui.screens.chat.SendChatRequest
import com.rudraksha.secretchat.ui.theme.circleShape
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ChatListScreen(
    modifier: Modifier = Modifier,
    onChatItemClick: (String) -> Unit = {}
) {
    var longPressed by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val chatList by remember { mutableStateOf(getChatList()) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Secret Chat", color = MaterialTheme.colorScheme.onBackground) },
                actions = {
                    if (longPressed) {
                        IconButton(onClick = { /* Camera action */ }) {
                            Icon(
                                Icons.Default.PushPin,
                                contentDescription = "Pin",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    IconButton(onClick = { /* Camera action */ }) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Camera",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { /* More options */ }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        bottomBar = {
            // Bottom Navigation
            BottomNavigationBar()
        },
        floatingActionButton = {
            // Floating Action Button
            FloatingActionButton(
                onClick = { scope.launch { showDialog = true } },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Icon(
                    Icons.Default.PersonAdd,
                    contentDescription = "New Chat",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        floatingActionButtonPosition = if (chatList.isEmpty()) FabPosition.Center else FabPosition.End
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Chat List
            if (showDialog) {
                SendChatRequest()
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .alpha(if (showDialog) 0.7f else 1f)
            ) {
                item {
                    // Search Bar
                    SearchBar()
                }
                if (chatList.isEmpty()) {
                    item { }
                } else {
                    items(chatList) { chat ->
                        ChatRow(
                            chat = chat,
                            delete = { /* Handle delete action */ },
                            onClick = onChatItemClick,
                            onLongPress = { longPressed = !longPressed }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    onSearchClick: (String) -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    TextField(
        value = searchText,
        onValueChange = {
            searchText = it
        },
        placeholder = {
            Text(
                "Search chats, calls, and more",
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.background),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearchClick(searchText)
            }
        )
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatRow(
    chat: ChatItem,
    delete: (ChatItem) -> Unit = {},
    onClick: (String) -> Unit = {},
    onLongPress: () -> Unit = {}
) {
    var longPressed by remember { mutableStateOf(false) }
    val elevation by animateDpAsState(
        targetValue = 4.dp,
        label = ""
    )
    val swipeState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.StartToEnd) {
                delete(chat)
                true
            } else {
                false
            }
        }
    )
    SwipeToDismissBox(
        state = swipeState,
        backgroundContent = {
            val color by animateColorAsState(
                when (swipeState.targetValue) {
                    SwipeToDismissBoxValue.Settled -> MaterialTheme.colorScheme.background
                    else -> MaterialTheme.colorScheme.tertiary
                },
                label = ""
            )
            Box(
                Modifier
                .fillMaxSize()
                .background(color)
            )
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .combinedClickable(
                    onClick = {
                        longPressed = false
                        onClick(chat.id)
                    },
                    onLongClick = {
                        onLongPress()
                        longPressed = true
                    }
                )
                .padding(if (longPressed) elevation else 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = chat.profilePic),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(50.dp)
                    .clip(circleShape)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.tertiary,
                        circleShape
                    )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    chat.name,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    chat.lastMessage,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(chat.time, color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp)
                if (chat.unreadCount > 0) {
                    BadgeBox(chat.unreadCount)
                }
            }
        }
    }
}

@Composable
fun EmptyChatList() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("You have not started any chat yet.")
    }
}

@Composable
fun BadgeBox(count: Int) {
    AnimatedVisibility(
        visible = count > 0,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer, shape = circleShape)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("$count", color = MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BottomNavigationBar() {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        val items = listOf(
            "Chats",
//            "Updates", "Communities",
            "Calls"
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.Chat,
                    contentDescription = items[0],
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            label = { Text(items[0]) },
            selected = items[0] == "Chats",
            onClick = { /* Handle click */ }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.Call,
                    contentDescription = items[1],
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            label = { Text(items[1]) },
            selected = items[1] == "Chats",
            onClick = { /* Handle click */ }
        )

    }
}

fun getChatList() = listOf(
    ChatItem(
        id = UUID.randomUUID().toString(),
        "+91 94553 66424 (You)",
        "import androidx.compose.foundati...",
        "Yesterday",
        0,
        R.drawable.profile_pic
    ),
    ChatItem(
        id = UUID.randomUUID().toString(),
        "Coding Club India : Coder",
        "Job At Zomato Success St...",
        "09:46",
        6,
        R.drawable.profile_pic
    ),
    ChatItem(
        id = UUID.randomUUID().toString(),"Sandhya 215", "📷 Photo", "07:16", 1, R.drawable.profile_pic),
    ChatItem(
        id = UUID.randomUUID().toString(),"DAD", "📄 SUBHASH CHAND - CV.doc", "Yesterday", 0, R.drawable.profile_pic),
    ChatItem(
        id = UUID.randomUUID().toString(),"Amresh Thailand", "❌ Missed voice call", "Yesterday", 0, R.drawable.profile_pic),
    ChatItem(
        id = UUID.randomUUID().toString(),
        "LetsUpgrade Community",
        "📢 Help Us Build the...",
        "Yesterday",
        1,
        R.drawable.profile_pic
    ),
    ChatItem(
        id = UUID.randomUUID().toString(),"Ujjwal Jr", "👍 You reacted to \"Audio\"", "Yesterday", 0, R.drawable.profile_pic),
    ChatItem(
        id = UUID.randomUUID().toString(),"Access Denied Official", "Priyanka: Yes", "Yesterday", 3, R.drawable.profile_pic)
)

