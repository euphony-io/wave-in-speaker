package com.example.check_in_speaker.repository

import androidx.annotation.WorkerThread
import com.example.check_in_speaker.db.User
import com.example.check_in_speaker.db.UserDao
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    val allUsers: Flow<List<User>> = userDao.getAllUsers()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(user: User) {
        userDao.insertUser(user)
    }
}