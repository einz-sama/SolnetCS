package com.einz.solnetcs.data.remote.responses

import com.google.gson.annotations.SerializedName

data class ResponseGetCustomer(

	@field:SerializedName("data")
	val data: DataCustomer? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataCustomer(

	@field:SerializedName("no_hp")
	val noHp: String? = null,

	@field:SerializedName("id_customer")
	val idCustomer: String? = null,

	@field:SerializedName("nama_lengkap")
	val namaLengkap: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("alamat")
	val alamat: String? = null,

	@field:SerializedName("daerah")
	val daerah: String? = null
)
