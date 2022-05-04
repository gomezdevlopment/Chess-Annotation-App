package com.gomezdevlopment.chessnotationapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun createButtonHandlers(dialog: Dialog, textView: TextView) {
        val king: Button = dialog.findViewById(R.id.king)
        val queen: Button = dialog.findViewById(R.id.queen)
        val rook: Button = dialog.findViewById(R.id.rook)
        val bishop: Button = dialog.findViewById(R.id.bishop)
        val knight: Button = dialog.findViewById(R.id.knight)
        val a: Button = dialog.findViewById(R.id.a)
        val b: Button = dialog.findViewById(R.id.b)
        val c: Button = dialog.findViewById(R.id.c)
        val d: Button = dialog.findViewById(R.id.d)
        val e: Button = dialog.findViewById(R.id.e)
        val f: Button = dialog.findViewById(R.id.f)
        val g: Button = dialog.findViewById(R.id.g)
        val h: Button = dialog.findViewById(R.id.h)
        val one: Button = dialog.findViewById(R.id.one)
        val two: Button = dialog.findViewById(R.id.two)
        val three: Button = dialog.findViewById(R.id.three)
        val four: Button = dialog.findViewById(R.id.four)
        val five: Button = dialog.findViewById(R.id.five)
        val six: Button = dialog.findViewById(R.id.six)
        val seven: Button = dialog.findViewById(R.id.seven)
        val eight: Button = dialog.findViewById(R.id.eight)
        val capture: Button = dialog.findViewById(R.id.capture)
        val check: Button = dialog.findViewById(R.id.check)
        val checkmate: Button = dialog.findViewById(R.id.checkmate)
        val kingSideCastle: Button = dialog.findViewById(R.id.kingSideCastle)
        val queenSideCastle: Button = dialog.findViewById(R.id.queenSideCastle)

        val buttons: ArrayList<Button> = arrayListOf(
            king, queen, rook, bishop, knight,
            a, b, c, d, e, f, g, h,
            one, two, three, four, five, six, seven, eight,
            capture, check, checkmate, kingSideCastle, queenSideCastle
        )

        for(button in buttons){
            button.setOnClickListener {
                textView.append(button.text)
            }
        }
    }
}