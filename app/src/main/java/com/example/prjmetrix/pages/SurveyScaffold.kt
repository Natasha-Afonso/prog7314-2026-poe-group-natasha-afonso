package com.example.prjmetrix.pages

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

private const val stronglyDeemphasizedAlpha = 0.6f
private const val slightlyDeemphasizedAlpha = 0.87f

@Composable
fun SurveyScaffold(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit = {},
    onFinished: () -> Unit = {}
) {
    val surveyViewModel: SurveyViewModel =
        viewModel(factory = SurveyViewModelFactory(LocalContext.current))
    val progress = surveyViewModel.progress
    val question = surveyViewModel.question
    val total = surveyQuestions.size
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(surveyViewModel.isFinished) {
        if (surveyViewModel.isFinished) onFinished()
    }
    LaunchedEffect(surveyViewModel.errorMessage) {
        surveyViewModel.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            surveyViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "$progress of $total",
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 48.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.labelLarge
                    )
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close")
                    }
                }
                LinearProgressIndicator(
                    progress = { progress / total.toFloat() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        bottomBar = {
            SurveyBottomBar(
                progress = progress,
                total = total,
                isNextEnabled = surveyViewModel.isNextEnabled,
                isSaving = surveyViewModel.isSaving,
                onPreviousPressed = surveyViewModel::previousQuestion,
                onNextPressed = if (progress != total) {
                    surveyViewModel::nextQuestion
                } else {
                    surveyViewModel::saveProfile
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.statusBarsPadding()
    ) { innerPadding ->
        AnimatedContent(targetState = question, label = "question") { surveyQuestion ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    surveyQuestion.question,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(color = MaterialTheme.colorScheme.inverseOnSurface)
                        .padding(20.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = slightlyDeemphasizedAlpha),
                )
                if (surveyQuestion.description != "") {
                    Text(
                        text = surveyQuestion.description,
                        modifier = Modifier.padding(start = 20.dp, bottom = 20.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                            .copy(alpha = stronglyDeemphasizedAlpha),
                    )
                }

                when (val options = surveyQuestion.options) {
                    SurveyOptions.TextInput -> {
                        TextQuestion(
                            value = surveyViewModel.nickname,
                            onValueChange = surveyViewModel::onNicknameChange,
                            onDone = { if (surveyViewModel.isNextEnabled) surveyViewModel.nextQuestion() }
                        )
                    }

                    is SurveyOptions.SingleChoice -> {
                        SingleOptionsSection(
                            selected = surveyViewModel.personality,
                            options = options,
                            onOptionSelected = surveyViewModel::onPersonalityPicked
                        )
                    }

                    SurveyOptions.DateChoice -> {
                        DateQuestion(
                            date = surveyViewModel.formattedBirthday,
                            birthday = surveyViewModel.birthday,
                            onDatePicked = surveyViewModel::onBirthdayPicked
                        )
                    }
                }
            }
        }
    }
}