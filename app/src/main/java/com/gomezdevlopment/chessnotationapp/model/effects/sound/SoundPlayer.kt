package com.gomezdevlopment.chessnotationapp.model.effects.sound

import android.content.Context
import android.media.MediaPlayer

class SoundPlayer {
    fun playSound(context: Context, audioResource: Int){
        val player = MediaPlayer.create(context, audioResource)
        if(!player.isPlaying){
            player.start()
        }else{
            player.pause()
            player.stop()
            player.reset()
        }

        player.setOnCompletionListener {
            player.reset()
            player.release()
        }
    }
}