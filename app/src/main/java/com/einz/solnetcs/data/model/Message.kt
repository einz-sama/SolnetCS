package com.einz.solnetcs.data.model

data class Message(
    val sender:String?="",
    val text:String?="",
    val timestamp:Long?=0
)