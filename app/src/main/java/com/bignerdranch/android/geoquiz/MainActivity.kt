package com.bignerdranch.android.geoquiz

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
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
private const val REQUEST_CODE_CHEAT =
    0 // user defined integer that is sent to the child activity and then received back by the parent
// It is used when an activity starts more than one type of child activity and needs to know who is reporting back

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var cheatButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var prevButton: ImageButton
    private lateinit var cheatCountTextView: TextView

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
        cheatButton = findViewById(R.id.cheat_button)
        nextButton = findViewById(R.id.next_button)
        questionTextView = findViewById(R.id.question_text_view)
        prevButton = findViewById(R.id.prev_button)
        cheatCountTextView = findViewById(R.id.cheat_available_text_view)

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
            quizViewModel.isCheater = false
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

        cheatButton.setOnClickListener { view ->
            // Start CheatActivity
            // val intent = Intent(this, CheatActivity::class.java)
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            quizViewModel.cheatAvailable = quizViewModel.cheatAvailable - 1 // reduces the number of times for cheating available with each click of cheatButton
            cheatCountTextView.text = quizViewModel.cheatAvailable.toString()
            if (quizViewModel.cheatAvailable == 0) {
                cheatButton.isEnabled = false
            }
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // SDK_INT constant is the device's version of Android, M stands for Marshmallow
                val options =
                    ActivityOptions.makeClipRevealAnimation(view, 0, 0, view.width, view.height) // method introduced in SDK API level 23
                // startActivity(intent)
                startActivityForResult(
                    intent,
                    REQUEST_CODE_CHEAT,
                    options.toBundle(),
                )
            } else {
                startActivityForResult(intent, REQUEST_CODE_CHEAT)
            }
            // associates the CheatActivity and MainActivity with the code
        }

        // puts the first question in the text view
        updateQuestion()
        cheatCountTextView.text = quizViewModel.cheatAvailable.toString() // initial set up for number of cheatings available
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /* this method pulls the value out of the result sent back from CheatActivity */
        super.onActivityResult(requestCode, resultCode, data)

        // Log.i(TAG, "OnActivityResult called, $requestCode")

        if (requestCode == REQUEST_CODE_CHEAT) { // If it is the same activity which was called initially
            // Log.i(TAG, quizViewModel.isCheater.toString())
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            Log.i(TAG, "onActivityResult requestCode ${quizViewModel.isCheater}")
        }

        if (resultCode != Activity.RESULT_OK) { // if user had pressed back button from CheatActivity, it would be RESULT_CANCELED
            return
        }
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
        val questionTextResId =
            quizViewModel.currentQuestionText // the resource id for the question
        questionTextView.setText(questionTextResId) // sets the question textview with the question on the index
    }

    private fun checkAnswer(userAnswer: Boolean) {
        /* Checks the answer stored in the list with the user's answer */
        val correctAnswer =
            quizViewModel.currentQuestionAnswer // gets the answer for the question in the list

        if (userAnswer == correctAnswer) { // if answer is correct, score is updated
            correctAnswerFlag = true
            quizScore++
        } else {
            correctAnswerFlag = false
        }
        /*
        val messageResId = if (userAnswer == correctAnswer) {
            R.string.correct_toast // correct answer toast is set
        } else {
            R.string.incorrect_toast
        }
         */
        Log.i(
            TAG,
            "checkAnswer cheatStatus ${quizViewModel.isCheater}",
        )
        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
            .show() // shows the user , toast with message whether they are correct or not
    }

    private fun buttonDisable(enable: Boolean) {
        /* This function disables or enables the true and false choice button */
        //  it needs to maintain status of button - enabled or disable on orientation changes
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
