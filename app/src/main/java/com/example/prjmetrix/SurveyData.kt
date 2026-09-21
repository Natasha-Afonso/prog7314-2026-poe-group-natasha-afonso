package com.example.prjmetrix

sealed interface SurveyOptions {
    data object TextInput : SurveyOptions
    data class SingleChoice(val options: List<Pair<String, String>>) : SurveyOptions // label to emoji
    data object DateChoice : SurveyOptions
}

data class SurveyQuestion(
    val question: String,
    val description: String = "",
    val options: SurveyOptions
)

val surveyQuestions = listOf(
    SurveyQuestion(
        question = "What should we call you?",
        description = "This is the name you'll see in the app.",
        options = SurveyOptions.TextInput
    ),
    SurveyQuestion(
        question = "Pick your avatar's personality",
        description = "Choose the one that feels most like you.",
        options = SurveyOptions.SingleChoice(
            listOf(
                "Curious" to "🔍",
                "Playful" to "🎈",
                "Calm" to "🌿",
                "Ambitious" to "🚀",
                "Creative" to "🎨"
            )
        )
    ),
    SurveyQuestion(
        question = "When is your birthday?",
        options = SurveyOptions.DateChoice
    )
)