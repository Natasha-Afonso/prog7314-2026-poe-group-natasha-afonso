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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Edit
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Sync
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.abs
import kotlinx.coroutines.delay
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prjmetrix.pages.AvatarView
import com.samsung.android.sdk.health.data.error.HealthDataException
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalTime

// Data models used to display habits, to-dos and notifications.
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
   val type: NotificationType,// e.g. rewards, alerts and avatar messages
   val timestamp: String ="Just now"
)

private  const val FALLBACK_STEP_GOAL = 6000

// Room background
val roomBackgrounds = listOf(R.drawable.img_10)

// Shared colours used across the screen.
private val Ink = Color(0xFF43302E)
private val InkSoft = Color(0xFF8D6E63)
private val Accent = Color(0xFF0284C7)
private val GlassWhite = Color.White.copy(alpha = 0.94f)
private val CardBorder = Color(0xFFE7B4BE)

private fun timeGreeting(): String = when (LocalTime.now().hour) {
   in 5..11 -> "Good Morning"
   in 12..16 -> "Good afternoon"
   in 17..21 -> "Good evening"
   else -> "Hello"
}

// Converts the selected mood emoji into a score.
private fun moodScore(emoji: String): Float = when (emoji) {
   "😍", "😆" -> 1f
   "😃", "😎" -> 0.8f
   "😐", "😶" -> 0.5f
   "😔" -> 0.25f
   "😡" -> 0.1f
   else -> 0.5f
}

private fun moodLabel(score: Float): String = when {
   score >= 0.8f -> "HAPPINESS: FEELING GREAT"
   score >= 0.5f -> "HAPPINESS: DOING OKAY"
   score >= 0.25f -> "HAPPINESS: COULD BE BETTER"
   else -> "HAPPINESS: HAVING A ROUGH DAY"
}

// Chooses a colour based on the mood score.
private fun moodColor(score: Float): Color = when {
   score >= 0.8f -> Color(0xFF4CAF50)
   score >= 0.5f -> Color(0xFF9CCC65)
   score >= 0.25f -> Color(0xFFFFB74D)
   else -> Color(0xFFE57373)
}

data class PieMenuItem(
   val id: String,
   val icon: ImageVector,
   val label: String,
   val color: Color,
   val onSelect: () -> Unit,
   val progress: Float = 0f // shown on the habit pill once progress is above 0
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
fun iconForKey(key: String): ImageVector = when (key) {
   "water" -> Icons.Default.WaterDrop
   "brush" -> Icons.Default.Brush
   "cardio" -> Icons.Default.DirectionsRun
   else -> Icons.Default.Check
}

fun colorFromHex(hex: String): Color =
   runCatching { Color(android.graphics.Color.parseColor(hex)) }
      .getOrDefault(Color(0xFF29B6F6))

fun HabitItemDto.toCircle() = HabitCircle(
   id = id.toString(),
   label = label,
   icon = iconForKey(iconKey),
   progress = progress,
   color = colorFromHex(colorHex)
)

fun HabitItemDto.toTodo(linkedToCircle: Boolean) = TodoItem(
   id = id.toString(),
   label = label,
   coinReward = goldCoins,
   done = progress >= 1f,
   habitId = if (linkedToCircle) id.toString() else null
)

// Maps Room habits to the same UI models used for API habits.

fun HabitEntity.toCircle(today: String) = HabitCircle(
   id = localId.toString(),
   label = label,
   icon = iconForKey(iconKey),
   progress = if (progressDate == today) progress else 0f,
   color = colorFromHex(colorHex)
)

fun HabitEntity.toTodo(linkedToCircle: Boolean, today: String) = TodoItem(
   id = localId.toString(),
   label = label,
   coinReward = goldCoins,
   done = (if (progressDate == today) progress else 0f) >= 1f,
   habitId = if (linkedToCircle) localId.toString() else null
)
@Composable
fun HomeScreen(
   onSettingsClick: () -> Unit ={},
   onSessionExpired: () -> Unit = {},
   onShopClick: () -> Unit = {}
) {
   val context = LocalContext.current
   val scope = rememberCoroutineScope()
   val density = LocalDensity.current

   // Profile and avatar
   // Uses the same ViewModel as the Profile screen, so saved changes appear here too.

   val profileViewModel: ProfileViewModel = viewModel(
      factory = ProfileViewModelFactory(context)
   )
   val profileUiState by profileViewModel.uiState.collectAsState()

   val avatarLayers = remember(profileUiState) {
      val profile = (profileUiState as? ProfileUiState.Ready)?.profile
      if (profile != null) {
         buildAvatarLayers(
            bodyRef = profile.bodyRef,
            hairRef = profile.hairRef,
            extensionRef = profile.extensionRef,
            eyeColor = profile.eyeColor,
            mouthRef = profile.mouthRef,
            lipstickRef = profile.lipstickRef
         )
      } else {
         emptyList()
      }
   }

   // Screen state
   // The coin balance lives in Room (wallet table) so the shop and this screen share it
   val walletRepo = remember { WalletRepository(context.applicationContext) }
   val userCoins by walletRepo.observeCoins().collectAsState(initial = 0)
   LaunchedEffect(Unit) { walletRepo.ensureWallet() }
   var showNewsBoard by remember { mutableStateOf(false) }
   val newsItems = remember {mutableStateListOf(*sampleNewList.toTypedArray())}
   // Quest card visibility and expansion
   var isQuestCardVisible by remember { mutableStateOf(true) }
   var isQuestCardExpanded by remember { mutableStateOf(false) }
   val questScroll = rememberScrollState()

   var showAddHabitDialog by remember { mutableStateOf(false) }
   var newHabitText by remember { mutableStateOf("") }
   var avatarRootPosition by remember { mutableStateOf(Offset.Zero) }
   var pieMenuCenter by remember { mutableStateOf<Offset?>(null) }

   var dailyRewardClaimed by remember { mutableStateOf(false) }
   var showRewardPopup by remember { mutableStateOf(false) }

   // Habits (offline-first)


   // Room supplies the habit list and updates the screen when the table changes, even offline.
   val habitRepo = remember { OfflineHabitRepository(context.applicationContext) }
   val habitEntities by habitRepo.observeHabits().collectAsState(initial = emptyList())
   val todayString = remember { LocalDate.now().toString() }

   // Furniture inventory (Room)
   // Same pattern as the habits: the table is observed as a Flow, so the room and the edit-mode
   // tray update whenever a row changes (Android Developers, 2026).
   val furnitureRepo = remember { FurnitureRepository(context.applicationContext) }
   val furniture by furnitureRepo.observeFurniture().collectAsState(initial = emptyList())
   var editMode by remember { mutableStateOf(false) }
   var selectedFurnitureId by remember { mutableStateOf<Int?>(null) }
   val furnitureScope = rememberCoroutineScope()
   LaunchedEffect(Unit) { furnitureRepo.ensureStarterFurniture() }

   // Back leaves edit mode instead of the screen
   BackHandler(enabled = editMode) {
      editMode = false
      selectedFurnitureId = null
   }

   val habitCircles = remember(habitEntities) {
      habitEntities.take(3).map { it.toCircle(todayString) }
   }

   val todos = remember(habitEntities) {
      habitEntities.mapIndexed { i, h -> h.toTodo(linkedToCircle = i < 3, today = todayString) }
   }





   // Tracks whether the last sync worked; habits still work locally when offline.
   var syncStatus by remember { mutableStateOf(SyncResult.OK) }

   // Uploads pending changes and fetches the latest habits.
   val syncHabits: () -> Unit = {
      scope.launch {
         val result = habitRepo.sync()
         syncStatus = result
         if (result == SyncResult.SESSION_EXPIRED) onSessionExpired()
      }
   }

   // Saves progress locally first, then syncs it with the API.
   val saveProgress: (String, Float) -> Unit = { habitId, progress ->
      scope.launch { habitRepo.setProgress(habitId.toInt(), progress) }
   }



   // Each interaction adds 25% progress instead of completing the habit straight away.
   val bumpHabitProgress: (Int) -> Unit = { index ->
      val habit = habitCircles[index]
      val newProg = (habit.progress + 0.25f).coerceAtMost(1f)
      saveProgress(habit.id, newProg)
   }

   // Adds 25% progress using the habit ID, including habits outside the three circles.
   val bumpHabitById: (String, Float) -> Unit = { habitId, currentProgress ->
      saveProgress(habitId, (currentProgress + 0.25f).coerceAtMost(1f))
   }

   // The first three habits are shown as the daily pinned habits.
   val pinnedHabits = todos.take(3)
   val otherHabits = todos.drop(3)
   val allPinnedDone = pinnedHabits.isNotEmpty() && pinnedHabits.all { it.done }

   LaunchedEffect(allPinnedDone) {
      if (allPinnedDone && !dailyRewardClaimed) {
         showRewardPopup = true
      }
   }


   // The avatar menu can show up to six habits.
   val pieMenuItems = habitEntities.take(6).map { entity ->
      val habit = entity.toCircle(todayString)
      PieMenuItem(
         id = habit.id,
         icon = habit.icon,
         label = habit.label,
         color = habit.color,
         onSelect = { bumpHabitById(habit.id, habit.progress) },
         progress = habit.progress
      )
   }


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

               // Shows the step greeting once per app launch.
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

               // Saves the date so the goal notification only appears once a day.
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

// Refreshes steps when the app opens or returns to the foreground.
   LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { refreshSteps() }


   // Syncs habits on launch and whenever the app returns to the foreground.
   LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { syncHabits() }

   val todayDateString = remember {
      SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())
   }

   Box(modifier = Modifier.fillMaxSize()) {

      // Room background
      Image(
         painter = painterResource(id = roomBackgrounds[0]),
         contentDescription = "Room background",
         modifier = Modifier.fillMaxWidth().height(700.dp).align(Alignment.TopStart),
         contentScale = ContentScale.FillHeight
      )

      // Furniture: drawn from the owned_furniture table (the blue lamp is now a row in it).
      // Drawn behind the avatar; draggable only in edit mode.
      FurnitureLayer(
         furniture = furniture,
         editMode = editMode,
         selectedId = selectedFurnitureId,
         onSelect = { selectedFurnitureId = it },
         onMove = { id, x, y -> furnitureScope.launch { furnitureRepo.move(id, x, y) } },
         onPickUp = { id -> furnitureScope.launch { furnitureRepo.pickUp(id) } }
      )

      // Edit mode hides everything except the room background and the furniture
      if (!editMode) {
         // Shadow under the avatar
         Box(
            modifier = Modifier
               .width(90.dp).height(16.dp)
               .align(Alignment.Center).offset(y = 232.dp)
               .clip(RoundedCornerShape(50))
               .background(Color.Black.copy(alpha = 0.18f))
         )


         // Uses the same avatar parts selected on the Profile screen.
         AvatarView(
            layers = avatarLayers,
            modifier = Modifier
               .fillMaxWidth()
               .height(500.dp)
               .align(Alignment.Center)
               .onGloballyPositioned { coords -> avatarRootPosition = coords.positionInRoot() }
               .pointerInput(Unit) {
                  detectTapGestures(onTap = { local ->
                     pieMenuCenter = avatarRootPosition + local
                  })
               }
         )

         // Keep the progress circles out of the way while the habit menu is open.
         if (pieMenuCenter == null) {
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
                     onClick = { bumpHabitProgress(index) }
                  )
               }
            }
         }

         // Top app bar
         JetpackTopNavbar(
            coins = userCoins,
            todoCount = todos.count { !it.done },
            todoPanelOpen = isQuestCardVisible,
            hasUnreadNews = newsItems.isNotEmpty(),
            newsBoardOpen = showNewsBoard,
            onTodoClick = {
               // Opens or hides the quest card.
               isQuestCardVisible = !isQuestCardVisible
            },
            onNewsClick = {
               showNewsBoard = !showNewsBoard
            },
            onProfileClick = { },
            onSettingsClick = onSettingsClick,
            onShopClick = onShopClick,
            onSyncClick = syncHabits,
            onEditModeClick = {
               // Close anything open on top of the room before editing
               pieMenuCenter = null
               showNewsBoard = false
               selectedFurnitureId = null
               editMode = true
            }
         )

         // Shows when the API is unavailable; habits are still saved locally.
         AnimatedVisibility(
            visible = syncStatus == SyncResult.OFFLINE,
            modifier = Modifier
               .align(Alignment.TopCenter)
               .statusBarsPadding()
               .padding(top = 62.dp),
            enter = expandVertically(),
            exit = shrinkVertically()
         ) {
            Row(
               modifier = Modifier
                  .clip(RoundedCornerShape(50))
                  .background(Color(0xFF37474F).copy(alpha = 0.88f))
                  .padding(horizontal = 12.dp, vertical = 5.dp),
               verticalAlignment = Alignment.CenterVertically
            ) {
               Icon(Icons.Default.CloudOff, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
               Spacer(modifier = Modifier.width(6.dp))
               Text("Offline · saved on this phone", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
         }

         // Quest and habits card
         AnimatedVisibility(
            visible = isQuestCardVisible,
            modifier = Modifier
               .align(Alignment.TopStart)
               .offset(x = 10.dp, y = 110.dp),
            enter = expandVertically(),
            exit = shrinkVertically()
         ) {
            Card(
               modifier = Modifier
                  .width(188.dp)
                  .heightIn(max = if (isQuestCardExpanded) 340.dp else Dp.Unspecified),
               shape = RoundedCornerShape(20.dp),
               colors = CardDefaults.cardColors(containerColor = GlassWhite),
               border = BorderStroke(1.dp, CardBorder),
               elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
               Column(modifier = Modifier.padding(12.dp)) {
                  // Card header and close button
                  Row(
                     modifier = Modifier.fillMaxWidth(),
                     verticalAlignment = Alignment.CenterVertically,
                     horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                     Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                           Text(
                              text = todayDateString,
                              fontSize = 10.sp,
                              fontWeight = FontWeight.Bold,
                              color = InkSoft,
                              maxLines = 1
                           )
                           Spacer(modifier = Modifier.width(6.dp))
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
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                           text = "Today's Quests",
                           fontSize = 15.sp,
                           fontWeight = FontWeight.ExtraBold,
                           color = Ink
                        )
                     }

                     // Close the quest card.
                     IconButton(
                        onClick = { isQuestCardVisible = false },
                        modifier = Modifier.size(22.dp)
                     ) {
                        Icon(
                           imageVector = Icons.Default.Close,
                           contentDescription = "Close Quests",
                           tint = InkSoft,
                           modifier = Modifier.size(16.dp)
                        )
                     }
                  }

                  // Shows progress for the three pinned habits.
                  Spacer(modifier = Modifier.height(8.dp))
                  Row(verticalAlignment = Alignment.CenterVertically) {
                     LinearProgressIndicator(
                        progress = {
                           if (pinnedHabits.isEmpty()) 0f
                           else pinnedHabits.count { it.done } / pinnedHabits.size.toFloat()
                        },
                        color = Color(0xFF66BB6A),
                        trackColor = Color(0xFFE8EAED),
                        modifier = Modifier
                           .weight(1f)
                           .height(6.dp)
                           .clip(RoundedCornerShape(50))
                     )
                     Spacer(modifier = Modifier.width(8.dp))
                     Text(
                        text = "${pinnedHabits.count { it.done }}/${pinnedHabits.size}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkSoft
                     )
                  }

                  Spacer(modifier = Modifier.height(6.dp))

                  // The list scrolls when the card is expanded.
                  Column(
                     modifier = Modifier
                        .weight(1f, fill = false)
                        .then(if (isQuestCardExpanded) Modifier.verticalScroll(questScroll) else Modifier)
                  ) {
                     if (pinnedHabits.isEmpty()) {
                        Text(
                           text = "No habits yet. Open the list below to add your first one.",
                           fontSize = 11.sp,
                           color = Color(0xFF90A4AE),
                           modifier = Modifier.padding(vertical = 6.dp)
                        )
                     }

                     // Show the three pinned habits.
                     pinnedHabits.forEach { item ->
                        TodoRow(item = item, onToggle = {
                           val updated = !item.done
                           saveProgress(item.id, if (updated) 1f else 0f)
                        })
                     }

                     // Show the remaining habits when expanded.
                     if (isQuestCardExpanded) {
                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                           text = "ALL HABITS",
                           fontSize = 10.sp,
                           fontWeight = FontWeight.Black,
                           color = Color(0xFF9E9E9E),
                           modifier = Modifier.padding(bottom = 2.dp)
                        )

                        otherHabits.forEach { item ->
                           TodoRow(item = item, onToggle = {
                              val updated = !item.done
                              saveProgress(item.id, if (updated) 1f else 0f)
                           })
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Opens the add-habit dialog.
                        Row(
                           modifier = Modifier
                              .fillMaxWidth()
                              .height(34.dp)
                              .clip(RoundedCornerShape(10.dp))
                              .background(Accent.copy(alpha = 0.08f))
                              .clickable { showAddHabitDialog = true },
                           verticalAlignment = Alignment.CenterVertically,
                           horizontalArrangement = Arrangement.Center
                        ) {
                           Icon(Icons.Default.Add, contentDescription = null, tint = Accent, modifier = Modifier.size(15.dp))
                           Spacer(modifier = Modifier.width(4.dp))
                           Text("Add habit", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Accent)
                        }
                     }
                  }

                  Spacer(modifier = Modifier.height(6.dp))

                  // Expands or collapses the habit list.
                  Row(
                     modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isQuestCardExpanded = !isQuestCardExpanded }
                        .padding(vertical = 2.dp),
                     verticalAlignment = Alignment.CenterVertically,
                     horizontalArrangement = Arrangement.Center
                  ) {
                     Text(
                        text = if (isQuestCardExpanded) "Show Less" else "View All Habits",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Accent
                     )
                     Icon(
                        imageVector = if (isQuestCardExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Accent,
                        modifier = Modifier.size(16.dp)
                     )
                  }
               }
            }
         }

         // Reward popup
         if (showRewardPopup) {
            Card(
               modifier = Modifier
                  .width(230.dp)
                  .align(Alignment.Center),
               shape = RoundedCornerShape(22.dp),
               colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
               border = BorderStroke(2.dp, Color(0xFFFBC02D)),
               elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
               Column(
                  modifier = Modifier.padding(16.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
               ) {
                  Text(
                     "✨ Quests Complete! ✨",
                     fontWeight = FontWeight.ExtraBold,
                     fontSize = 16.sp,
                     color = Color(0xFFF57F17)
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text("Completed today's 3 habits!", fontSize = 13.sp, color = Color(0xFF5D4037))
                  Spacer(modifier = Modifier.height(12.dp))
                  Button(
                     onClick = {
                        scope.launch { walletRepo.addCoins(500) }
                        dailyRewardClaimed = true
                        showRewardPopup = false
                     },
                     colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBC02D)),
                     shape = RoundedCornerShape(50)
                  ) {
                     Text(
                        "Claim 🪙 500",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                     )
                  }
               }
            }
         }

         // News board
         AnimatedVisibility(
            visible = showNewsBoard,
            modifier = Modifier.align(Alignment.TopEnd).offset(x = (-12).dp, y = 80.dp),
            enter = expandVertically(),
            exit = shrinkVertically()
         ) {
            NewsBoard(
               newsList = newsItems,
               onDismissItem = { id -> newsItems.removeAll { it.id == id } },
               onClearAll = { newsItems.clear() },
               onClose = { showNewsBoard = false })
         }


         // Avatar pie menu
         pieMenuCenter?.let { center ->
            AvatarPieMenu(
               center = center,
               items = pieMenuItems,
               onDismiss = { pieMenuCenter = null },
               onAddHabit = { showAddHabitDialog = true }
            )
         }
      } // end of !editMode

      // Edit mode: top bar with Done, and the tray of owned furniture at the bottom
      if (editMode) {
         EditModeTopBar(
            onDone = {
               editMode = false
               selectedFurnitureId = null
            },
            modifier = Modifier.align(Alignment.TopCenter)
         )
         FurnitureTray(
            furniture = furniture,
            onToggle = { item ->
               furnitureScope.launch {
                  if (item.placed) furnitureRepo.pickUp(item.localId) else furnitureRepo.place(item.localId)
               }
               if (item.placed && selectedFurnitureId == item.localId) selectedFurnitureId = null
            },
            modifier = Modifier.align(Alignment.BottomCenter)
         )
      }
   }

   // Add habit dialog
   if (showAddHabitDialog) {
      AlertDialog(
         onDismissRequest = { showAddHabitDialog = false; newHabitText = "" },
         shape = RoundedCornerShape(24.dp),
         containerColor = Color.White,
         title = { Text("New habit", fontWeight = FontWeight.ExtraBold, color = Ink) },
         text = {
            OutlinedTextField(
               value = newHabitText,
               onValueChange = { newHabitText = it },
               placeholder = { Text("e.g. Read for 10 minutes") },
               singleLine = true,
               shape = RoundedCornerShape(14.dp),
               keyboardOptions = KeyboardOptions.Default
            )
         },
         confirmButton = {
            TextButton(onClick = {
               val label = newHabitText.trim()
               if (label.isNotBlank()) {
                  // Saves the new habit locally first, then syncs it with the API.
                  scope.launch {
                     habitRepo.addHabit(label)
                     syncHabits()
                  }
               }
               newHabitText = ""
               showAddHabitDialog = false
            }) { Text("Add", fontWeight = FontWeight.Bold) }
         },
         dismissButton = {
            TextButton(onClick = { showAddHabitDialog = false; newHabitText = "" }) { Text("Cancel") }
         }
      )
   }
}

// Habit circle with animated liquid progress.
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
            .shadow(6.dp, CircleShape)
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
         colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f))
      ) {
         Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333),
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
         )
      }
   }
}

// Compact to-do row
@Composable
fun TodoRow(item: TodoItem, onToggle: () -> Unit) {
   Row(
      modifier = Modifier
         .fillMaxWidth()
         .height(34.dp)
         .clip(RoundedCornerShape(10.dp))
         .clickable { onToggle() },
      verticalAlignment = Alignment.CenterVertically
   ) {
      Box(
         modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(if (item.done) Color(0xFF66BB6A) else Color.Transparent)
            .border(1.5.dp, if (item.done) Color(0xFF66BB6A) else Color(0xFFB0BEC5), CircleShape),
         contentAlignment = Alignment.Center
      ) {
         if (item.done) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
         }
      }

      Spacer(modifier = Modifier.width(8.dp))

      Text(
         text = item.label,
         modifier = Modifier.weight(1f),
         fontSize = 13.sp,
         maxLines = 1,
         color = if (item.done) Color(0xFF9E9E9E) else Color(0xFF333333),
         textDecoration = if (item.done) TextDecoration.LineThrough else TextDecoration.None
      )

      Text(
         text = "🪙${item.coinReward}",
         fontSize = 11.sp,
         fontWeight = FontWeight.SemiBold,
         color = if (item.done) Color(0xFFCCCCCC) else InkSoft
      )
   }
}

// News board
@Composable
fun NewsBoard(
   newsList: List<NavigationItem>,
   onDismissItem: (String)-> Unit,
   onClearAll: () -> Unit,
   onClose: () -> Unit) {
   Card(
      modifier = Modifier.width(240.dp)
         .offset(x = 8.dp, y = 20.dp),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = GlassWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
      border = BorderStroke(1.dp, CardBorder),
   ) {
      Column(modifier = Modifier.padding(10.dp)
      ) {

         Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
         ) {
            // Clear all notifications.
            IconButton(onClick = onClearAll, modifier = Modifier.size(24.dp)) {
               Icon(
                  Icons.Default.DeleteForever,
                  contentDescription = "Delete All",
                  tint = Color(0xFF64748B),
                  modifier = Modifier.size(18.dp)
               )
            }
            // News board title.
            Text("METRICS NEWS", fontSize = 11.sp, fontWeight= FontWeight.ExtraBold, letterSpacing = 1.sp, color = Color(0xFF334155))
            // Close the news board.
            IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
               Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
            }
         }

         Spacer(modifier = Modifier.height(8.dp))

         if (newsList.isEmpty()) {
            Text(
               text = "You're all caught up",
               fontSize = 12.sp,
               color = Color(0xFF90A4AE),
               modifier = Modifier.padding(vertical = 8.dp)
            )
         }
         else {
            LazyColumn(
               modifier = Modifier
                  .fillMaxWidth()
                  .height(150.dp),// Fixed height for the notification list.
               verticalArrangement = Arrangement.spacedBy(8.dp)
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

         Spacer(modifier = Modifier.height(4.dp))

      }
   }
}

@Composable
fun  NotificationCardItem(
   item: NavigationItem,
   onDismiss: () -> Unit
) {
   val cardColor = when (item.type) {
      NotificationType.ALERT -> Color(0xFF389ED0) // Blue for alerts.
      NotificationType.REWARD -> Color(0xFFFFA726) // Orange for rewards.
      NotificationType.MILESTONE -> Color(0xFF8B5CF6)
      NotificationType.Social -> Color(0xFF43A047)
      NotificationType.STEPS -> Color(0xFF26A69A) // Teal for step updates.
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
         shape = RoundedCornerShape(14.dp),
         colors = CardDefaults.cardColors(containerColor = cardColor)
      ) {

         Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
         ) {
            Box(
               modifier = Modifier.size(34.dp)
                  .clip(RoundedCornerShape(11.dp))
                  .background(Color.White.copy(0.25f)),
               contentAlignment = Alignment.Center
            ) {
               Icon(
                  icon,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(20.dp)
               )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f).padding(end = 14.dp)) {
               Text(
                  text = item.timestamp,
                  fontSize = 9.sp,
                  color = Color.White.copy(alpha = 0.8f)

               )
               Spacer(modifier = Modifier.height(1.dp))

               Text(
                  text = item.title,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
               )
               Spacer(modifier = Modifier.height(1.dp))

               Text(
                  text = item.message,
                  fontSize = 11.sp,
                  color = Color.White
               )
            }
         }
      }
      // Keeps the dismiss button inside the card so it is not clipped.
      Box(
         modifier = Modifier.align(Alignment.TopEnd)
            .padding(6.dp)
            .size(20.dp)
            .clip(CircleShape)
            .background(Color.White)
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
         .padding(horizontal = 6.dp, vertical = 2.dp),
      verticalAlignment = Alignment.CenterVertically
   ) {
      Icon(
         imageVector = Icons.Default.DirectionsRun,
         contentDescription = "Steps",
         tint = Color(0xFFFFA726),
         modifier = Modifier.size(11.dp)
      )
      Spacer(modifier = Modifier.width(3.dp))
      Text(
         text = label,
         fontSize = 10.sp,
         fontWeight = FontWeight.Bold,
         color = InkSoft,
         maxLines = 1
      )
   }
}


// Avatar pie menu


// Shows each habit in a small pill beside the avatar. Selecting one adds 25% progress.
@Composable
fun AvatarPieMenu(
   center: Offset,
   items: List<PieMenuItem>,
   onDismiss: () -> Unit,
   onAddHabit: () -> Unit = {}
) {
   val density = LocalDensity.current
   val pillWidth = 164.dp
   val pillHeight = 40.dp
   val rowGap = 14.dp
   val columnGap = 14.dp

   // Show an Add Habit option when there are no habits yet.
   val pillItems = if (items.isEmpty()) {
      listOf(
         PieMenuItem(
            id = "add",
            icon = Icons.Default.Add,
            label = "Add a habit",
            color = Accent,
            onSelect = onAddHabit
         )
      )
   } else {
      items
   }

   BoxWithConstraints(
      modifier = Modifier
         .fillMaxSize()
         .background(Color.Black.copy(alpha = 0.22f))
         .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onDismiss
         )
   ) {
      val screenW = maxWidth
      val screenH = maxHeight
      val cy = with(density) { center.y.toDp() }
      val rows = (pillItems.size + 1) / 2
      val totalHeight = pillHeight * rows + rowGap * (rows - 1)
      val top = (cy - totalHeight / 2).coerceIn(
         90.dp,
         maxOf(90.dp, screenH - totalHeight - 24.dp)
      )
      val totalWidth = pillWidth * 2 + columnGap
      val leftX = ((screenW - totalWidth) / 2).coerceAtLeast(8.dp)
      val rightX = (leftX + pillWidth + columnGap).coerceAtMost(screenW - pillWidth - 8.dp)

      pillItems.forEachIndexed { index, item ->
         val column = index % 2
         val row = index / 2
         val x = if (column == 0) leftX else rightX
         val y = top + (pillHeight + rowGap) * row

         Row(
            modifier = Modifier
               .offset(x = x, y = y)
               .width(pillWidth),
            horizontalArrangement = Arrangement.Center
         ) {
            AvatarActionPill(
               item = item,
               index = index,
               onClick = {
                  item.onSelect()
                  onDismiss()
               }
            )
         }
      }
   }
}

// Displays a habit's icon, name and progress.
@Composable
private fun AvatarActionPill(item: PieMenuItem, index: Int, onClick: () -> Unit) {
   // Adds a short delay so the pills appear one after another.
   var shown by remember { mutableStateOf(false) }
   LaunchedEffect(Unit) {
      delay(index * 60L)
      shown = true
   }
   val appear by animateFloatAsState(
      targetValue = if (shown) 1f else 0f,
      animationSpec = tween(220),
      label = "pillAppear"
   )

   // Shrinks the pill slightly while it is pressed.
   val interaction = remember { MutableInteractionSource() }
   val pressed by interaction.collectIsPressedAsState()
   val pressScale by animateFloatAsState(
      targetValue = if (pressed) 0.95f else 1f,
      animationSpec = tween(90),
      label = "pillPress"
   )

   val shape = RoundedCornerShape(50)
   Row(
      modifier = Modifier
         .graphicsLayer {
            val s = (0.85f + 0.15f * appear) * pressScale
            scaleX = s
            scaleY = s
            alpha = appear
         }
         .height(40.dp)
         .shadow(6.dp, shape)
         .clip(shape)
         .background(androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color.White, Color(0xFFE6EEF9))))
         .border(1.dp, Color.White, shape)
         .clickable(interactionSource = interaction, indication = null, onClick = onClick)
         .padding(start = 6.dp, end = 14.dp),
      verticalAlignment = Alignment.CenterVertically
   ) {
      Box(
         modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(item.color.copy(alpha = 0.18f)),
         contentAlignment = Alignment.Center
      ) {
         Icon(item.icon, contentDescription = null, tint = item.color, modifier = Modifier.size(17.dp))
      }

      Spacer(modifier = Modifier.width(8.dp))

      Text(
         text = item.label,
         fontSize = 13.sp,
         fontWeight = FontWeight.SemiBold,
         color = Color(0xFF2B5C9E),
         maxLines = 1,
         overflow = TextOverflow.Ellipsis,
         modifier = Modifier.widthIn(max = 96.dp)
      )

      if (item.progress > 0f) {
         Spacer(modifier = Modifier.width(6.dp))
         Text(
            text = "${(item.progress * 100).toInt()}%",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF7C93B0)
         )
      }
   }
}

// Top navigation bar
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
   onSettingsClick: () -> Unit,
   onSyncClick: () -> Unit = {},
   onEditModeClick: () -> Unit = {},
   onShopClick: () -> Unit = {}
) {
   var showMoreMenu by remember { mutableStateOf(false) }


   Row(
      modifier = Modifier
         .fillMaxWidth()
         .statusBarsPadding()
         .padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
   ) {

      // Opens or hides the habit drawer.
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

      // Right-side menu and shortcuts.
      Row(
         modifier = Modifier
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(50))
            .background(NavbarSurface)
            .border(BorderStroke(1.5.dp, NavbarBorder), RoundedCornerShape(50)),
         verticalAlignment = Alignment.CenterVertically
      ) {
         Box {
            NavStripIcon(
               onClick = { showMoreMenu = true },
               trailingPadding = 6.dp
            ) {
               Icon(
                  Icons.Default.MoreVert,
                  contentDescription = "More",
                  tint = NavbarIcon,
                  modifier = Modifier.size(19.dp)
               )
            }

            // Styles the dropdown to match the rest of the navigation bar.
            DropdownMenu(
               expanded = showMoreMenu,
               onDismissRequest = { showMoreMenu = false },
               offset = DpOffset(x = 0.dp, y = 6.dp),
               shape = RoundedCornerShape(18.dp),
               containerColor = Color.White.copy(alpha = 0.97f),
               tonalElevation = 0.dp,
               shadowElevation = 12.dp,
               border = BorderStroke(1.dp, NavbarBorder.copy(alpha = 0.6f))
            ) {
               DropdownMenuItem(
                  text = {
                     Text(
                        text = "Sync habits",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Ink
                     )
                  },
                  leadingIcon = {
                     Icon(Icons.Default.Sync, contentDescription = null, tint = NavbarIcon, modifier = Modifier.size(20.dp))
                  },
                  onClick = {
                     showMoreMenu = false
                     onSyncClick()
                  }
               )
               HorizontalDivider(
                  modifier = Modifier.padding(horizontal = 14.dp),
                  thickness = 1.dp,
                  color = NavbarDivider
               )
               DropdownMenuItem(
                  text = {
                     Text(
                        text = "Shop",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Ink
                     )
                  },
                  leadingIcon = {
                     Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = NavbarIcon, modifier = Modifier.size(20.dp))
                  },
                  onClick = {
                     showMoreMenu = false
                     onShopClick()
                  }
               )
               HorizontalDivider(
                  modifier = Modifier.padding(horizontal = 14.dp),
                  thickness = 1.dp,
                  color = NavbarDivider
               )
               DropdownMenuItem(
                  text = {
                     Text(
                        text = "Edit mode",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Ink
                     )
                  },
                  leadingIcon = {
                     Icon(Icons.Default.Edit, contentDescription = null, tint = NavbarIcon, modifier = Modifier.size(20.dp))
                  },
                  onClick = {
                     showMoreMenu = false
                     onEditModeClick()
                  }
               )
               HorizontalDivider(
                  modifier = Modifier.padding(horizontal = 14.dp),
                  thickness = 1.dp,
                  color = NavbarDivider
               )
               DropdownMenuItem(
                  text = {
                     Text(
                        text = "Settings",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Ink
                     )
                  },
                  leadingIcon = {
                     Icon(Icons.Default.Settings, contentDescription = null, tint = NavbarIcon, modifier = Modifier.size(20.dp))
                  },
                  onClick = {
                     showMoreMenu = false
                     onSettingsClick()
                  }
               )
            }
         }

         NavDivider()

         // Tapping the coins opens the shop
         Row(
            modifier = Modifier
               .clickable { onShopClick() }
               .padding(horizontal = 10.dp, vertical = 8.dp),
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


         // Shows the unread indicator on the news icon.
         Box {
            NavStripIcon(onClick = onNewsClick, leadingPadding = 12.dp) {
               Icon(Icons.Default.Article, contentDescription = "News", tint = NavbarIcon, modifier = Modifier.size(17.dp))
            }
            if (hasUnreadNews && !newsBoardOpen) {
               Box(
                  modifier = Modifier
                     .size(9.dp)
                     .align(Alignment.TopEnd)
                     .offset(x = (-6).dp, y = 7.dp)
                     .clip(CircleShape)
                     .background(Color(0xFFE53935))
                     .border(1.5.dp, NavbarSurface, CircleShape)
               )
            }
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

Teergarden, C., 2023. Mastering Lazy Lists in Jetpack Compose with Data Classes and MVI. (Version 10.0) [Source Code]. Available at: < https://proandroiddev.com/mastering-lazy-lists-in-jetpack-compose-with-data-classes-and-mvi-da240800c6e9 > [Accessed 21 September 2026]
Kavi, R., 2026. Mastering High-Performance Custom Canvas animations in jetpack compose: building a premium circular progress indicator. (Version 10.0) [Source Code]. Available at: < https://raghunandan2005.medium.com/mastering-high-performance-custom-canvas-animations-in-jetpack-compose-building-a-premium-circular-b9f9e465b118 > [Accessed 21 September 2026]
Malla, M., 2023. Jetsurvey. (Version 10.0) [Source Code]. Available at: < https://github.com/ManasMalla/Jetsurvey > [Accessed 21 September 2026]
Easy Tuto, 2024. Simple to do App jetpackcompose androidtutorial. (Version 10.0) [Source Code]. Available at: < https://youtu.be/P3xQdINdrWY?si=0Z52xSOqN96XhODE > [Accessed 21 September 2026]
Samsung Developer., 2025. Build a health app with steps from Samsung Health and its connected wearables. (Version 10.0) [Source Code]. Available at: < https://youtu.be/kEe0bRqhx-g?si=tn83CXqncnYI_ska > [Accessed 21 September 2026]
Gaur, A., 2025. Room Database in Android. (Version 10.0) [Source Code]. Available at: < https://medium.com/@anandgaur2207/room-database-in-android-d5f279d4648a > [Accessed 23 October 2026]
Android Developers, 2026. Save data in a local database using Room. (Version 10.0) [Source Code]. Available at: < https://developer.android.com/training/data-storage/room > [Accessed 23 October 2026]
Android Developers, 2026. Write asynchronous DAO queries. (Version 10.0) [Source Code]. Available at: < https://developer.android.com/training/data-storage/room/async-queries > [Accessed 23 October 2026]
 */