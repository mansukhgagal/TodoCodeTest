package com.codetest.todo.ui.create

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.codetest.todo.R
import com.codetest.todo.app.BaseActivity
import com.codetest.todo.databinding.ActivityCreateTodoBinding
import com.codetest.todo.network.Resource
import com.codetest.todo.service.AlarmWork
import com.codetest.todo.utils.Constants
import com.codetest.todo.utils.Constants.TYPE_DAILY
import com.codetest.todo.utils.Constants.TYPE_UNKNOWN
import com.codetest.todo.utils.Constants.TYPE_WEEKLY
import com.codetest.todo.utils.SnackBarHelper
import com.codetest.todo.utils.Utility
import com.codetest.todo.utils.hideKeyboard
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class CreateTodoActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityCreateTodoBinding
    private val viewModel: TodoViewModel by viewModels()
    private var selectedTime: String? = null
    private var selectedDate: Long? = null
    private var updateItemId: Int? = null
    private var todoData: TodoModel? = null

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        handleIntent()
        initControls()
    }

    private fun handleIntent() {
        intent?.let { _intent ->
            todoData = _intent.getParcelableExtra(Constants.KEY_DATA)
            Timber.d("hash ${todoData.hashCode()}")
            todoData?.apply {
                updateItemId = id
                binding.etTitle.setText(title)
                binding.etDescription.setText(description)
                binding.etTime.setText(time)
                binding.etDate.setText(Utility.getFormattedDate(date))
                selectedTime = time
                selectedDate = date
                when (type) {
                    TYPE_DAILY -> binding.rbDaily.isChecked = true
                    TYPE_WEEKLY -> binding.rbWeekly.isChecked = true
                }
            }
        }
    }

    private fun initControls() {
        binding.includeToolbar.toolbar.setNavigationIcon(R.drawable.ic_back)
        val toolbarTitle = if (updateItemId == null) getString(R.string.toolbar_title_create_todo)
        else getString(R.string.toolbar_title_update_todo)
        setToolbarTitle(
            binding.includeToolbar.toolbar,
            toolbarTitle
        )
        subscribeUI()
        binding.etTime.setOnClickListener(this)
        binding.etDate.setOnClickListener(this)
        binding.fabAdd.setOnClickListener(this)
    }

    private fun subscribeUI() {
        viewModel.liveDataInsert.observe(this, {
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    val resultIntent = Intent()
                    resultIntent.putExtra(Constants.KEY_IS_UPDATE, updateItemId != null)
                    resultIntent.putExtra(Constants.KEY_DATA, it.data)
                    Timber.d("hash ${it.data?.hashCode()}")
                    setResult(Activity.RESULT_OK, resultIntent)

                    //set alarm request
                   scheduleAlarmForTodo(it.data)

                    finish()
                }
                is Resource.Error -> {
                    SnackBarHelper.infoSnackBar(
                        binding.root,
                        getString(R.string.error_in_adding_todo),
                        null,
                        null
                    )
                }
            }
        })
    }

    private fun getTodoTitle(): String = binding.etTitle.text.toString()
    private fun getTodoDescription(): String = binding.etDescription.text.toString()
    private fun getTodoType(): Int = binding.radioLayout.checkedRadioButtonId

    private fun scheduleAlarmForTodo(data:TodoModel?) {
        data?.apply {
            val dataBuilder = Data.Builder()
            id?.let { _id -> dataBuilder.putInt(Constants.KEY_ALARM_ID, _id) }
            date?.let { _date -> dataBuilder.putLong(Constants.KEY_DATE, _date) }
            dataBuilder.putString(Constants.KEY_TIME, time)
            dataBuilder.putInt(Constants.KEY_TYPE, type!!)
            val alarmWork: WorkRequest = OneTimeWorkRequestBuilder<AlarmWork>()
                .setInputData(dataBuilder.build())
                .build()
            WorkManager.getInstance(this@CreateTodoActivity).enqueue((alarmWork))
        }
    }

    private fun createTodo() {
        val title = getTodoTitle()
        val description = getTodoDescription()
        val type = when (getTodoType()) {
            R.id.rb_daily -> TYPE_DAILY
            R.id.rb_weekly -> TYPE_WEEKLY
            else -> TYPE_UNKNOWN
        }

        binding.tilTitle.error = null
        binding.tilDescription.error = null
        binding.tilTime.error = null
        binding.tilDate.error = null

        if (TodoModel.invalidTitle(title)) {
            binding.tilTitle.error = getString(R.string.error_enter_title)
            binding.etTitle.requestFocus()
        } else if (TodoModel.invalidTime(selectedTime)) {
            binding.tilTime.error = getString(R.string.error_select_time)
        } else if (TodoModel.invalidDate(selectedDate)) {
            binding.tilDate.error = getString(R.string.error_select_date)
        } else if (TodoModel.invalidType(type)) {
            SnackBarHelper.infoSnackBar(
                binding.root,
                getString(R.string.error_select_type),
                null,
                null
            )
        } else {
            hideKeyboard()
            if (updateItemId == null) {
                todoData =
                    TodoModel(updateItemId, title, description, selectedTime!!, selectedDate, type)
                viewModel.insertTodo(todoData!!)
            } else {
                todoData?.apply {
                    this.title = title
                    this.description = description
                    time = selectedTime!!
                    date = selectedDate
                    this.type = type
                }
                viewModel.updateTodo(todoData!!)
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.fab_add -> {
                createTodo()
            }
            R.id.et_time -> {
                hideKeyboard()
                openTimePicker()
            }
            R.id.et_date -> {
                hideKeyboard()
                openDatePicker()
            }
        }
    }

    private fun openTimePicker() {

        val picker =
            MaterialTimePicker.Builder().apply {
                setTimeFormat(TimeFormat.CLOCK_12H)
                if (updateItemId == null) {
                    val calender = Calendar.getInstance()
                    calender.timeInMillis = System.currentTimeMillis()
                    setHour(calender.get(Calendar.HOUR_OF_DAY))
                    setMinute(calender.get(Calendar.MINUTE))
                } else {
                    Utility.getHoursAndMinute(todoData?.time)?.let {
                        setHour(it.first)
                        setMinute(it.second)
                    }
                }
                setTitleText(getString(R.string.title_select_time))
            }
                .build()
        picker.show(supportFragmentManager, "timePicker")


        picker.addOnPositiveButtonClickListener {
            val hour = picker.hour
            val minute = picker.minute
            Timber.d("hour ${hour}:${minute}")
            selectedTime = "${hour}:${minute}"
            binding.etTime.setText(Utility.getFormattedTime(hour, minute))
        }
    }

    private fun openDatePicker() {
        val picker =
            MaterialDatePicker.Builder.datePicker().apply {
                setTitleText(getString(R.string.title_select_date))
                todoData?.date?.let {
                    setSelection(it)
                } ?: kotlin.run {
                    setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                }
            }
                .build()
        picker.show(supportFragmentManager, "datePicker")

        picker.addOnPositiveButtonClickListener {
            Timber.d("headerText ${picker.headerText}")
            Timber.d("headerText $it")
            selectedDate = it
            binding.etDate.setText(Utility.getFormattedDate(it))
        }
    }
}