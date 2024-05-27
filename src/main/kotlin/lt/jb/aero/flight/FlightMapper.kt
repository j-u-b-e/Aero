package lt.jb.aero.flight

import lt.jb.aero.flight.db.Flight
import lt.jb.aero.flight.rest.dto.CreateFlightDto
import lt.jb.aero.flight.rest.dto.FlightDto

// Extension function to convert Flight entity to FlightDto
fun Flight.toDto(): FlightDto {
    return FlightDto(
        flightNumber = this.flightNumber,
        aircraftType = this.aircraftType,
        origin = this.origin,
        destination = this.destination,
        estimatedDepartureTime = this.estimatedDepartureTime,
        estimatedArrivalTime = this.estimatedArrivalTime,
        actualArrivalTime = this.actualArrivalTime,
        actualDepartureTime = this.actualDepartureTime,
        status = this.status
    )
}

// Extension function to convert CreateFlightDto to Flight entity
fun CreateFlightDto.toEntity(): Flight {
    return Flight(
        flightNumber = this.flightNumber,
        aircraftType = this.aircraftType,
        origin = this.origin,
        destination = this.destination,
        estimatedDepartureTime = this.estimatedDepartureTime,
        estimatedArrivalTime = this.estimatedArrivalTime,
        status = this.status
    )
}