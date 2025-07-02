package net.imknown.gitlabcontribution.gitlab

object UsersTemplate {
    enum class Team {
        Android
    }

    enum class Role {
        CTO
    }

    val users = listOf<User>(
        // User(Team.Android, Role.CTO, "imknown.kin", "imknown", "1970/01/01", emptyDate),
    )
}