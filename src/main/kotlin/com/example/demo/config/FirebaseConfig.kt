package com.example.demo.config


import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import java.io.IOException

@Component
open class FirebaseConfig {

    @Value("\${firebase.url}")
    private lateinit var firebaseDatabaseUrl: String

    @Value("\${firebase.service-account-path}")
    private lateinit var firebaseConfigPath: String

    @PostConstruct
    @Throws(IOException::class)
    fun initializeFirebaseApp(): FirebaseApp {
        val credentials = GoogleCredentials
            .fromStream(ClassPathResource(firebaseConfigPath).inputStream)

        val options = FirebaseOptions.builder()
            .setCredentials(credentials)
            .setDatabaseUrl(firebaseDatabaseUrl)
            .build()

        return FirebaseApp.initializeApp(options)
    }
}
