package com.example.check_in_speaker

import android.app.Application
import com.example.check_in_speaker.db.UserDatabase
import com.example.check_in_speaker.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MainApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { UserDatabase.getDatabase(this) }
    val repository by lazy { UserRepository(database.userDao())}
}