package com.einz.solnetcs.ui.cust.chatbot

//Redirect Map
//1 -> New Ticket
//2 -> FAQ
//3 -> Change Password
//4 -> Change Alamat
//5 -> Change No HP


val ubahAlamat_Response = ChatBotResponse(
    "Untuk mengubah Alamat anda, bisa langsung menekan tombol dibawah",
    null,
    true,
    4
)

val ubahNoHP_Response = ChatBotResponse(
    "Untuk mengubah Nomor HP anda, bisa langsung menekan tombol dibawah",
    null,
    true,
    5
)

val ubahData_Question = ChatBotQuestion(
    "Apakah anda ingin mengubah data diri anda?",
    listOf(
        ubahAlamat_Response,
        ubahNoHP_Response
    )
)

val info_Response = ChatBotResponse(
    "Untuk mendapatkan informasi lebih lanjut, bisa langsung menekan tombol dibawah",
    null,
    true,
    2
)

val info_Question = ChatBotQuestion(
    "Apakah anda ingin mendapatkan informasi lebih lanjut?",
    listOf(
        info_Response
    )
)

val changePassword_Response = ChatBotResponse(
    "Untuk mengubah Password anda, bisa langsung menekan tombol dibawah",
    null,
    true,
    3
)

val changePassword_Question = ChatBotQuestion(
    "Apakah anda ingin mengubah Password anda?",
    listOf(
        changePassword_Response
    )
)

val newTicket_Response = ChatBotResponse(
    "Untuk membuat tiket baru, bisa langsung menekan tombol dibawah",
    null,
    true,
    1
)

val newTicket_Question = ChatBotQuestion(
    "Apakah anda sudah melakukan restart modem dan perangkat? jika sudah maka buat Laporan Gangguan",
    listOf(
        newTicket_Response
    )
)

val noInternet_Response = ChatBotResponse(
    "Wifi terhubung tapi tidak ada Internet?",
    newTicket_Question,
    false,
    0
)

val noWifi_Response = ChatBotResponse(
    "Wifi tidak menyala? Modem tidak Hidup?",
    newTicket_Question,
    false,
    0
)

val cannotConnect_Response = ChatBotResponse(
    "Tidak bisa tersambung ke WiFi? Lupa Password WiFi?",
    newTicket_Question,
    false,
    0
)

val siteError_Response = ChatBotResponse(
    "Tidak bisa akses situs tertentu Melalui WiFi?",
    newTicket_Question,
    false,
    0
)

val slowInternet_Response = ChatBotResponse(
    "Internet lambat? Download lelet?",
    newTicket_Question,
    false,
    0
)

val other_Response = ChatBotResponse(
    "Gangguan lainnya?",
    newTicket_Question,
    false,
    0
)

val gangguan_Question = ChatBotQuestion(
    "Apa yang terjadi?",
    listOf(
        noInternet_Response,
        noWifi_Response,
        cannotConnect_Response,
        siteError_Response,
        slowInternet_Response,
        other_Response
    )
)

val ubahGreeting_Response = ChatBotResponse(
    "Apakah anda ingin merubah Data Pelanggan anda?",
    ubahData_Question,
    false,
    0
)

val gangguanGreeting_Response = ChatBotResponse(
    "Apakah anda mengalami gangguan?",
    gangguan_Question,
    false,
    0
)


val passwordGreeting_Response = ChatBotResponse(
    "Apakah anda ingin mengubah Password anda?",
    changePassword_Question,
    false,
    0
)

val infoGreeting_Response = ChatBotResponse(
    "Info lainya?",
    info_Question,
    false,
    0
)

val greeting_Question = ChatBotQuestion(
    "Halo, Apa yang bisa saya bantu?",
    listOf(
        ubahGreeting_Response,
        gangguanGreeting_Response,
        passwordGreeting_Response,
        infoGreeting_Response
    )
)


