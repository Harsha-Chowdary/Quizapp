package com.quizapp

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.quizapp.databinding.ActivityQuizactivityBinding
import com.quizapp.databinding.QuizItemRecyclerRowBinding
import com.quizapp.databinding.ScoreDialogBinding

class quizactivity : AppCompatActivity(),View.OnClickListener {

    companion object {
        var questionModelList: List<QuestionModel> = listOf()
        var timer: String = ""
    }

    lateinit var binding: ActivityQuizactivityBinding

    var currentQuestionIndex = 0;
    var selectedans = ""
    var score = 0;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizactivityBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.apply {
            button0.setOnClickListener(this@quizactivity)
            button1.setOnClickListener(this@quizactivity)
            button2.setOnClickListener(this@quizactivity)
            button3.setOnClickListener(this@quizactivity)
            nextBtn.setOnClickListener(this@quizactivity)
        }
        loadQuestions()
        startTimer()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun startTimer() {
        val totalTimeInMillis = timer.toInt() * 60 * 1000L
        object : CountDownTimer(totalTimeInMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val min = seconds / 60
                val remaningseconds = seconds % 60
                binding.timerIndicatorTextview.text =
                    String.format("%02d:%02d", min, remaningseconds)
            }

            override fun onFinish() {
                ///finish the quiz
            }

        }.start()
    }

    private fun loadQuestions() {

        selectedans = ""

        if (currentQuestionIndex == questionModelList.size) {
            finishQuiz()
            return
        }

        binding.apply {
            questionIndicatorTextview.text =
                "Question ${currentQuestionIndex + 1}/ ${questionModelList.size}"
            questionProgressIndicator.progress =
                (currentQuestionIndex.toFloat() / questionModelList.size.toFloat() * 100).toInt()
            questionTextview.text = questionModelList[currentQuestionIndex].question
            button0.text = questionModelList[currentQuestionIndex].options[0]
            button1.text = questionModelList[currentQuestionIndex].options[1]
            button2.text = questionModelList[currentQuestionIndex].options[2]
            button3.text = questionModelList[currentQuestionIndex].options[3]
        }

    }

    override fun onClick(view: View?) {

        binding.apply {
            button0.setBackgroundColor(getColor(R.color.gray))
            button1.setBackgroundColor(getColor(R.color.gray))
            button2.setBackgroundColor(getColor(R.color.gray))
            button3.setBackgroundColor(getColor(R.color.gray))
        }
        val clickbtn = view as Button
        if (clickbtn.id == R.id.next_btn) {
            //next button has been clicked
            if (selectedans.isEmpty()){
                Toast.makeText(applicationContext,"Please select answer to continue",Toast.LENGTH_SHORT).show()
                return;
            }
            if (selectedans == questionModelList[currentQuestionIndex].correct) {
                score++
            }
            currentQuestionIndex++
            loadQuestions()
        } else {
            // options btn has been clicked

            selectedans = clickbtn.text.toString()
            clickbtn.setBackgroundColor(getColor(R.color.orange))
        }
    }

    private fun finishQuiz() {
        val totalQuestions = questionModelList.size
        val percentage = ((score.toFloat() / totalQuestions.toFloat()) * 100).toInt()

        val dialogBinding = ScoreDialogBinding.inflate(layoutInflater)
        dialogBinding.apply {
            scoreProgressIndicator.progress = percentage
            scoreProgressText.text = "$percentage %"
            if (percentage > 60) {
                scoreTitle.text = "Congrats you have passed"
                scoreTitle.setTextColor(Color.BLUE)

            } else {
                scoreTitle.text = "Oops! You have failed"
                scoreTitle.setTextColor(Color.RED)
            }
            scoreSubtitle.text = "$score out of $totalQuestions are correct"
            finishBtn.setOnClickListener{
                finish()
            }
        }
        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .show()
    }
}