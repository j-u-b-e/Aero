package lt.jb.aero.flight

import kotlinx.coroutines.runBlocking
import lt.jb.aero.flight.db.FlightRepository
import lt.jb.aero.flight.exception.FlightNotFoundException
import lt.jb.aero.flight.rest.dto.CreateFlightDto
import lt.jb.aero.flight.rest.dto.FlightDto
import lt.jb.aero.terminal.TerminalService
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.logging.Logger

@Service
class FlightService(private val flightRepository: FlightRepository, private val terminalService: TerminalService) {

    companion object {
        val logger = Logger.getLogger(FlightService::class.java.name)
    }

    fun saveFlight(createFlightDto: CreateFlightDto): FlightDto {
        if (createFlightDto.estimatedDepartureTime.isAfter(createFlightDto.estimatedArrivalTime)) {
            throw IllegalArgumentException("Estimated Departure Time cannot be after Estimated Arrival Time")
        }

        val flight = createFlightDto.toEntity()
        val savedFlight = flightRepository.save(flight)
        logger.info("Flight ${savedFlight.flightNumber} has been registered")
        return savedFlight.toDto()
    }

    fun registerDeparture(flightNumber: String): FlightDto {
        val flight = flightRepository.findMostRecentFlightByFlightNumberAndStatus(flightNumber, FlightStatus.SCHEDULED)
            ?: throw FlightNotFoundException("Flight not found: $flightNumber")

        flight.actualDepartureTime = LocalDateTime.now()
        flight.status = FlightStatus.DEPARTED
        val updatedFlight = flightRepository.save(flight)

        logger.info("Flight number=[$flightNumber] has departed")
        return updatedFlight.toDto()
    }

    fun registerArrival(flightNumber: String): FlightDto {
        val flight = flightRepository.findMostRecentFlightByFlightNumberAndStatus(flightNumber, FlightStatus.DEPARTED)
            ?: throw FlightNotFoundException("Flight not found: $flightNumber")
        flight.actualArrivalTime = LocalDateTime.now()
        flight.status = FlightStatus.ARRIVED
        val updatedFlight = flightRepository.save(flight)

        runBlocking {
            terminalService.addToArrivalList(flightNumber)
        }

        logger.info("Flight number=[$flightNumber] has arrived")
        return updatedFlight.toDto()
    }

    fun getAllFlights(): List<FlightDto> {
        return flightRepository.findAll().map { it.toDto() }
    }

    fun getFlightStatus(flightNumber: String): FlightStatus {
        return getFlightByNumber(flightNumber).status
    }

    fun getFlightByNumber(flightNumber: String): FlightDto {
        return getFlightByNumberAndByDepartureDate(flightNumber, LocalDate.now())
    }

    //If there are more scheduled flights, then we need to find flight which is departing today.
    private fun getFlightByNumberAndByDepartureDate(flightNumber: String, departureDate: LocalDate): FlightDto {
        val startOfDay = departureDate.atStartOfDay()
        val endOfDay = departureDate.atTime(LocalTime.MAX)
        return flightRepository.findByFlightNumberAndEstimatedDepartureDate(flightNumber, startOfDay, endOfDay)?.toDto()
            ?: throw FlightNotFoundException("Flight not found: $flightNumber")
    }

    fun searchFlightsByOriginDestination(origin: String?, destination: String?): List<FlightDto> {
        return when {
            origin != null && destination != null -> flightRepository.findByOriginAndDestination(origin, destination)
            origin != null -> flightRepository.findByOrigin(origin)
            destination != null -> flightRepository.findByDestination(destination)
            else -> emptyList()
        }.map { it.toDto() }
    }

    fun getFlightsByStatus(status: FlightStatus): List<FlightDto> {
        return flightRepository.findByStatus(status).map { it.toDto() }
    }

}