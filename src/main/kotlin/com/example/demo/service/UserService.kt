package com.example.demo.service

import com.google.firebase.auth.UserRecord

interface UserService {
    fun register(email: String, password: String)
    fun saveToDatabase(userRecord: UserRecord)
    fun assignUserRole(uid: String, role: String)
}

