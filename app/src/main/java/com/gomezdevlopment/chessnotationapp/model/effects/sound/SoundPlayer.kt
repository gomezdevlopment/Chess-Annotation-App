package com.gomezdevlopment.chessnotationapp.model.effects.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import com.gomezdevlopment.chessnotationapp.R

class SoundPlayer(context: Context) {
    private val audioAttributes: AudioAttributes =
        AudioAttributes.Builder()
            .setLegacyStreamType(AudioManager.STREAM_MUSIC)
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()

    private val soundPool: SoundPool =
        SoundPool.Builder().setMaxStreams(1).setAudioAttributes(audioAttributes).build()
    private val pieceSound = soundPool.load(context, R.raw.piece_sound, 1)
    private val checkSound = soundPool.load(context, R.raw.check_sound, 1)
    private val captureSound = soundPool.load(context, R.raw.capture_sound, 1)
    private val castlingSound = soundPool.load(context, R.raw.castling_sound, 1)
    private val endSound = soundPool.load(context, R.raw.end_of_game_sound, 1)

    fun playSoundPool(soundName: String) {
        var sound = pieceSound
        when (soundName) {
            "check" -> sound = checkSound
            "capture" -> sound = captureSound
            "castle" -> sound = castlingSound
            "end" -> sound = endSound
        }
        soundPool.autoPause()
        soundPool.play(sound, 1f, 1f, 1, 0, 1f)
    }
}