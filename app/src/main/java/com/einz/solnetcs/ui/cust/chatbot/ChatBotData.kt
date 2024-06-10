package com.einz.solnetcs.ui.cust.chatbot

//Redirect Map
//1 -> New Ticket
//2 -> FAQ
//3 -> Change Password
//4 -> Change Alamat
//5 -> Change No HP


// Questions and Responses
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

val newModemSetting_Response = ChatBotResponse(
    "Apakah ingin ubah Password WiFi atau Pengaturan Lain?",
    null,
    true,
    1,
    "Seting Modem"
)

val newModemDown_Response = ChatBotResponse(
    "Apakah modem mati?",
    null,
    true,
    1,
    "Modem Down"
)

val newModemError_Response = ChatBotResponse(
    "Apakah modem menunjukkan kesalahan?",
    null,
    true,
    1,
    "Modem Error"
)

val newModemLoss_Response = ChatBotResponse(
    "Apakah sinyal lemah?",
    null,
    true,
    1,
    "Modem Loss"
)

val newAdapterPower_Response = ChatBotResponse(
    "Apakah ada masalah dengan adapter power?",
    null,
    true,
    1,
    "Adapter Power"
)

val newIndikatorMerah_Response = ChatBotResponse(
    "Apakah indikator modem warna merah?",
    null,
    true,
    1,
    "Indikator Merah"
)

val newPingIntermittent_Response = ChatBotResponse(
    "Apakah Anda mengalami masalah ping yang terputus-putus?",
    null,
    true,
    1,
    "Ping Intermittent"
)

val newNoInternetAccess_Response = ChatBotResponse(
    "Apakah tidak ada akses internet?",
    null,
    true,
    1,
    "No Internet Access"
)

val modemIssues_Question = ChatBotQuestion(
    "Mari kita mendiagnosis masalah modem lebih lanjut.",
    listOf(
        newModemSetting_Response,
        newModemDown_Response,
        newModemError_Response,
        newModemLoss_Response,
        newAdapterPower_Response,
        newIndikatorMerah_Response
    )
)

val connectionIssues_Question = ChatBotQuestion(
    "Mari kita mendiagnosis masalah koneksi lebih lanjut.",
    listOf(
        newPingIntermittent_Response,
        newNoInternetAccess_Response
    )
)

val noInternet_Response = ChatBotResponse(
    "Wifi terhubung tapi tidak ada Internet?",
    connectionIssues_Question,
    false,
    0
)

val noWifi_Response = ChatBotResponse(
    "Wifi tidak menyala? Modem tidak Hidup?",
    modemIssues_Question,
    false,
    0
)

val cannotConnect_Response = ChatBotResponse(
    "Tidak bisa tersambung ke WiFi? Lupa Password WiFi?",
    modemIssues_Question,
    false,
    0
)

val siteError_Response = ChatBotResponse(
    "Tidak bisa akses situs tertentu Melalui WiFi?",
    connectionIssues_Question,
    false,
    0
)

val slowInternet_Response = ChatBotResponse(
    "Internet lambat? Download lelet?",
    connectionIssues_Question,
    false,
    0
)

val other_Response = ChatBotResponse(
    "Gangguan lainnya?",
    modemIssues_Question,
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

val changeLocation_Response = ChatBotResponse(
    "Apakah Anda ingin mengganti posisi modem di rumah Anda?",
    null,
    true,
    1,
    "Ganti Lokasi Modem"
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


val changeLocation_Question = ChatBotQuestion(
    "Apakah anda ingin mengganti posisi modem di rumah anda?",
    listOf(changeLocation_Response)
)

val locationGreeting_Response = ChatBotResponse(
    "Apakah anda ingin mengganti posisi modem di rumah anda?",
    changeLocation_Question,
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
        locationGreeting_Response,
        infoGreeting_Response
    )
)

val problem_Question = ChatBotQuestion(
    "Apa masalah yang anda alami?",
    listOf(
        gangguanGreeting_Response,
        locationGreeting_Response
    )
)


