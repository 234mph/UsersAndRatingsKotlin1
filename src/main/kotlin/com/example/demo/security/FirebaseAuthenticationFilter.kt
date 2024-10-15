package com.example.demo.security

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class FirebaseAuthenticationFilter : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)

            try {
                val decodedToken: FirebaseToken = FirebaseAuth.getInstance().verifyIdToken(token)
                val uid = decodedToken.uid
                val role = decodedToken.claims["role"] as String?

                val authorities = getAuthoritiesFromToken(decodedToken)

                SecurityContextHolder.getContext().authentication =
                    FirebaseAuthenticationToken(token, decodedToken, authorities).apply {
                        isAuthenticated = true
                    }
            } catch (e: Exception) {
                println("Invalid Firebase token: ${e.message}")
            }
        }

        chain.doFilter(request, response)
    }

    private fun getAuthoritiesFromToken(token: FirebaseToken): List<GrantedAuthority> {
        val claims = token.claims["role"] as String?
        val permissions = mutableListOf<String>()
        claims?.let { permissions.add(it) }
        return if (permissions.isNotEmpty()) {
            AuthorityUtils.createAuthorityList(*permissions.toTypedArray())
        } else {
            AuthorityUtils.NO_AUTHORITIES
        }
    }
}
