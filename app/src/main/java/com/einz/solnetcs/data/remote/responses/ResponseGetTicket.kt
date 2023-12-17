package com.einz.solnetcs.data.remote.responses

import com.google.gson.annotations.SerializedName

data class ResponseGetTicket(

	@field:SerializedName("data")
	val data: DataTicket? = null,

	@field:SerializedName("active_ticket")
	val activeTicket: Boolean? = null
)

data class DataTicket(

	@field:SerializedName("Kategori")
	val kategori: String? = null,

	@field:SerializedName("Status")
	val status: Int? = null,

	@field:SerializedName("Tgl_Keluhan")
	val tglKeluhan: String? = null,

	@field:SerializedName("Deskripsi")
	val deskripsi: String? = null,

	@field:SerializedName("ID_Keluhan")
	val iDKeluhan: String? = null
)
