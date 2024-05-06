package com.einz.solnetcs.ui.cust.chatbot

// data is questionText, and responses
// questionText contains the question that will be asked to the user
// responses contains the possible responses that the user can choose from, which is a list of a ChatBotResponse object

class ChatBotQuestion(val questionText: String, val responses: List<ChatBotResponse>){
    val textString = questionText
    val responseList = responses
}















