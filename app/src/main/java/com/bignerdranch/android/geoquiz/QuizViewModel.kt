package com.bignerdranch.android.geoquiz

import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"

class QuizViewModel : ViewModel() {
    /*
        init {
            Log.d(TAG, "ViewModel instance created")
        }

        override fun onCleared() {
            // method called just before VieModel is destroyed, useful place to perform any clean up such as un-observing a datasource
            super.onCleared()
            Log.d(TAG, "ViewModel instance about to be destroyed")
        }
    */
    // create a list of question objects, each question object with a single question and the answer for the question
    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true),
    )

    var currentIndex = 0
    var cheatAvailable = 3

    // to hold the value that CheatActivity is passing back
    var isCheater = false
    // users cheat status is part of UI, better to save it in viewModel so that value persists across a configuration changes

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun totalQuestions(): Int {
        return questionBank.size
    }
}
