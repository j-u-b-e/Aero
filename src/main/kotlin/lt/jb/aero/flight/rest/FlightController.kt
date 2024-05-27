package lt.jb.aero.flight.rest

import lt.jb.aero.flight.FlightService
import lt.jb.aero.flight.FlightStatus
import lt.jb.aero.flight.rest.dto.CreateFlightDto
import lt.jb.aero.flight.rest.dto.FlightDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.logging.Logger

@RestController
@RequestMapping("/api/flights")
class FlightController(val flightService: FlightService) {

    companion object {
        val logger = Logger.getLogger(FlightController::class.java.name)
    }

    @GetMapping
    fun getAllFlights(): ResponseEntity<List<FlightDto>> {
        logger.info("Started GET request for allFlights.")

        val flights = flightService.getAllFlights()

        logger.info("Finished GET request for allFlights. Found ${flights.size} flights.")
        return ResponseEntity(flights, HttpStatus.OK)
    }

    @GetMapping("/{flightNumber}/status")
    fun getFlightStatus(@PathVariable flightNumber: String): ResponseEntity<FlightStatus> {
        logger.info("Started GET request for flight status by flight number. Flight number=$flightNumber.")

        val flightStatus = flightService.getFlightStatus(flightNumber)

        logger.info("Finished GET request for flight status by flight number. Flight number=$flightNumber.")
        return ResponseEntity(flightStatus, HttpStatus.OK)
    }

    @GetMapping("/status/{status}")
    fun getFlightsByStatus(@PathVariable status: FlightStatus): ResponseEntity<List<FlightDto>> {
        logger.info("Started GET request for flights by status. Flight status=$status.")

        val flights = flightService.getFlightsByStatus(status)

        logger.info("Finished GET request for flights by status. Flight status=$status. Found ${flights.size} flights.")
        return ResponseEntity(flights, HttpStatus.OK)
    }

    @GetMapping("/search")
    fun searchFlightsByOriginDestination(
        @RequestParam(required = false) origin: String?,
        @RequestParam(required = false) destination: String?
    ): ResponseEntity<List<FlightDto>> {
        logger.info("Started GET request to search for flights. Origin=$origin, destination=$destination.")

        val flights = flightService.searchFlightsByOriginDestination(origin, destination)

        logger.info("Finished GET request to search for flights. Origin=$origin, destination=$destination. Found ${flights.size} flights.")
        return ResponseEntity(flights, HttpStatus.OK)
    }

    @PostMapping
    fun createFlight(@RequestBody createFlightDto: CreateFlightDto): ResponseEntity<FlightDto> {
        logger.info("Started POST to create flight. CreateFlightDto=$createFlightDto")

        val savedFlightDto = flightService.saveFlight(createFlightDto)

        logger.info("Finished POST to create flight.")
        return ResponseEntity(savedFlightDto, HttpStatus.CREATED)
    }

    @PutMapping("/registerDeparture/{flightNumber}")
    fun registerDeparture(@PathVariable flightNumber: String): ResponseEntity<FlightDto> {
        logger.info("Started PUT to register departure. flightNumber=$flightNumber")

        val updatedFlightDto = flightService.registerDeparture(flightNumber)

        logger.info("Finished PUT to register departure. flightNumber=$flightNumber")
        return ResponseEntity(updatedFlightDto, HttpStatus.OK)
    }

    @PutMapping("/registerArrival/{flightNumber}")
    fun registerArrival(@PathVariable flightNumber: String): ResponseEntity<FlightDto> {
        logger.info("Started PUT to register arrival. flightNumber=$flightNumber")

        val updatedFlightDto = flightService.registerArrival(flightNumber)

        logger.info("Finished PUT to register arrival. flightNumber=$flightNumber")
        return ResponseEntity(updatedFlightDto, HttpStatus.OK)
    }
}