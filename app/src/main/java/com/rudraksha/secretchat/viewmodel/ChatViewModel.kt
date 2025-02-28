package com.rudraksha.secretchat.viewmodel

import androidx.lifecycle.ViewModel
import com.rudraksha.secretchat.data.model.Message

class ChatViewModel: ViewModel() {
}

data class ChatUiState(
    var messageList: MutableList<Message>
)