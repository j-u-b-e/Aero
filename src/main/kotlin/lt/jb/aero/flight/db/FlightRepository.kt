package lt.jb.aero.flight.db

import lt.jb.aero.flight.FlightStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
interface FlightRepository : JpaRepository<Flight, Long> {
    fun findByOrigin(origin: String): List<Flight>
    fun findByDestination(destination: String): List<Flight>
    fun findByOriginAndDestination(origin: String, destination: String): List<Flight>
    fun findByStatus(status: FlightStatus): List<Flight>
    fun findByStatusAndFlightNumber(status: FlightStatus, flightNumber: String): Flight?

    @Query("SELECT f FROM Flight f WHERE f.flightNumber = :flightNumber AND f.status = :status ORDER BY f.estimatedDepartureTime ASC")
    fun findMostRecentFlightByFlightNumberAndStatus(
        @Param("flightNumber") flightNumber: String,
        @Param("status") status: FlightStatus
    ): List<Flight>

    @Query("SELECT f FROM Flight f WHERE f.flightNumber = :flightNumber AND CAST(f.estimatedDepartureTime AS DATE) = :date ORDER BY f.estimatedDepartureTime ASC")
    fun findMostRecentFlightByFlightNumberAndDate(
        @Param("flightNumber") flightNumber: String,
        @Param("date") date: LocalDate
    ): List<Flight>

}