package com.swackles.jellyfin.data.room

import android.content.Context
import com.swackles.jellyfin.data.room.server.ServerRepository
import com.swackles.jellyfin.data.room.server.ServerRepositoryImpl
import com.swackles.jellyfin.data.room.user.UserRepository
import com.swackles.jellyfin.data.room.user.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AppModule {
    @Provides
    @Singleton
    fun provideServerRepository(@ApplicationContext context: Context): ServerRepository {
        return ServerRepositoryImpl(
            ServerRoomDatabase.getInstance(context).serverDao()
        )
    }

    @Provides
    @Singleton
    fun provideUserRepository(@ApplicationContext context: Context): UserRepository {
        return UserRepositoryImpl(
            ServerRoomDatabase.getInstance(context).userDao()
        )
    }
}
