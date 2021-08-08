package com.dicoding.habitapp.ui.countdown

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.Data.Builder
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.dicoding.habitapp.R
import com.dicoding.habitapp.data.Habit
import com.dicoding.habitapp.notification.NotificationWorker
import com.dicoding.habitapp.utils.HABIT
import com.dicoding.habitapp.utils.HABIT_TITLE

class CountDownActivity : AppCompatActivity() {

    private lateinit var tvCountDown: TextView
    private lateinit var tvCountDownTitle: TextView
    private lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_down)
        supportActionBar?.title = "Count Down"

        tvCountDown = findViewById(R.id.tv_count_down)
        tvCountDownTitle = findViewById(R.id.tv_count_down_title)
        val habit = intent.getParcelableExtra<Habit>(HABIT) as Habit

        tvCountDownTitle.text = habit.title
        workManager = WorkManager.getInstance(this)
        val viewModel = ViewModelProvider(this).get(CountDownViewModel::class.java)

        //TODO 10 : Set initial time and observe current time. Update button state when countdown is finished
        viewModel.setInitialTime(habit.minutesFocus)
        viewModel.currentTimeString.observe(this, {
            tvCountDown.text = it
        })
        viewModel.eventCountDownFinish.observe(this, {
            startOneTime()
            updateButtonState(it)
        })
        //TODO 13 : Start and cancel One Time Request WorkManager to notify when time is up.

        findViewById<Button>(R.id.btn_start).setOnClickListener {
            viewModel.startTimer()
            updateButtonState(true)
        }

        findViewById<Button>(R.id.btn_stop).setOnClickListener {
            viewModel.resetTimer()
            cancelOneTime()
            updateButtonState(false)
        }
    }

    private fun startOneTime() {
        val data = Builder()
                .putString(HABIT_TITLE, tvCountDownTitle.text.toString())
                .build()
        val constraints = Constraints.Builder()
                .build()
        val oneTimeRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                .setInputData(data)
                .addTag(TAG)
                .setConstraints(constraints)
                .build()
        workManager.enqueue(oneTimeRequest)
        workManager.getWorkInfosByTag(TAG)

    }

    private fun updateButtonState(isRunning: Boolean) {
        findViewById<Button>(R.id.btn_start).isEnabled = !isRunning
        findViewById<Button>(R.id.btn_stop).isEnabled = isRunning
    }

    private fun cancelOneTime() {
        workManager.cancelAllWorkByTag(TAG)
    }

    companion object {
        private val TAG = CountDownActivity::class.java.simpleName
    }
}