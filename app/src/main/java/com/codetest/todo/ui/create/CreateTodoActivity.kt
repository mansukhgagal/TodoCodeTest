package com.codetest.todo.ui.create

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.codetest.todo.R
import com.codetest.todo.app.BaseActivity
import com.codetest.todo.databinding.ActivityCreateTodoBinding
import com.codetest.todo.network.Resource
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

@AndroidEntryPoint
class CreateTodoActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityCreateTodoBinding
    private val viewModel: TodoViewModel by viewModels()
    private var selectedTime:String?=null
    private var selectedDate:Long?=null

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initControls()
        handleIntent()
    }

    private fun handleIntent() {
        intent?.let { _intent->
            val todoData: TodoModel? = _intent.getParcelableExtra(Constants.KEY_DATA)
            todoData?.apply {
                binding.etTitle.setText(title)
                binding.etDescription.setText(description)
                binding.etTime.setText(time)
                binding.etDate.setText(Utility.getFormattedDate(date))
            }
        }
    }

    private fun initControls() {
        binding.includeToolbar.toolbar.setNavigationIcon(R.drawable.ic_back)
        setToolbarTitle(binding.includeToolbar.toolbar,getString(R.string.toolbar_title_create_todo))
        subscribeUI()
        binding.etTime.setOnClickListener(this)
        binding.etDate.setOnClickListener(this)
        binding.fabAdd.setOnClickListener(this)
    }

    private fun subscribeUI() {
        viewModel.liveDataInsert.observe(this, Observer {
            when(it) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    val resultIntent = Intent()
                    resultIntent.putExtra(Constants.KEY_DATA,it.data)
                    setResult(Activity.RESULT_OK,resultIntent)
                    finish()
                }
                is Resource.Error -> {
                    SnackBarHelper.infoSnackBar(binding.root,getString(R.string.error_in_adding_todo),null,null)
                }
            }
        })
    }

    private fun getTodoTitle(): String = binding.etTitle.text.toString()
    private fun getTodoDescription(): String = binding.etDescription.text.toString()
    private fun getTodoTime(): String = binding.etTitle.text.toString()
    private fun getTodoDate(): String = binding.etDate.text.toString()
    private fun getTodoType(): Int = binding.radioLayout.checkedRadioButtonId

    private fun createTodo() {
        val title = getTodoTitle()
        val description = getTodoDescription()
        val type =  when(getTodoType()) {
            R.id.rb_daily-> TYPE_DAILY
            R.id.rb_weekly-> TYPE_WEEKLY
            else-> TYPE_UNKNOWN
        }

        binding.tilTitle.error = null
        binding.tilDescription.error = null
        binding.tilTime.error = null
        binding.tilDate.error = null

        if (title.isEmpty()) {
            binding.tilTitle.error = getString(R.string.error_enter_title)
            binding.etTitle.requestFocus()
        } else if (selectedTime.isNullOrEmpty()) {
            binding.tilTime.error = getString(R.string.error_select_time)
            binding.etTime.requestFocus()
        } else if (type == TYPE_UNKNOWN) {
            SnackBarHelper.infoSnackBar(
                binding.root,
                getString(R.string.error_select_type),
                null,
                null
            )
        } else {
            hideKeyboard()
            val data = TodoModel(null,title, description, selectedTime!!, selectedDate, type)
            viewModel.insertTodo(data)
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
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(10)
                .setTitleText(getString(R.string.title_select_time))
                .build()
        picker.show(supportFragmentManager,"timePicker")


        picker.addOnPositiveButtonClickListener {
            val hour = picker.hour
            val minute = picker.minute
            Timber.d("hour ${hour}:${minute}")
            selectedTime = "${hour}:${minute}"
            binding.etTime.setText(Utility.getFormattedTime(hour,minute))
            // call back code
        }
        picker.addOnNegativeButtonClickListener {
            // call back code
        }
        picker.addOnCancelListener {
            // call back code
        }
        picker.addOnDismissListener {
            // call back code
        }
    }

    private fun openDatePicker() {
        val picker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.title_select_date))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        picker.show(supportFragmentManager,"datePicker")


        picker.addOnPositiveButtonClickListener {
            Timber.d("headerText ${picker.headerText}")
            Timber.d("headerText ${it}")
            selectedDate = it
            binding.etDate.setText(Utility.getFormattedDate(it))
            // call back code
        }
        picker.addOnNegativeButtonClickListener {
            // call back code
        }
        picker.addOnCancelListener {
            // call back code
        }
        picker.addOnDismissListener {
            // call back code
        }
    }
}