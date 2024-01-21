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

    val registerSuccessLiveData: MutableLiveData<Result<Boolean?>> = MutableLiveData()
    val loginSuccessLiveData: MutableLiveData<Result<Boolean?>> = MutableLiveData()
    val loggedOutLiveData: MutableLiveData<Result<Boolean?>> = MutableLiveData()

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
                    } else {
                        userLiveData.postValue(Result.Error(task.exception?.message ?: "Login failed"))
                    }
                }
        } catch (e: Exception) {
            userLiveData.postValue(Result.Error(e.message ?: "Unknown error"))
        }
    }

    suspend fun logout() {
        firebaseAuth.signOut()
        loggedOutLiveData.postValue(Result.Success(true))
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
        try {
            val databaseReference = FirebaseDatabase.getInstance().getReference("reports").child(idCustomer)

            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (reportSnapshot in snapshot.children) {
                            val laporan = reportSnapshot.getValue(Laporan::class.java)
                            if (laporan?.idLaporan == idLaporan) {
                                reportSnapshot.ref.updateChildren(mapOf("status" to newStatus))
                                    .addOnSuccessListener {
                                        laporanDoneLiveData.postValue(Result.Success(true))
                                    }
                                    .addOnFailureListener {
                                        laporanDoneLiveData.postValue(Result.Error("Update laporan failed"))
                                    }
                                break // Exit the loop once the correct laporan is found and updated
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error
                    laporanDoneLiveData.postValue(Result.Error(databaseError.message))
                }
            })
        } catch (e: Exception) {
            // Handle any exceptions
            laporanDoneLiveData.postValue(Result.Error(e.message ?: "Unknown error"))
        }
    }

    suspend fun checkActiveLaporanByIdCust(idCustomer: String) {
        checkLaporanLiveData.postValue(Result.Loading)

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





}
