package com.robocar.app.ui.blocks

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import com.robocar.app.model.*

@Composable
fun BlockItem(
    block: ProgramBlock,
    isActive: Boolean,
    depth: Int = 0,
    onEdit: (ProgramBlock) -> Unit,
    onDelete: (String) -> Unit,
    onMoveUp: (String) -> Unit,
    onMoveDown: (String) -> Unit,
    onAddSubBlock: (String, Boolean) -> Unit,
    onDeleteSubBlock: (String, String, Boolean) -> Unit,
) {
    val blockColor = Color(block.type.color)
    val isStart = block.type == BlockType.START_HAT
    val indentDp = (depth * 16).dp

    Column(modifier = Modifier.padding(start = indentDp)) {
        // === Connector top notch (не для START) ===
        if (!isStart && depth == 0) {
            Box(
                modifier = Modifier
                    .padding(start = 24.dp)
                    .width(16.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                    .background(blockColor.copy(alpha = 0.6f))
            )
        }

        // === Основний блок ===
        val glowAlpha = if (isActive) 0.35f else 0f
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    if (isStart) RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 8.dp, bottomEnd = 8.dp)
                    else RoundedCornerShape(8.dp)
                )
                .background(blockColor)
                .then(
                    if (isActive) Modifier.border(2.dp, Color.White.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                    else Modifier
                )
        ) {
            // Glow overlay
            if (isActive) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.White.copy(alpha = glowAlpha))
                )
            }

            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                // Header row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Emoji + Label
                    Text(
                        text = block.type.emoji,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(
                        text = block.type.label,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )

                    // Actions (не для START)
                    if (!isStart) {
                        Row {
                            SmallIconBtn(Icons.Default.KeyboardArrowUp) { onMoveUp(block.id) }
                            SmallIconBtn(Icons.Default.KeyboardArrowDown) { onMoveDown(block.id) }
                            if (block.params.isNotEmpty()) {
                                SmallIconBtn(Icons.Default.Edit) { onEdit(block) }
                            }
                            SmallIconBtn(Icons.Default.Delete, tint = Color(0xFFFF6B6B)) { onDelete(block.id) }
                        }
                    }
                }

                // Параметри (короткий перегляд)
                if (block.params.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        block.params.forEach { param ->
                            ParamChip(param = param, blockColor = blockColor)
                        }
                    }
                }
            }
        }

        // === Підблоки (для циклів/умов) ===
        if (block.type.hasSub) {
            // Тіло "ТО"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp)
            ) {
                // Вертикальна лінія
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .fillMaxHeight()
                        .background(blockColor.copy(alpha = 0.5f))
                )

                Column(modifier = Modifier.padding(start = 8.dp)) {
                    if (block.subBlocks.isEmpty()) {
                        EmptySlot(label = if (block.type.hasSub2) "ТО:" else "ВИКОНАТИ:")
                    } else {
                        if (block.type.hasSub2) {
                            Text(
                                "ТО:",
                                color = blockColor.copy(alpha = 0.7f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                            )
                        }
                        block.subBlocks.forEach { sub ->
                            BlockItem(
                                block = sub,
                                isActive = false,
                                depth = 0,
                                onEdit = onEdit,
                                onDelete = { onDeleteSubBlock(block.id, it, false) },
                                onMoveUp = {},
                                onMoveDown = {},
                                onAddSubBlock = { _, _ -> },
                                onDeleteSubBlock = { _, _, _ -> }
                            )
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                    // + кнопка додати підблок
                    AddSubBlockBtn(color = blockColor) { onAddSubBlock(block.id, false) }
                }
            }

            // ELSE гілка
            if (block.type.hasSub2) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(blockColor.copy(alpha = 0.3f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("ІНАКШЕ:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .fillMaxHeight()
                            .background(blockColor.copy(alpha = 0.3f))
                    )
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        if (block.subBlocks2.isEmpty()) {
                            EmptySlot(label = "ІНАКШЕ:")
                        } else {
                            block.subBlocks2.forEach { sub ->
                                BlockItem(
                                    block = sub,
                                    isActive = false,
                                    depth = 0,
                                    onEdit = onEdit,
                                    onDelete = { onDeleteSubBlock(block.id, it, true) },
                                    onMoveUp = {},
                                    onMoveDown = {},
                                    onAddSubBlock = { _, _ -> },
                                    onDeleteSubBlock = { _, _, _ -> }
                                )
                                Spacer(Modifier.height(4.dp))
                            }
                        }
                        AddSubBlockBtn(color = blockColor, label = "+ ІНАКШЕ") { onAddSubBlock(block.id, true) }
                    }
                }
            }

            // Закриваюча плашка
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                    .background(blockColor.copy(alpha = 0.6f))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "кінець ${block.type.emoji}",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Bottom connector
        if (!isStart && depth == 0) {
            Box(
                modifier = Modifier
                    .padding(start = 24.dp)
                    .width(16.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    .background(blockColor.copy(alpha = 0.6f))
            )
        }
    }
}

@Composable
private fun ParamChip(param: BlockParam, blockColor: Color) {
    val text = when (param) {
        is BlockParam.NumberInput -> "${param.label}: ${
            if (param.value == param.value.toLong().toFloat()) 
                param.value.toLong().toString() 
            else 
                "%.1f".format(param.value)
        }"
        is BlockParam.DropdownInput -> "${param.label}: ${
            param.options.find { it.second == param.selected }?.first ?: param.selected
        }"
        is BlockParam.TextInput -> "${param.label}: \"${param.value}\""
        is BlockParam.SubProgram -> "${param.label}: ${param.blocks.size} блоків"
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color.Black.copy(alpha = 0.25f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text, color = Color.White, fontSize = 11.sp)
    }
}

@Composable
private fun EmptySlot(label: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
    }
}

@Composable
private fun AddSubBlockBtn(color: Color, label: String = "+ Додати блок", onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.padding(top = 2.dp)
    ) {
        Icon(Icons.Default.Add, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(4.dp))
        Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SmallIconBtn(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color = Color.White.copy(alpha = 0.7f),
    onClick: () -> Unit
) {
    IconButton(onClick = onClick, modifier = Modifier.size(28.dp)) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
    }
}
