package com.einz.solnetcs.data.model

import com.google.firebase.Timestamp

class FirebaseTimestamp {
    var seconds: Long = 0
    var nanoseconds: Int = 0

    constructor() // No-argument constructor needed for Firebase

    constructor(timestamp: Timestamp) {
        this.seconds = timestamp.seconds
        this.nanoseconds = timestamp.nanoseconds
    }

    fun toTimestamp(): Timestamp {
        return Timestamp(seconds, nanoseconds)
    }

    companion object {
        fun fromTimestamp(timestamp: Timestamp): FirebaseTimestamp {
            return FirebaseTimestamp(timestamp)
        }
    }
}
