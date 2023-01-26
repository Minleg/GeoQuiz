package com.bignerdranch.android.geoquiz

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var prevButton: ImageButton

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
        setContentView(R.layout.activity_main)

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        questionTextView = findViewById(R.id.question_text_view)
        prevButton = findViewById(R.id.prev_button)

        trueButton.setOnClickListener {
            checkAnswer(true)
        }

        falseButton.setOnClickListener {
            checkAnswer(false)
        }

        nextButton.setOnClickListener {
            currentIndex = (currentIndex + 1) % questionBank.size
            updateQuestion()
        }

        // next question can be loaded by clicking the question text view
        questionTextView.setOnClickListener {
            currentIndex = (currentIndex + 1) % questionBank.size
            updateQuestion()
        }

        // if previous button is clicked
        prevButton.setOnClickListener {
            if (currentIndex == 0) { // if index is 0, point to the last question in the list
                currentIndex = questionBank.size - 1
            } else { // point to the previous question otherwise
                currentIndex -= 1
            }
            updateQuestion()
        }

        // puts the first question in the text view
        updateQuestion()
    }

    private fun updateQuestion() { // gets the question at the index
        val questionTextResId =
            questionBank[currentIndex].textResId // the resource id for the question
        questionTextView.setText(questionTextResId) // sets the question textview with the question on the index
    }

    private fun checkAnswer(userAnswer: Boolean) {
        /* Checks the answer stored in the list with the user's answer */
        val correctAnswer = questionBank[currentIndex].answer // gets the answer for the question in the list

        val messageResId = if (userAnswer == correctAnswer) {
            R.string.correct_toast // correct answer toast is set
        } else {
            R.string.incorrect_toast
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show() // shows the user , toast with message whether they are correct or not
    }
}
