package com.gomezdevlopment.chessnotationapp.di

import android.content.Context
import androidx.room.Room
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
    fun providePuzzleDb(context: Context) = Room.databaseBuilder(
        context,
        PuzzleDatabase::class.java,
        "puzzle_database"
    ).createFromAsset("database/puzzles.db").build()

    @Singleton
    @Provides
    fun providePuzzleDao(puzzleDb: PuzzleDatabase) = puzzleDb.puzzleDAO()
}