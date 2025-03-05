package com.example.metinproximityfront.services.token

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import java.util.Base64
import org.json.JSONObject

object TokenService {

    fun decodeJWT(jwt: String) : Map<String, Any> {
        try {

            val parts = jwt.split(".")

            if (parts.size != 3) {
                throw IllegalArgumentException("Invalid JWT token format")
            }

            val payloadJson = String(Base64.getUrlDecoder().decode(parts[1]))

            val mapper = jacksonObjectMapper()

            return mapper.readValue(payloadJson, object : TypeReference<Map<String, Any>>() {})

        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid JWT token: " + e.message)
        }
    }

}