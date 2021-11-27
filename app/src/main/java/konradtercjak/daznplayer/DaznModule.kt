package konradtercjak.daznplayer

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import konradtercjak.daznplayer.network.DaznApi
import konradtercjak.daznplayer.ui.home.NetworkInteractor
import konradtercjak.daznplayer.ui.home.NetworkUsecase
import konradtercjak.daznplayer.ui.home.ViewInteractor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import javax.inject.Singleton


@Module
@Suppress("unused")
@InstallIn(SingletonComponent::class)
class DaznModule {

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

    @Provides
    @Singleton
    fun provideNetworkInteractor(api: DaznApi): NetworkInteractor {
        return NetworkInteractor(api)
    }

    @Provides
    @Singleton
    fun provideViewInteractor(): ViewInteractor {
        return ViewInteractor()
    }

    @Provides
    @Singleton
    fun provideNetworkUsecase(networkInteractor: NetworkInteractor, viewInteractor: ViewInteractor): NetworkUsecase {
        return NetworkUsecase(networkInteractor, viewInteractor)
    }
}