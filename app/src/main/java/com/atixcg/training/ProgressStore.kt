package com.atixcg.training

import android.content.Context
import org.json.JSONObject

/**
 * Persists completed-set counts and the selected day, mirroring the
 * localStorage("gym-program-v1") behaviour of the original web page.
 * Keys look like "d1-2" (day 1, exercise #2) -> number of completed sets.
 */
class ProgressStore(context: Context) {
    private val prefs = context.getSharedPreferences("gym-program-v1", Context.MODE_PRIVATE)

    fun loadDay(): Int = if (prefs.getInt("day", 1) == 2) 2 else 1

    fun saveDay(day: Int) {
        prefs.edit().putInt("day", day).apply()
    }

    fun loadSets(): Map<String, Int> {
        val raw = prefs.getString("sets", null) ?: return emptyMap()
        return try {
            val obj = JSONObject(raw)
            buildMap {
                for (key in obj.keys()) put(key, obj.getInt(key))
            }
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun saveSets(sets: Map<String, Int>) {
        val obj = JSONObject()
        for ((k, v) in sets) if (v > 0) obj.put(k, v)
        prefs.edit().putString("sets", obj.toString()).apply()
    }
}
