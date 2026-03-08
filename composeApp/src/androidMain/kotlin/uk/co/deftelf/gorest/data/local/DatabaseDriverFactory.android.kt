package uk.co.deftelf.gorest.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DatabaseDriverFactory actual constructor() {
    actual fun createDriver(): SqlDriver = AndroidSqliteDriver(
        GoRestDatabase.Schema,
        appContext,
        "gorest.db"
    )

    companion object {
        lateinit var appContext: Context
    }
}
