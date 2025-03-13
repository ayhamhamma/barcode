package com.plcoding.barcodescanner.utils

import android.R
import com.google.gson.Gson
import com.google.gson.JsonObject
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
    return distancePairs
        .sortedBy { it.second }
        .take(minOf(limit, strings.size))
        .map { it.first }
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
                ),
                dp[i - 1][j - 1] + cost    // substitution
            )
        }
    }

    return dp[m][n]
}

fun getErrorFromErrorBody(errorBody: String): String {
    try {
        val jsonObject = JSONObject(errorBody)
        val errorResponse = Gson().fromJson(jsonObject.toString(), ErrorResponse::class.java)
        return errorResponse.error ?:""
    } catch (e: Exception) {
        return "Error"


    }

}