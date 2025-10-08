import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf(Screen.Planner) }
    var dragOffset by remember { mutableStateOf(0f) }
    var screenWidth by remember { mutableStateOf(0f) }
    var isAnimating by remember { mutableStateOf(false) }
    var animationType by remember { mutableStateOf<AnimationType?>(null) }
    var shouldInterruptAnimation by remember { mutableStateOf(false) }
    var isGestureInProgress by remember { mutableStateOf(false) }

    val animationOffset = remember { Animatable(0f) }

    // Обработка прерывания анимации
    LaunchedEffect(shouldInterruptAnimation) {
        if (shouldInterruptAnimation) {
            // Определяем, была ли это анимация перехода на новую страницу
            val wasSwitching = abs(animationOffset.value) > screenWidth * 0.5f

            if (wasSwitching) {
                // Если мы почти переключили страницу, завершаем переход
                val newScreen = if (animationOffset.value < 0) currentScreen.next() else currentScreen.previous()
                // Атомарно меняем экран и сбрасываем состояние
                currentScreen = newScreen
                dragOffset = 0f
                animationOffset.snapTo(0f)
            } else {
                // Сохраняем текущую позицию анимации для продолжения драга
                dragOffset = animationOffset.value
                animationOffset.snapTo(0f)
            }

            isAnimating = false
            animationType = null
            shouldInterruptAnimation = false
        }
    }

    // Обработка анимации
    LaunchedEffect(animationType) {
        when (animationType) {
            is AnimationType.Switch -> {
                // Если жест все еще активен, откладываем анимацию
                if (isGestureInProgress) {
                    return@LaunchedEffect
                }

                val targetScreen = (animationType as AnimationType.Switch).targetScreen
                isAnimating = true

                // Плавная анимация перехода
                animationOffset.snapTo(dragOffset)
                animationOffset.animateTo(
                    targetValue = if (targetScreen == currentScreen.next()) -screenWidth else screenWidth,
                    animationSpec = tween(durationMillis = 300)
                )

                // Атомарно меняем экран и сбрасываем состояние
                currentScreen = targetScreen
                dragOffset = 0f
                animationOffset.snapTo(0f)
                isAnimating = false
                animationType = null
            }

            is AnimationType.QuickSwitch -> {
                // Если жест все еще активен, откладываем анимацию
                if (isGestureInProgress) {
                    return@LaunchedEffect
                }

                val targetScreen = (animationType as AnimationType.QuickSwitch).targetScreen
                isAnimating = true

                // Быстрая анимация для мгновенных свайпов
                animationOffset.snapTo(dragOffset)
                animationOffset.animateTo(
                    targetValue = if (targetScreen == currentScreen.next()) -screenWidth else screenWidth,
                    animationSpec = tween(durationMillis = 150)
                )

                // Атомарно меняем экран и сбрасываем состояние
                currentScreen = targetScreen
                dragOffset = 0f
                animationOffset.snapTo(0f)
                isAnimating = false
                animationType = null
            }

            is AnimationType.Return -> {
                // Если жест все еще активен, откладываем анимацию
                if (isGestureInProgress) {
                    return@LaunchedEffect
                }

                isAnimating = true

                // Анимация возврата
                animationOffset.snapTo(dragOffset)
                animationOffset.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 200)
                )

                // Атомарно сбрасываем состояние
                dragOffset = 0f
                animationOffset.snapTo(0f)
                isAnimating = false
                animationType = null
            }

            null -> {
                // Нет анимации
            }

            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .onSizeChanged { size ->
                screenWidth = size.width.toFloat()
            }
            .pointerInput(Unit) {
                var lastDragTime = 0L
                var totalDragAmount = 0f
                var isFastSwipeDetected = false

                detectHorizontalDragGestures(
                    onDragStart = {
                        lastDragTime = System.currentTimeMillis()
                        totalDragAmount = 0f
                        isGestureInProgress = true
                        isFastSwipeDetected = false

                        // При начале жеста прерываем текущую анимацию
                        if (isAnimating) {
                            shouldInterruptAnimation = true
                        }
                    },
                    onDragEnd = {
                        isGestureInProgress = false

                        // Если уже был обнаружен быстрый свайп, не запускаем новую анимацию
                        if (isFastSwipeDetected) {
                            return@detectHorizontalDragGestures
                        }

                        val dragDuration = System.currentTimeMillis() - lastDragTime
                        val progress = abs(dragOffset) / screenWidth
                        val velocity = if (dragDuration > 0) abs(totalDragAmount) / dragDuration else 0f

                        // Определяем тип жеста
                        when {
                            // Очень быстрый свайп - мгновенный переход
                            velocity > 3f && progress > 0.1f -> {
                                val targetScreen = if (dragOffset < 0) currentScreen.next() else currentScreen.previous()
                                animationType = AnimationType.QuickSwitch(targetScreen)
                            }
                            // Достаточный сдвиг для перехода
                            progress > 0.3f -> {
                                val targetScreen = if (dragOffset < 0) currentScreen.next() else currentScreen.previous()
                                animationType = AnimationType.Switch(targetScreen)
                            }
                            // Возврат на место
                            else -> {
                                animationType = AnimationType.Return
                            }
                        }
                    },
                    onDragCancel = {
                        isGestureInProgress = false
                        animationType = AnimationType.Return
                    }
                ) { change, dragAmount ->
                    totalDragAmount += abs(dragAmount)

                    // Если нет активной анимации, обновляем смещение
                    if (!isAnimating) {
                        dragOffset += dragAmount

                        // Ограничиваем максимальное смещение
                        val maxOffset = screenWidth * 0.8f
                        dragOffset = dragOffset.coerceIn(-maxOffset, maxOffset)

                        // Если очень быстрый свайп по краю - мгновенный переход
                        val isEdgeSwipe = abs(dragOffset) > screenWidth * 0.5f && abs(dragAmount) > screenWidth * 0.2f
                        if (isEdgeSwipe && !isAnimating && !isFastSwipeDetected) {
                            val targetScreen = if (dragOffset < 0) currentScreen.next() else currentScreen.previous()
                            animationType = AnimationType.QuickSwitch(targetScreen)
                            isFastSwipeDetected = true
                        }
                    }
                }
            }
    ) {
        // Определяем смещение для отображения
        val displayOffset = if (isAnimating) animationOffset.value else dragOffset

        // Определяем следующую страницу для анимации
        val nextScreen = if (displayOffset < 0) {
            currentScreen.next()
        } else {
            currentScreen.previous()
        }

        // Всегда показываем обе страницы во время драга/анимации
        if (displayOffset != 0f) {
            // Следующая страница
            PageContent(
                screen = nextScreen,
                modifier = Modifier
                    .fillMaxSize()
                    .offset {
                        val offset = if (displayOffset < 0) {
                            (screenWidth + displayOffset).roundToInt()
                        } else {
                            (-screenWidth + displayOffset).roundToInt()
                        }
                        IntOffset(x = offset, y = 0)
                    }
                    .alpha(abs(displayOffset) / screenWidth)
            )

            // Текущая страница
            PageContent(
                screen = currentScreen,
                modifier = Modifier
                    .fillMaxSize()
                    .offset {
                        IntOffset(x = displayOffset.roundToInt(), y = 0)
                    }
                    .alpha(1f - (abs(displayOffset) / screenWidth))
            )
        } else {
            // Когда нет смещения, показываем только текущую страницу
            PageContent(
                screen = currentScreen,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Центрированный индикатор с плавной анимацией
        CenteredAnimatedPageIndicator(
            currentScreen = currentScreen,
            dragOffset = displayOffset,
            screenWidth = screenWidth,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

@Composable
fun CenteredAnimatedPageIndicator(
    currentScreen: Screen,
    dragOffset: Float,
    screenWidth: Float,
    modifier: Modifier = Modifier
) {
    val screens = Screen.values()

    // Вычисляем прогресс свайпа (от -1 до 1)
    val swipeProgress = if (screenWidth > 0) dragOffset / screenWidth else 0f
    val absoluteProgress = abs(swipeProgress)

    // Определяем направление и целевую страницу
    val targetScreen = if (swipeProgress < 0) {
        currentScreen.next()
    } else {
        currentScreen.previous()
    }

    // Находим индексы текущей и целевой страниц
    val currentIndex = screens.indexOf(currentScreen)
    val targetIndex = screens.indexOf(targetScreen)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        screens.forEachIndexed { index, screen ->
            // Вычисляем прогресс для каждой точки
            val dotProgress = when {
                // Текущая точка - плавно уменьшается
                index == currentIndex -> 1f - absoluteProgress
                // Целевая точка - плавно увеличивается
                index == targetIndex -> absoluteProgress
                // Остальные точки - остаются минимальными
                else -> 0f
            }

            // Плавно интерполируем размер (от 6dp до 12dp)
            val dotSize = (6 + (6 * dotProgress)).dp

            // Плавно интерполируем цвет от серого к основному
            val baseColor = MaterialTheme.colorScheme.onBackground
            val primaryColor = MaterialTheme.colorScheme.primary

            // Создаем плавный переход цвета
            val dotColor = when {
                dotProgress > 0.8f -> primaryColor
                dotProgress > 0.1f -> androidx.compose.ui.graphics.Color(
                    baseColor.red + (primaryColor.red - baseColor.red) * dotProgress,
                    baseColor.green + (primaryColor.green - baseColor.green) * dotProgress,
                    baseColor.blue + (primaryColor.blue - baseColor.blue) * dotProgress,
                    0.3f + (0.7f * dotProgress) // Плавное изменение альфы
                )
                else -> baseColor.copy(alpha = 0.3f)
            }

            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
    }
}

// Типы анимаций
sealed class AnimationType {
    data class Switch(val targetScreen: Screen) : AnimationType()
    data class QuickSwitch(val targetScreen: Screen) : AnimationType()
    object Return : AnimationType()
}

// Остальной код без изменений...
@Composable
fun PageContent(screen: Screen, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (screen) {
            Screen.Planner -> PlannerPage()
            Screen.Notes -> NotesPage()
            Screen.Statistics -> StatisticsPage()
            Screen.Settings -> SettingsPage()
            Screen.Server -> ServerPage()
        }
    }
}

@Composable
fun PlannerPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Планировщик",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        CustomBlock(
            title = "Ежедневные задачи",
            onSettingsClick = { }
        ) {
            Text(
                text = "• Задача 1\n• Задача 2\n• Задача 3",
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        CustomBlock(
            title = "Проекты",
            onSettingsClick = { }
        ) {
            Text(
                text = "Текущие проекты:\n- Проект А\n- Проект Б",
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        CustomBlock(
            title = "Календарь",
            onSettingsClick = { }
        ) {
            Text(
                text = "Сегодня:\n10:00 - Встреча\n14:00 - Обед",
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun NotesPage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📝", fontSize = 48.sp)
            Text("Страница заметок", fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground)
            Text("Плавно проведите пальцем влево/вправо", fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun StatisticsPage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📊", fontSize = 48.sp)
            Text("Страница статистики", fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground)
            Text("Плавно проведите пальцем влево/вправо", fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun SettingsPage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⚙️", fontSize = 48.sp)
            Text("Страница настроек", fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground)
            Text("Плавно проведите пальцем влево/вправо", fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun ServerPage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🌐", fontSize = 48.sp)
            Text("Страница сервера", fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground)
            Text("Плавно проведите пальцем влево/вправо", fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
        }
    }
}

enum class Screen {
    Planner, Notes, Statistics, Settings, Server
}

fun Screen.next(): Screen {
    return when (this) {
        Screen.Planner -> Screen.Notes
        Screen.Notes -> Screen.Statistics
        Screen.Statistics -> Screen.Settings
        Screen.Settings -> Screen.Server
        Screen.Server -> Screen.Planner
    }
}

fun Screen.previous(): Screen {
    return when (this) {
        Screen.Planner -> Screen.Server
        Screen.Notes -> Screen.Planner
        Screen.Statistics -> Screen.Notes
        Screen.Settings -> Screen.Statistics
        Screen.Server -> Screen.Settings
    }
}