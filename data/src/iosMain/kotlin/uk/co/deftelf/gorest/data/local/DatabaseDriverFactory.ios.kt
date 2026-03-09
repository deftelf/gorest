package uk.co.deftelf.gorest.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DatabaseDriverFactory actual constructor() {
    actual fun createDriver(): SqlDriver = NativeSqliteDriver(GoRestDatabase.Schema, "gorest.db")
}
