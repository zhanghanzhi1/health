package com.example.health

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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.shape.RoundedCornerShape
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.example.health.ui.theme.HealthTheme
//import com.example.health.R
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.layout.ContentScale
//import androidx.navigation.NavController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.mutableStateOf



data class DailyStatus(
    val sleep: String,
    val exercise: String,
    val mental: String,
    val diet: String
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
    data object Sleep : Page()
    data object Exercise : Page()
    data object Mental : Page()
    data object Diet : Page()
}

@Composable
fun MainApp() {
    var currentPage by remember { mutableStateOf<Page>(Page.Main)}

    when (currentPage) {
        is Page.Main -> MainScreen(
            onSleepButtonClick = { currentPage = Page.Sleep },
            onExerciseButtonClick = { currentPage = Page.Exercise },
            onMentalButtonClick = { currentPage = Page.Mental },
            onDietButtonClick = { currentPage = Page.Diet }
        )
        is Page.Sleep -> ItemPage(title = "Sleep", onBackClick = { currentPage = Page.Main })
        is Page.Exercise -> ItemPage(title = "Sport", onBackClick = { currentPage = Page.Main })
        is Page.Mental -> ItemPage(title = "Mental", onBackClick = { currentPage = Page.Main })
        is Page.Diet -> ItemPage(title = "Diet", onBackClick = { currentPage = Page.Main })
    }
}


@Composable
fun MainScreen(    onSleepButtonClick: () -> Unit,
                   onExerciseButtonClick: () -> Unit,
                   onMentalButtonClick: () -> Unit,
                   onDietButtonClick: () -> Unit) {

    val dailyStatus = androidx.compose.runtime.remember {
        DailyStatus(
            sleep = "7h",
            exercise = "30min",
            mental = "Good",
            diet = "Balanced"
        )
    }

    var selectedItem by remember { mutableIntStateOf(0)}
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
                    onClick = { selectedItem = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(painterResource(id = R.drawable.ic_mine), contentDescription = null) },
                    label = { Text(stringResource(id = R.string.mine)) },
                    selected = selectedItem == 1,
                    onClick = { selectedItem = 1 }
                )
            }
        }
    ) { innerPadding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
//            .padding(16.dp)
//            .background(Brush.verticalGradient(colors = gradientColors)
            )
            {
                when (selectedItem) {
                    0 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = "Let’s get started !",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
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
                                    // 大圆形
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
                                    val statusList = listOf(
                                        dailyStatus.sleep,
                                        dailyStatus.exercise,
                                        dailyStatus.mental,
                                        dailyStatus.diet
                                    )
                                    val colors = listOf(
                                        colorResource(id = R.color.data_container_sleep),
                                        colorResource(id = R.color.data_container_exercise),
                                        colorResource(id = R.color.data_container_mental_health),
                                        colorResource(id = R.color.data_container_diet)
                                    )
                                    val titles = listOf("Sleep", "Sport", "Mental", "Diet")

                                    circlePositions.forEachIndexed { index, position ->
                                        Box(
                                            modifier = Modifier
                                                .size(circleRadius * 2)
                                                .offset {
                                                    androidx.compose.ui.unit.IntOffset(
                                                        position.x.toInt(),
                                                        position.y.toInt()
                                                    )
                                                }
                                                .align(Alignment.Center)
                                                .clip(androidx.compose.foundation.shape.CircleShape)
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
                                                    text = statusList[index],
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
                                onSleepButtonClick = onSleepButtonClick,
                                onExerciseButtonClick = onExerciseButtonClick,
                                onMentalButtonClick = onMentalButtonClick,
                                onDietButtonClick = onDietButtonClick
                            )
                        }
                    }

                    1 -> {

                        val personalData = PersonalData(
                            sleep = SleepData(hours = 7.5f, quality = "good"),
                            exercise = ExerciseData(minutes = 60, type = "running"),
                            diet = DietData(calories = 1500, balanced = true),
                            mentalHealth = MentalHealthData(score = 80)
                        )
                        PersonalPage(personalData)
                    }
                }
            }
    }

}

@Composable
fun DateList() {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
    val today = Date()
    val dates = mutableListOf<String>()


    for (i in 6 downTo 0) {
        calendar.time = today
        calendar.add(Calendar.DAY_OF_YEAR, -i)
        dates.add(dateFormat.format(calendar.time))
    }

    val checkedDays = mutableSetOf<Int>()
    while (checkedDays.size < 5) {
        val randomDay = (0..6).random()
        checkedDays.add(randomDay)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        dates.forEachIndexed { index, date ->
            val isToday = index == dates.size - 1
            val backgroundColor = if (isToday) colorResource(id = R.color.date_background_today) else colorResource(id = R.color.date_background_normal)
            Box(
                modifier = Modifier
                    .width(50.dp)
                    .height(80.dp)
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
                    text = date,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isToday) Color.White else Color.Black
                )
                if (checkedDays.contains(index)) {
                    Text(
                        text = "√",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.checkmark_color)
                    )
                }
                else {

                    Spacer(modifier = Modifier.height(20.dp))
                }
                }
            }
        }
    }
}

@Composable
fun ButtonArea(    onSleepButtonClick: () -> Unit,
                   onExerciseButtonClick: () -> Unit,
                   onMentalButtonClick: () -> Unit,
                   onDietButtonClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FeatureButton(
            text = "Sleep      >",
            onClick = onSleepButtonClick,
            colorResId = R.color.data_container_sleep
        )
        FeatureButton(
            text = "Sport      >",
            onClick = onExerciseButtonClick,
            colorResId = R.color.data_container_exercise
        )
        FeatureButton(
            text = "Mental    >",
            onClick = onMentalButtonClick,
            colorResId = R.color.data_container_mental_health
        )
        FeatureButton(
            text = "Diet         >",
            onClick =  onDietButtonClick ,
            colorResId = R.color.data_container_diet
        )
    }
}

@Composable
fun FeatureButton(text: String, onClick: () -> Unit, colorResId: Int) {

//    val buttonWidth = Modifier.fillMaxWidth()
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
            .fillMaxWidth()
            .height(buttonHeight)
            .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(10.dp),
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
                .background(colorResource(id = R.color.personal_card_background)),
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
                    Text(text = "Nick Name", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(text = "This is a personal profile", fontSize = 14.sp, color = Color.Gray)
                }
            }
        }

        Box(modifier = Modifier.height(35.dp))


        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(19.dp)
        ) {

            DataContainer(
                title = "Sleep",
                value = "${personalData.sleep.hours} hour(s)，Quality：${personalData.sleep.quality}",
                backgroundColor = colorResource(id = R.color.data_container_sleep)
            )

            DataContainer(
                title = "Sport",
                value = "${personalData.exercise.minutes} Minute(s) ${personalData.exercise.type}",
                backgroundColor = colorResource(id = R.color.data_container_exercise)
            )

            DataContainer(
                title = "Diet",
                value = "${personalData.diet.calories} Calories，${if (personalData.diet.balanced) "Balanced" else "Unbalanced"}",
                backgroundColor = colorResource(id = R.color.data_container_diet)
            )

            DataContainer(
                title = "Mental",
                value = "score：${personalData.mentalHealth.score}",
                backgroundColor = colorResource(id = R.color.data_container_mental_health)
            )
        }
    }
}

@Composable
fun DataContainer(title: String, value: String, backgroundColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Text(text = value, fontSize = 16.sp)
        }
    }
}


@Composable
fun ItemPage(title: String, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(
            onClick = {  },
            modifier = Modifier
                .width(270.dp)
                .height(120.dp)
                .padding(vertical = 25.dp)
        ) {
            Text(text = "Record", fontSize = 23.sp)
        }
        Button(
            onClick = {  },
            modifier = Modifier
                .width(270.dp)
                .height(120.dp)
                .padding(vertical = 25.dp)
        ) {
            val actionText = if (title == "Sleep" || title == "Diet") "Suggestion" else "Training"
            Text(text = actionText, fontSize = 23.sp)
        }
        Button(
            onClick = onBackClick,
            modifier = Modifier
                .width(270.dp)
                .height(120.dp)
                .padding(vertical = 25.dp)
        ) {
            Text(text = "Back", fontSize = 23.sp)
        }
    }
}