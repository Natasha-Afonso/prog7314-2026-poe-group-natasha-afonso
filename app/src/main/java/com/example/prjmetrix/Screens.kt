package com.example.prjmetrix

import android.app.Notification
import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.samsung.android.sdk.health.data.error.HealthDataException
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalTime


// DATA MODELS
data class HabitCircle(
   val id: String,
   val label: String,
   val icon: ImageVector,
   val progress: Float,
   val color: Color
)

data class TodoItem(
   val id: String,
   val label: String,
   val coinReward: Int = 5,
   val done: Boolean = false,
   val habitId: String? = null
)

data class NavigationItem(
   val id: String,
   val title: String,
   val message: String,
   val type: NotificationType,//eg reward , outfit reccommendations, avatar messsages
   val timestamp: String ="Just now"
)

private  const val FALLBACK_STEP_GOAL = 6000

private fun timeGreeting(): String = when (LocalTime.now().hour) {
   in 5..11 -> "Good Morning"
   in 12..16 -> "Good afternoon"
   in 17..21 -> "Good evening"
   else -> "Hello"
}

data class PieMenuItem(
   val id: String,
   val icon: ImageVector,
   val label: String,
   val color: Color,
   val onSelect: () -> Unit
)



enum class NotificationType {
   ALERT, REWARD,MILESTONE,Social,STEPS
}
val sampleNewList = listOf(
   NavigationItem(
      id = "1",
      title = "BRUSH TEETH PLEASE",
      message = "Stinky sticky",
      type = NotificationType.ALERT,
      timestamp = "Just now"
   ),
   NavigationItem(
      id = "2",
      title = "YOU WON A FREE GIFT",
      message = "OPEN THE STORE TO SEE",
      type = NotificationType.REWARD,
      timestamp = "Just now"
   )
)
@Composable
fun HomeScreen() {

   // STATE
   var userCoins by remember { mutableIntStateOf(1250) }
   var showNewsBoard by remember { mutableStateOf(false) }
    val newsItems = remember {mutableStateListOf(*sampleNewList.toTypedArray())}
   // QUEST CARD VISIBILITY & EXPANSION STATES
   var isQuestCardVisible by remember { mutableStateOf(true) }
   var isQuestCardExpanded by remember { mutableStateOf(false) }

   var showAddHabitDialog by remember { mutableStateOf(false) }
   var newHabitText by remember { mutableStateOf("") }
   var selectedMood by remember { mutableStateOf<String?>(null) }
   var avatarRootPosition by remember { mutableStateOf(Offset.Zero) }
   var pieMenuCenter by remember { mutableStateOf<Offset?>(null) }

   var dailyRewardClaimed by remember { mutableStateOf(false) }
   var showRewardPopup by remember { mutableStateOf(false) }

   val habitCircles = remember {
      mutableStateListOf(
         HabitCircle("water", "Drink Water", Icons.Default.WaterDrop, 0.6f, Color(0xFF29B6F6)),
         HabitCircle("brush", "Brush Teeth", Icons.Default.Brush, 0.3f, Color(0xFF66BB6A)),
         HabitCircle("cardio", "Cardio", Icons.Default.DirectionsRun, 0.85f, Color(0xFFFFA726))
      )
   }

   val todos = remember {
      mutableStateListOf(
         TodoItem("t1", "Drink water", habitId = "water"),
         TodoItem("t2", "Pick an outfit", habitId = "brush"),
         TodoItem("t3", "Cardio workout", habitId = "cardio"),
         TodoItem("t4", "Read 10 pages"),
         TodoItem("t5", "Meditate 5 mins"),
         TodoItem("t6", "Organize desk")
      )
   }

   // Auto-check sync when habit reaches 100%
   LaunchedEffect(habitCircles.map { it.progress }) {
      habitCircles.forEach { habit ->
         if (habit.progress >= 1f) {
            val index = todos.indexOfFirst { it.habitId == habit.id }
            if (index != -1 && !todos[index].done) {
               todos[index] = todos[index].copy(done = true)
            }
         }
      }
   }

   // Pinned top 3 daily habits
   val pinnedHabits = todos.take(3)
   val otherHabits = todos.drop(3)
   val allPinnedDone = pinnedHabits.isNotEmpty() && pinnedHabits.all { it.done }

   LaunchedEffect(allPinnedDone) {
      if (allPinnedDone && !dailyRewardClaimed) {
         showRewardPopup = true
      }
   }

   val pieMenuItems = habitCircles.mapIndexed { index, habit ->
      PieMenuItem(
         id = habit.id,
         icon = habit.icon,
         label = habit.label,
         color = habit.color,
         onSelect = {
            habitCircles[index] = habit.copy(progress = 1f)
         }
      )
   }

   val context = LocalContext.current
   val scope = rememberCoroutineScope()
   val stepsRepo = remember { SamsungHealthStepsRepository(context.applicationContext) }
   var todaySteps by remember { mutableStateOf<Long?>(null) }
   var stepsPermissionGranted by remember { mutableStateOf(false) }
   var prefs = remember {context.getSharedPreferences("metrix_prefs", Context.MODE_PRIVATE)}
   var greetingShown by remember { mutableStateOf(false) }

   val refreshSteps: () -> Unit = {
      scope.launch {
         try {
            stepsPermissionGranted = stepsRepo.hasPermissions()
            if (stepsPermissionGranted) {
               val steps = stepsRepo.readTodaySteps()
               val goal = stepsRepo.readStepGoal().takeIf { it > 0 } ?: FALLBACK_STEP_GOAL
               todaySteps = steps

               // Greeting: once per app launch
               if (!greetingShown) {
                  greetingShown = true
                  newsItems.add(
                     0,
                     NavigationItem(
                        id = "steps_greeting_${System.currentTimeMillis()}",
                        title = "${timeGreeting()}!",
                        message = "You've taken ${NumberFormat.getInstance().format(steps)} steps so far today.",
                        type = NotificationType.STEPS
                     )
                  )
               }

               // Goal milestone: once per day, even across app restarts
               val today = LocalDate.now().toString()
               if (steps >= goal && prefs.getString("goal_notified_date", null) != today) {
                  prefs.edit().putString("goal_notified_date", today).apply()
                  newsItems.add(
                     0,
                     NavigationItem(
                        id = "steps_goal_$today",
                        title = "GOAL REACHED!",
                        message = "Congratulations, you've reached $goal steps! 🎉",
                        type = NotificationType.MILESTONE
                     )
                  )
               }
            }
         } catch (e: HealthDataException) {
            Log.e("Steps", "Samsung Health error", e)
         }
      }
   }

// Runs on first launch and every time the app returns to the foreground
   LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { refreshSteps() }

   val todayDateString = remember {
      SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())
   }

   Box(modifier = Modifier.fillMaxSize()) {

      // Background
      Image(
         painter = painterResource(id = R.drawable.img_10),
         contentDescription = "Room background",
         modifier = Modifier.fillMaxWidth().height(700.dp).align(Alignment.TopStart),
         contentScale = ContentScale.FillHeight
      )

      // Avatar Shadow
      Box(
         modifier = Modifier
            .width(90.dp).height(16.dp)
            .align(Alignment.Center).offset(y = 232.dp)
            .clip(RoundedCornerShape(50))
            .background(Color.Black.copy(alpha = 0.18f))
      )

      // Avatar Layer
      Image(
         painter = painterResource(id = R.drawable.base_body_f),
         contentDescription = "Avatar",
         modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .align(Alignment.Center)
            .onGloballyPositioned { coords -> avatarRootPosition = coords.positionInRoot() }
            .pointerInput(Unit) {
               detectTapGestures(onTap = { local ->
                  pieMenuCenter = avatarRootPosition + local
               })
            },
         contentScale = ContentScale.FillHeight
      )

      Image(
         painter = painterResource(id = R.drawable.blue_lamp),
         contentDescription = "Blue lamp",
         modifier = Modifier
            .width(92.dp).height(354.dp)
            .align(Alignment.CenterStart)
            .offset(x = 26.dp, y = 100.dp),
         contentScale = ContentScale.Fit
      )

      // LIQUID HABIT CIRCLES
      Column(
         modifier = Modifier.align(Alignment.TopEnd).offset(x = (-16).dp, y = 210.dp),
         verticalArrangement = Arrangement.spacedBy(18.dp),
         horizontalAlignment = Alignment.End
      ) {
         habitCircles.forEachIndexed { index, habit ->
            LiquidHabitCircle(
               label = habit.label,
               icon = habit.icon,
               progress = habit.progress,
               liquidColor = habit.color,
               onClick = {
                  val newProg = (habit.progress + 0.25f).coerceAtMost(1f)
                  habitCircles[index] = habit.copy(progress = newProg)
               }
            )
         }
      }

      // TOP APP BAR
      JetpackTopNavbar(
         coins = userCoins,
         todoCount = todos.count { !it.done },
         todoPanelOpen = isQuestCardVisible,
         hasUnreadNews = newsItems.isNotEmpty(),
         newsBoardOpen = showNewsBoard,
         onTodoClick = {
            // Toggle visibility on menu button tap
            isQuestCardVisible = !isQuestCardVisible
         },
         onNewsClick = {
            showNewsBoard = !showNewsBoard
         },
         onProfileClick = { },
         onMoreClick = { }
      )

      // SINGLE ALL-IN-ONE QUEST & HABITS CONTAINER
      AnimatedVisibility(
         visible = isQuestCardVisible,
         modifier = Modifier
            .align(Alignment.TopStart)
            .offset(x = 8.dp, y = 110.dp),
         enter = expandVertically(),
         exit = shrinkVertically()
      ) {
         Card(
            modifier = Modifier
               .width(165.dp)
               .heightIn(max = if (isQuestCardExpanded) 270.dp else Dp.Unspecified),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f)),
            border = BorderStroke(1.5.dp, Color(0xFFE7B4BE)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
         ) {
            Column(modifier = Modifier.padding(8.dp)) {
               // Header Row with Title and Close (X) Button
               Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
               ) {
                  Column(modifier = Modifier.weight(1f)) {
                     Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                           text = todayDateString,
                           fontSize = 8.sp,
                           fontWeight = FontWeight.Bold,
                           color = Color(0xFF8D6E63),
                           maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        StepsChip(
                           steps = todaySteps,
                           hasPermission = stepsPermissionGranted,
                           onConnectClick = {
                              val activity = context.findActivity() ?: return@StepsChip
                              scope.launch {
                                 try {
                                    stepsRepo.requestPermissions(activity)
                                    refreshSteps()
                                 } catch (e: HealthDataException) {
                                    Log.e("Steps", "Permission request failed", e)
                                 }
                              }
                           }
                        )
                     }
                     Text(
                        text = "Today's Quests",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF43302E)
                     )
                  }

                  // Dismiss X Button
                  IconButton(
                     onClick = { isQuestCardVisible = false },
                     modifier = Modifier.size(16.dp)
                  ) {
                     Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Quests",
                        tint = Color(0xFF8D6E63)
                     )
                  }
               }

               Spacer(modifier = Modifier.height(4.dp))

               // SCROLLABLE CONTAINER (Active when expanded)
               Column(
                  modifier = Modifier
                     .weight(1f, fill = false)
                     .then(if (isQuestCardExpanded) Modifier.verticalScroll(rememberScrollState()) else Modifier)
               ) {
                  // Top 3 Daily Habits
                  pinnedHabits.forEach { item ->
                     val origIndex = todos.indexOf(item)
                     TodoRow(item = item, onToggle = {
                        if (origIndex != -1) {
                           val updated = !todos[origIndex].done
                           todos[origIndex] = todos[origIndex].copy(done = updated)
                           todos[origIndex].habitId?.let { hId ->
                              val hIndex = habitCircles.indexOfFirst { it.id == hId }
                              if (hIndex != -1) {
                                 habitCircles[hIndex] = habitCircles[hIndex].copy(progress = if (updated) 1f else 0f)
                              }
                           }
                        }
                     })
                  }

                  // EXPANDED PORTION: Divider + Remaining Habits
                  if (isQuestCardExpanded) {
                     Spacer(modifier = Modifier.height(6.dp))
                     HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))
                     Spacer(modifier = Modifier.height(6.dp))

                     Text(
                        text = "ALL HABITS",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF9E9E9E),
                        modifier = Modifier.padding(bottom = 2.dp)
                     )

                     otherHabits.forEach { item ->
                        val origIndex = todos.indexOf(item)
                        TodoRow(item = item, onToggle = {
                           if (origIndex != -1) {
                              val updated = !todos[origIndex].done
                              todos[origIndex] = todos[origIndex].copy(done = updated)
                           }
                        })
                     }

                     Spacer(modifier = Modifier.height(6.dp))

                     // Add Habit Trigger inside list
                     Row(
                        modifier = Modifier
                           .fillMaxWidth()
                           .height(24.dp)
                           .clickable { showAddHabitDialog = true },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                     ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Add habit", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                     }
                  }
               }

               Spacer(modifier = Modifier.height(4.dp))

               // Expand / Collapse Trigger
               Row(
                  modifier = Modifier
                     .fillMaxWidth()
                     .clickable { isQuestCardExpanded = !isQuestCardExpanded },
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.Center
               ) {
                  Text(
                     text = if (isQuestCardExpanded) "Show Less" else "View All Habits",
                     fontSize = 8.5.sp,
                     fontWeight = FontWeight.Bold,
                     color = Color(0xFF0284C7)
                  )
                  Icon(
                     imageVector = if (isQuestCardExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                     contentDescription = null,
                     tint = Color(0xFF0284C7),
                     modifier = Modifier.size(11.dp)
                  )
               }
            }
         }
      }

      // REWARD CLAIM POPUP
      if (showRewardPopup) {
         Card(
            modifier = Modifier
               .width(180.dp)
               .align(Alignment.TopStart)
               .offset(x = 8.dp, y = 250.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
            border = BorderStroke(2.dp, Color(0xFFFBC02D)),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
         ) {
            Column(
               modifier = Modifier.padding(10.dp),
               horizontalAlignment = Alignment.CenterHorizontally
            ) {
               Text("✨ Quests Complete! ✨", fontWeight = FontWeight.ExtraBold, fontSize = 10.sp, color = Color(0xFFF57F17))
               Spacer(modifier = Modifier.height(2.dp))
               Text("Completed today's 3 habits!", fontSize = 8.5.sp, color = Color(0xFF5D4037))
               Spacer(modifier = Modifier.height(6.dp))
               Button(
                  onClick = {
                     userCoins += 500
                     dailyRewardClaimed = true
                     showRewardPopup = false
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBC02D)),
                  shape = RoundedCornerShape(50)
               ) {
                  Text("Claim 🪙 500", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 9.5.sp)
               }
            }
         }
      }

      // NEWS BOARD
      AnimatedVisibility(
         visible = showNewsBoard,
         modifier = Modifier.align(Alignment.TopEnd).offset(x = (-12).dp, y = 80.dp),
         enter = expandVertically(),
         exit = shrinkVertically()
      ) {
         NewsBoard(
            newsList = newsItems,
            onDismissItem = {id -> newsItems.removeAll {it.id == id} },
            onClearAll = {newsItems.clear()},
            onClose = { showNewsBoard = false })
      }

      // BOTTOM CARD
      Card(
         modifier = Modifier
            .fillMaxWidth().height(180.dp)
            .padding(horizontal = 14.dp)
            .align(Alignment.BottomCenter)
            .offset(y = (-10).dp),
         shape = RoundedCornerShape(18.dp),
         colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
         elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
      ) {
         Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp)
         ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
               Text(
                  text = if (selectedMood == null) "ADD YOUR MOOD" else "HAPPINESS: EXTREMELY DELULU ✨",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = Color(0xFF43302E),
                  modifier = Modifier.weight(1f)
               )
               Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = Color(0xFF64748B))
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (selectedMood != null) {
               LinearProgressIndicator(
                  progress = { 0.85f },
                  color = Color(0xFF00BCD4),
                  modifier = Modifier.fillMaxWidth().height(9.dp)
               )
               Spacer(modifier = Modifier.height(12.dp))
            }

            Row(
               modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
               horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
               listOf("😃", "😔", "😐", "😍", "😆", "😎", "😶","😡").forEach { emoji ->
                  Card(
                     modifier = Modifier.size(36.dp),
                     shape = RoundedCornerShape(9.dp),
                     colors = CardDefaults.cardColors(
                        containerColor = if (emoji == selectedMood) Color(0xFFB2EBF2) else Color(0xFFE2E8F0)
                     ),
                     elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                     border = if (emoji == selectedMood) BorderStroke(1.5.dp, Color(0xFF00BCD4)) else null
                  ) {
                     Box(
                        modifier = Modifier.fillMaxSize().clickable { selectedMood = emoji },
                        contentAlignment = Alignment.Center
                     ) {
                        Text(text = emoji, fontSize = 17.sp)
                     }
                  }
               }
            }

            if (selectedMood == null) {
               Spacer(modifier = Modifier.height(8.dp))
               Text(
                  text = "Tap an emoji to log today's mood",
                  fontSize = 10.sp,
                  color = Color(0xFF90A4AE)
               )
            }
         }
      }

      // AVATAR PIE MENU
      pieMenuCenter?.let { center ->
         AvatarPieMenu(
            center = center,
            items = pieMenuItems,
            onDismiss = { pieMenuCenter = null }
         )
      }
   }

   // ADD HABIT DIALOG
   if (showAddHabitDialog) {
      AlertDialog(
         onDismissRequest = { showAddHabitDialog = false; newHabitText = "" },
         title = { Text("New habit") },
         text = {
            OutlinedTextField(
               value = newHabitText,
               onValueChange = { newHabitText = it },
               placeholder = { Text("e.g. Read for 10 minutes") },
               singleLine = true,
               keyboardOptions = KeyboardOptions.Default
            )
         },
         confirmButton = {
            TextButton(onClick = {
               if (newHabitText.isNotBlank()) {
                  todos.add(TodoItem(id = "t${todos.size + 1}", label = newHabitText.trim()))
               }
               newHabitText = ""
               showAddHabitDialog = false
            }) { Text("Add") }
         },
         dismissButton = {
            TextButton(onClick = { showAddHabitDialog = false; newHabitText = "" }) { Text("Cancel") }
         }
      )
   }
}

// LIQUID FILL HABIT CIRCLE
@Composable
fun LiquidHabitCircle(
   label: String,
   icon: ImageVector,
   progress: Float,
   liquidColor: Color,
   modifier: Modifier = Modifier,
   onClick: () -> Unit
) {
   val animatedProgress by animateFloatAsState(
      targetValue = progress.coerceIn(0f, 1f),
      animationSpec = tween(durationMillis = 700),
      label = "liquidProgress"
   )

   val infiniteTransition = rememberInfiniteTransition(label = "wave")
   val wavePhase by infiniteTransition.animateFloat(
      initialValue = 0f,
      targetValue = (2 * PI).toFloat(),
      animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing), RepeatMode.Restart),
      label = "wavePhase"
   )

   Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
      Box(
         modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(2.dp, liquidColor.copy(alpha = 0.55f), CircleShape)
            .clickable { onClick() },
         contentAlignment = Alignment.Center
      ) {
         Canvas(modifier = Modifier.matchParentSize()) {
            val waveAmplitude = 5.dp.toPx()
            val liquidTop = size.height * (1f - animatedProgress)
            val path = Path().apply {
               moveTo(0f, liquidTop)
               var x = 0f
               val step = 4f
               while (x <= size.width) {
                  val y = liquidTop + sin((x / size.width) * 4 * PI + wavePhase) * waveAmplitude
                  lineTo(x, y.toFloat())
                  x += step
               }
               lineTo(size.width, size.height)
               lineTo(0f, size.height)
               close()
            }
            drawPath(path, color = liquidColor.copy(alpha = 0.75f))
         }

         if (animatedProgress >= 1f) {
            Icon(
               imageVector = Icons.Default.Check,
               contentDescription = "Complete",
               tint = Color.White,
               modifier = Modifier.size(28.dp)
            )
         } else {
            Icon(
               imageVector = icon,
               contentDescription = label,
               tint = if (animatedProgress > 0.55f) Color.White else liquidColor,
               modifier = Modifier.size(16.dp).align(Alignment.TopCenter).padding(top = 8.dp)
            )

            Text(
               text = "${(animatedProgress * 100).toInt()}%",
               fontSize = 12.sp,
               fontWeight = FontWeight.ExtraBold,
               color = if (animatedProgress > 0.55f) Color.White else Color(0xFF333333),
               modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
            )
         }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Card(
         shape = RoundedCornerShape(50),
         colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.75f))
      ) {
         Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333),
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
         )
      }
   }
}

// COMPACT TO-DO ROW
@Composable
fun TodoRow(item: TodoItem, onToggle: () -> Unit) {
   Row(
      modifier = Modifier
         .fillMaxWidth()
         .height(24.dp)
         .clickable { onToggle() },
      verticalAlignment = Alignment.CenterVertically
   ) {
      Box(
         modifier = Modifier
            .size(14.dp)
            .clip(CircleShape)
            .background(if (item.done) Color(0xFF66BB6A) else Color.Transparent)
            .border(1.2.dp, if (item.done) Color(0xFF66BB6A) else Color(0xFFB0BEC5), CircleShape),
         contentAlignment = Alignment.Center
      ) {
         if (item.done) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(9.dp))
         }
      }

      Spacer(modifier = Modifier.width(5.dp))

      Text(
         text = item.label,
         modifier = Modifier.weight(1f),
         fontSize = 9.5.sp,
         color = if (item.done) Color(0xFF9E9E9E) else Color(0xFF333333),
         textDecoration = if (item.done) TextDecoration.LineThrough else TextDecoration.None
      )

      Text(
         text = "🪙${item.coinReward}",
         fontSize = 8.5.sp,
         color = if (item.done) Color(0xFFCCCCCC) else Color(0xFF8D6E63)
      )
   }
}

// NEWS BOARD
@Composable
fun NewsBoard(
   newsList: List<NavigationItem>,
   onDismissItem: (String)-> Unit,
   onClearAll: () -> Unit,
   onClose: () -> Unit) {
   Card(
      modifier = Modifier.width(220.dp)
      .offset(x = 8.dp, y = 20.dp),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xEECCCCCC)),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    //  border = BorderStroke(2.dp, Color.White)
   ) {
      Column(modifier = Modifier.padding(5.dp)
      ) {

         Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
         ) {
            //Header
            IconButton(onClick = onClearAll, modifier = Modifier.size(18.dp)) {
               Icon(
                  Icons.Default.DeleteForever,
                  contentDescription = "Delete All",
                  tint = Color(0xFF64748B)
               )
            }
            //header title
            Text("METRICS NEWS", fontSize = 8.sp, fontWeight= FontWeight.ExtraBold, color = Color(0xFF334155))
            //close X
            IconButton(onClick = onClose, modifier = Modifier.size(18.dp)) {
               Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
            }
         }

         Spacer(modifier = Modifier.height(6.dp))

   if (newsList.isEmpty()) {
         Text(
            text = "You're all caught up",
            fontSize = 8.sp,
            color = Color(0xFF90A4AE),
            modifier = Modifier.padding(vertical = 8.dp)
         )
      }
      else {
      LazyColumn(
         modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),//fixed height
         verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
         items(
            items = newsList,
            key = { it.id }
         ) { item ->
            NotificationCardItem(
               item = item,
               onDismiss = { onDismissItem(item.id) }
            )
         }

      }
      }



//         Column(
//            modifier = Modifier
//               .fillMaxWidth()
//               .background(Color(0xFFE2E8F0))
//               .padding(8.dp)
//         ) {
//            Row(modifier = Modifier.fillMaxWidth()) {
//               Text("Aspiration Level", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF334155), modifier = Modifier.weight(1f))
//               Text("75%", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF0284C7))
//            }
//            Spacer(modifier = Modifier.height(4.dp))
//            LinearProgressIndicator(
//               progress = { 0.75f },
//               color = Color(0xFF958705),
//               modifier = Modifier.fillMaxWidth().height(8.dp)
//            )
//         }

         Spacer(modifier = Modifier.height(8.dp))

         }
      }
   }

@Composable
fun  NotificationCardItem(
   item: NavigationItem,
   onDismiss: () -> Unit
) {
   val cardColor = when (item.type) {
      NotificationType.ALERT -> Color(0xFF389ED0) //BLUE
      NotificationType.REWARD -> Color(0xFFFFA726)//PURPLE
      NotificationType.MILESTONE -> Color(0xFF8B5CF6)
      NotificationType.Social -> Color(0xFF43A047)
      NotificationType.STEPS -> Color(0xFF26A69)
   }

   val icon: ImageVector = when (item.type) {
      NotificationType.ALERT -> Icons.Default.WaterDrop
      NotificationType.REWARD -> Icons.Default.CardGiftcard
      NotificationType.MILESTONE -> Icons.Default.EmojiEvents
      NotificationType.Social -> Icons.Default.Groups
      NotificationType.STEPS-> Icons.Default.DirectionsRun
   }
   Box(modifier = Modifier.fillMaxWidth()) {
      Card(
         modifier = Modifier.fillMaxWidth(),
         shape = RoundedCornerShape(6.dp),
         colors = CardDefaults.cardColors(containerColor = cardColor)
      ) {
         //Column(modifier = Modifier.padding(8.dp)) {
         Row(
            modifier = Modifier.padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
         ) {
            Box(
               modifier = Modifier.size(24.dp)
                  .clip(RoundedCornerShape(9.dp))
                  .background(Color.White.copy(0.25f)),
               contentAlignment = Alignment.Center
            ) {
               Icon(
                  icon,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(18.dp)
               )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
               Text(
                  text = item.timestamp,
                  fontSize = 7.sp,
                  color = Color.White.copy(alpha = 0.8f)

               )
               Spacer(modifier = Modifier.height(1.dp))

               Text(
                  text = item.title,
                  fontSize = 8.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
               )
               Spacer(modifier = Modifier.height(1.dp))

               Text(
                  text = item.message,
                  fontSize = 7.5.sp,
                  color = Color.White
               )
            }
         }
      }
      Box(
         modifier = Modifier.align(Alignment.TopEnd)
            .offset(x = 6.dp, y = (-6).dp)
            .size(20.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, cardColor, CircleShape)
            .clickable { onDismiss() },
         contentAlignment = Alignment.Center
      ) {
         Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color(0xFFE53935),
            modifier = Modifier.size(12.dp))
      }
   }
}

@Composable
fun StepsChip(
   steps: Long?,
   hasPermission: Boolean,
   onConnectClick: () -> Unit
) {
   val label = when {
      !hasPermission -> "Connect"
      steps == null -> "…"
      else -> NumberFormat.getInstance().format(steps)
   }

   Row(
      modifier = Modifier
         .clip(RoundedCornerShape(50))
         .background(Color(0xFFFFF3E0))
         .clickable(enabled = !hasPermission) { onConnectClick() }
         .padding(horizontal = 4.dp, vertical = 1.dp),
      verticalAlignment = Alignment.CenterVertically
   ) {
      Icon(
         imageVector = Icons.Default.DirectionsRun,
         contentDescription = "Steps",
         tint = Color(0xFFFFA726),
         modifier = Modifier.size(8.dp)
      )
      Spacer(modifier = Modifier.width(2.dp))
      Text(
         text = label,
         fontSize = 8.sp,
         fontWeight = FontWeight.Bold,
         color = Color(0xFF8D6E63),
         maxLines = 1
      )
   }
}

// AVATAR PIE MENU
@Composable
fun AvatarPieMenu(
   center: Offset,
   items: List<PieMenuItem>,
   onDismiss: () -> Unit
) {
   val density = LocalDensity.current
   val radiusPx = with(density) { 96.dp.toPx() }
   val itemSizeDp = 46.dp
   val itemSizePx = with(density) { itemSizeDp.toPx() }

   val startAngleDeg = -160f
   val endAngleDeg = -20f
   val angleStep = if (items.size > 1) (endAngleDeg - startAngleDeg) / (items.size - 1) else 0f

   Box(
      modifier = Modifier
         .fillMaxSize()
         .background(Color.Black.copy(alpha = 0.22f))
         .pointerInput(Unit) {
            detectTapGestures(onTap = { onDismiss() })
         }
   ) {
      items.forEachIndexed { index, item ->
         val angleRad = Math.toRadians((startAngleDeg + angleStep * index).toDouble())
         val x = center.x + radiusPx * cos(angleRad).toFloat() - itemSizePx / 2f
         val y = center.y + radiusPx * sin(angleRad).toFloat() - itemSizePx / 2f

         Box(
            modifier = Modifier
               .offset { IntOffset(x.roundToInt(), y.roundToInt()) }
               .size(itemSizeDp)
               .clip(CircleShape)
               .background(Color.White)
               .border(2.dp, item.color, CircleShape)
               .clickable {
                  item.onSelect()
                  onDismiss()
               },
            contentAlignment = Alignment.Center
         ) {
            Icon(item.icon, contentDescription = item.label, tint = item.color, modifier = Modifier.size(20.dp))
         }
      }

      Box(
         modifier = Modifier
            .offset { IntOffset((center.x - 4.dp.toPx()).roundToInt(), (center.y - 4.dp.toPx()).roundToInt()) }
            .size(8.dp)
            .clip(CircleShape)
            .background(Color.White)
      )
   }
}

// TOP HUD
private val NavbarSurface = Color.White.copy(alpha = 0.88f)
private val NavbarBorder = Color(0xFFE7B4BE)
private val NavbarIcon = Color(0xFF6D4C41)
private val NavbarCoinText = Color(0xFF8D6E63)
private val NavbarDivider = Color(0xFFE7B4BE).copy(alpha = 0.5f)

@Composable
fun JetpackTopNavbar(
   coins: Int,
   todoCount: Int,
   todoPanelOpen: Boolean = false,
   hasUnreadNews: Boolean = false,
   newsBoardOpen: Boolean = false,
   onTodoClick: () -> Unit,
   onNewsClick: () -> Unit,
   onProfileClick: () -> Unit,
   onMoreClick: () -> Unit
) {
   Row(
      modifier = Modifier
         .fillMaxWidth()
         .statusBarsPadding()
         .padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
   ) {

      // LEFT ANCHOR - Toggle Habit Drawer
      Box {
         Box(
            modifier = Modifier
               .size(38.dp)
               .clip(CircleShape)
               .background(if (todoPanelOpen) NavbarBorder.copy(alpha = 0.35f) else NavbarSurface)
               .border(BorderStroke(1.5.dp, NavbarBorder), CircleShape)
               .clickable { onTodoClick() },
            contentAlignment = Alignment.Center
         ) {
            Icon(Icons.Default.List, contentDescription = "To-Do", tint = NavbarIcon, modifier = Modifier.size(19.dp))
         }
         if (todoCount > 0 && !todoPanelOpen) {
            Box(
               modifier = Modifier
                  .size(17.dp)
                  .align(Alignment.TopEnd)
                  .offset(x = 3.dp, y = (-3).dp)
                  .clip(CircleShape)
                  .background(Color(0xFFE53935))
                  .border(1.5.dp, NavbarSurface, CircleShape),
               contentAlignment = Alignment.Center
            ) {
               Text(text = todoCount.toString(), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
         }
      }

      // RIGHT ANCHOR
      Row(
         modifier = Modifier
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(50))
            .background(NavbarSurface)
            .border(BorderStroke(1.5.dp, NavbarBorder), RoundedCornerShape(50)),
         verticalAlignment = Alignment.CenterVertically
      ) {
         Box {
            NavStripIcon(onClick = onMoreClick, trailingPadding = 6.dp) {
               Icon(Icons.Default.MoreVert, contentDescription = "More", tint = NavbarIcon, modifier = Modifier.size(19.dp))
            }
         }

         NavDivider()

         Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
         ) {
            Text(text = "🪙", fontSize = 15.sp)
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = coins.toString(), color = NavbarCoinText, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
         }

         NavDivider()

         NavStripIcon(onClick = onProfileClick) {
            Icon(Icons.Default.Person, contentDescription = "Profile", tint = NavbarIcon, modifier = Modifier.size(17.dp))
         }

         NavDivider()



         NavStripIcon(onClick = onNewsClick, leadingPadding = 12.dp) {
            Icon(Icons.Default.Article, contentDescription = "News", tint = NavbarIcon, modifier = Modifier.size(17.dp))
         }
         if (hasUnreadNews && !newsBoardOpen) {
            Box(
               modifier = Modifier
                  .size(9.dp)
                //  .align(Alignment.TopEnd)
                  .clip(CircleShape)
                  .background(Color(0xFFE53935))
                  .border(1.5.dp, NavbarSurface, CircleShape)
            )
         }
      }
   }
}

@Composable
private fun NavDivider() {
   Box(
      modifier = Modifier
         .fillMaxHeight()
         .padding(vertical = 8.dp)
         .width(1.dp)
         .background(NavbarDivider)
   )
}

@Composable
private fun NavStripIcon(
   onClick: () -> Unit,
   leadingPadding: Dp = 10.dp,
   trailingPadding: Dp = 0.dp,
   content: @Composable () -> Unit
) {
   Box(
      modifier = Modifier
         .padding(start = leadingPadding, end = 10.dp + trailingPadding, top = 8.dp, bottom = 8.dp)
         .clickable { onClick() },
      contentAlignment = Alignment.Center
   ) {
      content()
   }
}
/*References
https://proandroiddev.com/mastering-lazy-lists-in-jetpack-compose-with-data-classes-and-mvi-da240800c6e9
https://raghunandan2005.medium.com/mastering-high-performance-custom-canvas-animations-in-jetpack-compose-building-a-premium-circular-b9f9e465b118
 */