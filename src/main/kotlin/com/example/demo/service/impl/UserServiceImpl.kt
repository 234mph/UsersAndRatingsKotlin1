package com.example.demo.service.impl

import com.example.demo.model.Rating
import com.example.demo.model.User
import com.example.demo.service.UserService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserRecord
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UserServiceImpl : UserService {

    private val logger: Logger = LoggerFactory.getLogger(UserServiceImpl::class.java)
    private val usersRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")

    override fun register(email: String, password: String) {
        logger.info("Starting registration for email: {}", email)

        val request = UserRecord.CreateRequest().apply {
            setEmail(email)
            setPassword(password)
        }

        try {
            val userRecord = FirebaseAuth.getInstance().createUser(request)
            logger.info("User registered successfully with UID: {}", userRecord.uid)
            saveToDatabase(userRecord)
        } catch (e: FirebaseAuthException) {
            logger.error("Error registering user with email: {}: {}", email, e.message)
            throw RuntimeException(e)
        }
    }

    override fun saveToDatabase(userRecord: UserRecord) {
        logger.info("Saving user to database with UID: {}", userRecord.uid)

        val user = User().apply {
            email = userRecord.email
            rating = Rating(0, 0, 0.0)
        }

        usersRef.child(userRecord.uid).setValue(user) { error, _ ->
            if (error == null) {
                logger.info("User saved successfully to database with UID: {}", userRecord.uid)
            } else {
                logger.error("Error saving user to database with UID: {}: {}", userRecord.uid, error.message)
            }
        }
    }

    override fun assignUserRole(uid: String, role: String) {
        logger.info("Assigning role '{}' to user with UID: {}", role, uid)

        val claims = mapOf("role" to role)

        try {
            FirebaseAuth.getInstance().setCustomUserClaims(uid, claims)
            logger.info("Role '{}' assigned successfully to user with UID: {}", role, uid)
        } catch (e: FirebaseAuthException) {
            logger.error("Error assigning role '{}' to user with UID: {}: {}", role, uid, e.message)
            throw RuntimeException(e)
        }
    }
}
