import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Table
import org.postgresql.util.PGobject

object DemoTable : IntIdTable() {

  val name = varchar("name", 50)
  val status = DemoStatus.pgColumn(this, "status")
}

class ExposedDemo(id: EntityID<Int>) : IntEntity(id) {

  companion object : IntEntityClass<ExposedDemo>(DemoTable)

  var name by DemoTable.name
  var status by DemoTable.status
}

enum class DemoStatus {
  draft,
  live,
  completed,
  archive;

  companion object {
    const val dbName = "demo_status"

    fun pgColumn(table: Table, name: String) = table.customEnumeration(
      name = name,
      sql = dbName,
      fromDb = { it.fromPg() },
      toDb = { it.toPg() }
    )

    private fun Any.fromPg() = valueOf(this as String)
    private fun DemoStatus?.toPg() = PgEnum(this)
  }

  class PgEnum(enumValue: DemoStatus?) : PGobject() {
    init {
      value = enumValue?.name
      type = dbName
    }
  }
}
