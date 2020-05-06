package com.rcorp.mapts.data.db

import com.rcorp.mapts.model.user.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.security.MessageDigest
import java.util.*

object UserDao {

    private val loginUsers = mutableListOf<User>()

    fun register(user: User) {
        val login: String = user.login ?: throw Exception("Cannot find parameter login")
        val email: String = user.email ?: throw Exception("Cannot find parameter email")
        val password: String = user.password ?: throw Exception("Cannot find parameter password")
        transaction {
            addLogger(StdOutSqlLogger)
            UserSQLData.insert {
                it[this.login] = login
                it[this.email] = email
                it[this.password] = password.toMD5()
            }
        }
    }

    fun login(user: User): User? {
        var userResult: User? = null/**/
        val login: String = user.login ?: throw Exception("Cannot find parameter login")
        val password: String = user.password ?: throw Exception("Cannot find parameter password")
        transaction {
            val result = UserSQLData.select { (UserSQLData.login eq login) and (UserSQLData.password eq password.toMD5()) }.firstOrNull()
            result ?: return@transaction
            userResult = User(result[UserSQLData.id].toInt(), result[UserSQLData.login], result[UserSQLData.email], null, UUID.randomUUID().toString())
        }
        if (userResult != null)
            loginUsers.add(userResult!!)
        return userResult
    }

    object UserSQLData : Table("user") {
        val id = long("id")
        val login = text("login")
        val email = text("email")
        val password = text("password")
    }
}

fun String.toMD5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.toHex()
}

fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}