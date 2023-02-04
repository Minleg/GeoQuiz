package com.bignerdranch.android.geoquiz

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var prevButton: ImageButton

    private var quizScore = 0
    private var correctAnswerFlag = false

    private val quizViewModel: QuizViewModel by lazy { // lazy initialization allows us to use val here instead of var
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "OnCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex
        /*
        // ViewModelProviders(s) class provides instances of the ViewProvider class
        val provider: ViewModelProvider = ViewModelProviders.of(this) // creates and returns a ViewModelProvider associated with the activity
        // ViewModelProver class provides instances of ViewModel to the activity
        val quizViewModel = provider.get(QuizViewModel::class.java) // this get() returns an instance of QuizViewModel
        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")
         */

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        questionTextView = findViewById(R.id.question_text_view)
        prevButton = findViewById(R.id.prev_button)

        trueButton.setOnClickListener {
            checkAnswer(true)
            buttonDisable(false)
        }

        falseButton.setOnClickListener {
            checkAnswer(false)
            buttonDisable(false)
        }

        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
            buttonDisable(true)
            if (quizViewModel.currentIndex == 0) { // signifies that we are at the start of the question again
                percentageScore(quizScore)
            }
        }

        // next question can be loaded by clicking the question text view
        questionTextView.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
            buttonDisable(true)
        }

        // if previous button is clicked
        prevButton.setOnClickListener {
            if (quizViewModel.currentIndex == 0) { // if index is 0, point to the last question in the list
                quizViewModel.currentIndex = quizViewModel.totalQuestions() - 1
            } else { // point to the previous question otherwise
                quizViewModel.currentIndex = quizViewModel.currentIndex - 1
            }
            updateQuestion()
            buttonDisable(true)
            if (correctAnswerFlag) { // if previous question was correct, score is deducted to resume count
                quizScore--
            }
        }

        // puts the first question in the text view
        updateQuestion()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "OnStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "OnResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "OnPause() called")
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "OnStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "OnDestroy() called")
    }

    private fun updateQuestion() { // gets the question at the index
        // Log.d(TAG, "Updating question text", Exception())
        val questionTextResId = quizViewModel.currentQuestionText // the resource id for the question
        questionTextView.setText(questionTextResId) // sets the question textview with the question on the index
    }

    private fun checkAnswer(userAnswer: Boolean) {
        /* Checks the answer stored in the list with the user's answer */
        val correctAnswer = quizViewModel.currentQuestionAnswer // gets the answer for the question in the list

        if (userAnswer == correctAnswer) { // if answer is correct, score is updated
            correctAnswerFlag = true
            quizScore++
        } else {
            correctAnswerFlag = false
        }

        val messageResId = if (userAnswer == correctAnswer) {
            R.string.correct_toast // correct answer toast is set
        } else {
            R.string.incorrect_toast
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
            .show() // shows the user , toast with message whether they are correct or not
    }

    private fun buttonDisable(enable: Boolean) {
        /* This function disables or enables the true and false choice button */
        trueButton.isEnabled = enable
        falseButton.isEnabled = enable
    }

    private fun percentageScore(score: Int) {
        /* This method is called after coming to the first question again after answering all the questions, the score
        * obtained is displayed in a toast*/
        val scoreMessage = "You score $score out of ${quizViewModel.totalQuestions()}"
        Toast.makeText(this, scoreMessage, Toast.LENGTH_LONG).show()
        quizScore = 0 // resets the score to 0
    }
}
