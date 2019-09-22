import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class PostgresClient(
  host: String,
  databaseName: String,
  username: String,
  password: String
) {

  init {
    Database.connect(
      url = "jdbc:postgresql://$host/$databaseName",
      driver = "org.postgresql.Driver",
      user = username,
      password = password
    )

    transaction {
      addLogger(StdOutSqlLogger)
    }
  }

  fun addEntity(origName: String, origStatus: Status): DemoEntity = wrapDb("addEntity") {
    ExposedDemo.new {
      name = origName
      status = origStatus.fromDomain()
    }.toDomain()
  }

  fun editStatus(id: Int, newStatus: Status): DemoEntity = wrapDb("editStatus") {
    DemoTable.update({ DemoTable.id eq id }) {
      it[status] = newStatus.fromDomain()
    }
    ExposedDemo
      .findById(id)!!
      .toDomain()
  }

  fun editStatusViaDao(id: Int, newStatus: Status): DemoEntity = wrapDb("editStatusViaDao") {
    ExposedDemo
      .findById(id)!!
      .apply { status = newStatus.fromDomain() }
      .toDomain()
  }

  companion object {

    fun <T> wrapDb(opName: String = "operation", op: () -> T): T = try {
      transaction { op() }
    } catch (ex: Exception) {
      println("\n$opName Error: ${ex.localizedMessage}\n")
      throw ex
    }
  }
}

enum class Status { Draft, Live, Completed, Archive }
data class DemoEntity(val id: Int, val name: String, val status: Status)

fun ExposedDemo.toDomain() = DemoEntity(
  id = id.value,
  name = name,
  status = status.toDomain()
)

fun DemoStatus.toDomain() = when (this) {
  DemoStatus.draft -> Status.Draft
  DemoStatus.live -> Status.Live
  DemoStatus.completed -> Status.Completed
  DemoStatus.archive -> Status.Archive
}

fun Status.fromDomain() = when (this) {
  Status.Draft -> DemoStatus.draft
  Status.Live -> DemoStatus.live
  Status.Completed -> DemoStatus.completed
  Status.Archive -> DemoStatus.archive
}
