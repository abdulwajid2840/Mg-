package com.mgplus

import java.util.UUID

enum class ScheduleMode { FIXED_TIMES, INTERVAL, AS_NEEDED }

data class Medicine(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val dose: String,
    val mode: ScheduleMode = ScheduleMode.FIXED_TIMES,
    val times: List<String> = emptyList(),
    val intervalMinutes: Int = 0,
    val windowStart: String = "08:00",
    val windowEnd: String = "20:00",
    val weekdays: Set<Int> = (1..7).toSet(),
    val notes: String = "",
    val stock: Int = -1,
    val colorIndex: Int = 0,
    val taken: Boolean = false
)
