package com.mhirrr.videophotoeditor.utils.di

import com.mhirrr.videophotoeditor.data.local.database.EditedPhotosDatabase
import com.mhirrr.videophotoeditor.data.repository.IEditedPhotosRepository
import com.mhirrr.videophotoeditor.domain.EditedPhotosRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideEditedPhotosRepository(editedPhotosDatabase: EditedPhotosDatabase): EditedPhotosRepository {
        return IEditedPhotosRepository(editedPhotosDatabase.editedPhotosDao())
    }

}