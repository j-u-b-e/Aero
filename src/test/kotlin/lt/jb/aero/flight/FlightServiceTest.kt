package lt.jb.aero.flight

import lt.jb.aero.flight.db.FlightRepository
import lt.jb.aero.flight.exception.FlightNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import kotlin.test.assertNull

@ActiveProfiles("test")
@SpringBootTest
class FlightServiceTest {

    @Autowired
    lateinit var flightService: FlightService

    @Autowired
    lateinit var flightRepository: FlightRepository

    @BeforeEach
    fun init() {
        flightRepository.deleteAll()
    }

    @Test
    fun `register scheduled flight saves to DB correct fields`() {
        val scheduledFlight = FlightDataFactory.generateCreateFlightDto()

        val savedEntity = flightService.saveFlight(scheduledFlight)

        assertEquals(scheduledFlight.flightNumber, savedEntity.flightNumber)
        assertEquals(scheduledFlight.aircraftType, savedEntity.aircraftType)
        assertEquals(scheduledFlight.origin, savedEntity.origin)
        assertEquals(scheduledFlight.destination, savedEntity.destination)
        assertEquals(scheduledFlight.estimatedArrivalTime, savedEntity.estimatedArrivalTime)
        assertEquals(scheduledFlight.estimatedDepartureTime, savedEntity.estimatedDepartureTime)
        assertNull(savedEntity.actualDepartureTime)
        assertNull(savedEntity.actualArrivalTime)
        assertEquals(FlightStatus.SCHEDULED, savedEntity.status)

    }

    @Test
    fun `register scheduled flight with departure time after arrival time throws exception`() {
        val scheduledFlight =
            FlightDataFactory.generateCreateFlightDto(estimatedDepartureTime = LocalDateTime.now().plusHours(10))

        assertThrows<IllegalArgumentException> {
            flightService.saveFlight(scheduledFlight)
        }
    }

    @Test
    fun `register departure flight saves to DB correct fields`() {
        val scheduledFlight = FlightDataFactory.generateFlightEntity()
        flightRepository.save(scheduledFlight)

        val savedEntity = flightService.registerDeparture(scheduledFlight.flightNumber)

        assertEquals(scheduledFlight.flightNumber, savedEntity.flightNumber)
        assertEquals(scheduledFlight.aircraftType, savedEntity.aircraftType)
        assertEquals(scheduledFlight.origin, savedEntity.origin)
        assertEquals(scheduledFlight.destination, savedEntity.destination)
        assertNotNull(savedEntity.estimatedArrivalTime)
        assertNotNull(savedEntity.estimatedDepartureTime)
        assertNotNull(savedEntity.actualDepartureTime)
        assertNull(savedEntity.actualArrivalTime)
        assertEquals(FlightStatus.DEPARTED, savedEntity.status)
    }

    @Test
    fun `registering departure of not scheduled flight throws FlightNotFoundException`() {
        assertThrows<FlightNotFoundException> {
            flightService.registerDeparture("XX-XXX")
        }
    }

    @Test
    fun `register arrival flight saves to DB correct fields`() {
        val scheduledFlight = FlightDataFactory.generateFlightEntity(
            status = FlightStatus.DEPARTED,
            actualDepartureTime = LocalDateTime.now()
        )
        flightRepository.save(scheduledFlight)

        val savedEntity = flightService.registerArrival(scheduledFlight.flightNumber)

        assertEquals(scheduledFlight.flightNumber, savedEntity.flightNumber)
        assertEquals(scheduledFlight.aircraftType, savedEntity.aircraftType)
        assertEquals(scheduledFlight.origin, savedEntity.origin)
        assertEquals(scheduledFlight.destination, savedEntity.destination)
        assertNotNull(savedEntity.estimatedArrivalTime)
        assertNotNull(savedEntity.estimatedDepartureTime)
        assertNotNull(savedEntity.actualDepartureTime)
        assertNotNull(savedEntity.actualArrivalTime)
        assertEquals(FlightStatus.ARRIVED, savedEntity.status)
    }

    @Test
    fun `registering arrival of not departed flight throws FlightNotFoundException`() {
        val scheduledFlight = FlightDataFactory.generateCreateFlightDto()
        flightService.saveFlight(scheduledFlight)

        assertThrows<FlightNotFoundException> {
            flightService.registerArrival(scheduledFlight.flightNumber)
        }
    }

    @Test
    fun `getting flight status returns correct flight status`() {
        val scheduledFlight = FlightDataFactory.generateFlightEntity()
        flightRepository.save(scheduledFlight)

        val flightStatus = flightService.getFlightStatus("ABC123")
        assertEquals(FlightStatus.SCHEDULED, flightStatus)
    }

    @Test
    fun `getting flight status of non existing flight returns FlightNotFound exception`() {
        val scheduledFlight = FlightDataFactory.generateFlightEntity()
        flightRepository.save(scheduledFlight)

        assertThrows<FlightNotFoundException> {
            flightService.getFlightStatus("XXX")
        }
    }

    @Test
    fun `getting flight by number returns flight by today's departure date if there are multiple flights with same flight number`() {
        val oldFlight = FlightDataFactory.generateFlightEntity(
            estimatedDepartureTime = LocalDateTime.now().minusDays(1),
            estimatedArrivalTime = LocalDateTime.now().minusDays(1),
            status = FlightStatus.ARRIVED
        )
        flightRepository.save(oldFlight)
        val scheduledFlight = FlightDataFactory.generateFlightEntity()
        flightRepository.save(scheduledFlight)

        val result = flightService.getFlightByNumber("ABC123")
        assertEquals(scheduledFlight.flightNumber, result.flightNumber)
        assertEquals(scheduledFlight.aircraftType, result.aircraftType)
        assertEquals(scheduledFlight.origin, result.origin)
        assertEquals(scheduledFlight.destination, result.destination)
        assertNotNull(result.estimatedArrivalTime)
        assertNotNull(result.estimatedDepartureTime)
        assertNull(result.actualDepartureTime)
        assertNull(result.actualArrivalTime)
        assertEquals(FlightStatus.SCHEDULED, result.status)
    }

    @Test
    fun `search flights by origin returns correct flights`() {
        val vnoFlight = FlightDataFactory.generateFlightEntity()
        flightRepository.save(vnoFlight)
        val wawFlight = FlightDataFactory.generateFlightEntity(origin = "WAW")
        flightRepository.save(wawFlight)

        val result = flightService.searchFlightsByOriginDestination(vnoFlight.origin, null)

        assertEquals(1, result.size)
        assertEquals(vnoFlight.origin, result.get(0).origin)
    }

    @Test
    fun `search flights by destination returns correct flights`() {
        val jfkFlight = FlightDataFactory.generateFlightEntity(destination = "JFK")
        flightRepository.save(jfkFlight)
        val wawFlight = FlightDataFactory.generateFlightEntity()
        flightRepository.save(wawFlight)

        val result = flightService.searchFlightsByOriginDestination(null, jfkFlight.destination)

        assertEquals(1, result.size)
        assertEquals(jfkFlight.destination, result.get(0).destination)
    }

    @Test
    fun `search flights by origin and destination returns correct flights`() {
        val jfkFlight1 = FlightDataFactory.generateFlightEntity(destination = "JFK")
        val jfkFlight2 = FlightDataFactory.generateFlightEntity(
            destination = "JFK",
            estimatedDepartureTime = LocalDateTime.now().plusDays(1),
            estimatedArrivalTime = LocalDateTime.now().plusDays(1)
        )
        flightRepository.save(jfkFlight1)
        flightRepository.save(jfkFlight2)
        val wawFlight = FlightDataFactory.generateFlightEntity()
        flightRepository.save(wawFlight)

        val result = flightService.searchFlightsByOriginDestination(jfkFlight1.origin, jfkFlight1.destination)

        assertEquals(2, result.size)
        assertEquals(jfkFlight1.origin, result.get(0).origin)
        assertEquals(jfkFlight1.destination, result.get(0).destination)
        assertEquals(jfkFlight2.origin, result.get(1).origin)
        assertEquals(jfkFlight2.destination, result.get(1).destination)
    }

    @Test
    fun `search flights by origin and destination returns empty list if no flights found`() {
        val result = flightService.searchFlightsByOriginDestination(null, null)
        assertEquals(0, result.size)
    }

    @Test
    fun `get all flights returns all flights`() {
        val jfkFlight1 = FlightDataFactory.generateFlightEntity(destination = "JFK")
        val jfkFlight2 = FlightDataFactory.generateFlightEntity(
            destination = "JFK",
            estimatedDepartureTime = LocalDateTime.now().plusDays(1),
            estimatedArrivalTime = LocalDateTime.now().plusDays(1)
        )
        flightRepository.save(jfkFlight1)
        flightRepository.save(jfkFlight2)
        val wawFlight = FlightDataFactory.generateFlightEntity()
        flightRepository.save(wawFlight)

        val result = flightService.getAllFlights()
        assertEquals(3, result.size)
    }

    @Test
    fun `get flights by status returns correct flights`() {
        val jfkFlight1 = FlightDataFactory.generateFlightEntity(destination = "JFK", status = FlightStatus.ARRIVED)
        val jfkFlight2 = FlightDataFactory.generateFlightEntity(
            destination = "JFK",
            estimatedDepartureTime = LocalDateTime.now().plusDays(1),
            estimatedArrivalTime = LocalDateTime.now().plusDays(1)
        )
        flightRepository.save(jfkFlight1)
        flightRepository.save(jfkFlight2)
        val wawFlight = FlightDataFactory.generateFlightEntity()
        flightRepository.save(wawFlight)

        val result = flightService.getFlightsByStatus(FlightStatus.SCHEDULED)
        assertEquals(2, result.size)
    }

}