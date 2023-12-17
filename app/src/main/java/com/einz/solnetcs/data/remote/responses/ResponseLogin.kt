package com.einz.solnetcs.data.remote.responses

import com.google.gson.annotations.SerializedName

data class ResponseLogin(

	@field:SerializedName("data")
	val data: Login? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Login(

	@field:SerializedName("no_hp")
	val noHp: String? = null,

	@field:SerializedName("id_customer")
	val idCustomer: String? = null,

	@field:SerializedName("nama_lengkap")
	val namaLengkap: String? = null,

	@field:SerializedName("email")
	val email: String? = null
)
