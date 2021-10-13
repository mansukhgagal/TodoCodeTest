package com.codetest.todo.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.codetest.todo.BuildConfig
import com.codetest.todo.db.TodoDAO
import com.codetest.todo.db.TodoDatabase
import com.codetest.todo.network.NetworkInterceptor
import com.codetest.todo.network.WebServices
import com.codetest.todo.ui.create.TodoRepository
import com.codetest.todo.ui.login.LoginRepository
import com.codetest.todo.utils.Constants.TODO_DATABASE_NAME
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private var BASE_URL = "https://reqres.in/api/"
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L

    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache {
        val cacheSize = (5 * 1024 * 1024).toLong()
        return Cache(context.cacheDir, cacheSize)
    }

    @Provides
    @Singleton
    fun provideApi(cache: Cache): WebServices {
        val client = OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .addNetworkInterceptor(NetworkInterceptor())

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            client.addNetworkInterceptor(logging)
        }

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(client.build())
            .build().create(WebServices::class.java)
    }

    @Provides
    @Singleton
    fun provideLoginRepository(api: WebServices): LoginRepository = LoginRepository(api)

    @Provides
    @Singleton
    fun provideTodoRepository(dao: TodoDAO): TodoRepository = TodoRepository(dao)


    @Singleton
    @Provides
    fun provideTodoDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        TodoDatabase::class.java,
        TODO_DATABASE_NAME
    )
        .addMigrations(TodoDatabase.MIGRATION_1_2)
        .build()

    @Singleton
    @Provides
    fun provideTodoDao(db: TodoDatabase) = db.getTodoDao()
}