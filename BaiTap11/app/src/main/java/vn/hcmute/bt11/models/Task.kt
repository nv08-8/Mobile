package vn.hcmute.bt11.models

data class Task(
    val id: Int = 0,
    val userId: Int,
    val title: String,
    val description: String?,
    val date: String?,
    val time: String?,
    var isCompleted: Boolean = false
)

