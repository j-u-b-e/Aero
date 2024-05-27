package lt.jb.aero.flight.db

import jakarta.persistence.*
import lt.jb.aero.flight.FlightStatus
import java.time.LocalDateTime

@Entity
data class Flight(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    val flightNumber: String,
    val aircraftType: String,
    val origin: String,
    val destination: String,
    val estimatedDepartureTime: LocalDateTime,
    val estimatedArrivalTime: LocalDateTime,
    var actualDepartureTime: LocalDateTime? = null,
    var actualArrivalTime: LocalDateTime? = null,

    @Enumerated(EnumType.STRING)
    var status: FlightStatus
)