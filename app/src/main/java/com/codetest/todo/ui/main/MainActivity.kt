package com.codetest.todo.ui.main

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import com.codetest.todo.R
import com.codetest.todo.app.BaseActivity
import com.codetest.todo.databinding.ActivityMainBinding
import com.codetest.todo.network.Resource
import com.codetest.todo.ui.create.CreateTodoActivity
import com.codetest.todo.ui.create.TodoModel
import com.codetest.todo.ui.create.TodoViewModel
import com.codetest.todo.utils.Constants.KEY_DATA
import com.codetest.todo.utils.EqualSpacingItemDecoration
import com.codetest.todo.utils.convertDpToPixel
import com.codetest.todo.utils.hide
import com.codetest.todo.utils.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: TodoViewModel by viewModels()
    private val todoList = mutableListOf<TodoModel>()
    private var todoAdapter: TodoAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initControls()
    }

    private fun initControls() {
        setToolbarTitle(binding.includeToolbar.toolbar,getString(R.string.toolbar_title_todo_code))

        binding.recyclerView.addItemDecoration(
            EqualSpacingItemDecoration(
                convertDpToPixel(16f),
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        todoAdapter = TodoAdapter(this)
        binding.recyclerView.adapter = todoAdapter

        viewModel.getAllTodoList(0)
        subscribeUI()

        binding.fabAdd.setOnClickListener {
            createTodo()
        }
    }

    private fun subscribeUI() {
        viewModel.liveDataTodoList.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    hideError()
                }
                is Resource.Success -> {
                    val list = it.data
                    if (list.isNullOrEmpty()) {
                        showError(getString(R.string.error_no_todo))
                    } else {
                        todoList.addAll(list)
                        todoAdapter?.submitList(todoList)
                    }
                }
                is Resource.Error -> {
                    if (todoList.isEmpty()) {
                        showError(it.message)
                    }
                }
            }
        })
    }

    private fun showError(message: String?) {
        message?.let {
            binding.textErrorMessage.show()
            binding.textErrorMessage.text = it
        }
    }

    private fun hideError() {
        binding.textErrorMessage.hide()
    }

    private fun createTodo() {
        val intent = Intent(this, CreateTodoActivity::class.java)
        resultLauncher.launch(intent)
    }

    val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val todoData: TodoModel? = result.data?.getParcelableExtra(KEY_DATA)
                todoData?.let {
                    todoList.add(0,it)
                    todoAdapter?.submitList(todoList)
//                    todoAdapter?.notifyItemInserted(0)
                }
            }
        }

}