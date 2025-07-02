package net.imknown.gitlabcontribution.gitlab

import io.ktor.client.call.body
import io.ktor.client.request.cookie
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.imknown.gitlabcontribution.createHttpClient
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

private const val undefinedCount = -1

@OptIn(ExperimentalTime::class)
fun getDateRange(): String {
    val systemTimeZone = TimeZone.currentSystemDefault()
    val dateNow = Clock.System.now()
    val dateOneYearBefore = dateNow.minus(1, DateTimeUnit.YEAR, systemTimeZone)
    fun Instant.getDateString() = toString().subSequence(0, 10)
    val dateFromString = dateOneYearBefore.getDateString()
    val dateToString = dateNow.getDateString()
    return "From $dateFromString to $dateToString"
}

suspend fun getCountList(users: List<User>): List<String> {
    class UserCount(val user: User, val count: Int)

    val client = createHttpClient()

    val results = mutableListOf<UserCount>()

    users.forEach { user ->
        if (user.dateLeaveTeam.isNotEmpty()) {
            results.add(UserCount(user, undefinedCount))
            return@forEach
        }

        val url = "https://$gitLabHost/users/${user.ldapName}/calendar.json"
        val response: HttpResponse = client.get(url) {
            cookie(name = "_gitlab_session", value = session)
        }
        val jsonString: String = response.body()

        val totalCount = try {
            getTotalCount(jsonString)
        } catch (_: Exception) {
            // User blocked
            undefinedCount
        }
        results.add(UserCount(user, totalCount))
    }

    client.close()

    val result = results.sortedByDescending(
        UserCount::count
    ).mapIndexed { index, userCount ->
        val figureInt = index + 1
        val figure = (if (figureInt < 10) "0" else "") + figureInt

        var name = userCount.user.name
        if (name.length == 2) {
            name = name.chunked(1).joinToString("　")
        }

        val count = userCount.count
        val comment = userCount.user.leaveTeamFormatted

        "#$figure $name: $count$comment"
    }

    return result
}

private fun getTotalCount(jsonString: String): Int {
    val json = Json.parseToJsonElement(jsonString)
    val list = json.jsonObject.toList()
    return list.groupBy { (date, json) ->
        // YYYY-MM
        date.take(7)
    }.map { (dateWithoutDay, listPair) ->
        // Total count per month
        listPair.sumOf { (date, json) ->
            json.jsonPrimitive.int
        }
    }.sum()
}

suspend fun makeText() = fetchGitLabCount()

private suspend fun fetchGitLabCount(): String {
    if (session.isEmpty()) {
        return "Please fill in the `_gitlab_session`."
    }

    return """
    |$gitLabHost
    |Users' contributions
    |${getDateRange()}
    |
    |${getCountList(users).joinToString("\n")}
    """.trimMargin()
}

private fun sortedByDateJoinTeam() = users.sortedBy(User::dateJoinTeam)
//    .filterNot {
//        it.dateLeaveTeam != emptyDate
//    }
    .mapIndexed { index, user ->
        val state = user.leaveTeamFormatted
        "#${index + 1} ${user.dateJoinTeam}: ${user.name}$state"
    }
    .joinToString("\n")