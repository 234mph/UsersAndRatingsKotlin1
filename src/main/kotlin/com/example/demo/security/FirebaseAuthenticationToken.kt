package com.example.demo.security

import com.google.firebase.auth.FirebaseToken
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

//class FirebaseAuthenticationToken(
//    private val idToken: String,
//    private val firebaseToken: FirebaseToken,
//    authorities: List<GrantedAuthority>
//) : AbstractAuthenticationToken(authorities) {
//
//    constructor(authorities: Collection<GrantedAuthority>) : this("", FirebaseToken(), authorities.toList())
//
//    override fun getCredentials(): Any? = null
//
//    override fun getPrincipal(): Any = firebaseToken.uid
//}
class FirebaseAuthenticationToken(
    private val idToken: String,
    private val firebaseToken: FirebaseToken,
    authorities: List<GrantedAuthority>
) : AbstractAuthenticationToken(authorities) {

   // constructor(authorities: Collection<GrantedAuthority>) : this("", FirebaseToken(), authorities.toList())

    override fun getCredentials(): Any? = null
    override fun getPrincipal(): Any = firebaseToken.uid
}

