package com.example.metinproximityfront.services.token

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts

object TokenService {

    fun decodeJWT(jwt: String) : Claims{
        try {
            val claims = Jwts.parserBuilder()
                .build()
                .parseClaimsJws(jwt)

            return claims.body

        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid JWT token: " + e.message)
        }
    }

}