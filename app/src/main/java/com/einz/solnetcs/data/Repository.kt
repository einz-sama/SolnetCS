package com.einz.solnetcs.data

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.einz.solnetcs.data.model.Customer
import com.einz.solnetcs.data.model.Laporan
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Repository(private val context: Context) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db: FirebaseDatabase = FirebaseDatabase.getInstance()

    val userLiveData: MutableLiveData<Result<FirebaseUser?>> = MutableLiveData()
    val customerLiveData: MutableLiveData<Result<Customer?>> = MutableLiveData()
    val checkCustomerLiveData: MutableLiveData<Result<Customer?>> = MutableLiveData()

    val registerSuccessLiveData: MutableLiveData<Result<Boolean?>> = MutableLiveData()
    val loginSuccessLiveData: MutableLiveData<Result<Boolean?>> = MutableLiveData()
    val loggedOutLiveData: MutableLiveData<Result<Boolean?>> = MutableLiveData()

    val changePasswordLiveData: MutableLiveData<Result<Boolean?>> = MutableLiveData()
    val changeAlamatLiveData: MutableLiveData<Result<Boolean?>> = MutableLiveData()
    val changePhoneLiveData: MutableLiveData<Result<Boolean?>> = MutableLiveData()

    val createLaporanLiveData: MutableLiveData<Result<Boolean?>> = MutableLiveData()
    val getLaporanLiveData: MutableLiveData<Result<Laporan?>> = MutableLiveData()
    val checkLaporanLiveData: MutableLiveData<Result<Boolean?>> = MutableLiveData()
    val laporanDoneLiveData: MutableLiveData<Result<Boolean?>> = MutableLiveData()

    init {
        if (firebaseAuth.currentUser != null) {
            userLiveData.postValue(Result.Success(firebaseAuth.currentUser))
        }
    }

    suspend fun register(customer: Customer, password: String) {
        userLiveData.postValue(Result.Loading)

        try {
            firebaseAuth.createUserWithEmailAndPassword(customer.email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = firebaseAuth.currentUser?.uid
                        val databaseReference = FirebaseDatabase.getInstance().getReference("customers")

                        userId?.let { uid ->
                            databaseReference.child(uid).setValue(customer)
                                .addOnSuccessListener {
                                    registerSuccessLiveData.postValue(Result.Success(true))
                                }
                                .addOnFailureListener { exception ->
                                    registerSuccessLiveData.postValue(Result.Error(exception.message ?: "Registration failed"))
                                }
                        } ?: run {
                            registerSuccessLiveData.postValue(Result.Error("Error"))
                        }
                    } else {
                        registerSuccessLiveData.postValue(Result.Error(task.exception?.message ?: "Registration failed"))
                    }
                }
        } catch (e: Exception) {
            userLiveData.postValue(Result.Error(e.message ?: "Unknown error"))
        }
    }

    suspend fun login(email: String, password: String) {
        userLiveData.postValue(Result.Loading)

        try {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        userLiveData.postValue(Result.Success(firebaseAuth.currentUser))
                        loggedOutLiveData.postValue(Result.Error("Logged in"))
                    } else {
                        userLiveData.postValue(Result.Error(task.exception?.message ?: "Login failed"))
                    }
                }
        } catch (e: Exception) {
            userLiveData.postValue(Result.Error(e.message ?: "Unknown error"))
        }
    }

    suspend fun logout() {
        loggedOutLiveData.postValue(Result.Success(true))
        firebaseAuth.signOut()


    }
    suspend fun getCustomerData() {
        customerLiveData.postValue(Result.Loading)
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
                                    customerLiveData.postValue(Result.Success(it))
                                    loggedOutLiveData.postValue(Result.Error("Logged in"))
                                }
                            }
                        } else {
                            customerLiveData.postValue(Result.Error("Customer not found"))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        customerLiveData.postValue(Result.Error(error.message))
                    }
                })
        } catch (e: Exception) {
            customerLiveData.postValue(Result.Error(e.message ?: "Unknown error"))
        }
    }

    suspend fun checkCustomerData() {
        checkCustomerLiveData.postValue(Result.Loading)
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
                                    checkCustomerLiveData.postValue(Result.Success(it))
                                    loggedOutLiveData.postValue(Result.Error("Logged in"))
                                }
                            }
                        } else {
                            checkCustomerLiveData.postValue(Result.Error("Customer not found"))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        checkCustomerLiveData.postValue(Result.Error(error.message))
                    }
                })
        } catch (e: Exception) {
            checkCustomerLiveData.postValue(Result.Error(e.message ?: "Unknown error"))
        }
    }

    suspend fun createLaporan(idCustomer: String, laporan: Laporan) {
        createLaporanLiveData.postValue(Result.Loading)
        try {
            val databaseReference = FirebaseDatabase.getInstance().getReference("reports")
            databaseReference.child(idCustomer).push().setValue(laporan)
                .addOnSuccessListener {
                    createLaporanLiveData.postValue(Result.Success(true))
                }
                .addOnFailureListener { exception ->
                    createLaporanLiveData.postValue(Result.Error(exception.message ?: "Create laporan failed"))
                }
        } catch (e: Exception) {
            createLaporanLiveData.postValue(Result.Error(e.message ?: "Unknown error"))
        }
    }

    suspend fun getActiveLaporanByIdCust(idCustomer: String) {
        getLaporanLiveData.postValue(Result.Loading)

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

                        getLaporanLiveData.postValue(Result.Success(laporan))
                    } else {
                        getLaporanLiveData.postValue(Result.Error("No Active Laporan found"))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    getLaporanLiveData.postValue(Result.Error(error.message))
                }
            })
        } catch (e: Exception) {
            getLaporanLiveData.postValue(Result.Error(e.message ?: "Unknown error"))
        }
    }


    suspend fun updateLaporanStatus(idCustomer: String, idLaporan: String, newStatus: Int) {
        laporanDoneLiveData.postValue(Result.Loading)
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
                                                laporanDoneLiveData.postValue(Result.Success(true))
                                            }
                                            .addOnFailureListener { e ->
                                                laporanDoneLiveData.postValue(Result.Error("Failed to clear Teknisi active Laporan ID: ${e.message}"))
                                            }
                                    }
                                }

                                override fun onCancelled(teknisiDbError: DatabaseError) {
                                    laporanDoneLiveData.postValue(Result.Error("Database error on Teknisi lookup: ${teknisiDbError.message}"))
                                }
                            })
                        } else {
                            laporanDoneLiveData.postValue(Result.Error("No Teknisi ID found in Laporan"))
                        }
                    }

                    override fun onCancelled(dbError: DatabaseError) {
                        laporanDoneLiveData.postValue(Result.Error("Database error on Laporan lookup: ${dbError.message}"))
                    }
                })
            }
            .addOnFailureListener { e ->
                laporanDoneLiveData.postValue(Result.Error("Failed to update Laporan status: ${e.message}"))
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

                        checkLaporanLiveData.postValue(Result.Success(true))
                    } else {
                        checkLaporanLiveData.postValue(Result.Success(false))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    checkLaporanLiveData.postValue(Result.Error(error.message))
                }
            })
        } catch (e: Exception) {
            checkLaporanLiveData.postValue(Result.Error(e.message ?: "Unknown error"))
        }
    }

    fun changePassword(newPassword: String) {
        val user: FirebaseUser? = firebaseAuth.currentUser
        changePasswordLiveData.postValue(Result.Loading)

        user?.let {
            it.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Password change successful
                        changePasswordLiveData.postValue(Result.Success(true))
                    } else {
                        // Password change failed
                        changePasswordLiveData.postValue(Result.Error(task.exception?.message ?: "Failed to update password."))
                    }
                }
        } ?: run {
            // User is not signed in or user data is not available
            changePasswordLiveData.postValue(Result.Error("No signed-in user."))
        }
    }

    fun changeAlamat(idCustomer: String, newAlamat: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("customers")
        changeAlamatLiveData.postValue(Result.Loading)

        databaseReference.child(idCustomer).updateChildren(mapOf("alamatCustomer" to newAlamat))
            .addOnSuccessListener {
                // Update successful
                changeAlamatLiveData.postValue(Result.Success(true))
            }
            .addOnFailureListener { exception ->
                // Update failed
                changeAlamatLiveData.postValue(Result.Error(exception.message ?: "Update alamat failed"))
            }
    }

    fun changePhone(idCustomer: String, newPhoneNumber: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("customers")
        changePhoneLiveData.postValue(Result.Loading)

        databaseReference.child(idCustomer).updateChildren(mapOf("noTelpCustomer" to newPhoneNumber))
            .addOnSuccessListener {
                // Update successful
                changePhoneLiveData.postValue(Result.Success(true))
            }
            .addOnFailureListener { exception ->
                // Update failed
                changePhoneLiveData.postValue(Result.Error(exception.message ?: "Update phone number failed"))
            }
    }

    fun finishLaporan(idLaporan: String, newStatus: Int = 3) {
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
                            reportSnapshot.ref.child("status").setValue(newStatus)

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
                                            laporanDoneLiveData.postValue(Result.Success(true))
                                        } else {
                                            laporanDoneLiveData.postValue(Result.Error("Teknisi not found"))
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        laporanDoneLiveData.postValue(Result.Error(databaseError.message))
                                    }
                                })
                            }
                            laporanFound = true
                            break@reportsLoop
                        }
                    }
                }

                if (!laporanFound) {
                    laporanDoneLiveData.postValue(Result.Error("Laporan not found"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                laporanDoneLiveData.postValue(Result.Error(databaseError.message))
            }
        })
    }

    fun resetLaporanDone(){
        laporanDoneLiveData.postValue(Result.Loading)
    }



}
