import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import java.util.Date

fun main(args: Array<String>): Unit = io.ktor.server.jetty.EngineMain.main(args)

val Application.env: (path: String) -> String
  get() = { environment.config.property("ktor.$it").getString() }

fun Application.module() {
  testDb(env)

  routing {
    get("/health") {
      call.respond("""hello, testing exposed-enum""")
    }
  }
}

fun testDb(env: (path: String) -> String) {
  PostgresClient(
    host = env("db.host"),
    databaseName = env("db.name"),
    username = env("db.username"),
    password = env("db.password")
  ).also {
    val demo = it.addEntity("demo - ${Date()}", Status.Draft)
    it.editStatus(demo.id, Status.Live)
    it.editStatusViaDao(demo.id, Status.Completed)
  }
}
