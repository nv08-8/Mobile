package vn.hcmute.bt11.repository

import vn.hcmute.bt11.database.DatabaseHelper
import vn.hcmute.bt11.models.User

class AuthRepository(private val databaseHelper: DatabaseHelper) {

    fun login(username: String, password: String): User? {
        return databaseHelper.loginUser(username, password)
    }

    fun register(username: String, email: String, password: String): Long {
        return databaseHelper.registerUser(username, email, password)
    }

    fun isUsernameExists(username: String): Boolean {
        return databaseHelper.checkUsernameExists(username)
    }

    fun isEmailExists(email: String): Boolean {
        return databaseHelper.checkEmailExists(email)
    }
}

