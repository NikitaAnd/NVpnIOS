package com.example.dembel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

@Composable
fun App() {
    val start = LocalDate.of(2025, 10, 15)
    val end = LocalDate.of(2026, 10, 15)
    val today = LocalDate.now()

    val total = ChronoUnit.DAYS.between(start, end)
    val passed = ChronoUnit.DAYS.between(start, today).coerceAtLeast(0)
    val left = ChronoUnit.DAYS.between(today, end).coerceAtLeast(0)

    val progressRaw = (passed.toFloat() / total).coerceIn(0f, 1f)
    val progress by animateFloatAsState(progressRaw)

    val milestones = listOf(
        0.0f to "Начало службы",
        0.05f to "Адаптация",
        0.25f to "25% — втянулся",
        0.5f to "Экватор 🔥",
        0.75f to "75% — почти дома",
        0.9f to "Финишная прямая",
        1f to "Дембель 🎉"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1D2671), Color(0xFFC33764))
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            GlassCard {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text("До дембеля", color = Color.White.copy(0.7f))

                    Text(
                        "$left",
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text("дней", color = Color.White.copy(0.7f))

                    Spacer(Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = Color.White
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "${(progress * 100).toInt()}%",
                        color = Color.White
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            GlassCard {
                Column {
                    milestones.forEach {
                        val done = progress >= it.first
                        Text(
                            (if (done) "✅ " else "⏳ ") + it.second,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GlassCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(0.85f)
            .blur(10.dp)
            .background(
                Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        content()
    }
}
