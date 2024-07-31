package com.einz.solnetcs.ui.cust.chatbot

// Redirect Map
// 1 -> New Ticket
// 2 -> FAQ
// 3 -> Change Password
// 4 -> Change Alamat
// 5 -> Change No HP

// ChatBotResponse instances
val reportNoInternet_Response = ChatBotResponse(
    "Indikator tidak Merah, tapi Tidak Ada Internet",
    null,
    true,
    1,
    "No Internet Access"
)

val reportPingIntermittent_Response = ChatBotResponse(
    "Internet putus putus (intermittent)",
    null,
    true,
    1,
    "Ping Intermittent"
)

val reportIndicator_Response = ChatBotResponse(
    "Modem indikator merah",
    null,
    true,
    1,
    "Indikator Merah"
)

val reportSinyal_Response = ChatBotResponse(
    "Sinyal WiFi Lemah",
    null,
    true,
    1,
    "Modem Loss"
)

val reportModemPower_Response = ChatBotResponse(
    "Adaptor Modem Bermasalah",
    null,
    true,
    1,
    "Adapter Power"
)

val reportModemError_Response = ChatBotResponse(
    "Modem Hidup Tapi Tidak Ada Internet",
    null,
    true,
    1,
    "Modem Error"
)

val reportModemDown_Response = ChatBotResponse(
    "Modem WiFi Mati, Tidak bisa Hidup",
    null,
    true,
    1,
    "Modem Down"
)

val reportModemSetting_Response = ChatBotResponse(
    "Request Ganti Password atau Pengaturan WiFi",
    null,
    true,
    1,
    "Seting Modem"
)

val reportChangeLocation_Response = ChatBotResponse(
    "Request Perubahan Lokasi Modem WiFi",
    null,
    true,
    1,
    "Ganti Lokasi Modem"
)

val accountChangePassword_Response = ChatBotResponse(
    "Apakah anda Ingin Mengubah Kata Sandi / Password Aplikasi Anda?",
    null,
    true,
    3,
    ""
)

val accountChangePhone_Response = ChatBotResponse(
    "Apakah anda Ingin Mengubah Nomor Kontak Anda?",
    null,
    true,
    5,
    ""
)

val infoFAQ_Response = ChatBotResponse(
    "Info Lainya",
    null,
    true,
    2,
    ""
)

// ChatBotQuestion instances
val questionInternetIssue = ChatBotQuestion(
    "Gejala Apa yang Anda Alami?",
    listOf(
        reportSinyal_Response,
        reportPingIntermittent_Response,
        reportModemSetting_Response,
        reportModemError_Response
    )
)

val responseInternetIssue = ChatBotResponse(
    "Sudah Hidup, tetapi ada Masalah Lain",
    questionInternetIssue,
    false,
    0,
    ""
)

val questionConnectionIssue = ChatBotQuestion(
    "Jika Modem Sudah Menyala, Apa Kendala yang Dialami?",
    listOf(
        reportIndicator_Response,
        reportNoInternet_Response,
        responseInternetIssue
    )
)

val responseModemRestarted = ChatBotResponse(
    "Modem Sudah di Restart",
    questionConnectionIssue,
    false,
    0,
    ""
)

val questionRestartModem = ChatBotQuestion(
    "Sebelumnya, tolong Lakukan Restart Modem dengan Menekan Tombol Power dibelakang Modem sehingga Modem Mati, kemudian tunggu 10 detik sebelum menyalakan kembali",
    listOf(
        responseModemRestarted,
        reportModemPower_Response,
        reportModemDown_Response
    )
)

val responseMasalah = ChatBotResponse(
    "Saya Mengalami Masalah atau Keluhan Internet",
    questionRestartModem,
    false,
    0,
    ""
)

val greeting_Question = ChatBotQuestion(
    "Halo, apa ada yang bisa dibantu?",
    listOf(
        responseMasalah,
        reportModemSetting_Response,
        reportChangeLocation_Response,
        accountChangePassword_Response,
        accountChangePhone_Response,
        infoFAQ_Response
    )
)
