package lt.jb.aero.flight

import lt.jb.aero.flight.db.Flight
import lt.jb.aero.flight.rest.dto.CreateFlightDto
import lt.jb.aero.flight.rest.dto.FlightDto
import java.time.LocalDateTime

object FlightDataFactory {

    fun generateFlightEntity(
        flightNumber: String = "ABC123",
        aircraftType: String = "Boeing 737",
        origin: String = "VNO",
        destination: String = "RIX",
        status: FlightStatus = FlightStatus.SCHEDULED,
        estimatedDepartureTime: LocalDateTime = LocalDateTime.now().plusHours(2),
        estimatedArrivalTime: LocalDateTime = LocalDateTime.now().plusHours(4),
        actualDepartureTime: LocalDateTime? = null,
        actualArrivalTime: LocalDateTime? = null

    ): Flight {
        return Flight(
            flightNumber = flightNumber,
            aircraftType = aircraftType,
            origin = origin,
            destination = destination,
            status = status,
            estimatedDepartureTime = estimatedDepartureTime,
            estimatedArrivalTime = estimatedArrivalTime,
            actualDepartureTime = actualDepartureTime,
            actualArrivalTime = actualArrivalTime
        )
    }

    fun generateFlightDto(
        flightNumber: String = "ABC123",
        aircraftType: String = "Boeing 737",
        origin: String = "VNO",
        destination: String = "RIX",
        status: FlightStatus = FlightStatus.SCHEDULED,
        estimatedDepartureTime: LocalDateTime = LocalDateTime.now().plusHours(2),
        estimatedArrivalTime: LocalDateTime = LocalDateTime.now().plusHours(4),
        actualDepartureTime: LocalDateTime? = null,
        actualArrivalTime: LocalDateTime? = null

    ): FlightDto {
        return FlightDto(
            flightNumber = flightNumber,
            aircraftType = aircraftType,
            origin = origin,
            destination = destination,
            status = status,
            estimatedDepartureTime = estimatedDepartureTime,
            estimatedArrivalTime = estimatedArrivalTime,
            actualDepartureTime = actualDepartureTime,
            actualArrivalTime = actualArrivalTime
        )
    }

    fun generateCreateFlightDto(
        flightNumber: String = "ABC123",
        aircraftType: String = "Boeing 737",
        origin: String = "VNO",
        destination: String = "RIX",
        status: FlightStatus = FlightStatus.SCHEDULED,
        estimatedDepartureTime: LocalDateTime = LocalDateTime.now().plusHours(2),
        estimatedArrivalTime: LocalDateTime = LocalDateTime.now().plusHours(4)

    ): CreateFlightDto {
        return CreateFlightDto(
            flightNumber = flightNumber,
            aircraftType = aircraftType,
            origin = origin,
            destination = destination,
            status = status,
            estimatedDepartureTime = estimatedDepartureTime,
            estimatedArrivalTime = estimatedArrivalTime
        )
    }
}