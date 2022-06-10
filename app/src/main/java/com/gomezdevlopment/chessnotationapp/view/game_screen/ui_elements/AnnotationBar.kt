package com.gomezdevlopment.chessnotationapp.view.game_screen.ui_elements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gomezdevlopment.chessnotationapp.R
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.orange
import com.gomezdevlopment.chessnotationapp.view.game_screen.utils.teal
import com.gomezdevlopment.chessnotationapp.view_model.GameViewModel

@Composable
fun AnnotationBar(viewModel: GameViewModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(colorResource(id = R.color.teal_darker)),
        contentAlignment = Alignment.CenterStart
    ) {

        val scrollState = rememberLazyListState()
        //viewModel.onUpdate.value
        val annotationsSelection = remember {
            mutableStateOf(0)
        }
        val annotations = remember {
            viewModel.getAnnotations()
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 10.dp),
            state = scrollState
        ) {
            itemsIndexed(annotations) { index, annotation ->
                if (index % 2 != 0 && index != 0) {
                    Text(
                        text = "${((index / 2) + 1)}.",
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(4.dp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                val isSelected = index == viewModel.selectedNotationIndex.value
                val isCheck = viewModel.kingInCheck()
                Annotation(annotation = annotation, isSelected = isSelected, isCheck, viewModel, index)
            }
        }
        annotationsSelection.value = viewModel.selectedNotationIndex.value
        if (annotationsSelection.value >= 0) {
            LaunchedEffect(viewModel.selectedNotationIndex.value) {
                scrollState.scrollToItem(viewModel.selectedNotationIndex.value)
            }
        }
    }
}

@Composable
fun Annotation(annotation: String, isSelected: Boolean, isCheck: Boolean, viewModel: GameViewModel, index: Int) {
    Text(
        text = annotation,
        textAlign = TextAlign.Center,
        fontSize = 18.sp,
        modifier = (if (isSelected) Modifier.highlight(isCheck) else Modifier)
            .padding(4.dp)
            .clickable {
                viewModel.selectedNotationIndex.value = index
                viewModel.setGameState(index)
            }
        ,
        color = Color.White
    )
}

private fun Modifier.highlight(isCheck: Boolean) =
    background(
        color = if (isCheck) orange else teal,
        shape = RoundedCornerShape(6.dp)
    )