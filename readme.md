# Exposed Enum

Sample code to demonstrate [an issue with Exposed's DAO DSL](
https://github.com/JetBrains/Exposed/issues/609).

I've set this up to have the bare minimum dependencies to demonstrate
the issue, while also replicating my original environment and tooling.

## Getting Started

Make sure the postgres database is up and running:

```bash
docker-compose up db
```

Apply migrations to set up the enum type, and a table that uses it:

```bash
./gradlew update
```

Start the app to run the database integrated test:

```bash
./gradlew run
```

## Detail

`Application.kt` shows some test interactions. (The fact that it's
running in a `ktor` app is expected to be irrelevant.)

Interestingly, only updating via the DAO DSL throws this exception,
and the regular table DSL doesn't.

One can also prove to themselves that it's not a specific enum value
causing the issue; changing the status back to the original value
(thusly: `it.editStatusViaDao(demo.id, Status.Draft)`) also fails.

The stack trace begins:

```
Caused by: java.lang.ClassCastException: DemoStatus$PgEnum cannot be cast to java.lang.Enum
           at org.jetbrains.exposed.sql.Table$customEnumeration$1.notNullValueToDB(Table.kt:235)
           at org.jetbrains.exposed.sql.IColumnType$DefaultImpls.nonNullValueToString(ColumnType.kt:51)
           at org.jetbrains.exposed.sql.ColumnType.nonNullValueToString(ColumnType.kt:60)
           at org.jetbrains.exposed.sql.IColumnType$DefaultImpls.valueToString(ColumnType.kt:43)
           at org.jetbrains.exposed.sql.ColumnType.valueToString(ColumnType.kt:60)
           at org.jetbrains.exposed.sql.QueryBuilder$registerArguments$1.invoke(Expression.kt:19)
           at org.jetbrains.exposed.sql.QueryBuilder.registerArguments(Expression.kt:21)
           ...
```
