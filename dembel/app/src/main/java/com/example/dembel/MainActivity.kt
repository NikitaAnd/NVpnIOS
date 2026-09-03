package com.example.dembel

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { DembelApp() }
    }
}

private val BgGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFF8FAFC), // slate-50
        Color(0xFFEEF2FF), // indigo-50
    )
)

private val Accent = Color(0xFF4F46E5) // indigo-600
private val AccentSoft = Color(0xFFE0E7FF)
private val OnSurface = Color(0xFF0F172A)
private val OnSurfaceMuted = Color(0xFF475569)

@Composable
fun DembelApp() {
    val context = LocalContext.current
    val today = remember { LocalDate.now() }
    // Значение по умолчанию — год вперёд
    var targetDate by remember { mutableStateOf(today.plusYears(1)) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Тикаем каждую секунду, чтобы цифры шли живьём
    var nowMs by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            nowMs = System.currentTimeMillis()
            kotlinx.coroutines.delay(1000)
        }
    }

    val totalDays = ChronoUnit.DAYS.between(today, targetDate).coerceAtLeast(0)
    val isPast = targetDate.isBefore(today)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgGradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "До дембеля",
                    color = OnSurfaceMuted,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = targetDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru"))),
                    color = OnSurface,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(28.dp))

                CountdownRing(
                    totalDays = totalDays,
                    targetDate = targetDate,
                    nowMs = nowMs
                )

                Spacer(Modifier.height(32.dp))

                MilestonesRow(progress = if (totalDays == 0L) 1f else 1f - (totalDays.toFloat() / 365f).coerceIn(0f, 1f))

                Spacer(Modifier.height(24.dp))

                OutlinedButton(
                    onClick = { showDatePicker = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Accent
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentSoft)
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Изменить дату", fontWeight = FontWeight.Medium)
                }

                Spacer(Modifier.height(16.dp))

                if (isPast) {
                    Text(
                        "Эта дата уже прошла 🎉",
                        color = OnSurfaceMuted,
                        fontSize = 14.sp
                    )
                } else {
                    Text(
                        "Дембель через $totalDays ${daysWord(totalDays)}",
                        color = OnSurfaceMuted,
                        fontSize = 14.sp
                    )
                }
            }

            if (showDatePicker) {
                val d = targetDate
                DatePickerDialog(
                    context,
                    { _, y, m, day ->
                        targetDate = LocalDate.of(y, m + 1, day)
                        showDatePicker = false
                    },
                    d.year,
                    d.monthValue - 1,
                    d.dayOfMonth
                ).apply {
                    setOnCancelListener { showDatePicker = false }
                    show()
                }
            }
        }
    }
}

@Composable
private fun CountdownRing(totalDays: Long, targetDate: LocalDate, nowMs: Long) {
    val now = LocalDate.now()
    // Точное время до конца целевого дня
    val endOfTargetMs = targetDate.plusDays(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
    val totalRemainingMs = (endOfTargetMs - nowMs).coerceAtLeast(0L)

    val totalSec = totalRemainingMs / 1000
    val days = totalSec / 86_400
    val hours = (totalSec % 86_400) / 3_600
    val minutes = (totalSec % 3_600) / 60
    val seconds = totalSec % 60

    // Прогресс: 1.0 = только начали (1 год), 0.0 = сегодня
    val targetMax = targetDate
    val start = targetDate.minusYears(1)
    val span = ChronoUnit.DAYS.between(start, targetMax).coerceAtLeast(1)
    val elapsed = ChronoUnit.DAYS.between(start, now).coerceIn(0, span)
    val targetProgress = (elapsed.toFloat() / span.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(900),
        label = "progress"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(260.dp)
    ) {
        CircularProgressIndicator(
            progress = 1f,
            modifier = Modifier.fillMaxSize(),
            strokeWidth = 14.dp,
            color = AccentSoft,
            trackColor = Color.Transparent
        )
        CircularProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier.fillMaxSize(),
            strokeWidth = 14.dp,
            color = Accent,
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (totalDays == 0L) "0" else days.toString(),
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )
            Text(
                text = daysWord(days),
                color = OnSurfaceMuted,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TimePill("${hours.toString().padStart(2, '0')}", "ч")
                TimePill("${minutes.toString().padStart(2, '0')}", "мин")
                TimePill("${seconds.toString().padStart(2, '0')}", "сек")
            }
        }
    }
}

@Composable
private fun TimePill(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(value, fontWeight = FontWeight.SemiBold, color = OnSurface, fontSize = 16.sp)
        Text(label, color = OnSurfaceMuted, fontSize = 10.sp)
    }
}

@Composable
private fun MilestonesRow(progress: Float) {
    val items = listOf("0%", "25%", "50%", "75%", "🎉")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { idx, label ->
            val milestone = idx / (items.size - 1).toFloat()
            val reached = progress >= milestone
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(if (reached) 14.dp else 10.dp)
                        .clip(CircleShape)
                        .background(if (reached) Accent else AccentSoft)
                )
                Spacer(Modifier.height(6.dp))
                Text(label, color = OnSurfaceMuted, fontSize = 11.sp)
            }
        }
    }
}

private fun daysWord(n: Long): String {
    val abs = if (n < 0) -n else n
    val n10 = abs % 10
    val n100 = abs % 100
    return when {
        n10 == 1L && n100 != 11L -> "день"
        n10 in 2L..4L && (n100 < 12L || n100 > 14L) -> "дня"
        else -> "дней"
    }
}
