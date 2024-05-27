package lt.jb.aero.flight.rest.dto

import lt.jb.aero.flight.FlightStatus
import java.time.LocalDateTime

data class CreateFlightDto(
    val flightNumber: String,
    val aircraftType: String,
    val origin: String,
    val destination: String,
    val estimatedDepartureTime: LocalDateTime,
    val estimatedArrivalTime: LocalDateTime,
    val status: FlightStatus = FlightStatus.SCHEDULED
)