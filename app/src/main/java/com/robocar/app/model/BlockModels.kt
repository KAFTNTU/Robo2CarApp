package com.robocar.app.model

import java.util.UUID

// ===== Категорії =====
enum class BlockCategory(val label: String, val color: Long) {
    CAR       ("🚗 Машинка",    0xFF0062BAL),
    CONTROL   ("🔁 Керування",  0xFFFFBF00L),
    SENSORS   ("📡 Сенсори",    0xFF00897BL),
    MATH      ("📐 Математика", 0xFF5C6BC0L),
    STATE     ("🧠 Стан",       0xFF8E24AAL),
    SMART     ("⚡ Розумні",    0xFFE65100L),
}

// ===== Тип параметру блоку =====
sealed class BlockParam {
    data class NumberInput(val label: String, val value: Float, val min: Float = -100f, val max: Float = 100f) : BlockParam()
    data class DropdownInput(val label: String, val options: List<Pair<String,String>>, val selected: String) : BlockParam()
    data class TextInput(val label: String, val value: String) : BlockParam()
    data class SubProgram(val label: String, val blocks: List<ProgramBlock> = emptyList()) : BlockParam()
}

// ===== Один блок у програмі =====
data class ProgramBlock(
    val id: String = UUID.randomUUID().toString(),
    val type: BlockType,
    val params: List<BlockParam> = emptyList(),
    val subBlocks: List<ProgramBlock> = emptyList(), // для циклів/умов
    val subBlocks2: List<ProgramBlock> = emptyList(), // для else
)

// ===== Всі типи блоків =====
enum class BlockType(
    val label: String,
    val emoji: String,
    val category: BlockCategory,
    val color: Long,
    val hasNext: Boolean = true,
    val hasPrev: Boolean = true,
    val hasSub: Boolean = false,
    val hasSub2: Boolean = false,
) {
    // === 🚗 МАШИНКА ===
    START_HAT       ("СТАРТ",                   "🏁", BlockCategory.CAR,     0xFF2E7D32L, hasPrev = false),
    ROBOT_MOVE      ("Їхати L/R",               "🚗", BlockCategory.CAR,     0xFF0062BAL),
    ROBOT_MOVE_SOFT ("Плавний старт",            "🚀", BlockCategory.CAR,     0xFF0062BAL),
    ROBOT_TURN      ("Поворот",                 "🔄", BlockCategory.CAR,     0xFF0062BAL),
    ROBOT_SET_SPEED ("Швидкість",               "⚡", BlockCategory.CAR,     0xFF0062BAL),
    ROBOT_STOP      ("Стоп",                    "🛑", BlockCategory.CAR,     0xFFB71C1CL),
    MOTOR_SINGLE    ("Мотор A/B/C/D",           "⚙️", BlockCategory.CAR,     0xFF4527A0L),
    GO_HOME         ("Додому (Назад)",           "🏠", BlockCategory.CAR,     0xFF0062BAL),
    RECORD_START    ("Почати запис траси",       "🔴", BlockCategory.CAR,     0xFF6A1B9AL),
    REPLAY_TRACK    ("Відтворити трасу",         "▶️", BlockCategory.CAR,     0xFF6A1B9AL),
    REPLAY_LOOP     ("Відтворити N разів",       "🔄", BlockCategory.CAR,     0xFF6A1B9AL),
    WAIT_START      ("Чекати Старт (лінія)",     "🏁", BlockCategory.CAR,     0xFF37474FL),
    STOP_AT_START   ("Зупинитись на старті",     "🛑", BlockCategory.CAR,     0xFF37474FL),
    COUNT_LAPS      ("Лічити кола",             "🔢", BlockCategory.CAR,     0xFF37474FL),
    AUTOPILOT       ("Автопілот (датчик)",       "🤖", BlockCategory.CAR,     0xFFE65100L),

    // === 🔁 КЕРУВАННЯ ===
    WAIT_SECONDS    ("Чекати (сек)",             "⏳", BlockCategory.CONTROL, 0xFF37474FL),
    LOOP_FOREVER    ("Цикл назавжди",            "♾️", BlockCategory.CONTROL, 0xFF2E7D32L, hasSub = true),
    LOOP_REPEAT     ("Повторити N разів",        "🔁", BlockCategory.CONTROL, 0xFF2E7D32L, hasSub = true),
    LOOP_REPEAT_PAUSE("Повторити з паузою",      "🔁", BlockCategory.CONTROL, 0xFF2E7D32L, hasSub = true),
    LOOP_EVERY_SEC  ("Кожні N секунд",           "⏱", BlockCategory.CONTROL, 0xFF2E7D32L, hasSub = true),
    TIMER_RESET     ("Скинути таймер",           "🔄", BlockCategory.CONTROL, 0xFF37474FL),

    // === 📡 СЕНСОРИ ===
    WAIT_UNTIL_SENSOR("Чекати поки сенсор",      "⏳", BlockCategory.SENSORS, 0xFF00695CL),

    // === 📐 МАТЕМАТИКА ===
    TIMER_GET       ("Таймер (с)",              "⏱️", BlockCategory.MATH,    0xFF283593L),
    MATH_PID        ("PID Регулятор",           "🎛️", BlockCategory.MATH,    0xFF283593L),
    MATH_SMOOTH     ("Згладити",               "🌊", BlockCategory.MATH,    0xFF283593L),
    MATH_PYTHAGORAS ("Піфагор (діагональ)",     "📐", BlockCategory.MATH,    0xFF283593L),
    MATH_PATH_VT    ("Довжина шляху v×t",       "📏", BlockCategory.MATH,    0xFF283593L),
    MATH_SPEED_CMS  ("Швидкість (см/с)",        "🚗", BlockCategory.MATH,    0xFF283593L),
    CALIBRATE_SPEED ("Калібрувати швидкість",   "⚙️", BlockCategory.MATH,    0xFF283593L),

    // === 🧠 СТАН (State Machine) ===
    STATE_SET       ("Стан =",                  "🧠", BlockCategory.STATE,   0xFF6A1B9AL),
    STATE_SET_REASON("Стан = (з причиною)",     "🧠", BlockCategory.STATE,   0xFF6A1B9AL),
    STATE_PREV      ("Повернутись у попередній","↩️", BlockCategory.STATE,   0xFF6A1B9AL),
    STATE_IF        ("Якщо стан =",             "🧠", BlockCategory.STATE,   0xFF6A1B9AL, hasSub = true, hasSub2 = true),

    // === ⚡ РОЗУМНІ УМОВИ ===
    WAIT_UNTIL_TRUE_FOR("Чекати поки умова тримається", "⏳", BlockCategory.SMART, 0xFFBF360CL),
    TIMEOUT_DO_UNTIL("Робити до умови (таймаут)","⏱",  BlockCategory.SMART, 0xFFBF360CL, hasSub = true),
    COOLDOWN_DO     ("Не частіше ніж раз на N с","🧊",  BlockCategory.SMART, 0xFFBF360CL, hasSub = true),
    LATCH_SET       ("Прапор встановити",       "📌",  BlockCategory.SMART, 0xFFBF360CL),
    LATCH_RESET     ("Прапор скинути",          "🧽",  BlockCategory.SMART, 0xFFBF360CL),
}

// ===== Фабрика блоків з дефолтними параметрами =====
fun createBlock(type: BlockType): ProgramBlock {
    val params = mutableListOf<BlockParam>()
    when (type) {
        BlockType.ROBOT_MOVE -> {
            params += BlockParam.NumberInput("L", 100f, -100f, 100f)
            params += BlockParam.NumberInput("R", 100f, -100f, 100f)
        }
        BlockType.ROBOT_MOVE_SOFT -> {
            params += BlockParam.NumberInput("Ціль", 100f, -100f, 100f)
            params += BlockParam.NumberInput("Сек", 1f, 0f, 10f)
        }
        BlockType.ROBOT_TURN -> {
            params += BlockParam.DropdownInput("Напрям", listOf("Ліворуч ⬅️" to "LEFT", "Праворуч ➡️" to "RIGHT"), "LEFT")
            params += BlockParam.NumberInput("Сек", 0.5f, 0f, 10f)
        }
        BlockType.ROBOT_SET_SPEED -> {
            params += BlockParam.NumberInput("Швидкість %", 50f, 0f, 100f)
        }
        BlockType.MOTOR_SINGLE -> {
            params += BlockParam.DropdownInput("Мотор", listOf("A" to "1","B" to "2","C" to "3","D" to "4"), "1")
            params += BlockParam.NumberInput("Шв", 100f, -100f, 100f)
        }
        BlockType.REPLAY_LOOP -> {
            params += BlockParam.NumberInput("Разів", 1f, 1f, 99f)
        }
        BlockType.COUNT_LAPS -> {
            params += BlockParam.NumberInput("Кіл", 3f, 1f, 99f)
        }
        BlockType.AUTOPILOT -> {
            params += BlockParam.DropdownInput("Порт", listOf("1" to "0","2" to "1","3" to "2","4" to "3"), "0")
            params += BlockParam.DropdownInput("Поворот", listOf("RIGHT" to "RIGHT","LEFT" to "LEFT"), "RIGHT")
            params += BlockParam.NumberInput("Поріг <", 40f, 0f, 255f)
            params += BlockParam.NumberInput("Швидк.", 60f, 0f, 100f)
        }
        BlockType.WAIT_SECONDS -> {
            params += BlockParam.NumberInput("Сек", 1f, 0f, 60f)
        }
        BlockType.LOOP_REPEAT -> {
            params += BlockParam.NumberInput("Разів", 3f, 1f, 99f)
        }
        BlockType.LOOP_REPEAT_PAUSE -> {
            params += BlockParam.NumberInput("Разів", 3f, 1f, 99f)
            params += BlockParam.NumberInput("Пауза (с)", 1f, 0f, 10f)
        }
        BlockType.LOOP_EVERY_SEC -> {
            params += BlockParam.NumberInput("Кожні (с)", 1f, 0.1f, 60f)
        }
        BlockType.WAIT_UNTIL_SENSOR -> {
            params += BlockParam.DropdownInput("Порт", listOf("1" to "0","2" to "1","3" to "2","4" to "3"), "0")
            params += BlockParam.DropdownInput("Умова", listOf("< менше" to "LT","> більше" to "GT"), "LT")
            params += BlockParam.NumberInput("Значення", 25f, 0f, 255f)
        }
        BlockType.MATH_PID -> {
            params += BlockParam.NumberInput("Kp", 1f, 0f, 100f)
            params += BlockParam.NumberInput("Ki", 0f, 0f, 100f)
            params += BlockParam.NumberInput("Kd", 0f, 0f, 100f)
        }
        BlockType.MATH_SMOOTH -> {
            params += BlockParam.NumberInput("К-сть", 5f, 2f, 50f)
        }
        BlockType.CALIBRATE_SPEED -> {
            params += BlockParam.NumberInput("Відстань (см)", 50f, 1f, 500f)
            params += BlockParam.DropdownInput("Порт", listOf("1" to "0","2" to "1","3" to "2","4" to "3"), "0")
            params += BlockParam.NumberInput("Поріг", 30f, 0f, 255f)
            params += BlockParam.NumberInput("Швидк.", 60f, 0f, 100f)
        }
        BlockType.STATE_SET -> {
            params += BlockParam.TextInput("Стан", "SEARCH")
        }
        BlockType.STATE_SET_REASON -> {
            params += BlockParam.TextInput("Стан", "ATTACK")
            params += BlockParam.TextInput("Причина", "sensor")
        }
        BlockType.STATE_IF -> {
            params += BlockParam.TextInput("Стан", "SEARCH")
        }
        BlockType.WAIT_UNTIL_TRUE_FOR -> {
            params += BlockParam.DropdownInput("Порт", listOf("1" to "0","2" to "1","3" to "2","4" to "3"), "0")
            params += BlockParam.DropdownInput("Умова", listOf("< менше" to "LT","> більше" to "GT"), "LT")
            params += BlockParam.NumberInput("Значення", 25f, 0f, 255f)
            params += BlockParam.NumberInput("Час (с)", 0.2f, 0f, 10f)
        }
        BlockType.TIMEOUT_DO_UNTIL -> {
            params += BlockParam.DropdownInput("Порт", listOf("1" to "0","2" to "1","3" to "2","4" to "3"), "0")
            params += BlockParam.DropdownInput("Умова", listOf("< менше" to "LT","> більше" to "GT"), "LT")
            params += BlockParam.NumberInput("Значення", 25f, 0f, 255f)
            params += BlockParam.NumberInput("Макс (с)", 3f, 0f, 30f)
        }
        BlockType.COOLDOWN_DO -> {
            params += BlockParam.NumberInput("Пауза (с)", 1f, 0f, 30f)
        }
        BlockType.LATCH_SET, BlockType.LATCH_RESET -> {
            params += BlockParam.TextInput("Прапор", "flag1")
        }
        else -> {}
    }
    return ProgramBlock(type = type, params = params)
}
