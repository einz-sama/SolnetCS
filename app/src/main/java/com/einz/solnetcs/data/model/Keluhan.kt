package com.einz.solnetcs.data.model

data class Keluhan(
    val idKeluhan : String,
    val idCustomer: String,
    val kategori: String,
    val deskripsi:String,
    val status:String,
    val tanggal:String
)