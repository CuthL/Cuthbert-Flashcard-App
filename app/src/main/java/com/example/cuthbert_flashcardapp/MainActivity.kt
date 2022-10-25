package com.example.cuthbert_flashcardapp

import android.annotation.SuppressLint
import android.app.ProgressDialog.show
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import java.lang.Math.hypot

class MainActivity : AppCompatActivity() {

    lateinit var flashcardDatabase: FlashcardDatabase
    var allFlashcards = mutableListOf<Flashcard>()

    var currentCardDisplayedIndex = 0

    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        flashcardDatabase = FlashcardDatabase(this)
        allFlashcards = flashcardDatabase.getAllCards().toMutableList()


        val flashcardQuestion = findViewById<TextView>(R.id.flashcard_question)
        val flashcardAnswer = findViewById<TextView>(R.id.flashcard_answer)

        if (allFlashcards.size > 0) {
            flashcardQuestion.text = allFlashcards[0].question
            flashcardAnswer.text = allFlashcards[0].answer
        }


        flashcardQuestion.setOnClickListener {

// get the center for the clipping circle

// get the center for the clipping circle
            val cx = flashcardAnswer.width / 2
            val cy = flashcardAnswer.height / 2

// get the final radius for the clipping circle

// get the final radius for the clipping circle
            val finalRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()

// create the animator for this view (the start radius is zero)

// create the animator for this view (the start radius is zero)
            val anim =
                ViewAnimationUtils.createCircularReveal(flashcardAnswer, cx, cy, 0f, finalRadius)

// hide the question and show the answer to prepare for playing the animation!

// hide the question and show the answer to prepare for playing the animation!
            flashcardQuestion.visibility = View.INVISIBLE
            flashcardAnswer.visibility = View.VISIBLE

            anim.duration = 3000
            anim.start()
        }

        flashcardAnswer.setOnClickListener {
            flashcardQuestion.visibility = View.VISIBLE
            flashcardAnswer.visibility = View.INVISIBLE
        }


        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
                { result ->
                    val data: Intent? = result.data
                    val extras = data?.extras

                    if (data != null) {
                        val questionString = data.getStringExtra("string1")
                        val answerString = data.getStringExtra("string2")


                        flashcardQuestion.text = questionString
                        flashcardAnswer.text = answerString


                        Log.i("MainActivity", "question: $questionString")
                        Log.i("MainActivity", "answer: $answerString")

                            findViewById<TextView>(R.id.flashcard_question).text = questionString
                            findViewById<TextView>(R.id.flashcard_answer).text = answerString

                        if (questionString != null && answerString != null) {
                            flashcardDatabase.insertCard(Flashcard(questionString, answerString))
                            allFlashcards = flashcardDatabase.getAllCards().toMutableList()
                        } else {
                            Log.e("TAG", "Missing question or answer to input into database. Question is $questionString and answer is $answerString")
                        }


                    } else {
                        Log.i("MainActivity", "Returned null data from AddCardActivity")
                    }

                }

                val addQuestionButton = findViewById<ImageView>(R.id.add_question_button)
                addQuestionButton.setOnClickListener {
                    val intent = Intent(this, AddCardActivity::class.java)
                    resultLauncher.launch(intent)

                }

        val nextButton = findViewById<View>(R.id.next_button)
            nextButton.setOnClickListener{
            if (allFlashcards.isEmpty()) {
                return@setOnClickListener
            }


                val leftOutAnim = AnimationUtils.loadAnimation(it.context, R.anim.left_out)
                val rightInAnim = AnimationUtils.loadAnimation(it.context, R.anim.right_in)

                leftOutAnim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {

                    }
                    override fun onAnimationEnd(animation: Animation?) {
                        flashcardQuestion.startAnimation(rightInAnim)

                        currentCardDisplayedIndex++

                        if(currentCardDisplayedIndex >= allFlashcards.size) {
                            Snackbar.make(
                                findViewById<TextView>(R.id.flashcard_question),
                                "You've reached the end of the cards, going back to start.",
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                            currentCardDisplayedIndex = 0
                        }

                        allFlashcards = flashcardDatabase.getAllCards().toMutableList()

                        val question = allFlashcards[currentCardDisplayedIndex].question
                        val answer = allFlashcards[currentCardDisplayedIndex].answer

                        flashcardQuestion.text = question
                        flashcardAnswer.text = answer

                    }
                    override fun onAnimationRepeat(animation: Animation?) {

                    }
                })
                flashcardQuestion.startAnimation(leftOutAnim)
            }
            }


    }






