package net.imknown.gitlabcontribution.gitlab

const val emptyDate = ""

data class User(
    val team: Team,
    val role: Role,
    val ldapName: String,
    val name: String,
    val dateJoinTeam: String,
    val dateLeaveTeam: String
) {
    val leaveTeamFormatted =
        if (dateLeaveTeam.isNotEmpty()) " ($dateLeaveTeam left)" else emptyDate
}