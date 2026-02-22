package com.robocar.app.ui.blocks

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.robocar.app.MainViewModel
import com.robocar.app.model.BlockType

@Composable
fun BlocksScreen(viewModel: MainViewModel, blockViewModel: BlockViewModel) {
    val program by blockViewModel.program.collectAsState()
    val isRunning by blockViewModel.isRunning.collectAsState()
    val activeBlockId by blockViewModel.activeBlockId.collectAsState()
    val editingBlock by blockViewModel.editingBlock.collectAsState()
    val bleState by viewModel.bleState.collectAsState()
    val isConnected = viewModel.bleManager.isConnected

    var showPicker by remember { mutableStateOf(false) }
    var showExamples by remember { mutableStateOf(false) }
    var pickerForParent by remember { mutableStateOf<Pair<String, Boolean>?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A1020))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // === TOP BAR ===
            BlocksTopBar(
                isRunning = isRunning,
                isConnected = isConnected,
                onRun = {
                    if (isRunning) blockViewModel.stopProgram()
                    else blockViewModel.runProgram(viewModel.bleManager, viewModel.sensorData)
                },
                onClear = { blockViewModel.clearProgram() },
                onExamples = { showExamples = true }
            )

            // === СЕНСОРИ ===
            SensorBar(viewModel = viewModel)

            // === БЛОКИ ===
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(program, key = { it.id }) { block ->
                    BlockItem(
                        block = block,
                        isActive = block.id == activeBlockId,
                        onEdit = { blockViewModel.startEditing(it) },
                        onDelete = { blockViewModel.removeBlock(it) },
                        onMoveUp = { blockViewModel.moveBlockUp(it) },
                        onMoveDown = { blockViewModel.moveBlockDown(it) },
                        onAddSubBlock = { parentId, isElse ->
                            pickerForParent = Pair(parentId, isElse)
                            showPicker = true
                        },
                        onDeleteSubBlock = { parentId, subId, isElse ->
                            blockViewModel.removeSubBlock(parentId, subId, isElse)
                        }
                    )
                    Spacer(Modifier.height(2.dp))
                }
            }
        }

        // === FAB — ДОДАТИ БЛОК ===
        FloatingActionButton(
            onClick = {
                pickerForParent = null
                showPicker = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor = Color(0xFF3A80FF),
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Додати блок", modifier = Modifier.size(28.dp))
        }
    }

    // === ДІАЛОГИ ===
    if (showPicker) {
        BlockPickerDialog(
            onDismiss = { showPicker = false; pickerForParent = null },
            onSelect = { type ->
                val parent = pickerForParent
                if (parent != null) {
                    blockViewModel.addSubBlock(parent.first, type, parent.second)
                } else {
                    blockViewModel.addBlock(type)
                }
                pickerForParent = null
            }
        )
    }

    editingBlock?.let { block ->
        BlockParamEditor(
            block = block,
            onDismiss = { blockViewModel.stopEditing() },
            onUpdate = { paramIdx, value -> blockViewModel.updateParam(block.id, paramIdx, value) }
        )
    }

    if (showExamples) {
        ExamplesDialog(
            onDismiss = { showExamples = false },
            onSelect = { name ->
                blockViewModel.loadExample(name)
                showExamples = false
            }
        )
    }
}

@Composable
private fun BlocksTopBar(
    isRunning: Boolean,
    isConnected: Boolean,
    onRun: () -> Unit,
    onClear: () -> Unit,
    onExamples: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A1525))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Кнопка Старт/Стоп
        Button(
            onClick = onRun,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRunning) Color(0xFFDC2626) else Color(0xFF16A34A)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(40.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = if (isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = if (isRunning) "СТОП" else "СТАРТ",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp
            )
        }

        Spacer(Modifier.weight(1f))

        // Приклади
        IconButton(
            onClick = onExamples,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF1A2540))
        ) {
            Icon(Icons.Default.FolderOpen, contentDescription = "Приклади", tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
        }

        // Очистити
        IconButton(
            onClick = onClear,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF1A2540))
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Очистити", tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun SensorBar(viewModel: MainViewModel) {
    val sensors by viewModel.sensorData.collectAsState()
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0D1525))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        listOf(
            "P1" to sensors.p1,
            "P2" to sensors.p2,
            "P3" to sensors.p3,
            "P4" to sensors.p4,
        ).forEach { (label, value) ->
            SensorChip(label = label, value = value)
        }
    }
}

@Composable
private fun SensorChip(label: String, value: Int) {
    val filled = (value / 255f).coerceIn(0f, 1f)
    val color = when {
        filled > 0.7f -> Color(0xFFEF4444)
        filled > 0.4f -> Color(0xFFF59E0B)
        else -> Color(0xFF34D399)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1A2540))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(label, color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(4.dp))
        Text(value.toString(), color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ExamplesDialog(
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F1B2E),
        title = { Text("📂 Приклади", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExampleItem("🤖 Автопілот", "Об'їзд перешкод по датчику") { onSelect("autopilot") }
                ExampleItem("⬜ Квадрат", "Їхати квадратом (4 кути)") { onSelect("square") }
                ExampleItem("⏺ Запис траси", "Записати та відтворити 3 рази") { onSelect("record") }
                ExampleItem("🆕 Новий", "Очистити програму") { onSelect("new") }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрити", color = Color(0xFF94A3B8))
            }
        }
    )
}

@Composable
private fun ExampleItem(title: String, desc: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF1A2540))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(desc, color = Color(0xFF64748B), fontSize = 11.sp)
        }
        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color(0xFF3A80FF), modifier = Modifier.size(16.dp))
    }
}
