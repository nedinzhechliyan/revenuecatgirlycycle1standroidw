package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.DayBurdenPoint
import com.example.ui.theme.BurntOrange
import com.example.ui.theme.Mustard
import com.example.ui.theme.PlumDark
import com.example.ui.theme.PlumLight
import com.example.ui.theme.PlumPrimary

@Composable
fun TrendLineChart(
    dataPoints: List<DayBurdenPoint>,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val outlineColor = MaterialTheme.colorScheme.outline

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("trend_line_chart"),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, outlineColor),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Chart Header & Interactive Tooltip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "30-DAY SYMPTOM BURDEN TREND",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Mustard
                        )
                    )
                    Text(
                        text = "Cumulative daily severity index over time",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                // Legend indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(BurntOrange)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Daily Load",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            // Selected point details badge
            val point = selectedIndex?.let { dataPoints.getOrNull(it) } ?: dataPoints.lastOrNull()
            if (point != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${point.dayLabel}: ${if (point.totalSeverityBurden > 0) "Daily Severity Score: ${point.totalSeverityBurden}" else "No symptoms logged"}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    if (point.primarySymptom != null) {
                        Text(
                            text = "Peak: ${point.primarySymptom.displayName}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Mustard
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas Line & Area Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                if (dataPoints.isEmpty()) {
                    Box(
                        modifier = Modifier.matchParentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Log symptoms across days to populate your trend chart",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                } else {
                    val maxVal = (dataPoints.maxOfOrNull { it.totalSeverityBurden } ?: 1).coerceAtLeast(10).toFloat()

                    Canvas(
                        modifier = Modifier
                            .matchParentSize()
                            .pointerInput(dataPoints) {
                                detectTapGestures { offset ->
                                    val stepX = size.width / (dataPoints.size - 1).coerceAtLeast(1)
                                    val clickedIdx = (offset.x / stepX).toInt().coerceIn(0, dataPoints.size - 1)
                                    selectedIndex = clickedIdx
                                }
                            }
                    ) {
                        val w = size.width
                        val h = size.height
                        val paddingBottom = 16.dp.toPx()
                        val chartHeight = h - paddingBottom
                        val stepX = w / (dataPoints.size - 1).coerceAtLeast(1)

                        // Draw subtle horizontal baseline grid lines
                        val gridLines = 4
                        for (i in 0..gridLines) {
                            val y = chartHeight * (i.toFloat() / gridLines)
                            drawLine(
                                color = Color.White.copy(alpha = 0.07f),
                                start = Offset(0f, y),
                                end = Offset(w, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        // Compute points
                        val points = dataPoints.mapIndexed { index, dp ->
                            val x = index * stepX
                            val normalizedY = (dp.totalSeverityBurden.toFloat() / maxVal).coerceIn(0f, 1f)
                            val y = chartHeight - (normalizedY * chartHeight)
                            Offset(x, y)
                        }

                        // Build filled area path
                        val areaPath = Path().apply {
                            if (points.isNotEmpty()) {
                                moveTo(points.first().x, chartHeight)
                                points.forEach { pt -> lineTo(pt.x, pt.y) }
                                lineTo(points.last().x, chartHeight)
                                close()
                            }
                        }

                        // Draw area gradient
                        drawPath(
                            path = areaPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    BurntOrange.copy(alpha = 0.40f),
                                    PlumPrimary.copy(alpha = 0.20f),
                                    Color.Transparent
                                ),
                                startY = 0f,
                                endY = chartHeight
                            )
                        )

                        // Draw trend line
                        val linePath = Path().apply {
                            points.forEachIndexed { i, pt ->
                                if (i == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y)
                            }
                        }
                        drawPath(
                            path = linePath,
                            color = BurntOrange,
                            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Draw data points circles
                        points.forEachIndexed { idx, pt ->
                            val isSelected = selectedIndex == idx
                            val hasLoad = dataPoints[idx].totalSeverityBurden > 0

                            if (hasLoad || isSelected) {
                                drawCircle(
                                    color = if (isSelected) Mustard else BurntOrange,
                                    radius = if (isSelected) 5.dp.toPx() else 3.dp.toPx(),
                                    center = pt
                                )
                                if (isSelected) {
                                    drawCircle(
                                        color = Color.White,
                                        radius = 2.dp.toPx(),
                                        center = pt
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // X-axis Date Markers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (dataPoints.isNotEmpty()) {
                    Text(
                        text = dataPoints.first().dayLabel.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    )
                    if (dataPoints.size >= 15) {
                        Text(
                            text = dataPoints[dataPoints.size / 2].dayLabel.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        )
                    }
                    Text(
                        text = "TODAY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Mustard,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}
