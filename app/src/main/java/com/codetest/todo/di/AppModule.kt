package com.codetest.todo.di

import android.content.Context
import androidx.room.Room
import com.codetest.todo.BuildConfig
import com.codetest.todo.db.TodoDAO
import com.codetest.todo.db.TodoDatabase
import com.codetest.todo.network.NetworkInterceptor
import com.codetest.todo.network.WebServices
import com.codetest.todo.ui.create.TodoRepository
import com.codetest.todo.ui.login.LoginRepository
import com.codetest.todo.utils.Constants.TODO_DATABASE_NAME
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

    @Provides
    @Singleton
    fun provideApi(cache: Cache): WebServices {
        val baseUrl = "https://reqres.in/api/"
        val connectionTimeout = 30L
        val readTimeout = 30L
        val writeTimeout = 30L

        val client = OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(connectionTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeout, TimeUnit.SECONDS)
            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
            .addNetworkInterceptor(NetworkInterceptor())

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            client.addNetworkInterceptor(logging)
        }

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .client(client.build())
            .build().create(WebServices::class.java)
    }

    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache {
        val cacheSize = (5 * 1024 * 1024).toLong()
        return Cache(context.cacheDir, cacheSize)
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
//        .createFromAsset("database/todo_db.db")//only for debug
        .addMigrations(TodoDatabase.MIGRATION_1_2) //small migration sample
        .build()

    @Singleton
    @Provides
    fun provideTodoDao(db: TodoDatabase) = db.getTodoDao()

}