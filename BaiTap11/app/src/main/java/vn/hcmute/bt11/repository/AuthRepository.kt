package vn.hcmute.bt11.repository
}
    }
        return databaseHelper.checkEmailExists(email)
    fun isEmailExists(email: String): Boolean {

    }
        return databaseHelper.checkUsernameExists(username)
    fun isUsernameExists(username: String): Boolean {

    }
        return databaseHelper.registerUser(username, email, password)
    fun register(username: String, email: String, password: String): Long {

    }
        return databaseHelper.loginUser(username, password)
    fun login(username: String, password: String): User? {

class AuthRepository(private val databaseHelper: DatabaseHelper) {

import vn.hcmute.bt11.models.User
import vn.hcmute.bt11.database.DatabaseHelper


