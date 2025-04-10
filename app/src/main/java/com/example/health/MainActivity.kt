package com.example.health

import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Icon
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.Scaffold
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.shape.RoundedCornerShape
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.example.health.ui.theme.HealthTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch
import androidx.compose.animation.core.*


data class DailyStatus(
    val sleep: String,
    val exercise: String,
    val mental: String,
    val diet: String
)

data class PersonalData(
    val sleepGoal: String = "Not Set",
    val exerciseGoal: String = "Not Set",
    val dietGoal: String = "Not Set",
    val mentalGoal: String = "Not Set",
    val nickname: String = "Guest",
    val bio: String = "This is a personal profile"
)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthTheme {
                MainApp()
            }
        }
    }
}

sealed class Page {
    data object Main : Page()
    data object MentalSleep : Page()
    data object SportDiet : Page()
    data object Mine : Page()
}

@Composable
fun MainApp() {
    var currentPage by remember { mutableStateOf<Page>(Page.Main)}


    when (currentPage) {
        is Page.Main -> MainScreen(
            onHomeClick = { currentPage = Page.Main },
            onMentalSleepClick = { currentPage = Page.MentalSleep },
            onSportDietClick = { currentPage = Page.SportDiet },
            onMineClick = { currentPage = Page.Mine },
            currentPage = currentPage
        )

        is Page.MentalSleep -> MainScreen(
            onHomeClick = { currentPage = Page.Main },
            onMentalSleepClick = { currentPage = Page.MentalSleep },
            onSportDietClick = { currentPage = Page.SportDiet },
            onMineClick = { currentPage = Page.Mine },
            currentPage = currentPage
                           )
               is Page.SportDiet -> MainScreen(
                   onHomeClick = { currentPage = Page.Main },
                   onMentalSleepClick = { currentPage = Page.MentalSleep },
                   onSportDietClick = { currentPage = Page.SportDiet },
                   onMineClick = { currentPage = Page.Mine },
                   currentPage = currentPage
                           )
        is Page.Mine -> MainScreen(
            onHomeClick = { currentPage = Page.Main },
            onMentalSleepClick = { currentPage = Page.MentalSleep },
            onSportDietClick = { currentPage = Page.SportDiet },
            onMineClick = { currentPage = Page.Mine },
            currentPage = currentPage
                           )
    }
    }



@Composable
fun MainScreen(
    onHomeClick: () -> Unit,
    onMentalSleepClick: () -> Unit,
    onSportDietClick: () -> Unit,
    onMineClick: () -> Unit,
    currentPage: Page,
        )
        {

            val context = LocalContext.current
            val mentalData = getSevenDayData(context)
            val sportData = getSevenDaySportData(context)

            val avgSleepHours = mentalData.filter { it.sleepLevel > 0 }
                .map { it.sleepLevel.toFloat() * 1.5f }
                .average()
                .let { if (it.isNaN()) 0.0 else it }
            val sleepDisplay = if (avgSleepHours > 0) {
                "%.1f".format(avgSleepHours) + "h"
            } else {
                "Not Record"
            }

            val avgExerciseMinutes = sportData.filter { it.exerciseLevel > 0 }
                .map { when (it.exerciseLevel) {
                    1 -> 15
                    2 -> 45
                    3 -> 75
                    4 -> 105
                    5 -> 150
                    else -> 0
                }}
                .average()
                .toInt()
            val exerciseDisplay = if (avgExerciseMinutes > 0) "${avgExerciseMinutes}min" else "未记录"

            val avgMental = mentalData.filter { it.mood > 0 }
                .map { it.mood }
                .average()
                .let { if (it.isNaN()) 0.0 else it }
            val mentalDisplay = when {
                avgMental >= 4.5 -> "Excellent"
                avgMental >= 3.0 -> "Good"
                avgMental >= 1.5 -> "Neutral"
                else -> "Poor"
            } ?: "Not Record"

            val avgDiet = sportData.filter { it.diet > 0 }
                .map { it.diet }
                .average()
                .let { if (it.isNaN()) 0.0 else it }
            val dietDisplay = when {
                avgDiet >= 4.5 -> "Excellent"
                avgDiet >= 3.0 -> "Balanced"
                avgDiet >= 1.5 -> "Poor"
                else -> "Not Record"
            } ?: "Not Record"

            val dailyStatus = remember {
                DailyStatus(
                    sleep = sleepDisplay,
                    exercise = exerciseDisplay,
                    mental = mentalDisplay,
                    diet = dietDisplay
                )
            }

    var selectedItem by remember { mutableIntStateOf(0)}

            LaunchedEffect(currentPage) {
                       when (currentPage) {
                               Page.Main -> selectedItem = 0
                               Page.MentalSleep -> selectedItem = 1
                              Page.SportDiet -> selectedItem = 2
                               Page.Mine -> selectedItem = 3
                           }
                   }

    val gradientColors = listOf(
        colorResource(id = R.color.gradient_start_color),
        colorResource(id = R.color.gradient_end_color)
    )
    val gradientBrush = Brush.verticalGradient(colors = gradientColors)
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(painterResource(id = R.drawable.ic_home), contentDescription = null) },
                    label = { Text(stringResource(id = R.string.home)) },
                    selected = selectedItem == 0,
                    onClick = { selectedItem = 0
                        onHomeClick()}
                )
                NavigationBarItem(
                    icon = { Icon(painterResource(id = R.drawable.ic_mental_sleep), contentDescription = null) },
                    label = { Text(stringResource(id = R.string.mental_sleep)) },
                    selected = selectedItem == 1,
                    onClick = { selectedItem = 1
                        onMentalSleepClick()}
                )
                NavigationBarItem(
                    icon = { Icon(painterResource(id = R.drawable.ic_sport_diet), contentDescription = null) },
                    label = { Text(stringResource(id = R.string.sport_diet)) },
                    selected = selectedItem == 2,
                    onClick = { selectedItem = 2
                        onSportDietClick()}
                )
                NavigationBarItem(
                    icon = { Icon(painterResource(id = R.drawable.ic_mine), contentDescription = null) },
                    label = { Text(stringResource(id = R.string.mine)) },
                    selected = selectedItem == 3,
                    onClick = { selectedItem = 3
                        onMineClick()}
                )
            }
        }
    ) { innerPadding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            )
            {
                when (currentPage) {
                    is Page.Main -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(11.dp),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = "Let’s get started !",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                                    .padding(top = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            Box(modifier = Modifier.height(3.dp))


                            DateList()

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .padding(16.dp)
                            )  {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(400.dp)
                                        .padding(16.dp)
                                ) {
                                    Canvas(
                                        modifier = Modifier
                                            .size(300.dp)
                                            .align(Alignment.Center)
                                    ) {
                                        drawCircle(
                                            color =  Color.LightGray.copy(alpha = 0.3f),
                                            radius = size.minDimension / 2,
                                            style = Stroke(width = 5.dp.toPx())
                                        )
                                    }


                                    val circleRadius = 60.dp
                                    val circlePositions = listOf(
                                        Offset(-210f, -200f),
                                        Offset(190f, -180f),
                                        Offset(-180f, 190f),
                                        Offset(200f, 210f)
                                    )
                                    val colors = listOf(
                                        colorResource(id = R.color.data_container_sleep),
                                        colorResource(id = R.color.data_container_exercise),
                                        colorResource(id = R.color.data_container_mental_health),
                                        colorResource(id = R.color.data_container_diet)
                                    )
                                    val titles = listOf("Sleep", "Sport", "Mental", "Diet")
                                    circlePositions.forEachIndexed { index, position ->
                                        val infiniteTransition = rememberInfiniteTransition(label = "circle_animation_$index")
                                        val floatAnimation by infiniteTransition.animateFloat(
                                            initialValue = 0f,
                                            targetValue = 20f,
                                            animationSpec = infiniteRepeatable(
                                                animation = tween(
                                                    durationMillis = (1800 + index * 200),
                                                    easing = LinearEasing
                                                ),
                                                repeatMode = RepeatMode.Reverse
                                            )
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(circleRadius * 2)
                                                .offset {
                                                    androidx.compose.ui.unit.IntOffset(
                                                        position.x.toInt(),
                                                        (position.y + floatAnimation).toInt()
                                                    )
                                                }
                                                .align(Alignment.Center)
                                                .clip(androidx.compose.foundation.shape.CircleShape)
                                                .shadow(
                                                    elevation = 12.dp,
                                                    shape = CircleShape,
                                                    ambientColor = Color.Black.copy(alpha = 0.2f),
                                                    spotColor = Color.Black.copy(alpha = 0.1f)
                                                )
                                                .background(
                                                    Brush.verticalGradient(
                                                        colors = listOf(
                                                            colors[index].copy(alpha = 1f),
                                                            colors[index].copy(alpha = 0.6f)
                                                        )
                                                    )
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = titles[index],
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = when (index) {
                                                        0 -> dailyStatus.sleep
                                                        1 -> dailyStatus.exercise
                                                        2 -> dailyStatus.mental
                                                        else -> dailyStatus.diet
                                                    },
                                                    fontSize = 22.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Box(modifier = Modifier.height(8.dp))


                            ButtonArea(
                                        onMentalSleepClick = {
                                    selectedItem = 1
                                    onMentalSleepClick()
                                },
                                onSportDietClick = {
                                    selectedItem = 2
                                    onSportDietClick()
                                }
                            )
                        }
                    }

                    is Page.MentalSleep -> {
                                   MentalSleepPage()
                               }
                           is Page.SportDiet -> {
                                   SportDietPage()
                               }

                    is Page.Mine -> {

                        val prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
                        val personalData = PersonalData(
                            sleepGoal = prefs.getString("sleep_goal", "Not set") ?: "Not set",
                            exerciseGoal = prefs.getString("exercise_goal", "Not set") ?: "Not set",
                            dietGoal = prefs.getString("diet_goal", "Not set") ?: "Not set",
                            mentalGoal = prefs.getString("mental_goal", "Not set") ?: "Not set",
                            nickname = prefs.getString("nickname", "Guest") ?: "Guest",
                            bio = prefs.getString("bio", "This is a personal profile") ?: "This is a personal profile"
                        )
                        PersonalPage(personalData)
                    }
                }
            }
    }

}




@Composable
fun DateList() {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
    val displayDateFormat = SimpleDateFormat("dd", Locale.getDefault())
    val today = Date()
    val dates = mutableListOf<String>()
    val displayDates = mutableListOf<String>()


    for (i in 6 downTo 0) {
        calendar.time = today
        calendar.add(Calendar.DAY_OF_YEAR, -i)
        dates.add(dateFormat.format(calendar.time))
        displayDates.add(displayDateFormat.format(calendar.time))
    }

    val dailyRecords = dates.map { date ->
        val mentalPrefs = context.getSharedPreferences("mental_data", Context.MODE_PRIVATE)
        val hasMental = mentalPrefs.getInt("mood_$date", 0) > 0
        val hasSleep = mentalPrefs.getInt("sleep_level_$date", 0) > 0
        val sportPrefs = context.getSharedPreferences("sport_diet_data", Context.MODE_PRIVATE)
        val hasExercise = sportPrefs.getInt("exercise_level_$date", 0) > 0
        val hasDiet = sportPrefs.getInt("diet_$date", 0) > 0
        val recordCount = listOf(hasMental, hasSleep, hasExercise, hasDiet).count { it }

        when {
            recordCount == 4 -> Pair("√", Color(0xFF4CAF50).copy(alpha = 0.6f))
            recordCount > 0 -> Pair("?", Color(0xFFFFA500).copy(alpha = 0.8f))
            else -> Pair("×", Color(0xFFF44336).copy(alpha = 0.6f))
        }
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        displayDates.forEachIndexed { index, displayDate ->
            val isToday = index == dates.size - 1
            val backgroundColor = if (isToday) colorResource(id = R.color.date_background_today) else colorResource(id = R.color.date_background_normal).copy(alpha = 0.5f)
            val (statusSymbol, statusColor) = dailyRecords[index]

            Box(
                modifier = Modifier
                    .width(50.dp)
                    .height(70.dp)
                    .shadow(
                        elevation = 9.dp,
                        shape = RoundedCornerShape(12.dp),
                        ambientColor = Color.Black.copy(alpha = 0.1f),
                        spotColor = Color.Black.copy(alpha = 0.05f)
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .background(backgroundColor)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = displayDate,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isToday) Color.White else Color.Black
                    )
                    Text(
                        text = statusSymbol,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }
        }
    }
}

@Composable
fun ButtonArea(        onMentalSleepClick: () -> Unit,
                       onSportDietClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .wrapContentWidth(Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        FeatureButton(
            text = "Mental & Sleep >",
            onClick = onMentalSleepClick,
            colorResId = R.color.data_container_mental_health,
        )
        FeatureButton(
            text = "Sport & Diet >",
            onClick = onSportDietClick,
            colorResId = R.color.data_container_exercise
        )
    }
}

@Composable
fun FeatureButton(text: String, onClick: () -> Unit, colorResId: Int) {

    val buttonHeight = 65.dp
    val customButtonColors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = colorResource(id = colorResId),
        contentColor = Color.Black,
        disabledContainerColor = Color.LightGray,
        disabledContentColor = Color.DarkGray
    )
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(250.dp)
            .height(80.dp)
            .padding(horizontal = 20.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
            .shadow(
                elevation = 9.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color.Black.copy(alpha = 1f),
                spotColor = Color.Black.copy(alpha = 0.5f)
            ),
                shape = RoundedCornerShape(12.dp),
            colors = customButtonColors
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, fontSize = 20.sp)
        }
    }
}

@Composable
fun PersonalPage(personalData: PersonalData) {
    var personalDataState by remember {
        mutableStateOf(personalData)
    }
    var showSleepGoalDialog by remember { mutableStateOf(false) }
    var showExerciseGoalDialog by remember { mutableStateOf(false) }
    var showDietGoalDialog by remember { mutableStateOf(false) }
    var showMentalGoalDialog by remember { mutableStateOf(false) }

    val sleepGoals = listOf("7-8 hours of sleep", "Improve sleep quality", "Maintain regular sleep schedule")
    val exerciseGoals = listOf("30 minutes of daily exercise", "Increase exercise intensity", "Try new exercise types")
    val dietGoals = listOf("Balanced diet", "Control calorie intake", "Increase vegetable and fruit intake")
    val mentalGoals = listOf("Maintain emotional well-being", "Reduce stress", "10 minutes of daily meditation")

    val context = LocalContext.current
    var nickname by remember { mutableStateOf(personalData.nickname) }
    var bio by remember { mutableStateOf(personalData.bio) }
    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var originalNickname by remember { mutableStateOf(nickname) }
    var originalBio by remember { mutableStateOf(bio) }

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        nickname = prefs.getString("nickname", "Guest") ?: "Guest"
        bio = prefs.getString("bio", "This is a personal profile") ?: "This is a personal profile"
        originalNickname = nickname
        originalBio = bio
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(modifier = Modifier.height(25.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.personal_card_background))
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .clickable {
                    showDialog = true
                    originalNickname = nickname
                    originalBio = bio
                },
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile_image),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = nickname, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(text = bio, fontSize = 14.sp, color = Color.Gray)
                }
            }
        }

        Box(modifier = Modifier.height(35.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "My Goals",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GoalCard(
                    title = "Sleep",
                    content = personalDataState.sleepGoal,
                    color = colorResource(id = R.color.data_container_sleep),
                    onClick = { showSleepGoalDialog = true }
                )
                GoalCard(
                    title = "Exercise",
                    content = personalDataState.exerciseGoal,
                    color = colorResource(id = R.color.data_container_exercise),
                    onClick = { showExerciseGoalDialog = true }
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GoalCard(
                    title = "Diet",
                    content = personalDataState.dietGoal,
                    color = colorResource(id = R.color.data_container_diet),
                    onClick = { showDietGoalDialog = true }
                )
                GoalCard(
                    title = "Mental",
                    content = personalDataState.mentalGoal,
                    color = colorResource(id = R.color.data_container_mental_health),
                    onClick = { showMentalGoalDialog = true }
                )
            }
        }

        if (showSleepGoalDialog) {
            GoalSettingDialog(
                title = "Set Sleep Goal",
                options = sleepGoals,
                currentGoal = personalDataState.sleepGoal,
                onDismiss = { showSleepGoalDialog = false },
                onSave = { newGoal ->
                    scope.launch {
                        saveGoal(context, "sleep_goal", newGoal)
                        personalDataState = personalDataState.copy(sleepGoal = newGoal)
                    }
                    showSleepGoalDialog = false
                }
            )
        }
        if (showExerciseGoalDialog) {
            GoalSettingDialog(
                title = "Set Exercise Goal",
                options = exerciseGoals,
                currentGoal = personalDataState.exerciseGoal,
                onDismiss = { showExerciseGoalDialog = false },
                onSave = { newGoal ->
                    scope.launch {
                        saveGoal(context, "exercise_goal", newGoal)
                        personalDataState = personalDataState.copy(exerciseGoal = newGoal)
                    }
                    showExerciseGoalDialog = false
                }
            )
        }

        if (showDietGoalDialog) {
            GoalSettingDialog(
                title = "Set Diet Goal",
                options = dietGoals,
                currentGoal = personalDataState.dietGoal,
                onDismiss = { showDietGoalDialog = false },
                onSave = { newGoal ->
                    scope.launch {
                        saveGoal(context, "diet_goal", newGoal)
                        personalDataState = personalDataState.copy(dietGoal = newGoal)
                    }
                    showDietGoalDialog = false
                }
            )
        }

        if (showMentalGoalDialog) {
            GoalSettingDialog(
                title = "Set Mental Goal",
                options = mentalGoals,
                currentGoal = personalDataState.mentalGoal,
                onDismiss = { showMentalGoalDialog = false },
                onSave = { newGoal ->
                    scope.launch {
                        saveGoal(context, "mental_goal", newGoal)
                        personalDataState = personalDataState.copy(mentalGoal = newGoal)
                    }
                    showMentalGoalDialog = false
                }
            )
        }

    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Edit Profile") },
            text = {
                Column {
                    TextField(
                        value = nickname,
                        onValueChange = { nickname = it },
                        label = { Text("Nickname") }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Bio") },
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            val prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
                            with(prefs.edit()) {
                                putString("nickname", nickname)
                                putString("bio", bio)
                                apply()
                            }
                        }
                        showDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false
                        nickname = originalNickname
                                                  bio = originalBio}
                ) {
                    Text("Cancel")
                }
            }
        )
    }

}

@Composable
fun GoalCard(
    title: String,
    content: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(120.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = content,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun GoalSettingDialog(
    title: String,
    options: List<String>,
    currentGoal: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                Text(
                    text = "Please select a goal",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                options.forEach { option ->
                    TextButton(
                        onClick = { onSave(option) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = option,
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = if (option == currentGoal) colorResource(id = R.color.data_container_mental_health) else Color.Black
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text("Cancel")
            }
        }
    )
}

private fun saveGoal(context: Context, key: String, goal: String) {
    val prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
    prefs.edit().putString(key, goal).apply()
}



@Composable
fun MentalSleepPage() {
    val context = LocalContext.current
    var showRecordDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var sevenDayData by remember { mutableStateOf<List<MentalDataPoint>>(emptyList()) }

    val validData = sevenDayData.filter { it.mood > 0 && it.sleepQuality > 0 }

    val (recentStatus, statusColor, descriptionSuggestions) = remember(validData) {
        if (validData.isEmpty()) {
            Triple(
                "None",
                Color.Gray,
                Pair(
                    "Click the record button to start tracking!",
                    emptyList<String>()
                )
            )
        } else {
            calculateStatus(validData)
        }
    }
    val description = descriptionSuggestions.first
    val suggestions = descriptionSuggestions.second

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Mental & Sleep",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Button(
            onClick = { showRecordDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 32.dp)
                .shadow(11.dp, RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.data_container_mental_health)
            )
        ) {
            Text("Record", fontSize = 20.sp, color = Color.Black)
        }

        Spacer(modifier = Modifier.height(24.dp))

        ChartLegend()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
        ) {
            MentalHealthChart(data = sevenDayData)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(6.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = recentStatus,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                    modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally)
                )

                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )


                if (validData.isNotEmpty()) { Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "suggestions",
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(start = 16.dp)
                    )

                    suggestions.forEach { suggestion ->
                        Text(
                            text = "- $suggestion",
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }}}
            }
        }


        LaunchedEffect(Unit) {
                       sevenDayData = getSevenDayData(context)
                   }


        if (showRecordDialog) {
            MentalRecordDialog(
                onDismiss = { showRecordDialog = false },
                onSave = { mood, sleepHours, sleepQuality ->
                    scope.launch {
                        val sleepLevel = when {
                            sleepHours < 5f -> 1
                            sleepHours < 7f -> 2
                            sleepHours < 8f -> 3
                            sleepHours < 9f -> 4
                            else -> 5
                        }
                        saveDailyRecord(context, mood, sleepLevel, sleepQuality)
                        sevenDayData = getSevenDayData(context)
                        showRecordDialog = false
                    }
                }
            )
        }
    }
}


private fun calculateStatus(validData: List<MentalDataPoint>): Triple<String, Color, Pair<String, List<String>>> {
    if (validData.isEmpty()) {
        return Triple("None", Color.Gray, Pair("", emptyList()))
    }

    val avgMood = validData.map { it.mood }.average()
    val avgQuality = validData.map { it.sleepQuality }.average()
    val combinedScore = (avgMood + avgQuality) / 2

    return when (combinedScore) {
        in 4.5..5.0 -> Triple(
            "Great",
            Color(0xFF4CAF50),
            Pair(
                "Your recent sleep and mental states are excellent.",
                listOf(
                    "Maintain your current routine!",
                    "Continue practicing stress management techniques."
                )
            )
        )
        in 3.0..4.5 -> Triple(
            "Good",
            Color(0xFF2196F3),
            Pair(
                "Your recent sleep and mental states are generally good.",
                listOf(
                    "Try to get 7-8 hours of sleep regularly.",
                    "Take time for daily relaxation activities."
                )
            )
        )
        in 1.5..3.0 -> Triple(
            "Neutral",
            Color(0xFFFFC107),
            Pair(
                "Your recent sleep and mental states are moderate.",
                listOf(
                    "Adjust your sleep schedule for better rest.",
                    "Consider talking to a professional for mental support."
                )
            )
        )
        else -> Triple(
            "Poor",
            Color(0xFFF44336),
            Pair(
                "Your recent sleep and mental states need improvement.",
                listOf(
                    "Seek professional advice for better sleep habits.",
                    "Incorporate daily exercise and mindfulness practices."
                )
            )
        )
    }
}


@Composable
fun ChartLegend() {
    val legendItems = listOf(
        Pair(Color(0xFF42A5F5), "Mood (0: No record)"),
        Pair(Color(0xFF81C784), "Sleep Level (0: No record)"),
        Pair(Color(0xFFFFA726), "Sleep Quality (0: No record)")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(-6.dp),
    ) {
        Text(
            text = "Mental & Sleep Conditions",
            fontSize = 19.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 12.dp)
        )
        Spacer(modifier = Modifier.height(7.dp))

        legendItems.forEach { (color, text) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.width(48.dp))
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(color, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = text,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}


@Composable
fun MentalRecordDialog(
    onDismiss: () -> Unit,
    onSave: (mood: Int, sleepHours: Float, sleepQuality: Int) -> Unit
) {
    var selectedMood by remember { mutableStateOf(0) }
    var sleepHours by remember { mutableStateOf(0f) }
    var selectedQuality by remember { mutableStateOf(0) }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Mental & Sleep") },
        text = {
            Column {
                Text("Please record your current mood", fontSize = 16.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    (1..5).forEach { level ->
                        MoodButton(
                            level = level,
                            selected = selectedMood == level,
                            onClick = { selectedMood = level }
                        )
                    }
                }

                Text("Please select your sleep duration (hours)", fontSize = 16.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(
                                  Triple("<5h", 1f, 5f),
                                  Triple("5-7h", 5f, 7f),
                                 Triple("7-8h", 7f, 8f),
                                   Triple("8-9h", 8f, 9f),
                                   Triple(">9h", 9f, 24f)
                                       ).forEach { (label, start, end) ->
                        SleepDurationButton(
                            label = label,
                            selected = sleepHours in start..end,
                            onClick = { sleepHours = (start + end) / 2 },
                            colors = listOf(
                                when (label) {
                                    "<5h" -> Color.Red
                                    "5-7h" -> Color(0xFFFFA500)
                                    "7-8h" -> Color.Yellow
                                    "8-9h" -> Color.Green
                                    else -> Color(0xFF00FF00)
                                }.copy(alpha = 0.8f),
                                when (label) {
                                    "<5h" -> Color.Red
                                    "5-7h" -> Color(0xFFFFA500)
                                    "7-8h" -> Color.Yellow
                                    "8-9h" -> Color.Green
                                    else -> Color(0xFF00FF00)
                                }.copy(alpha = 0.5f)
                            )
                        )
                    }
                }

                Text("Please select your sleep quality", fontSize = 16.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    (1..5).forEach { level ->
                        MoodButton(
                            level = level,
                            selected = selectedQuality == level,
                            onClick = { selectedQuality = level },
                            color = when (level) {
                                1 -> Color.Red
                                2 -> Color(0xFFFFA500)
                                3 -> Color.Yellow
                                4 -> Color.Green
                                else -> Color(0xFF00FF00)
                            }
                        )
                    }
                }

                if (showError) {
                    Text(
                        text = "Please fill in all information",
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    onClick = {
                        if (selectedMood == 0 || sleepHours == 1f || selectedQuality == 0) {
                            showError = true
                        } else {
                            onSave(selectedMood, sleepHours, selectedQuality)
                        }
                    },
                    modifier = Modifier
                        .width(120.dp)
                        .fillMaxWidth(0.6f)
                        .padding(vertical = 16.dp)
                        .background(
                            colorResource(id = R.color.data_container_mental_health),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .height(60.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Black
                    )
                ) {
                    Text("Save", fontSize = 20.sp)
                }
            }},
            dismissButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .width(120.dp)
                        .fillMaxWidth(0.6f)
                        .height(60.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    Text("Cancel", fontSize = 13.sp, color = Color.Black)
                }}
            }
        )
}

@Composable
fun SleepDurationButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    colors: List<Color>
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Brush.verticalGradient(colors))
            .border(2.dp, if (selected) Color.Black else Color.Transparent, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun MoodButton(
    level: Int,
    selected: Boolean,
    onClick: () -> Unit,
    color: Color = when (level) {
        1 -> Color.Red
        2 -> Color(0xFFFFA500)
        3 -> Color.Yellow
        4 -> Color.Green
        else -> Color(0xFF00FF00)
    }
) {
    val backgroundColor = if (selected) color.copy(alpha = 0.8f) else color.copy(alpha = 0.5f)
    val borderColor = if (selected) Color.Black else Color.Transparent

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(2.dp, borderColor, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
                       text = level.toString(),
                       color = Color.White,
                       fontSize = 10.sp,
                       fontWeight = FontWeight.Bold
                           )
    }
}



private suspend fun saveDailyRecord(
    context: Context,
    mood: Int,
    sleepLevel: Int,
    sleepQuality: Int
) {
    val date = SimpleDateFormat("MM-dd", Locale.getDefault()).format(Date())
    val prefs = context.getSharedPreferences("mental_data", Context.MODE_PRIVATE)
    prefs.edit()
        .putInt("mood_$date", mood)
        .putInt("sleep_level_$date", sleepLevel)
        .putInt("sleep_quality_$date", sleepQuality)
        .apply()
}


private fun getSevenDayData(context: Context): List<MentalDataPoint> {
    val data = mutableListOf<MentalDataPoint>()
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
    val prefs = context.getSharedPreferences("mental_data", Context.MODE_PRIVATE)
    val today = Date()

    calendar.time = today
    for (i in 0..6) {
        val date = dateFormat.format(calendar.time)
        val mood = prefs.getInt("mood_$date", 0)
        val sleepLevel = prefs.getInt("sleep_level_$date", 0)
        val quality = prefs.getInt("sleep_quality_$date", 0)
        data.add(MentalDataPoint(date, mood, sleepLevel, quality))
        calendar.add(Calendar.DAY_OF_YEAR, -1)
    }
    return data.reversed()
}


data class MentalDataPoint(
    val date: String,
    val mood: Int,
    val sleepLevel: Int,
    val sleepQuality: Int
)




@Composable
fun MentalHealthChart(data: List<MentalDataPoint>) {
    val density = LocalDensity.current
    val width = with(density) { 300.dp.toPx() }
    val height = with(density) { 200.dp.toPx() }
    val padding = with(density) { 20.dp.toPx() }
    val lineWidth = with(density) { 2.dp.toPx() }
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = Modifier
            .width(300.dp)
            .height(200.dp)
    ) {
        drawLine(
            color = Color.LightGray,
            start = Offset(padding, height - padding),
            end = Offset(width - padding, height - padding),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = Color.LightGray,
            start = Offset(padding, padding),
            end = Offset(padding, height - padding),
            strokeWidth = 1.dp.toPx()
        )

        val moodPoints = data.mapIndexed { index, point ->
            Offset(
                x = padding + (width - 2 * padding) * index / (data.size.coerceAtLeast(1) - 1),
                y = height - padding - point.mood * (height - 2 * padding) / 5
            )
        }
        val validMoodPoints = moodPoints.filterIndexed { index, _ -> data[index].mood > 0 }
        if (validMoodPoints.isNotEmpty()) {
            drawPath(
                color = Color(0xFF42A5F5),
                path = Path().apply {
                    validMoodPoints.forEachIndexed { index, point ->
                        if (index == 0) moveTo(point.x, point.y)
                        else lineTo(point.x, point.y)
                    }
                },
                style = Stroke(width = lineWidth)
            )
        }
        moodPoints.forEach { point ->
            drawCircle(
                color = Color(0xFF42A5F5),
                radius = 4.dp.toPx(),
                center = point,
                alpha = if (data[moodPoints.indexOf(point)].mood == 0) 0.5f else 1f
            )
        }

        val hoursPoints = data.mapIndexed { index, point ->
            Offset(
                x = padding + (width - 2 * padding) * index / (data.size.coerceAtLeast(1) - 1),
                y = height - padding - point.sleepLevel * (height - 2 * padding) / 5
            )
        }
        val validHoursPoints = hoursPoints.filterIndexed { index, _ -> data[index].sleepLevel > 0 }
        if (validHoursPoints.isNotEmpty()) {
            drawPath(
                color = Color(0xFF81C784),
                path = Path().apply {
                    validHoursPoints.forEachIndexed { index, point ->
                        if (index == 0) moveTo(point.x, point.y)
                        else lineTo(point.x, point.y)
                    }
                },
                style = Stroke(width = lineWidth)
            )
        }
        hoursPoints.forEach { point ->
            drawCircle(
                color = Color(0xFF81C784),
                radius = 4.dp.toPx(),
                center = point,
                alpha = if (data[hoursPoints.indexOf(point)].sleepLevel == 0) 0.5f else 1f
            )
        }

        val qualityPoints = data.mapIndexed { index, point ->
            Offset(
                x = padding + (width - 2 * padding) * index / (data.size.coerceAtLeast(1) - 1),
                y = height - padding - point.sleepQuality * (height - 2 * padding) / 5
            )
        }
        val validQualityPoints = qualityPoints.filterIndexed { index, _ -> data[index].sleepQuality > 0 }
        if (validQualityPoints.isNotEmpty()) {
            drawPath(
                color = Color(0xFFFFA726),
                path = Path().apply {
                    validQualityPoints.forEachIndexed { index, point ->
                        if (index == 0) moveTo(point.x, point.y)
                        else lineTo(point.x, point.y)
                    }
                },
                style = Stroke(width = lineWidth)
            )
        }
        qualityPoints.forEach { point ->
            drawCircle(
                color = Color(0xFFFFA726),
                radius = 4.dp.toPx(),
                center = point,
                alpha = if (data[qualityPoints.indexOf(point)].sleepQuality == 0) 0.5f else 1f
            )
        }

        data.forEachIndexed { index, point ->
            val textLayoutResult = textMeasurer.measure(
                text = point.date,
                style = TextStyle(fontSize = 12.sp, color = Color.Gray)
            )
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    x = padding + (width - 2 * padding) * index / (data.size.coerceAtLeast(1) - 1) - 16.dp.toPx(),
                    y = height - padding + 16.dp.toPx()
                )
            )
        }
    }
}

data class SportDataPoint(
    val date: String,
    val diet: Int,
    val exerciseLevel: Int,
    val intensity: Int
)



@Composable
fun SportDietPage() {
    val context = LocalContext.current
    var showRecordDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var sevenDayData by remember { mutableStateOf<List<SportDataPoint>>(emptyList()) }

    val validData = sevenDayData.filter { it.diet > 0 && it.intensity > 0 }

    val (recentStatus, statusColor, descriptionSuggestions) = remember(validData) {
        if (validData.isEmpty()) {
            Triple(
                "None",
                Color.Gray,
                Pair(
                    "Click the record button to start tracking!",
                    emptyList<String>()
                )
            )
        } else {
            calculateSportDietStatus(validData)
        }
    }
    val description = descriptionSuggestions.first
    val suggestions = descriptionSuggestions.second

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sport & Diet",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
            textAlign = TextAlign.Center
        )

        Button(
            onClick = { showRecordDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 32.dp)
                .shadow(11.dp, RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.data_container_exercise)
            )
        ) {
            Text("Record", fontSize = 20.sp, color = Color.Black)
        }

        Spacer(modifier = Modifier.height(24.dp))

        SportDietChartLegend()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
        ) {
            SportDietChart(data = sevenDayData)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(6.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = recentStatus,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                    modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally)
                )

                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                if (validData.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Suggestions",
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                        suggestions.forEach { suggestion ->
                            Text(
                                text = "- $suggestion",
                                fontSize = 14.sp,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            }
        }

        LaunchedEffect(Unit) {
            sevenDayData = getSevenDaySportData(context)
        }

        if (showRecordDialog) {
            SportDietRecordDialog(
                onDismiss = { showRecordDialog = false },
                onSave = { diet, exerciseHours, intensity ->
                    scope.launch {
                        val exerciseLevel = when {
                            exerciseHours < 0.5f -> 1
                            exerciseHours < 1f -> 2
                            exerciseHours < 1.5f -> 3
                            exerciseHours < 2f -> 4
                            else -> 5
                        }
                        saveDailySportDietRecord(context, diet, exerciseLevel, intensity)
                        sevenDayData = getSevenDaySportData(context)
                        showRecordDialog = false
                    }
                }
            )
        }
    }
}



private fun calculateSportDietStatus(validData: List<SportDataPoint>): Triple<String, Color, Pair<String, List<String>>> {
    if (validData.isEmpty()) {
        return Triple("None", Color.Gray, Pair("", emptyList()))
    }

    val avgDiet = validData.map { it.diet }.average()
    val avgIntensity = validData.map { it.intensity }.average()
    val combinedScore = (avgDiet + avgIntensity) / 2

    return when (combinedScore) {
        in 4.5..5.0 -> Triple(
            "Excellent",
            Color(0xFF4CAF50),
            Pair(
                "Your recent diet and exercise are excellent.",
                listOf(
                    "Maintain your balanced diet and regular exercise!",
                    "Consider adding variety to your workout routine."
                )
            )
        )
        in 3.0..4.5 -> Triple(
            "Good",
            Color(0xFF2196F3),
            Pair(
                "Your recent diet and exercise are generally good.",
                listOf(
                    "Aim for at least 30 minutes of exercise daily.",
                    "Ensure a balanced intake of nutrients in your diet."
                )
            )
        )
        in 1.5..3.0 -> Triple(
            "Moderate",
            Color(0xFFFFC107),
            Pair(
                "Your recent diet and exercise need attention.",
                listOf(
                    "Increase your exercise duration or intensity gradually.",
                    "Reduce processed foods and add more vegetables to your meals."
                )
            )
        )
        else -> Triple(
            "Poor",
            Color(0xFFF44336),
            Pair(
                "Your recent diet and exercise are suboptimal.",
                listOf(
                    "Consult a nutritionist or trainer for a personalized plan.",
                    "Start with small exercise routines and focus on balanced meals."
                )
            )
        )
    }
}


@Composable
fun SportDietChartLegend() {
    val legendItems = listOf(
        Pair(Color(0xFF42A5F5), "Diet Quality (0: No record)"),
        Pair(Color(0xFF81C784), "Exercise Level (0: No record)"),
        Pair(Color(0xFFFFA726), "Exercise Intensity (0: No record)")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(-6.dp),
    ) {
        Text(
            text = "Sport & Diet Conditions",
            fontSize = 19.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 12.dp)
        )
        Spacer(modifier = Modifier.height(7.dp))

        legendItems.forEach { (color, text) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.width(48.dp))
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(color, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = text,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}


@Composable
fun SportDietRecordDialog(
    onDismiss: () -> Unit,
    onSave: (diet: Int, exerciseHours: Float, intensity: Int) -> Unit
) {
    var selectedDiet by remember { mutableStateOf(0) }
    var exerciseHours by remember { mutableStateOf(0f) }
    var selectedIntensity by remember { mutableStateOf(0) }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Sport & Diet") },
        text = {
            Column {
                Text("Please rate your diet quality", fontSize = 16.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    (1..5).forEach { level ->
                        DietButton(
                            level = level,
                            selected = selectedDiet == level,
                            onClick = { selectedDiet = level }
                        )
                    }
                }

                Text("Please select your exercise duration (hours)", fontSize = 16.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(
                        Triple("<0.5h", 0.25f, 0.5f),
                        Triple("0.5-1h", 0.5f, 1f),
                        Triple("1-1.5h", 1f, 1.5f),
                        Triple("1.5-2h", 1.5f, 2f),
                        Triple(">2h", 2.5f, 4f)
                    ).forEach { (label, start, end) ->
                        ExerciseDurationButton(
                            label = label,
                            selected = exerciseHours in start..end,
                            onClick = { exerciseHours = (start + end) / 2 },
                            colors = listOf(
                                when (label) {
                                    "<0.5h" -> Color.Red
                                    "0.5-1h" -> Color(0xFFFFA500)
                                    "1-1.5h" -> Color.Yellow
                                    "1.5-2h" -> Color.Green
                                    else -> Color(0xFF00FF00)
                                }.copy(alpha = 0.8f),
                                when (label) {
                                    "<0.5h" -> Color.Red
                                    "0.5-1h" -> Color(0xFFFFA500)
                                    "1-1.5h" -> Color.Yellow
                                    "1.5-2h" -> Color.Green
                                    else -> Color(0xFF00FF00)
                                }.copy(alpha = 0.5f)
                            )
                        )
                    }
                }


                Text("Please rate your exercise intensity", fontSize = 16.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    (1..5).forEach { level ->
                        IntensityButton(
                            level = level,
                            selected = selectedIntensity == level,
                            onClick = { selectedIntensity = level },
                            color = when (level) {
                                1 -> Color.Red
                                2 -> Color(0xFFFFA500)
                                3 -> Color.Yellow
                                4 -> Color.Green
                                else -> Color(0xFF00FF00)
                            }
                        )
                    }
                }

                if (showError) {
                    Text(
                        text = "Please fill in all information",
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    onClick = {
                        if (selectedDiet == 0 || exerciseHours == 0f || selectedIntensity == 0) {
                            showError = true
                        } else {
                            onSave(selectedDiet, exerciseHours, selectedIntensity)
                        }
                    },
                    modifier = Modifier
                        .width(120.dp)
                        .fillMaxWidth(0.6f)
                        .padding(vertical = 16.dp)
                        .background(
                            colorResource(id = R.color.data_container_exercise),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .height(60.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Black
                    )
                ) {
                    Text("Save", fontSize = 20.sp)
                }
            }
        },
        dismissButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .width(120.dp)
                        .fillMaxWidth(0.6f)
                        .height(60.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    Text("Cancel", fontSize = 13.sp, color = Color.Black)
                }
            }
        }
    )
}


@Composable
fun DietButton(
    level: Int,
    selected: Boolean,
    onClick: () -> Unit,
    color: Color = when (level) {
        1 -> Color.Red
        2 -> Color(0xFFFFA500)
        3 -> Color.Yellow
        4 -> Color.Green
        else -> Color(0xFF00FF00)
    }
) {
    MoodButton(level = level, selected = selected, onClick = onClick, color = color)
}

@Composable
fun IntensityButton(
    level: Int,
    selected: Boolean,
    onClick: () -> Unit,
    color: Color
) {
    MoodButton(level = level, selected = selected, onClick = onClick, color = color)
}

@Composable
fun ExerciseDurationButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    colors: List<Color>
) {
    SleepDurationButton(label = label, selected = selected, onClick = onClick, colors = colors)
}

private suspend fun saveDailySportDietRecord(
    context: Context,
    diet: Int,
    exerciseLevel: Int,
    intensity: Int
) {
    val date = SimpleDateFormat("MM-dd", Locale.getDefault()).format(Date())
    val prefs = context.getSharedPreferences("sport_diet_data", Context.MODE_PRIVATE)
    prefs.edit()
        .putInt("diet_$date", diet)
        .putInt("exercise_level_$date", exerciseLevel)
        .putInt("intensity_$date", intensity)
        .apply()
}

private fun getSevenDaySportData(context: Context): List<SportDataPoint> {
    val data = mutableListOf<SportDataPoint>()
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
    val prefs = context.getSharedPreferences("sport_diet_data", Context.MODE_PRIVATE)
    val today = Date()

    calendar.time = today
    for (i in 0..6) {
        val date = dateFormat.format(calendar.time)
        val diet = prefs.getInt("diet_$date", 0)
        val exerciseLevel = prefs.getInt("exercise_level_$date", 0)
        val intensity = prefs.getInt("intensity_$date", 0)
        data.add(SportDataPoint(date, diet, exerciseLevel, intensity))
        calendar.add(Calendar.DAY_OF_YEAR, -1)
    }
    return data.reversed()
}


@Composable
fun SportDietChart(data: List<SportDataPoint>) {
    val density = LocalDensity.current
    val width = with(density) { 300.dp.toPx() }
    val height = with(density) { 200.dp.toPx() }
    val padding = with(density) { 20.dp.toPx() }
    val lineWidth = with(density) { 2.dp.toPx() }
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = Modifier
            .width(300.dp)
            .height(200.dp)
    ) {
        drawLine(Color.LightGray, Offset(padding, height - padding), Offset(width - padding, height - padding), 1.dp.toPx())
        drawLine(Color.LightGray, Offset(padding, padding), Offset(padding, height - padding), 1.dp.toPx())

        val dietPoints = data.mapIndexed { index, point ->
            Offset(
                x = padding + (width - 2 * padding) * index / (data.size.coerceAtLeast(1) - 1),
                y = height - padding - point.diet * (height - 2 * padding) / 5
            )
        }
        drawChartLines(dietPoints, data, Color(0xFF42A5F5), 0, lineWidth)

        val exercisePoints = data.mapIndexed { index, point ->
            val level = point.exerciseLevel.takeIf { it > 0 } ?: 0
            Offset(
                x = padding + (width - 2 * padding) * index / (data.size.coerceAtLeast(1) - 1),
                y = height - padding - level * (height - 2 * padding) / 5
            )
        }
        drawChartLines(exercisePoints, data, Color(0xFF81C784), 1, lineWidth)

        val intensityPoints = data.mapIndexed { index, point ->
            Offset(
                x = padding + (width - 2 * padding) * index / (data.size.coerceAtLeast(1) - 1),
                y = height - padding - point.intensity * (height - 2 * padding) / 5
            )
        }
        drawChartLines(intensityPoints, data, Color(0xFFFFA726), 2, lineWidth)

        data.forEachIndexed { index, point ->
            val textLayoutResult = textMeasurer.measure(
                text = point.date,
                style = TextStyle(fontSize = 12.sp, color = Color.Gray)
            )
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    x = padding + (width - 2 * padding) * index / (data.size.coerceAtLeast(1) - 1) - 16.dp.toPx(),
                    y = height - padding + 16.dp.toPx()
                )
            )
        }
    }
}


private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawChartLines(
    points: List<Offset>,
    data: List<SportDataPoint>,
    color: Color,
    type: Int,
    lineWidth: Float,
) {
    val validPoints = points.filterIndexed { index, _ ->
        when (type) {
            0 -> data[index].diet > 0
            1 -> data[index].exerciseLevel > 0
            else -> data[index].intensity > 0
        }
    }
    if (validPoints.isNotEmpty()) {
        val path = Path().apply {
            validPoints.forEachIndexed { index, point ->
                if (index == 0) moveTo(point.x, point.y)
                else lineTo(point.x, point.y)
            }
        }
        drawPath(path, color, style = Stroke(width = lineWidth))
    }
    points.forEachIndexed { index, point ->
        val alpha = when (type) {
            0 -> if (data[index].diet == 0) 0.5f else 1f
            1 -> if (data[index].exerciseLevel == 0) 0.5f else 1f
            else -> if (data[index].intensity == 0) 0.5f else 1f
        }
        drawCircle(color, 4.dp.toPx(), point, alpha)
    }
}
