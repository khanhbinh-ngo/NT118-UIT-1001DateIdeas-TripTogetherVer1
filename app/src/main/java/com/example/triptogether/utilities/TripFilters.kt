package com.example.triptogether.utilities

import com.example.triptogether.model.Trip
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.util.Locale

object TripFilters {
    private val dateFormat =
        DateTimeFormatter.ofPattern("dd/MM/uuuu", Locale.US).withResolverStyle(ResolverStyle.STRICT)

    fun date(value: String): LocalDate? =
        runCatching { LocalDate.parse(value, dateFormat) }.getOrNull()

    fun isPast(trip: Trip, today: LocalDate = LocalDate.now()): Boolean =
        date(trip.endDate)?.isBefore(today) == true

    fun matches(trip: Trip, query: String): Boolean {
        fun normalize(value: String) =
            value.lowercase(Locale.ROOT).replace(Regex("[^\\p{L}\\p{Nd}\\s]"), "").trim()
        val term = normalize(query)
        return term.isEmpty() ||
            normalize(trip.name).contains(term) ||
            normalize(trip.country).contains(term)
    }

    fun filter(
        trips: List<Trip>,
        query: String = "",
        destination: String = "",
        status: String = "all",
        from: LocalDate? = null,
        to: LocalDate? = null,
    ): List<Trip> =
        trips
            .filter { trip ->
                val start = date(trip.startDate)
                val end = date(trip.endDate)
                matches(trip, query) &&
                    trip.country.contains(destination.trim(), ignoreCase = true) &&
                    (status == "all" || (status == "past") == isPast(trip)) &&
                    (from == null || end != null && !end.isBefore(from)) &&
                    (to == null || start != null && !start.isAfter(to))
            }
            .sortedBy { date(it.startDate) ?: LocalDate.MAX }
}
