package com.gomezdevlopment.chessnotationapp.view.game_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gomezdevlopment.chessnotationapp.R

@Composable
fun ChessCanvas(width: Int) {

    val font = FontFamily(
        Font(R.font.alpha_chess_pieces)
    )

    val chessBoardVector: ImageVector = ImageVector.vectorResource(id = R.drawable.ic_chess_board_teal)
    val wr = ImageVector.vectorResource(id = R.drawable.ic_wr_alpha)
    val wn = ImageVector.vectorResource(id = R.drawable.ic_wn_alpha)
    val wb = ImageVector.vectorResource(id = R.drawable.ic_wb_alpha)
    val wq = ImageVector.vectorResource(id = R.drawable.ic_wq_alpha)
    val wk = ImageVector.vectorResource(id = R.drawable.ic_wk)
    val wp = ImageVector.vectorResource(id = R.drawable.ic_wp_alpha)
    val br = ImageVector.vectorResource(id = R.drawable.ic_br_alpha)
    val bn = ImageVector.vectorResource(id = R.drawable.ic_bn_alpha)
    val bb = ImageVector.vectorResource(id = R.drawable.ic_bb_alpha)
    val bq = ImageVector.vectorResource(id = R.drawable.ic_bq_alpha)
    val bk = ImageVector.vectorResource(id = R.drawable.ic_bk_alpha)
    val bp = ImageVector.vectorResource(id = R.drawable.ic_bp_alpha)

    val rank8 = mutableListOf(br, bn, bb, bq, bk, bb, bn, br)
    val rank7 = mutableListOf(bp, bp, bp, bp, bp, bp, bp, bp)
    val rank6 = mutableListOf<ImageVector>()
    val rank5 = mutableListOf<ImageVector>()
    val rank4 = mutableListOf<ImageVector>()
    val rank3 = mutableListOf<ImageVector>()
    val rank2 = mutableListOf(wp, wp, wp, wp, wp, wp, wp, wp)
    val rank1 = mutableListOf(wr, wn, wb, wq, wk, wb, wn, wr)

    Box(
        modifier = Modifier
            .width(width.dp)
            .aspectRatio(1f)
            .background(colorResource(id = R.color.orange))
    ) {
        Image(
            imageVector = chessBoardVector,
            contentDescription = "Chess Board",
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
        )
        val rowWidthAndHeight: Float = (width.toFloat() / 8F)
        val rowPositionY = width.toFloat() - rowWidthAndHeight
        println(width)
        println(rowWidthAndHeight)

        Column() {
            //rank8
            LazyRow(modifier = Modifier
                .fillMaxWidth()
                .height(rowWidthAndHeight.dp)) {

                items(rank8){square ->
                    Image(
                        imageVector = square,
                        contentDescription = "Chess Board",
                        modifier = Modifier
                            .height(rowWidthAndHeight.dp)
                            .aspectRatio(1f)
                            .absoluteOffset(0.dp, 0.dp)
                    )
                }
            }

            //rank7
            LazyRow(modifier = Modifier
                .fillMaxWidth()
                .height(rowWidthAndHeight.dp)) {

                items(rank7){square ->
                    Image(
                        imageVector = square,
                        contentDescription = "Chess Board",
                        modifier = Modifier
                            .height(rowWidthAndHeight.dp)
                            .aspectRatio(1f)
                    )
                }
            }
            //rank6
            LazyRow(modifier = Modifier
                .fillMaxWidth()
                .height(rowWidthAndHeight.dp)) {

                items(rank6){square ->
                    Image(
                        imageVector = square,
                        contentDescription = "Chess Board",
                        modifier = Modifier
                            .height(rowWidthAndHeight.dp)
                            .aspectRatio(1f)
                            .absoluteOffset(0.dp, 0.dp)
                    )
                }
            }

            //rank5
            LazyRow(modifier = Modifier
                .fillMaxWidth()
                .height(rowWidthAndHeight.dp)) {

                items(rank5){square ->
                    Image(
                        imageVector = square,
                        contentDescription = "Chess Board",
                        modifier = Modifier
                            .height(rowWidthAndHeight.dp)
                            .aspectRatio(1f)
                    )
                }
            }
            //rank4
            LazyRow(modifier = Modifier
                .fillMaxWidth()
                .height(rowWidthAndHeight.dp)) {

                items(rank4){square ->
                    Image(
                        imageVector = square,
                        contentDescription = "Chess Board",
                        modifier = Modifier
                            .height(rowWidthAndHeight.dp)
                            .aspectRatio(1f)
                            .absoluteOffset(0.dp, 0.dp)
                    )
                }
            }

            //rank3
            LazyRow(modifier = Modifier
                .fillMaxWidth()
                .height(rowWidthAndHeight.dp)) {

                items(rank3){square ->
                    Image(
                        imageVector = square,
                        contentDescription = "Chess Board",
                        modifier = Modifier
                            .height(rowWidthAndHeight.dp)
                            .aspectRatio(1f)
                    )
                }
            }
            //rank2
            LazyRow(modifier = Modifier
                .fillMaxWidth()
                .height(rowWidthAndHeight.dp)) {

                items(rank2){square ->
                    Image(
                        imageVector = square,
                        contentDescription = "Chess Board",
                        modifier = Modifier
                            .height(rowWidthAndHeight.dp)
                            .aspectRatio(1f)
                            .absoluteOffset(0.dp, 0.dp)
                    )
                }
            }

            //rank1
            LazyRow(modifier = Modifier
                .fillMaxWidth()
                .height(rowWidthAndHeight.dp)) {

                items(rank1){square ->
                    Image(
                        imageVector = square,
                        contentDescription = "Chess Board",
                        modifier = Modifier
                            .height(rowWidthAndHeight.dp)
                            .aspectRatio(1f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ChessCanvasPreview() {
    ChessCanvas(350)
}