package vn.edu.uit.devorbit.mobile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlinx.coroutines.delay
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme

@Composable
fun WheelTimePicker(
    initialHour: Int,
    initialMinute: Int,
    is24Hour: Boolean = true,
    onHourChanged: (Int) -> Unit,
    onMinuteChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val hours = remember { if (is24Hour) (0..23).toList() else (1..12).toList() }
    val minutes = remember { (0..59).toList() }

    val repeatedHours = remember(hours) { buildList { repeat(3) { addAll(hours) } } }
    val repeatedMinutes = remember(minutes) { buildList { repeat(3) { addAll(minutes) } } }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        WheelPickerColumn(
            items = repeatedHours,
            initialIndex = hours.indexOf(initialHour).let { if (it < 0) 0 else it + hours.size },
            itemHeight = 44.dp,
            onItemSelected = onHourChanged,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = ":",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        WheelPickerColumn(
            items = repeatedMinutes,
            initialIndex = minutes.indexOf(initialMinute).let { if (it < 0) 0 else it + minutes.size },
            itemHeight = 44.dp,
            onItemSelected = onMinuteChanged,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun WheelPickerColumn(
    items: List<Int>,
    initialIndex: Int,
    itemHeight: Dp,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val visibleItems = 5
    val density = LocalDensity.current
    val itemHeightPx = with(density) { itemHeight.toPx() }
    val nebulaColor = CosmicTheme.colors.nebula

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    var isSnapping by remember { mutableStateOf(false) }

    LaunchedEffect(listState) {
        snapshotFlow {
            listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
        }.collect { (index, offset) ->
            if (isSnapping) return@collect
            delay(100)
            if (!listState.isScrollInProgress) {
                val nearestIndex = if (offset > itemHeightPx.toInt() / 2) index + 1 else index
                val clampedIndex = nearestIndex.coerceIn(0, items.size - 1)
                isSnapping = true
                listState.animateScrollToItem(clampedIndex)
                isSnapping = false
                onItemSelected(items[clampedIndex])
            }
        }
    }

    val centerItem by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val viewportCenter = layoutInfo.viewportEndOffset / 2
            layoutInfo.visibleItemsInfo.minByOrNull {
                abs(it.offset + it.size / 2 - viewportCenter)
            }?.index ?: initialIndex
        }
    }

    Box(modifier = modifier.height(itemHeight * visibleItems)) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight * visibleItems)
                .drawWithContent {
                    drawContent()

                    val gradientHeight = size.height * 0.3f
                    val centerY = size.height / 2

                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(nebulaColor, Color.Transparent),
                            startY = 0f,
                            endY = gradientHeight
                        )
                    )

                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, nebulaColor),
                            startY = size.height - gradientHeight,
                            endY = size.height
                        )
                    )

                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.06f),
                        topLeft = Offset(0f, centerY - itemHeightPx / 2),
                        size = Size(size.width, itemHeightPx),
                        cornerRadius = CornerRadius(12.dp.toPx())
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(items) { index, item ->
                val distance = abs(index - centerItem)
                val alpha = maxOf(0.25f, 1f - distance * 0.2f)
                val isCenter = distance == 0

                Text(
                    text = item.toString().padStart(2, '0'),
                    fontSize = if (isCenter) 22.sp else (18 - distance * 2).sp,
                    color = Color.White.copy(alpha = alpha),
                    fontWeight = if (isCenter) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .wrapContentHeight(Alignment.CenterVertically),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
