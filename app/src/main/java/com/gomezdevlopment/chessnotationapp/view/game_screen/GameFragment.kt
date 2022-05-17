package com.gomezdevlopment.chessnotationapp.view.game_screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.databinding.FragmentGameBinding


class GameFragment: Fragment(){

    private lateinit var binding: FragmentGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        drawChessPiece(view)

//        binding.chessBoard2.setOnClickListener {
//            println("working")
//            val boardBitmap = createBitmap(it.width, it.height)
//            println(it.width/2f)
//            val paint = Paint()
//            paint.color = ContextCompat.getColor(view.context, R.color.purple_200)
//            //paint.textSize = (30f)
//            paint.style = Paint.Style.FILL
//            val canvas = binding.chessBoard2.holder.lockCanvas()
//            //paint.typeface = ResourcesCompat.getFont(view.context, R.font.alpha_chess_pieces)
//            canvas.drawColor(ContextCompat.getColor(view.context, R.color.purple_200))
//            canvas.drawCircle(it.width/2f, it.width/2f, it.width/5f, paint)
//            //canvas.drawText("Hello I am trying to paint", 0.toFloat(), 0.toFloat(), paint)
//            binding.chessBoard2.holder.unlockCanvasAndPost(canvas)
//            view.invalidate()
//        }
    }

    private fun getBitmapFromVector(context: Context, vectorDrawableRes: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_chessboard)
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(
            500,
            500,
            Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(bitmap)
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable?.draw(canvas)
        return bitmap
    }

    fun drawChessPiece(view: View){
        val canvas = Canvas()
        val paint = Paint()
        getBitmapFromVector(view.context, R.drawable.ic_chessboard)?.let { canvas.drawBitmap(it, 0f, 0f, paint) }


//        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
//        val boardWidthAndHeight = screenWidth - pxFromDp(view.context, 40f)
//
//        //val boardBitmap = createBitmap(dpFromPx(view.context, boardWidthAndHeight).toInt(), dpFromPx(view.context, boardWidthAndHeight).toInt())
//        val boardBitmap = createBitmap(300, 300)
//        val canvas = Canvas(boardBitmap)
//        val paint = Paint()
//        paint.style = Paint.Style.FILL_AND_STROKE
//        paint.color = Color.RED
//        canvas.drawPaint(paint)
//        //paint.textSize = (30f)
//
//        //paint.typeface = ResourcesCompat.getFont(view.context, R.font.alpha_chess_pieces)
//        //canvas.drawText("Hello I am trying to paint", 0.toFloat(), 0.toFloat(), paint)
    }

    fun pxFromDp(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    fun dpFromPx(context: Context, px: Float): Float {
        return px / context.resources.displayMetrics.density
    }
}