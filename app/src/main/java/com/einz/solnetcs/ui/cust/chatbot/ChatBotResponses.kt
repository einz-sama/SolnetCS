package com.einz.solnetcs.ui.cust.chatbot

// data is ResponseText, Redirect, Button, and Button Redirect
// ResponseText contains the response that the chatbot will give to the user
// Redirect will be a ChatBotQuestion object that will be asked next
// Button is a boolean that will determine if the response will have a button or not
// Button Redirect will be a number representing which Activity intent will be called when the button is clicked

class ChatBotResponse(
    val responseText: String,
    val redirect: ChatBotQuestion?,
    val button: Boolean,
    val buttonRedirect: Int,
    val problem: String?=""
){
}