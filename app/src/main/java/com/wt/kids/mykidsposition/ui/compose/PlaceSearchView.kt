package com.wt.kids.mykidsposition.ui.compose

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wt.kids.mykidsposition.ui.theme.MyKidsPositionTheme

@Composable
fun PlaceSearchView() {
    Row(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center
    ) {
        PlaceSearchEditText()
        PlaceSearchButton()
    }
}

@Composable
fun PlaceSearchEditText() {
    var text by rememberSaveable { mutableStateOf("") }
    TextField(
        value = text,
        onValueChange = {
            text = it
        },
        label = { Text(text = "등록할 위치 검색") },
        modifier = Modifier.wrapContentWidth(Alignment.Start).padding(10.dp)
    )
}

@Composable
fun PlaceSearchButton() {
    val context = LocalContext.current
    Button(
        onClick = { Toast.makeText(context, "검색 추가", Toast.LENGTH_SHORT).show() },
        modifier = Modifier.wrapContentWidth(Alignment.End)
            .padding(10.dp)
    ) {
        Text(text = "검색")
    }
}

@Preview(showBackground = true)
@Composable
fun PlaceSearchPreview() {
    MyKidsPositionTheme {
        PlaceSearchView()
    }
}