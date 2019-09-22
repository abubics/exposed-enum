import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing

fun main(args: Array<String>): Unit = io.ktor.server.jetty.EngineMain.main(args)

fun Application.module() {
  routing {
    get("/health") {
      call.respond("""hello, testing exposed-enum""")
    }
  }
}
