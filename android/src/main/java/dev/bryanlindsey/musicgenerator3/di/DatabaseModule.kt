package dev.bryanlindsey.musicgenerator3.di

import android.content.Context
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.bryanlindsey.musicgenerator3.Database
import devbryanlindseymusicgenerator3data.Composition
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSqlDriver(@ApplicationContext context: Context): SqlDriver {
        return AndroidSqliteDriver(Database.Schema, context, "test.db")
    }

    @Provides
    @Singleton
    fun provideDatabase(driver: SqlDriver): Database {
        return Database(
            driver = driver,
            compositionAdapter = Composition.Adapter(
                EnumColumnAdapter(),
                EnumColumnAdapter(),
                EnumColumnAdapter(),
                EnumColumnAdapter(),
                EnumColumnAdapter(),
            )
        )
    }
}
