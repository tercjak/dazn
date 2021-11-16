package konradtercjak.daznplayer.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import javax.inject.Singleton


@Module
@Suppress("unused")
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val httpClient = OkHttpClient.Builder()

        val moshiBuilder = Moshi.Builder().add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())

        return Retrofit.Builder()
            .client(httpClient.build())
            .baseUrl("https://us-central1-dazn-sandbox.cloudfunctions.net/")
            .addConverterFactory(MoshiConverterFactory.create(moshiBuilder.build()).asLenient())
            .build()
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): DaznApi {
        return retrofit.create(DaznApi::class.java)
    }


}