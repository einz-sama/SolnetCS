package com.einz.solnetcs.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.einz.solnetcs.data.model.Customer
import com.einz.solnetcs.data.remote.responses.DataCustomer
import com.einz.solnetcs.data.remote.responses.DataTicket
import com.einz.solnetcs.data.remote.responses.Login
import com.einz.solnetcs.data.remote.retrofit.ApiConfig
import com.einz.solnetcs.data.remote.retrofit.ApiService
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class Repository(private val apiService: ApiService, private val context: Context) {

    private val _responseLogin = MutableLiveData<Result<Login>>()
    val responseLogin: LiveData<Result<Login>> = _responseLogin

    private val _responseGetCustomer = MutableLiveData<Result<DataCustomer>>()
    val responseGetCustomer: LiveData<Result<DataCustomer>> = _responseGetCustomer

    private val _responseGetTicket = MutableLiveData<Result<DataTicket>>()
    val responseGetTicket: LiveData<Result<DataTicket>> = _responseGetTicket

    //Firebase-isasi

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var database: DatabaseReference

    val userLiveData: MutableLiveData<FirebaseUser?> = MutableLiveData()

    val loggedOutLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val customerLiveData: MutableLiveData<Customer?> = MutableLiveData()

    init{
        if(firebaseAuth.currentUser != null){
            userLiveData.postValue(firebaseAuth.currentUser)
            loggedOutLiveData.postValue(false)
            database = Firebase.database.reference
        }
    }

    suspend fun registerFirebase(customer: Customer, password: String){
        firebaseAuth.createUserWithEmailAndPassword(customer.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userLiveData.postValue(firebaseAuth.currentUser)
                } else {
                    //post value error
                    userLiveData.postValue(null)
                    Log.d("Register", "register: ${task.exception?.message}")

                }
            }
        database.child("customers").push().setValue(customer)
            .addOnSuccessListener { userLiveData.postValue(firebaseAuth.currentUser)  }
            .addOnFailureListener { userLiveData.postValue(null) }
    }

    suspend fun loginFirebase (email: String, password: String){
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userLiveData.postValue(firebaseAuth.currentUser)
                } else {
                    userLiveData.postValue(null)
                }
            }
    }


    suspend fun logout() {
        firebaseAuth.signOut()
        loggedOutLiveData.postValue(true)
    }

    suspend fun getCustomerData(){
        val databaseReference = FirebaseDatabase.getInstance().getReference("customers")
        val userEmail = firebaseAuth.currentUser?.email ?: return

        databaseReference.orderByChild("email").equalTo(userEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val biodata = userSnapshot.getValue(Customer::class.java)
                            biodata?.let {
                                customerLiveData.postValue(it)
                            }
                        }
                    } else {
                        customerLiveData.postValue(null)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("Error", "onCancelled: ${databaseError.message}")
                }
            })
    }

}