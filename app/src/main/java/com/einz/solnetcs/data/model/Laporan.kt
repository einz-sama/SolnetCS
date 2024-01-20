package com.einz.solnetcs.data.model

import java.sql.Timestamp

data class Laporan(
    val idLaporan:String?="",
    val idCustomer:String?="",
    val kategori:String?="",
    val deskripsi:String?="",
    val status:Int=0,
    val desiredTime:Timestamp?=null,
    val timestamp: Timestamp?=null,
    val teknisi:String?="",
    val messages:List<Message>?=null
)