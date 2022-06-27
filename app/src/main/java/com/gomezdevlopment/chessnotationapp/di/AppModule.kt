package com.gomezdevlopment.chessnotationapp.di

import android.content.Context
import com.gomezdevlopment.chessnotationapp.puzzle_database.PuzzleDAO
import com.gomezdevlopment.chessnotationapp.puzzle_database.PuzzleDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun getPuzzleDb(context: Context): PuzzleDatabase{
        return PuzzleDatabase.getPuzzleDatabase(context)
    }

    @Singleton
    @Provides
    fun getPuzzleDao(puzzleDb: PuzzleDatabase): PuzzleDAO{
        return puzzleDb.getDAO()
    }
}