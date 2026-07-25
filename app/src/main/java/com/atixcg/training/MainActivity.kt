package com.atixcg.training

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atixcg.training.ui.theme.BarBorder
import com.atixcg.training.ui.theme.CardBg
import com.atixcg.training.ui.theme.CardBorder
import com.atixcg.training.ui.theme.ChipBg
import com.atixcg.training.ui.theme.ChipWeightBg
import com.atixcg.training.ui.theme.ChipWeightText
import com.atixcg.training.ui.theme.DotBorder
import com.atixcg.training.ui.theme.Idx
import com.atixcg.training.ui.theme.Ink
import com.atixcg.training.ui.theme.InkSoft
import com.atixcg.training.ui.theme.LabelText
import com.atixcg.training.ui.theme.Muted
import com.atixcg.training.ui.theme.OnInk
import com.atixcg.training.ui.theme.Paper
import com.atixcg.training.ui.theme.SubText
import com.atixcg.training.ui.theme.TabInactive
import com.atixcg.training.ui.theme.TabsBg
import com.atixcg.training.ui.theme.TailText
import com.atixcg.training.ui.theme.TextBody
import com.atixcg.training.ui.theme.ThumbBg
import com.atixcg.training.ui.theme.TrainingTheme
import com.atixcg.training.ui.theme.WarnBg
import com.atixcg.training.ui.theme.WarnBullet
import com.atixcg.training.ui.theme.WarnLabel
import com.atixcg.training.ui.theme.WarnText
import kotlin.math.min

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val store = ProgressStore(applicationContext)
        setContent {
            TrainingTheme {
                WorkoutApp(store)
            }
        }
    }
}

@Composable
private fun WorkoutApp(store: ProgressStore) {
    var day by remember { mutableIntStateOf(store.loadDay()) }
    val sets = remember { mutableStateMapOf<String, Int>().apply { putAll(store.loadSets()) } }
    val open = remember { mutableStateMapOf<String, Boolean>() }

    val workout = WORKOUTS.first { it.id == day }
    val insets = WindowInsets.safeDrawing.asPaddingValues()

    fun key(ex: Exercise) = "d$day-${ex.n}"

    fun setDots(k: String, value: Int) {
        if (value <= 0) sets.remove(k) else sets[k] = value
        store.saveSets(sets)
    }

    val total = workout.exercises.sumOf { it.setsCount }
    val done = workout.exercises.sumOf { min(sets[key(it)] ?: 0, it.setsCount) }
    val anyOpen = workout.exercises.any { open[key(it)] == true }

    Box(
        Modifier
            .fillMaxSize()
            .background(Paper)
    ) {
        Column(Modifier.fillMaxSize()) {
            TopBar(
                day = day,
                progress = if (total > 0) "$done / $total подходов" else "",
                topInset = insets.calculateTopPadding(),
                onSelectDay = { day = it; store.saveDay(it) },
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = insets.calculateBottomPadding() + 24.dp),
            ) {
                item(key = "header-$day") {
                    CenteredColumn {
                        Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)) {
                            Text(
                                workout.title,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 32.sp,
                                letterSpacing = (-0.9).sp,
                                color = Ink,
                            )
                            Text(
                                workout.subtitle,
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                                color = SubText,
                                modifier = Modifier.padding(top = 6.dp),
                            )
                        }
                    }
                }

                items(workout.exercises, key = { "$day-${it.n}" }) { ex ->
                    val k = key(ex)
                    CenteredColumn {
                        Box(Modifier.padding(horizontal = 12.dp, vertical = 5.dp)) {
                            ExerciseCard(
                                ex = ex,
                                done = sets[k] ?: 0,
                                isOpen = open[k] == true,
                                onToggleOpen = { open[k] = !(open[k] ?: false) },
                                onDot = { i -> setDots(k, if ((sets[k] ?: 0) == i) i - 1 else i) },
                            )
                        }
                    }
                }

                item(key = "footer-$day") {
                    CenteredColumn {
                        Footer(
                            anyOpen = anyOpen,
                            onToggleAll = {
                                val target = !anyOpen
                                workout.exercises.forEach { open[key(it)] = target }
                            },
                            onReset = {
                                workout.exercises.forEach { sets.remove(key(it)) }
                                store.saveSets(sets)
                            },
                        )
                        Text(
                            REST_NOTE,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = TailText,
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 8.dp),
                        )
                    }
                }
            }
        }
    }
}

/** Constrains content to the ~560px column the web layout used. */
@Composable
private fun CenteredColumn(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        Column(
            Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth()
        ) { content() }
    }
}

@Composable
private fun TopBar(
    day: Int,
    progress: String,
    topInset: Dp,
    onSelectDay: (Int) -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Paper)
            .padding(top = topInset),
    ) {
        CenteredColumn {
            Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 12.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Программа тренировок",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.15).sp,
                        color = Ink,
                    )
                    if (progress.isNotEmpty()) {
                        Text(
                            progress,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = SubText,
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(TabsBg)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    TabButton("Тренировка 1", day == 1, Modifier.weight(1f)) { onSelectDay(1) }
                    TabButton("Тренировка 2", day == 2, Modifier.weight(1f)) { onSelectDay(2) }
                }
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BarBorder)
        )
    }
}

@Composable
private fun TabButton(label: String, active: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier
            .clip(RoundedCornerShape(9.dp))
            .background(if (active) Ink else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            fontSize = 14.sp,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
            color = if (active) OnInk else TabInactive,
        )
    }
}

@Composable
private fun ExerciseCard(
    ex: Exercise,
    done: Int,
    isOpen: Boolean,
    onToggleOpen: () -> Unit,
    onDot: (Int) -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CardBg)
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp)),
    ) {
        // Head
        Row(
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleOpen)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                ex.n.toString(),
                modifier = Modifier.width(30.dp),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Muted,
            )
            Column(Modifier.weight(1f)) {
                Text(
                    ex.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 21.sp,
                    letterSpacing = (-0.25).sp,
                    color = Ink,
                )
                if (ex.sets != null || ex.weight != null) {
                    Row(
                        Modifier.padding(top = 7.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        ex.sets?.let { Chip(it, ChipBg, Ink, mono = true) }
                        ex.weight?.let { Chip("Вес: $it", ChipWeightBg, ChipWeightText, mono = false) }
                    }
                }
            }
            if (ex.img != null) {
                Spacer(Modifier.width(12.dp))
                BlendImage(
                    res = ex.img,
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ThumbBg)
                        .padding(5.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(if (isOpen) "▲" else "▼", fontSize = 13.sp, color = Muted)
        }

        // Dots
        if (ex.setsCount > 0) {
            Row(
                Modifier.padding(start = 56.dp, end = 14.dp, bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                for (i in 1..ex.setsCount) {
                    val on = i <= done
                    Box(
                        Modifier
                            .size(width = 44.dp, height = 36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (on) Ink else Color.Transparent)
                            .border(
                                width = 1.5.dp,
                                color = if (on) Ink else DotBorder,
                                shape = RoundedCornerShape(10.dp),
                            )
                            .clickable { onDot(i) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            i.toString(),
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (on) FontWeight.Bold else FontWeight.Medium,
                            color = if (on) CardBg else Muted,
                        )
                    }
                }
            }
        }

        // Body
        if (isOpen) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(CardBorder)
            )
            if (ex.img != null) {
                BlendImage(
                    res = ex.img,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(ThumbBg)
                        .padding(14.dp),
                )
            }
            ex.note?.let {
                Text(
                    it,
                    fontSize = 15.sp,
                    lineHeight = 23.sp,
                    color = TextBody,
                    modifier = Modifier.padding(16.dp),
                )
            }
            if (ex.prep.isNotEmpty()) Section("Настройка", ex.prep, SectionKind.PREP)
            if (ex.how.isNotEmpty()) Section("Как делать", ex.how, SectionKind.HOW)
            if (ex.no.isNotEmpty()) WarnSection(ex.no)
            Spacer(Modifier.height(6.dp))
        }
    }
}

private enum class SectionKind { PREP, HOW }

@Composable
private fun Section(label: String, items: List<String>, kind: SectionKind) {
    Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp)) {
        SectionLabel(label, LabelText)
        Spacer(Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.forEachIndexed { i, text ->
                Row {
                    when (kind) {
                        SectionKind.HOW -> Badge((i + 1).toString())
                        SectionKind.PREP -> Text(
                            (i + 1).toString(),
                            modifier = Modifier.width(18.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Idx,
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(text, fontSize = 15.sp, lineHeight = 22.sp, color = InkSoft)
                }
            }
        }
    }
}

@Composable
private fun WarnSection(items: List<String>) {
    Column(
        Modifier
            .padding(start = 12.dp, end = 12.dp, top = 18.dp, bottom = 12.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(WarnBg)
            .padding(14.dp),
    ) {
        SectionLabel("Чего не делать", WarnLabel)
        Spacer(Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.forEach { text ->
                Row {
                    Box(
                        Modifier
                            .padding(top = 7.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(WarnBullet)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(text, fontSize = 14.sp, lineHeight = 21.sp, color = WarnText)
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String, color: Color) {
    Text(
        text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = color,
    )
}

@Composable
private fun Badge(text: String) {
    Box(
        Modifier
            .size(22.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(Ink),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = OnInk)
    }
}

@Composable
private fun Chip(text: String, bg: Color, fg: Color, mono: Boolean) {
    Box(
        Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 7.dp, vertical = 3.dp),
    ) {
        Text(
            text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = if (mono) FontFamily.Monospace else FontFamily.Default,
            color = fg,
        )
    }
}

@Composable
private fun Footer(anyOpen: Boolean, onToggleAll: () -> Unit, onReset: () -> Unit) {
    Row(
        Modifier.padding(start = 12.dp, end = 12.dp, top = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(CardBg)
                .border(1.dp, DotBorder, RoundedCornerShape(12.dp))
                .clickable(onClick = onToggleAll)
                .padding(vertical = 13.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                if (anyOpen) "Свернуть всё" else "Раскрыть всё",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextBody,
            )
        }
        Box(
            Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, DotBorder, RoundedCornerShape(12.dp))
                .clickable(onClick = onReset)
                .padding(horizontal = 16.dp, vertical = 13.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text("Сбросить", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = SubText)
        }
    }
}

/**
 * Draws a drawable with Multiply blend so the white background of the line-art
 * dissolves into the paper-coloured box, matching the web page's
 * `mix-blend-mode: multiply`.
 */
@Composable
private fun BlendImage(@DrawableRes res: Int, modifier: Modifier) {
    val image = ImageBitmap.imageResource(res)
    Canvas(modifier) {
        val scale = min(size.width / image.width, size.height / image.height)
        val w = image.width * scale
        val h = image.height * scale
        val left = (size.width - w) / 2f
        val top = (size.height - h) / 2f
        drawImage(
            image = image,
            dstOffset = IntOffset(left.toInt(), top.toInt()),
            dstSize = IntSize(w.toInt(), h.toInt()),
            blendMode = BlendMode.Multiply,
        )
    }
}
