package lt.jb.aero.terminal

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
@ExperimentalCoroutinesApi
class TerminalServiceTest {

    private lateinit var terminalService: TerminalService

    @BeforeEach
    fun setup() {
        terminalService = TerminalService()
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `multiple fligts added to the list - one terminal picks first flight`() = runTest {
        terminalService.addToArrivalList("AA123")
        terminalService.addToArrivalList("BB123")

        val terminal = Terminal("A1")
        terminalService.performTerminalWorkerJob(terminal, terminalService)

        assertEquals("AA123", terminal.currentFlight)
    }

    @Test
    fun `three terminals processing two flights - two terminals get flight, one left empty`() = runTest {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        val terminals = listOf(
            Terminal("A1"),
            Terminal("A2"),
            Terminal("B3")
        )

        terminalService.addToArrivalList("AA123")
        terminalService.addToArrivalList("BB123")

        terminals.forEach { terminal ->
            scope.launch {
                terminalService.performTerminalWorkerJob(terminal, terminalService)
            }
        }

        advanceTimeBy(2000) // Advance virtual time to simulate processing

        assertEquals("AA123", terminals[0].currentFlight)
        assertEquals("BB123", terminals[1].currentFlight)
        assertNull(terminals[2].currentFlight)
    }
}