package com.mhirrr.videophotoeditor.utils.di

import android.content.Context
import androidx.room.Room
import com.mhirrr.videophotoeditor.data.local.database.EditedPhotosDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext applicationContext: Context): EditedPhotosDatabase {
        return Room.databaseBuilder(applicationContext, EditedPhotosDatabase::class.java, "rtestt2232").build()
    }

}