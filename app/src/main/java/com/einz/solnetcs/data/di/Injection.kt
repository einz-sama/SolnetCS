package com.einz.solnetcs.data.di
import android.content.Context
import com.einz.solnetcs.data.Repository
import com.einz.solnetcs.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context) : Repository {
        val apiService = ApiConfig.getApiService()
        return Repository(apiService, context)
    }
}