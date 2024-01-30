package com.einz.solnetcs.data.model

import com.google.firebase.Timestamp

data class Laporan(
    val idLaporan:String?="",
    val idCustomer:String?="",
    val daerah:String?="",
    val alamat:String?="",
    val kategori:String?="",
    val deskripsi:String?="",
    val status:Int=0,
    val desiredTime:FirebaseTimestamp? = null,
    val timestamp: FirebaseTimestamp? = null,
    val customerPhone: String? = "",
    val teknisi:String?="",
    val messages:List<Message>?=null,
    val teknisiPhone:String?= whatsapp,
    val idTeknisi:Int=0
)