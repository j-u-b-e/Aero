package lt.jb.aero.terminal

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class TerminalService {

    companion object {
        val logger = Logger.getLogger(TerminalService::class.java.name)
    }

    private val arrivals = ArrayList<String>()
    private val mutex = Mutex()

    suspend fun addToArrivalList(flightNumber: String) {
        mutex.withLock {
            arrivals.add(flightNumber)
        }
        logger.info("Flight ${flightNumber} has been added to arrivals list")
    }

    suspend fun performTerminalWorkerJob(terminal: Terminal, terminalService: TerminalService) {
        val flightNumber: String? = terminalService.assignTerminalFlight(terminal)
        flightNumber?.let {
            logger.info("Flight ${flightNumber} assigned to terminal ${terminal.terminalNumber}")
            delay(10000) // Simulating processing of Flight in a terminal. Needed for this task. Could be replaced by APIs to clear terminal after it is again available.
            logger.info("Flight ${flightNumber} in terminal ${terminal.terminalNumber} was serviced. Terminal is clear.")
        }
        delay(1000) //check for new flights every second
    }

    suspend fun assignTerminalFlight(terminal: Terminal): String? {
        return mutex.withLock {
            if (arrivals.isNotEmpty()) {
                val flight = arrivals.removeAt(0)
                terminal.currentFlight = flight
                flight
            } else {
                null
            }
        }
    }

}