package lt.jb.aero

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AeroApplication

fun main(args: Array<String>) {
    runApplication<AeroApplication>(*args)
}
