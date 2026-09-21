package com.example.prjmetrix

//(Simplified Coding, 2021)

data class JournalRequest(
    val text: String,
    val mood: String
)

data class JournalResponse(
    val text: String,
    val mood: String,
    val journalId: Int,
    val createdAt: String,
    val updatedAt: String? //can be null because its related to if the user edits an entry or not
)

//(Jovanovic, 2024)
data class MoodOption(
    val emoji: String,
    val label: String
)

//(Krishna, 2025)
val journalMoods = listOf(
    //emoji being taken as hexcode
    MoodOption("\uD83D\uDE01", "Excited"),
    MoodOption("\uD83D\uDE42", "Alright"),
    MoodOption("\uD83D\uDE10", "Meh"),
    MoodOption("\uD83D\uDE14", "Sad"),
    MoodOption("\uD83D\uDE21", "Angry"),
    MoodOption("\uD83D\uDE30", "Worried"),

    )

/*References
*
* Jovanovic, S., 2024. Diary App. (Version 10.0) [Source Code]. Available at: < https://github.com/stevdza-san/DiaryApp/tree/master > [Accessed 20 September 2026]
* Krishna, M., 2025. Personal Diary App. (Version 10.0) [Source Code]. Available at: < https://github.com/Murali-KrishnaM/PersonalDiaryApp/blob/main/app/src/main/java/com/example/diaryapp/AddEntryActivity.kt > [Accessed 20 September 2026]
*
*
*
* */