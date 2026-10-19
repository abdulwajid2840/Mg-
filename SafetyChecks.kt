package com.mgplus

object SafetyChecks {
    data class Warning(val message: String, val severe: Boolean = false)

    /**
     * Conservative text checks only. This is not a clinical dose validator.
     * Always verify a medicine and its dose against its prescription or pharmacist.
     */
    fun inspect(name: String, dose: String, unitsPerDose: Double? = null): List<Warning> {
        val text = "$name $dose".lowercase()
        val warnings = mutableListOf<Warning>()
        val suspicious = listOf(
            "uranium", "radioactive", "radiation", "cyanide", "arsenic",
            "poison", "bleach", "pesticide", "mercury", "antifreeze", "methanol"
        )
        if (suspicious.any(text::contains)) {
            warnings += Warning("This entry contains a substance name that does not look like a medicine. Verify it before proceeding.", true)
        }
        if (unitsPerDose != null && unitsPerDose > 10) {
            warnings += Warning("More than 10 units per dose. Double-check the prescription.", true)
        }
        val mass = Regex("""(?i)(\d+(?:\.\d+)?)\s*(mg|mcg|µg|g|kg)\b""")
        mass.findAll(text).forEach { match ->
            val amount = match.groupValues[1].toDoubleOrNull() ?: return@forEach
            val unit = match.groupValues[2].lowercase()
            val grams = when (unit) {
                "kg" -> amount * 1000.0
                "g" -> amount
                "mg" -> amount / 1000.0
                else -> amount / 1_000_000.0
            }
            if (grams > 20) warnings += Warning("The stated mass is unusually large. Do not use it until verified by a qualified professional.", true)
            else if (grams > 5) warnings += Warning("The stated mass may be unusually large. Verify the prescription.", true)
        }
        return warnings.distinctBy { it.message }
    }
}
