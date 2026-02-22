package com.robocar.app.ui.joystick

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.*

// Розміри 1в1 з оригіналу:
// joystick-container: 280x280px
// joystick-stick: 90x90px (радіус 45px)
// maxDist: 90px

@Composable
fun JoystickControl(
    modifier: Modifier = Modifier,
    onMove: (x: Int, y: Int) -> Unit,
    onRelease: () -> Unit,
) {
    var stickOffset by remember { mutableStateOf(Offset.Zero) }
    var isBraking by remember { mutableStateOf(false) }

    // 280dp — точно як в оригіналі
    val sizeDp = 280.dp
    // maxDist з оригіналу = 90px (але у нас dp, тому масштабуємо відповідно)
    // В оригіналі: container 280px, maxDist = 90px (це ~64% від radius = 140px)
    // Тому maxDist = radius * 0.64

    Canvas(
        modifier = modifier
            .size(sizeDp)
            .pointerInput(Unit) {
                val maxDist = size.width * 0.64f / 2f  // ~90px еквівалент
                detectDragGestures(
                    onDragStart = {
                        isBraking = true
                    },
                    onDragEnd = {
                        isBraking = false
                        stickOffset = Offset.Zero
                        onRelease()
                    },
                    onDragCancel = {
                        isBraking = false
                        stickOffset = Offset.Zero
                        onRelease()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val newOffset = stickOffset + dragAmount
                        val dist = sqrt(newOffset.x.pow(2) + newOffset.y.pow(2))
                        val angle = atan2(newOffset.y, newOffset.x)
                        val clamped = min(dist, maxDist)
                        stickOffset = Offset(cos(angle) * clamped, sin(angle) * clamped)
                        isBraking = dist < maxDist * 0.1f
                        val vx = ((stickOffset.x / maxDist) * 100).toInt().coerceIn(-100, 100)
                        val vy = ((-stickOffset.y / maxDist) * 100).toInt().coerceIn(-100, 100)
                        onMove(vx, vy)
                    }
                )
            }
    ) {
        val w = size.width
        val center = Offset(w / 2f, w / 2f)
        val baseRadius = w / 2f - 2.dp.toPx()

        // === #joystick-base ===
        // background: radial-gradient(circle at center, #1e293b 0%, #0f172a 70%)
        drawCircle(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0.0f to Color(0xFF1e293b),
                    0.7f to Color(0xFF0f172a),
                    1.0f to Color(0xFF0f172a),
                ),
                center = center,
                radius = baseRadius
            ),
            radius = baseRadius,
            center = center
        )

        // border: 2px solid rgba(59,130,246,0.3)
        drawCircle(
            color = Color(0x4C3B82F6), // rgba(59,130,246,0.3)
            radius = baseRadius,
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )

        // box-shadow inset: inset 0 0 20px rgba(0,0,0,0.8)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x00000000),
                    Color(0xCC000000),
                ),
                center = center,
                radius = baseRadius
            ),
            radius = baseRadius,
            center = center
        )

        // === #joystick-stick ===
        // width: 90px; height: 90px; => radius = 45dp
        val stickR = 45.dp.toPx()
        val stickCenter = center + stickOffset

        // background: radial-gradient(circle at 30% 30%, #3b82f6, #1d4ed8)
        // В оригіналі стік червоний коли braking, синій в нормі
        val stickColors = if (isBraking)
            listOf(Color(0xFFEF4444), Color(0xFFB91C1C)) // braking червоний
        else
            listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)) // нормальний синій

        drawCircle(
            brush = Brush.radialGradient(
                colors = stickColors,
                center = stickCenter - Offset(stickR * 0.4f, stickR * 0.4f), // 30% 30%
                radius = stickR
            ),
            radius = stickR,
            center = stickCenter
        )

        // box-shadow: 0 8px 20px rgba(0,0,0,0.7)
        // Імітуємо тінню знизу
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x00000000),
                    Color(0x55000000),
                ),
                center = stickCenter + Offset(0f, stickR * 0.4f),
                radius = stickR * 0.8f
            ),
            radius = stickR * 0.8f,
            center = stickCenter + Offset(0f, stickR * 0.4f)
        )

        // ::after — бліка: top:20px left:20px width:25px height:25px rgba(255,255,255,0.2)
        val afterRadius = 12.5.dp.toPx()
        val afterOffset = stickCenter + Offset(
            -stickR + 20.dp.toPx() + afterRadius,
            -stickR + 20.dp.toPx() + afterRadius
        )
        drawCircle(
            color = Color(0x33FFFFFF), // rgba(255,255,255,0.2)
            radius = afterRadius,
            center = afterOffset
        )
    }
}
