package com.plcoding.barcodescanner.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.plcoding.barcodescanner.model.ErrorResponse
import org.json.JSONObject
import kotlin.math.min

/**
 * Finds the closest strings from the given list to the target string
 * based on Levenshtein distance, and returns the top N results.
 */
fun findClosestStrings(target: String, strings: List<String>, limit: Int): List<String> {
    if (strings.isEmpty()) return emptyList()

    // Calculate distance for each string and store as pairs of (string, distance)
    val distancePairs = strings.map { str ->
        Pair(str, levenshteinDistance(target, str))
    }

    // Sort by distance (ascending) and take the specified number of results
    // Extract just the strings from the pairs
    return distancePairs.sortedBy { it.second }.take(minOf(limit, strings.size)).map { it.first }
}

/**
 * Calculates the Levenshtein distance between two strings.
 * This measures how many single character edits are needed
 * to transform one string into another.
 */
fun levenshteinDistance(s1: String, s2: String): Int {
    val m = s1.length
    val n = s2.length

    // Create a matrix of size (m+1) x (n+1)
    val dp = Array(m + 1) { IntArray(n + 1) }

    // Initialize the matrix
    for (i in 0..m) {
        dp[i][0] = i
    }

    for (j in 0..n) {
        dp[0][j] = j
    }

    // Fill the matrix
    for (i in 1..m) {
        for (j in 1..n) {
            val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
            dp[i][j] = min(
                min(
                    dp[i - 1][j] + 1,      // deletion
                    dp[i][j - 1] + 1       // insertion
                ), dp[i - 1][j - 1] + cost    // substitution
            )
        }
    }

    return dp[m][n]
}

fun getErrorFromErrorBody(errorBody: String): String {
    try {
        val jsonObject = JSONObject(errorBody)
        val errorResponse = Gson().fromJson(jsonObject.toString(), ErrorResponse::class.java)
        return errorResponse.error ?: ""
    } catch (e: Exception) {
        return "Error"

    }
}


fun saveUsername(context: Context, username: String) {
    val sharedPref: SharedPreferences =
        context.getSharedPreferences("my_prefrences_2", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.apply {
        putString("username1", username)
        apply()
    }


}

fun getUsername(context: Context): String? {
    val sharedPref: SharedPreferences =
        context.getSharedPreferences("my_prefrences_2", Context.MODE_PRIVATE)

    return sharedPref.getString("username1", null)

}
fun saveTeamNumberString(context: Context, teamNumberString: String) {
    val sharedPref: SharedPreferences =
        context.getSharedPreferences("my_prefrences_2", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.apply {
        putString("team_number_string", teamNumberString)
        apply()
    }


}

fun getTeamNumberString(context: Context): String? {
    val sharedPref: SharedPreferences =
        context.getSharedPreferences("my_prefrences_2", Context.MODE_PRIVATE)

    return sharedPref.getString("team_number_string", null)

}









