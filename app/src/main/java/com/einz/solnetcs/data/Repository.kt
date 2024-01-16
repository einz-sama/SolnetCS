package com.einz.solnetcs.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.einz.solnetcs.data.model.Customer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Repository(private val context: Context) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val userLiveData: MutableLiveData<Result<FirebaseUser?>> = MutableLiveData()
    val customerLiveData: MutableLiveData<Result<Customer?>> = MutableLiveData()

    val registerSuccessLiveData: MutableLiveData<Result<Boolean?>> = MutableLiveData()
    val loginSuccessLiveData: MutableLiveData<Result<Boolean?>> = MutableLiveData()
    val loggedOutLiveData: MutableLiveData<Result<Boolean?>> = MutableLiveData()

    init {
        if (firebaseAuth.currentUser != null) {
            userLiveData.postValue(Result.Success(firebaseAuth.currentUser))
        }
    }

    suspend fun registerFirebase(customer: Customer, password: String) {
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

    suspend fun loginFirebase(email: String, password: String) {
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

    // ... Other methods
}
