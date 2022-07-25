package com.gomezdevlopment.chessnotationapp.di

import android.app.Application
import androidx.room.Room
import com.gomezdevlopment.chessnotationapp.App
import com.gomezdevlopment.chessnotationapp.model.firestore_interaction.FirestoreInteraction
import com.gomezdevlopment.chessnotationapp.model.repositories.AuthenticationRepository
import com.gomezdevlopment.chessnotationapp.model.repositories.MatchmakingRepository
import com.gomezdevlopment.chessnotationapp.model.repositories.PuzzleRepository
import com.gomezdevlopment.chessnotationapp.model.repositories.UserRepository
import com.gomezdevlopment.chessnotationapp.model.utils.UserSettings
import com.gomezdevlopment.chessnotationapp.puzzle_database.PuzzleDAO
import com.gomezdevlopment.chessnotationapp.puzzle_database.PuzzleDatabase
import com.gomezdevlopment.chessnotationapp.puzzle_database.RoomRepository
import com.gomezdevlopment.chessnotationapp.realtime_database.RealtimeDatabaseDAO
import com.gomezdevlopment.chessnotationapp.realtime_database.RealtimeDatabaseGameInteraction
import com.gomezdevlopment.chessnotationapp.realtime_database.RealtimeDatabaseRepository
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
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
    fun providePuzzleDb(context: Application) = Room.databaseBuilder(
        context,
        PuzzleDatabase::class.java,
        "puzzles"
    ).createFromAsset("database/puzzles.db").build()

    @Singleton
    @Provides
    fun providePuzzleDao(puzzleDb: PuzzleDatabase) = puzzleDb.puzzleDAO()

    @Singleton
    @Provides
    fun provideRoomRepository(puzzleDAO: PuzzleDAO) = RoomRepository(puzzleDAO)

    //@Singleton
    @Provides
    fun providePuzzleRepository(firestore: FirestoreInteraction) = PuzzleRepository(firestore)

    //@Singleton
    @Provides
    fun provideMatchmakingRepository(
        db: FirebaseDatabase,
        dbRepository: RealtimeDatabaseRepository
    ) = MatchmakingRepository(db, dbRepository)

    //@Singleton
    @Provides
    fun provideAuthenticationRepository(
        realtimeDBRepository: RealtimeDatabaseRepository,
        context: Application
    ) = AuthenticationRepository(realtimeDBRepository, context)


    @Provides
    fun provideUserRepository(realtimeDBRepository: RealtimeDatabaseRepository) = UserRepository(realtimeDBRepository)

    @Singleton
    @Provides
    fun provideFirestoreDatabase() = FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun provideRealtimeDatabase() = FirebaseDatabase.getInstance()

    @Singleton
    @Provides
    fun providesRealtimeDatabaseDAO(realtimeDB: FirebaseDatabase, app: Application) = RealtimeDatabaseDAO(realtimeDB, app)

    @Singleton
    @Provides
    fun providesRealtimeDatabaseRepository(realtimeDatabaseDAO: RealtimeDatabaseDAO) =
        RealtimeDatabaseRepository(realtimeDatabaseDAO)

    @Singleton
    @Provides
    fun providesRealtimeDatabaseInteractionClass(realtimeDB: FirebaseDatabase) =
        RealtimeDatabaseGameInteraction(realtimeDB)

    @Singleton
    @Provides
    fun provideFirestoreInteractionClass(db: FirebaseFirestore) = FirestoreInteraction(db)

    @Singleton
    @Provides
    fun provideSettingsClass(app: Application) = UserSettings(app)
}