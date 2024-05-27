package lt.jb.aero.terminal

import kotlinx.coroutines.*
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.util.logging.Logger

@Configuration
@Profile("!test")
class CoroutineStarter(val terminalService: TerminalService) {

    companion object {
        val logger = Logger.getLogger(CoroutineStarter::class.java.name)
    }

    fun CoroutineScope.terminalWorker(
        terminal: Terminal,
        terminalService: TerminalService
    ) = launch {
        logger.info("Starting terminal worker. Terminal=${terminal.terminalNumber}")
        while (isActive) {
            terminalService.performTerminalWorkerJob(terminal, terminalService)
        }
    }

    @Bean
    fun startCoroutines(): ApplicationRunner {
        return ApplicationRunner { args: ApplicationArguments? ->
            val scope = CoroutineScope(Job() + Dispatchers.Default)

            //TODO this could be read from config, api or database. For simplicity of this task it is hard coded here.
            val terminals = listOf(
                Terminal("A1"),
                Terminal("A2"),
                Terminal("B3")
            )

            // Start workers for each terminal
            terminals.forEach { terminal ->
                scope.terminalWorker(terminal, terminalService)
            }

            // Cancel all coroutines when the Spring application shuts down
            runBlocking {
                while (isActive) {
                    delay(1000)
                }
                scope.cancel()
            }
        }
    }
}