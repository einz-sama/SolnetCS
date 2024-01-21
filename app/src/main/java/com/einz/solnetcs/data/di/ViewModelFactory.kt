package com.einz.solnetcs.data.di


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.einz.solnetcs.data.Repository
import com.einz.solnetcs.ui.auth.login.LoginViewModel
import com.einz.solnetcs.ui.auth.register.RegisterViewModel
import com.einz.solnetcs.ui.cust.active_ticket.ActiveTicketViewModel
import com.einz.solnetcs.ui.cust.customer.CustomerViewModel
import com.einz.solnetcs.ui.cust.new_ticket.NewTicketViewModel


class ViewModelFactory private constructor(private val repository: Repository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                return LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(CustomerViewModel::class.java) -> {
                return CustomerViewModel(repository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                return RegisterViewModel(repository) as T
            }
            modelClass.isAssignableFrom(NewTicketViewModel::class.java) -> {
                return NewTicketViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ActiveTicketViewModel::class.java) -> {
                return ActiveTicketViewModel(repository) as T
            }



            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }

}