package com.robocar.app.ui.joystick

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robocar.app.MainViewModel
import com.robocar.app.ui.settings.TuningDialog

// Кольори з оригінального HTML
private val BgPage       = Color(0xFF0f172a)  // body background-color
private val BgPanel      = Color(0xFF1e293b)  // bg-slate-800/40 approx
private val BgCard       = Color(0xFF0f172a)  // bg-slate-900
private val BorderColor  = Color(0xFF334155)  // border-slate-700
private val TextMuted    = Color(0xFF94a3b8)  // text-slate-400
private val TextGreen    = Color(0xFF22c55e)  // text-green-500
private val TextBlue     = Color(0xFF3b82f6)  // text-blue-400
private val TextYellow   = Color(0xFFEAB308)  // text-yellow-500
private val SliderTrack  = Color(0xFF334155)  // slider track

@Composable
fun JoystickScreen(viewModel: MainViewModel) {
    val motorL by viewModel.motorL.collectAsState()
    val motorR by viewModel.motorR.collectAsState()
    val gyroEnabled by viewModel.gyroEnabled.collectAsState()
    var speedPercent by remember { mutableStateOf(100f) }
    var showTuning by remember { mutableStateOf(false) }

    // === Оригінальний layout:
    // flex flex-col items-center justify-center
    // джойстик зверху (280x280), знизу панель max-w-xs
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPage),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // #joystick-container: width:280px, height:280px, margin: 0 auto 5vh auto
        Box(
            modifier = Modifier
                .padding(bottom = 24.dp), // ~5vh
            contentAlignment = Alignment.Center
        ) {
            JoystickControl(
                onMove = { vx, vy ->
                    if (!gyroEnabled) viewModel.updateJoystick(vx, vy)
                },
                onRelease = {
                    if (!gyroEnabled) viewModel.resetJoystick()
                }
            )
        }

        // === Нижня панель ===
        // w-full max-w-xs flex flex-col gap-3 bg-slate-800/40 p-4 rounded-2xl border border-slate-700/30
        Column(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x661e293b)) // bg-slate-800/40
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // === L / Gyro / R рядок ===
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // L мотор — bg-slate-900 px-3 py-1.5 rounded-lg border border-slate-700
                MotorDisplay(label = "L", value = motorL, modifier = Modifier.weight(1f))

                Spacer(Modifier.width(16.dp))

                // gyroBtn — w-10 h-10 rounded-full bg-slate-700
                val gyroColor by animateColorAsState(
                    targetValue = if (gyroEnabled) Color(0xFF3B82F6) else Color(0xFF334155),
                    animationSpec = tween(300), label = "gyro"
                )
                IconButton(
                    onClick = { viewModel.toggleGyro() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(gyroColor)
                ) {
                    Icon(
                        Icons.Default.Smartphone,
                        contentDescription = "Gyro",
                        tint = if (gyroEnabled) Color.White else Color(0xFF94a3b8),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(Modifier.width(16.dp))

                // R мотор
                MotorDisplay(label = "R", value = motorR, modifier = Modifier.weight(1f))
            }

            // === Divider + Потужність слайдер ===
            // border-t border-white/5
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0x0DFFFFFF))
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Потужність",
                        fontSize = 10.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        "${speedPercent.toInt()}%",
                        fontSize = 10.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(4.dp))
                Slider(
                    value = speedPercent,
                    onValueChange = {
                        speedPercent = it
                        viewModel.setSpeed(it.toInt())
                    },
                    valueRange = 10f..100f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = TextBlue,
                        activeTrackColor = TextBlue,
                        inactiveTrackColor = SliderTrack
                    )
                )
            }

            // === Кнопка "Налаштування моторів" ===
            // w-full py-2 mt-1 rounded-lg bg-slate-700 text-xs font-bold text-slate-300 border border-slate-600
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF334155)) // bg-slate-700
                    .clickable { showTuning = true }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Tune,
                    contentDescription = null,
                    tint = Color(0xFFCBD5E1), // text-slate-300
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "Налаштування моторів",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFCBD5E1)
                )
            }

            // === HEX рядок ===
            // bg-black/40 px-3 py-1 rounded text-[10px] font-mono tracking-widest text-slate-400 border border-slate-800/50
            val lHex = (motorL.toByte().toInt() and 0xFF).toString(16).uppercase().padStart(2, '0')
            val rHex = (motorR.toByte().toInt() and 0xFF).toString(16).uppercase().padStart(2, '0')
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0x660000000))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row {
                    Text(
                        "HEX: ",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF64748B),
                        letterSpacing = 2.sp
                    )
                    Text(
                        "$lHex $rHex",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = TextYellow, // text-yellow-500
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }

    if (showTuning) {
        TuningDialog(viewModel = viewModel, onDismiss = { showTuning = false })
    }
}

// bg-slate-900 px-3 py-1.5 rounded-lg border border-slate-700 text-center flex-1
@Composable
private fun MotorDisplay(label: String, value: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BgCard)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = TextGreen, // text-green-500
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = value.toString(),
            fontSize = 18.sp,
            color = TextBlue, // text-blue-400
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
    }
}
