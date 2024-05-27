package lt.jb.aero.flight

import com.fasterxml.jackson.databind.ObjectMapper
import lt.jb.aero.flight.exception.FlightNotFoundException
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.time.LocalDateTime

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class FlightControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var mapper: ObjectMapper

    @MockBean
    lateinit var flightService: FlightService

    @Test
    fun `get all flights returns a list of flights and 200 http code`() {
        val flightDtoList = listOf(FlightDataFactory.generateFlightDto(), FlightDataFactory.generateFlightDto())
        `when`(flightService.getAllFlights()).thenReturn(flightDtoList)

        mockMvc.get("/api/flights")
            .andExpect {
                status { isOk() }
                jsonPath("$.length()") { value(2) }
            }
    }

    @Test
    fun `get flight status by flight number returns flight status and 200 http code`() {
        `when`(flightService.getFlightStatus(anyString(), any())).thenReturn(FlightStatus.SCHEDULED)

        mockMvc.get("/api/flights/ABC123/status")
            .andExpect {
                status { isOk() }
                jsonPath("$") { value(FlightStatus.SCHEDULED.name) }
            }
    }

    @Test
    fun `get flights by status returns list of flights and 200 http code`() {
        val flightDtoList = listOf(FlightDataFactory.generateFlightDto(), FlightDataFactory.generateFlightDto())
        `when`(flightService.getFlightsByStatus(FlightStatus.SCHEDULED)).thenReturn(flightDtoList)

        mockMvc.get("/api/flights/status/SCHEDULED")
            .andExpect {
                status { isOk() }
                jsonPath("$.length()") { value(2) }
            }
    }

    @Test
    fun `search flights by origin and destination returns list of flights and 200 http code`() {
        val flightDtoList = listOf(FlightDataFactory.generateFlightDto(), FlightDataFactory.generateFlightDto())
        `when`(flightService.searchFlightsByOriginDestination(anyString(), anyString())).thenReturn(flightDtoList)

        mockMvc.get("/api/flights/search?origin=VNO&destination=RIX")
            .andExpect {
                status { isOk() }
                jsonPath("$.length()") { value(2) }
            }
    }

    @Test
    fun `create flight returns saved dto and 201 http code`() {
        val createFlightDto = FlightDataFactory.generateCreateFlightDto()
        `when`(flightService.saveFlight(createFlightDto)).thenReturn(FlightDataFactory.generateFlightDto())

        mockMvc.post("/api/flights") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(createFlightDto)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            jsonPath("$.flightNumber") { value(createFlightDto.flightNumber) }
            jsonPath("$.aircraftType") { value(createFlightDto.aircraftType) }
            jsonPath("$.origin") { value(createFlightDto.origin) }
            jsonPath("$.destination") { value(createFlightDto.destination) }
            jsonPath("$.estimatedDepartureTime") { isNotEmpty() }
            jsonPath("$.estimatedArrivalTime") { isNotEmpty() }
            jsonPath("$.actualDepartureTime") { isEmpty() }
            jsonPath("$.actualArrivalTime") { isEmpty() }
            jsonPath("$.status") { value(createFlightDto.status.name) }
        }
    }

    @Test
    fun `create flight gives exception if estimated departure is after arrival is not valid`() {
        val createFlightDto =
            FlightDataFactory.generateCreateFlightDto(estimatedDepartureTime = LocalDateTime.now().plusHours(10))
        `when`(flightService.saveFlight(createFlightDto)).thenThrow(IllegalArgumentException("Date is not valid"))

        mockMvc.post("/api/flights") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(createFlightDto)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.statusCode") { value(400) }
            jsonPath("$.message") { value("Date is not valid") }
        }
    }

    @Test
    fun `register departure returns dto and 200 http code`() {
        val flightDto = FlightDataFactory.generateFlightDto(
            actualDepartureTime = LocalDateTime.now(),
            status = FlightStatus.DEPARTED
        )
        `when`(flightService.registerDeparture("ABC123")).thenReturn(flightDto)

        mockMvc.put("/api/flights/registerDeparture/ABC123") {
        }.andExpect {
            status { isOk() }
            jsonPath("$.flightNumber") { value(flightDto.flightNumber) }
            jsonPath("$.aircraftType") { value(flightDto.aircraftType) }
            jsonPath("$.origin") { value(flightDto.origin) }
            jsonPath("$.destination") { value(flightDto.destination) }
            jsonPath("$.estimatedDepartureTime") { isNotEmpty() }
            jsonPath("$.estimatedArrivalTime") { isNotEmpty() }
            jsonPath("$.actualDepartureTime") { isNotEmpty() }
            jsonPath("$.actualArrivalTime") { isEmpty() }
            jsonPath("$.status") { value(FlightStatus.DEPARTED.name) }
        }
    }

    @Test
    fun `register departure on non existing flight returns not found 404 http code`() {
        `when`(flightService.registerDeparture("ABC123")).thenThrow(FlightNotFoundException("Flight not found"))

        mockMvc.put("/api/flights/registerDeparture/ABC123") {
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.statusCode") { value(404) }
            jsonPath("$.message") { value("Flight not found") }
        }
    }

    @Test
    fun `register arrival returns dto and 200 http code`() {
        val flightDto = FlightDataFactory.generateFlightDto(
            actualDepartureTime = LocalDateTime.now(),
            actualArrivalTime = LocalDateTime.now(),
            status = FlightStatus.ARRIVED
        )
        `when`(flightService.registerArrival("ABC123")).thenReturn(flightDto)

        mockMvc.put("/api/flights/registerArrival/ABC123") {
        }.andExpect {
            status { isOk() }
            jsonPath("$.flightNumber") { value(flightDto.flightNumber) }
            jsonPath("$.aircraftType") { value(flightDto.aircraftType) }
            jsonPath("$.origin") { value(flightDto.origin) }
            jsonPath("$.destination") { value(flightDto.destination) }
            jsonPath("$.estimatedDepartureTime") { isNotEmpty() }
            jsonPath("$.estimatedArrivalTime") { isNotEmpty() }
            jsonPath("$.actualDepartureTime") { isNotEmpty() }
            jsonPath("$.actualArrivalTime") { isNotEmpty() }
            jsonPath("$.status") { value(FlightStatus.ARRIVED.name) }
        }
    }


}