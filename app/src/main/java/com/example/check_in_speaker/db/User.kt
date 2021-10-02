package com.example.check_in_speaker.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) var id : Long? = 0,
    @ColumnInfo(name = "address") var address: String = "",
    @ColumnInfo(name = "date") var date: String = ""
)
