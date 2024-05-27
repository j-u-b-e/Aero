package lt.jb.aero.flight.rest.dto

import lt.jb.aero.flight.FlightStatus
import java.time.LocalDateTime

data class FlightDto(
    val flightNumber: String,
    val aircraftType: String,
    val origin: String,
    val destination: String,
    val estimatedDepartureTime: LocalDateTime,
    val estimatedArrivalTime: LocalDateTime,
    val actualDepartureTime: LocalDateTime? = null,
    val actualArrivalTime: LocalDateTime? = null,
    val status: FlightStatus
)