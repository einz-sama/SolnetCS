package com.einz.solnetcs.data

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.einz.solnetcs.data.model.Customer
import com.einz.solnetcs.data.model.FirebaseTimestamp
import com.einz.solnetcs.data.model.Laporan
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.einz.solnetcs.util.formatPhoneNumber

class Repository(private val context: Context) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db: FirebaseDatabase = FirebaseDatabase.getInstance()

    val userLiveData: MutableLiveData<State<FirebaseUser?>> = MutableLiveData()
    val customerLiveData: MutableLiveData<State<Customer?>> = MutableLiveData()
    val checkCustomerLiveData: MutableLiveData<State<Customer?>> = MutableLiveData()

    val verifyCustomerLiveData: MutableLiveData<State<Customer?>> = MutableLiveData()
    val registerSuccessLiveData: MutableLiveData<State<Boolean?>> = MutableLiveData()
    val loginSuccessLiveData: MutableLiveData<State<Boolean?>> = MutableLiveData()
    val loggedOutLiveData: MutableLiveData<State<Boolean?>> = MutableLiveData()
    val resetPasswordLiveData: MutableLiveData<State<Boolean?>> = MutableLiveData()

    val changePasswordLiveData: MutableLiveData<State<Boolean?>> = MutableLiveData()
    val changeAlamatLiveData: MutableLiveData<State<Boolean?>> = MutableLiveData()
    val changePhoneLiveData: MutableLiveData<State<Boolean?>> = MutableLiveData()

    val createLaporanLiveData: MutableLiveData<State<Boolean?>> = MutableLiveData()
    val getLaporanLiveData: MutableLiveData<State<Laporan?>> = MutableLiveData()
    val checkLaporanLiveData: MutableLiveData<State<Boolean?>> = MutableLiveData()
    val laporanDoneLiveData: MutableLiveData<State<Boolean?>> = MutableLiveData()
    val laporanListLiveData: MutableLiveData<State<List<Laporan?>>> = MutableLiveData()
    val laporanLiveData: MutableLiveData<State<Laporan?>> = MutableLiveData()

    init {
        if (firebaseAuth.currentUser != null) {
            userLiveData.postValue(State.Success(firebaseAuth.currentUser))
        }
    }

    fun verifyCustomer(idCustomer: String, noTelpCustomer: String) {
        verifyCustomerLiveData.postValue(State.Loading)

        try {
            val formattedPhone = formatPhoneNumber(noTelpCustomer)
            val databaseReference = db.getReference("customers")
            val idCust = idCustomer.toDouble()

            databaseReference.orderByChild("idCustomer").equalTo(idCust)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            var customerFound = false
                            for (customerSnapshot in snapshot.children) {
                                val customer = customerSnapshot.getValue(Customer::class.java)
                                if (customer != null) {
                                    val formattedCustomerPhone = formatPhoneNumber(customer.noTelpCustomer)
                                    if (formattedCustomerPhone == formattedPhone) {
                                        verifyCustomerLiveData.postValue(State.Success(customer))
                                        customerFound = true
                                        break
                                    } else {
                                        verifyCustomerLiveData.postValue(State.Error("Nomor telepon tidak sama"))
                                    }
                                }
                            }
                            if (!customerFound) {
                                verifyCustomerLiveData.postValue(State.Error("ID Customer tidak ditemukan"))
                            }
                        } else {
                            verifyCustomerLiveData.postValue(State.Error("Pengecekan Gagal"))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        verifyCustomerLiveData.postValue(State.Error(error.message))
                    }
                })
        } catch (e: Exception) {
            verifyCustomerLiveData.postValue(State.Error(e.message ?: "Unknown error"))
        }
    }

    fun newregister(idCustomer: Int, email: String, password: String) {
        registerSuccessLiveData.postValue(State.Loading)

        // Step 1: Create a new Firebase Authentication user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // User created successfully
                    val userId = firebaseAuth.currentUser?.uid

                    // Step 2: Verify the customer exists
                    verifyCustomerById(idCustomer) { customer ->
                        if (customer != null) {
                            // Customer exists, update the email
                            updateCustomerEmail(idCustomer, email)
                        } else {
                            // Customer does not exist
                            registerSuccessLiveData.postValue(State.Error("Customer ID not found"))
                        }
                    }
                } else {
                    // Handle duplicate email error
                    val errorMessage = task.exception?.message ?: "Registration failed"
                    registerSuccessLiveData.postValue(State.Error(errorMessage))
                }
            }
    }

    private fun verifyCustomerById(idCustomer: Int, callback: (Customer?) -> Unit) {
        val databaseReference = db.getReference("customers")
        databaseReference.child(idCustomer.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val customer = snapshot.getValue(Customer::class.java)
                        callback(customer)
                    } else {
                        callback(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }

    private fun updateCustomerEmail(idCustomer: Int, email: String) {
        val databaseReference = db.getReference("customers").child(idCustomer.toString())
        val updates = hashMapOf<String, Any>("email" to email)
        databaseReference.updateChildren(updates)
            .addOnSuccessListener {
                registerSuccessLiveData.postValue(State.Success(true))
            }
            .addOnFailureListener { exception ->
                registerSuccessLiveData.postValue(State.Error(exception.message ?: "Failed to update customer email"))
            }
    }

    suspend fun register(customer: Customer, password: String) {
        userLiveData.postValue(State.Loading)

        try {
            firebaseAuth.createUserWithEmailAndPassword(customer.email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = firebaseAuth.currentUser?.uid
                        val databaseReference = FirebaseDatabase.getInstance().getReference("customers")

                        userId?.let { uid ->
                            databaseReference.child(uid).setValue(customer)
                                .addOnSuccessListener {
                                    registerSuccessLiveData.postValue(State.Success(true))
                                }
                                .addOnFailureListener { exception ->
                                    registerSuccessLiveData.postValue(State.Error(exception.message ?: "Registration failed"))
                                }
                        } ?: run {
                            registerSuccessLiveData.postValue(State.Error("Error"))
                        }
                    } else {
                        registerSuccessLiveData.postValue(State.Error(task.exception?.message ?: "Registration failed"))
                    }
                }
        } catch (e: Exception) {
            userLiveData.postValue(State.Error(e.message ?: "Unknown error"))
        }
    }

    suspend fun login(email: String, password: String) {
        userLiveData.postValue(State.Loading)

        try {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        userLiveData.postValue(State.Success(firebaseAuth.currentUser))
                        loggedOutLiveData.postValue(State.Error("Logged in"))
                    } else {
                        userLiveData.postValue(State.Error(task.exception?.message ?: "Login failed"))
                    }
                }
        } catch (e: Exception) {
            userLiveData.postValue(State.Error(e.message ?: "Unknown error"))
        }
    }

    // implement firebase password reset via email
    suspend fun resetPassword(email: String) {
        resetPasswordLiveData.postValue(State.Loading)

        try {
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        resetPasswordLiveData.postValue(State.Success(true))
                    } else {
                        resetPasswordLiveData.postValue(State.Error(task.exception?.message ?: "Reset password failed"))
                    }
                }
        } catch (e: Exception) {
            resetPasswordLiveData.postValue(State.Error(e.message ?: "Unknown error"))
        }
    }

    suspend fun logout() {
        loggedOutLiveData.postValue(State.Success(true))
        firebaseAuth.signOut()


    }
    suspend fun getCustomerData() {
        customerLiveData.postValue(State.Loading)
        try {
            val userEmail = firebaseAuth.currentUser?.email ?: return
            val databaseReference = FirebaseDatabase.getInstance().getReference("customers")
            databaseReference.orderByChild("email").equalTo(userEmail)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (userSnapshot in snapshot.children) {
                                val customer = userSnapshot.getValue(Customer::class.java)
                                customer?.let {
                                    customerLiveData.postValue(State.Success(it))
                                    loggedOutLiveData.postValue(State.Error("Logged in"))
                                }
                            }
                        } else {
                            customerLiveData.postValue(State.Error("Customer not found"))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        customerLiveData.postValue(State.Error(error.message))
                    }
                })
        } catch (e: Exception) {
            customerLiveData.postValue(State.Error(e.message ?: "Unknown error"))
        }
    }

    suspend fun checkCustomerData() {
        checkCustomerLiveData.postValue(State.Loading)
        try {
            val userEmail = firebaseAuth.currentUser?.email ?: return
            val databaseReference = FirebaseDatabase.getInstance().getReference("customers")
            databaseReference.orderByChild("email").equalTo(userEmail)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (userSnapshot in snapshot.children) {
                                val customer = userSnapshot.getValue(Customer::class.java)
                                customer?.let {
                                    checkCustomerLiveData.postValue(State.Success(it))
                                    loggedOutLiveData.postValue(State.Error("Logged in"))
                                }
                            }
                        } else {
                            checkCustomerLiveData.postValue(State.Error("Customer not found"))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        checkCustomerLiveData.postValue(State.Error(error.message))
                    }
                })
        } catch (e: Exception) {
            checkCustomerLiveData.postValue(State.Error(e.message ?: "Unknown error"))
        }
    }

    suspend fun createLaporan(idCustomer: String, laporan: Laporan) {
        createLaporanLiveData.postValue(State.Loading)
        try {
            val databaseReference = FirebaseDatabase.getInstance().getReference("reports")
            databaseReference.child(idCustomer).push().setValue(laporan)
                .addOnSuccessListener {
                    createLaporanLiveData.postValue(State.Success(true))
                }
                .addOnFailureListener { exception ->
                    createLaporanLiveData.postValue(State.Error(exception.message ?: "Create laporan failed"))
                }
        } catch (e: Exception) {
            createLaporanLiveData.postValue(State.Error(e.message ?: "Unknown error"))
        }
    }

    suspend fun getActiveLaporanByIdCust(idCustomer: String) {
        getLaporanLiveData.postValue(State.Loading)

        try {
            val databaseReference = FirebaseDatabase.getInstance().getReference("reports")

            val query = databaseReference.child(idCustomer)
                .orderByChild("status")
                .startAt(0.toDouble())
                .endBefore(4.toDouble()) // Exclude status 4

            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Find the first matching Laporan with status not equal to 4
                        val laporan: Laporan? = snapshot.children
                            .mapNotNull { it.getValue(Laporan::class.java) }
                            .firstOrNull { it.status != 4 }

                        getLaporanLiveData.postValue(State.Success(laporan))
                    } else {
                        getLaporanLiveData.postValue(State.Error("No Active Laporan found"))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    getLaporanLiveData.postValue(State.Error(error.message))
                }
            })
        } catch (e: Exception) {
            getLaporanLiveData.postValue(State.Error(e.message ?: "Unknown error"))
        }
    }


    suspend fun updateLaporanStatus(idCustomer: String, idLaporan: String, newStatus: Int) {
        laporanDoneLiveData.postValue(State.Loading)
        val reportRef = FirebaseDatabase.getInstance().getReference("reports/$idCustomer/$idLaporan")

        // Update the Laporan status
        reportRef.child("status").setValue(newStatus)
            .addOnSuccessListener {
                // Successfully updated the Laporan, now fetch the Laporan to get idTeknisi
                reportRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val laporan = snapshot.getValue(Laporan::class.java)
                        val idTeknisi = laporan?.idTeknisi

                        if (idTeknisi != null) {
                            // Now, find and update the Teknisi's activeIdLaporan to null
                            val teknisiRef = FirebaseDatabase.getInstance().getReference("techs").orderByChild("idTeknisi").equalTo(idTeknisi.toDouble())
                            teknisiRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(teknisiSnapshot: DataSnapshot) {
                                    for (teknisiChild in teknisiSnapshot.children) {
                                        teknisiChild.ref.child("activeIdLaporan").setValue(null)
                                            .addOnSuccessListener {
                                                laporanDoneLiveData.postValue(State.Success(true))
                                            }
                                            .addOnFailureListener { e ->
                                                laporanDoneLiveData.postValue(State.Error("Failed to clear Teknisi active Laporan ID: ${e.message}"))
                                            }
                                    }
                                }

                                override fun onCancelled(teknisiDbError: DatabaseError) {
                                    laporanDoneLiveData.postValue(State.Error("Database error on Teknisi lookup: ${teknisiDbError.message}"))
                                }
                            })
                        } else {
                            laporanDoneLiveData.postValue(State.Error("No Teknisi ID found in Laporan"))
                        }
                    }

                    override fun onCancelled(dbError: DatabaseError) {
                        laporanDoneLiveData.postValue(State.Error("Database error on Laporan lookup: ${dbError.message}"))
                    }
                })
            }
            .addOnFailureListener { e ->
                laporanDoneLiveData.postValue(State.Error("Failed to update Laporan status: ${e.message}"))
            }
    }

    suspend fun checkActiveLaporanByIdCust(idCustomer: String) {
//        checkLaporanLiveData.postValue(Result.Loading)

        try {
            val databaseReference = FirebaseDatabase.getInstance().getReference("reports")

            val query = databaseReference.child(idCustomer)
                .orderByChild("status")
                .startAt(0.toDouble())
                .endBefore(4.toDouble()) // Exclude status 4

            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Find the first matching Laporan with status not equal to 4
                        val laporan: Laporan? = snapshot.children
                            .mapNotNull { it.getValue(Laporan::class.java) }
                            .firstOrNull { it.status != 4 }

                        checkLaporanLiveData.postValue(State.Success(true))
                    } else {
                        checkLaporanLiveData.postValue(State.Success(false))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    checkLaporanLiveData.postValue(State.Error(error.message))
                }
            })
        } catch (e: Exception) {
            checkLaporanLiveData.postValue(State.Error(e.message ?: "Unknown error"))
        }
    }

    fun getFinishedLaporan(idCust: String) {
        val reportsReference = db.getReference("reports")
        laporanListLiveData.postValue(State.Loading)

        // Real-time listener
        reportsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val allReports = mutableListOf<Laporan>()

                // Iterate through each customer's reports
                dataSnapshot.children.forEach { customerReportsSnapshot ->
                    // Iterate through each report under a customer ID
                    customerReportsSnapshot.children.forEach { reportSnapshot ->
                        val report = reportSnapshot.getValue(Laporan::class.java)
                        // Check if the report's status is '4'
                        if (report?.status == 4) {
                            if (report.idCustomer == idCust) {
                                report?.let { allReports.add(it) }
                            }
                        }
                    }
                }

                // Sort reports by timestamp from latest to oldest
                allReports.sortWith(compareByDescending<Laporan> {
                    it.timestamp?.seconds
                }.thenByDescending {
                    it.timestamp?.nanoseconds
                })

                laporanListLiveData.postValue(State.Success(allReports))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                laporanListLiveData.postValue(State.Error(databaseError.message))
            }
        })
    }

    fun resetLaporan(){
        laporanLiveData.postValue(State.Loading)
    }
    fun getLaporanById(idLaporan: String) {
        resetLaporan()
        laporanLiveData.postValue(State.Loading)

        val reportsReference = db.getReference("reports")

        reportsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var laporanFound = false

                // Iterate through each customer's reports
                for (customerSnapshot in dataSnapshot.children) {
                    // Iterate through each report under a customer ID
                    for (reportSnapshot in customerSnapshot.children) {
                        val report = reportSnapshot.getValue(Laporan::class.java)
                        if (report?.idLaporan == idLaporan) {
                            laporanLiveData.postValue(State.Success(report))
                            laporanFound = true
                            break
                        }
                    }
                    if (laporanFound) break
                }

                if (!laporanFound) {
                    laporanLiveData.postValue(State.Error("Laporan not found"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                laporanLiveData.postValue(State.Error(databaseError.message))
            }
        })
    }

    fun changePassword(newPassword: String) {
        val user: FirebaseUser? = firebaseAuth.currentUser
        changePasswordLiveData.postValue(State.Loading)

        user?.let {
            it.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Password change successful
                        changePasswordLiveData.postValue(State.Success(true))
                    } else {
                        // Password change failed
                        changePasswordLiveData.postValue(State.Error(task.exception?.message ?: "Failed to update password."))
                    }
                }
        } ?: run {
            // User is not signed in or user data is not available
            changePasswordLiveData.postValue(State.Error("No signed-in user."))
        }
    }

    fun changeAlamat(idCustomer: String, newAlamat: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("customers")
        changeAlamatLiveData.postValue(State.Loading)

        val idCust = idCustomer.toDouble()

        // Query the database for nodes with the matching idCustomer
        databaseReference.orderByChild("idCustomer").equalTo(idCust)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Iterate over the matching nodes (should be only one) and update alamatCustomer
                        for (customerSnapshot in snapshot.children) {
                            customerSnapshot.ref.updateChildren(mapOf("alamatCustomer" to newAlamat))
                                .addOnSuccessListener {
                                    // Update successful
                                    changeAlamatLiveData.postValue(State.Success(true))
                                }
                                .addOnFailureListener { exception ->
                                    // Update failed
                                    changeAlamatLiveData.postValue(State.Error(exception.message ?: "Update address failed"))
                                }
                        }
                    } else {
                        // No customer with the provided idCustomer found
                        changeAlamatLiveData.postValue(State.Error("Customer not found"))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Query cancelled
                    changeAlamatLiveData.postValue(State.Error(error.message ?: "Query cancelled"))
                }
            })

    }

    fun changePhone(idCustomer: String, newPhoneNumber: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("customers")
        changePhoneLiveData.postValue(State.Loading)

        val idCust = idCustomer.toDouble()

        // Query the database for nodes with the matching idCustomer
        databaseReference.orderByChild("idCustomer").equalTo(idCust)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Iterate over the matching nodes (should be only one) and update noTelpCustomer
                        for (customerSnapshot in snapshot.children) {
                            customerSnapshot.ref.updateChildren(mapOf("noTelpCustomer" to newPhoneNumber))
                                .addOnSuccessListener {
                                    // Update successful
                                    changePhoneLiveData.postValue(State.Success(true))
                                }
                                .addOnFailureListener { exception ->
                                    // Update failed
                                    changePhoneLiveData.postValue(State.Error(exception.message ?: "Update phone number failed"))
                                }
                        }
                    } else {
                        // No customer with the provided idCustomer found
                        changePhoneLiveData.postValue(State.Error("Customer not found"))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Query cancelled
                    changePhoneLiveData.postValue(State.Error(error.message ?: "Query cancelled"))
                }
            })
    }

    fun finishLaporan(idLaporan: String, newStatus: Int = 4) {
//        laporanDoneLiveData.postValue(Result.Loading)

        // Step 1: Find and update the Laporan status
        val reportsReference = db.getReference("reports")
        reportsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(reportsSnapshot: DataSnapshot) {
                var laporanFound = false

                reportsLoop@ for (customerSnapshot in reportsSnapshot.children) {
                    for (reportSnapshot in customerSnapshot.children) {
                        val laporan = reportSnapshot.getValue(Laporan::class.java)
                        if (laporan?.idLaporan == idLaporan) {
                            // Update Laporan status
                            val time_finish_seconds = System.currentTimeMillis() / 1000
                            val time_finish_nanos = System.currentTimeMillis() % 1000 * 1000000
                            val time_finish = FirebaseTimestamp()
                            time_finish.seconds = time_finish_seconds
                            time_finish.nanoseconds = time_finish_nanos.toInt()

                            reportSnapshot.ref.child("status").setValue(newStatus)
                            reportSnapshot.ref.child("time_repair_closed").setValue(time_finish)


                            val idTeknisi = laporan.idTeknisi
                            if (idTeknisi != null) {
                                // Step 2: Fetch and update Teknisi
                                val teknisiReference = db.getReference("techs")
                                teknisiReference.orderByChild("idTeknisi").equalTo(idTeknisi.toDouble()).addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(teknisiSnapshot: DataSnapshot) {
                                        if (teknisiSnapshot.exists()) {
                                            for (childSnapshot in teknisiSnapshot.children) {
                                                // Set activeIdLaporan to null or remove the property
                                                childSnapshot.ref.child("activeIdLaporan").removeValue() // or setValue(null)
                                            }
                                            laporanDoneLiveData.postValue(State.Success(true))
                                        } else {
                                            laporanDoneLiveData.postValue(State.Error("Teknisi not found"))
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        laporanDoneLiveData.postValue(State.Error(databaseError.message))
                                    }
                                })
                            }
                            laporanFound = true
                            break@reportsLoop
                        }
                    }
                }

                if (!laporanFound) {
                    laporanDoneLiveData.postValue(State.Error("Laporan not found"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                laporanDoneLiveData.postValue(State.Error(databaseError.message))
            }
        })
    }

    fun resetLaporanDone(){
        laporanDoneLiveData.postValue(State.Loading)
    }





}
