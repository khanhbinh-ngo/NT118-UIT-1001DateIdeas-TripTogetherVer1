package com.example.triptogether.utilities

import com.example.triptogether.model.Trip
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test

class TripFiltersTest {
    @Test
    fun todayIsStillAnActiveTripAndInvalidDatesAreRejected() {
        val today = LocalDate.of(2026, 10, 9)
        assertFalse(TripFilters.isPast(Trip(endDate = "09/10/2026"), today))
        assertTrue(TripFilters.isPast(Trip(endDate = "08/10/2026"), today))
        assertNull(TripFilters.date("31/02/2026"))
        assertNotNull(TripFilters.date("29/02/2028"))
    }

    @Test
    fun searchesPreserveVietnameseLettersAndDateRangesOverlapInclusively() {
        val trip =
            Trip(
                name = "Đà Lạt",
                country = "Vietnam",
                startDate = "09/10/2026",
                endDate = "12/10/2026",
            )
        assertTrue(TripFilters.matches(trip, "đà"))
        assertEquals(
            listOf(trip),
            TripFilters.filter(listOf(trip), from = LocalDate.of(2026, 10, 12)),
        )
        assertTrue(TripFilters.filter(listOf(trip), from = LocalDate.of(2026, 10, 13)).isEmpty())
    }
}
