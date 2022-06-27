package com.gomezdevlopment.chessnotationapp.puzzle_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Puzzle::class], version = 1)
abstract class PuzzleDatabase : RoomDatabase() {

    abstract fun getDAO(): PuzzleDAO

    companion object {
        private var puzzleDbINSTANCE: PuzzleDatabase? = null

        fun getPuzzleDatabase(context: Context): PuzzleDatabase {
            if (puzzleDbINSTANCE == null) {
                puzzleDbINSTANCE = Room.databaseBuilder(
                    context.applicationContext, PuzzleDatabase::class.java, "puzzle_database")
                    .createFromAsset("database/puzzles.db")
                    .build()

            }
            return puzzleDbINSTANCE!!
        }
    }
}