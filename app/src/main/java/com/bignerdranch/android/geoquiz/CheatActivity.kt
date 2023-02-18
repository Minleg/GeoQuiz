package com.bignerdranch.android.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders

const val EXTRA_ANSWER_SHOWN =
    "com.bignerdranch.android.geoquiz.answer_shown" // for sending data back to MainActivity
private const val EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.geoquiz.answer_is_true"

private const val TAG = "CheatActivity"

private const val IF_CHEATED = "cheated"

class CheatActivity : AppCompatActivity() {

    private lateinit var answerTextView: TextView
    private lateinit var showAnswerButton: Button
    private lateinit var apiLevelTextView: TextView

    private var answerIsTrue = false

    // private var hasCheated = false
    private val quizViewModel: QuizViewModel by lazy { // lazy initialization allows us to use val here instead of var
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        // quizViewModel.isCheater = savedInstanceState?.getBoolean(IF_CHEATED, false) ?: false //  isCheater value  is safe from orientation changes
        Log.i(TAG, " cheatedOrNot ${quizViewModel.isCheater}")

        answerTextView = findViewById(R.id.answer_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)
        apiLevelTextView = findViewById(R.id.show_api_level_text_view)

        val apiText = "API Level ${Build.VERSION.SDK_INT}" // to set the current API level that device is running
        apiLevelTextView.text = apiText

        showAnswerButton.setOnClickListener {
            quizViewModel.isCheater = true
            val answerText = when {
                answerIsTrue -> R.string.true_button
                else -> R.string.false_button
            }
            answerTextView.setText(answerText)
            setAnswerShownResult(quizViewModel.isCheater)
        }
        setAnswerShownResult(quizViewModel.isCheater) // to bind the isCheater value when orientation changes
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        /* When user presses the SHOW ANSWER button, the CheatActivity packages up the result code and the intent in the call to setResult(Int, Intent) */
        Log.i(TAG, "setAnswerShownResult $isAnswerShown")
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }
        // If Activity was launched with startActivityForResult, OS sends a default result code if setResult() is not called
        // then when user presses the back button, parent will receive Activity.RESULT_CANCELED
        setResult(Activity.RESULT_OK, data) // sends data back to MainActivity with result code
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putBoolean(IF_CHEATED, quizViewModel.isCheater)
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putBoolean(IF_CHEATED, hasCheated)
//    }

    // encapsulation - no reason for MainActivity or any other code to know the implementation details of what
    // CheatActivity expects as extras on its Intent
    companion object {
        /* This newIntent always returns an Intent that started the activity */
        fun newIntent(
            packageContext: Context,
            answerIsTrue: Boolean,
        ): Intent { // if you need to send more data, add as argument to newIntent
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }
}
