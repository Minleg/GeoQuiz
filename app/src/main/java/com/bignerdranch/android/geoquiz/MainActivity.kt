package com.bignerdranch.android.geoquiz

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var prevButton: ImageButton

    private var quizScore = 0
    private var quizScoreList = mutableListOf<Int>()

    // create a list of question objects, each question object with a single question and the answer for the question
    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "OnCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

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
            currentIndex = (currentIndex + 1) % questionBank.size
            updateQuestion()
            buttonDisable(true)
            if (currentIndex == 0) { // signifies that we are at the start of the question again
                percentageScore(quizScoreList.sum())
            }
        }

        // next question can be loaded by clicking the question text view
        questionTextView.setOnClickListener {
            currentIndex = (currentIndex + 1) % questionBank.size
            updateQuestion()
            buttonDisable(true)
        }

        // if previous button is clicked
        prevButton.setOnClickListener {
            if (currentIndex == 0) { // if index is 0, point to the last question in the list
                currentIndex = questionBank.size - 1
                quizScoreList.removeLast()
            } else { // point to the previous question otherwise
                currentIndex -= 1
                quizScoreList.removeLast()
            }
            updateQuestion()
            buttonDisable(true)

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

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "OnStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "OnDestroy() called")
    }

    private fun updateQuestion() { // gets the question at the index
        val questionTextResId =
            questionBank[currentIndex].textResId // the resource id for the question
        questionTextView.setText(questionTextResId) // sets the question textview with the question on the index
    }

    private fun checkAnswer(userAnswer: Boolean) {
        /* Checks the answer stored in the list with the user's answer */
        val correctAnswer =
            questionBank[currentIndex].answer // gets the answer for the question in the list

        if (userAnswer == correctAnswer) { // if answer is correct, score is updated
            quizScore++
            quizScoreList.add(1)
        } else {
            quizScoreList.add(0)
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
        val scoreMessage = "You score $score out of ${questionBank.size}"
        Toast.makeText(this, scoreMessage, Toast.LENGTH_LONG).show()
        quizScore = 0 // resets the score to 0
        quizScoreList.clear()
    }
}
