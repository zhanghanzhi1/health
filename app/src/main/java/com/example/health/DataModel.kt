package com.example.health

data class SleepData(
    val hours: Float,
    val quality: String // 例如："好", "一般", "差"
)

data class ExerciseData(
    val minutes: Int,
    val type: String // 例如："跑步", "游泳"
)

data class DietData(
    val calories: Int,
    val balanced: Boolean // 是否均衡饮食
)

data class MentalHealthData(
    val score: Int // 例如：0 - 100 的分数
)

data class PersonalData(
    val sleep: SleepData,
    val exercise: ExerciseData,
    val diet: DietData,
    val mentalHealth: MentalHealthData
)

//data class DailyStatus(
//    val sleep: String,
//    val exercise: String,
//    val mental: String,
//    val diet: String
//)