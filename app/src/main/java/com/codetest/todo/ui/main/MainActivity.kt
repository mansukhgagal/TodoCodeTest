package com.codetest.todo.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codetest.todo.R
import com.codetest.todo.app.BaseActivity
import com.codetest.todo.databinding.ActivityMainBinding
import com.codetest.todo.network.Resource
import com.codetest.todo.ui.create.CreateTodoActivity
import com.codetest.todo.ui.create.TodoModel
import com.codetest.todo.ui.create.TodoViewModel
import com.codetest.todo.utils.*
import com.codetest.todo.utils.Constants.KEY_DATA
import com.codetest.todo.utils.Constants.KEY_IS_UPDATE
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

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
        setToolbarTitle(binding.includeToolbar.toolbar, getString(R.string.toolbar_title_todo_code))
        binding.includeToolbar.toolbar.setNavigationOnClickListener(null)

        binding.recyclerView.addItemDecoration(
            EqualSpacingItemDecoration(
                convertDpToPixel(16f),
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        todoAdapter = TodoAdapter(this, todoList)
        binding.recyclerView.adapter = todoAdapter

        val scrollListener = object :
            EndlessRecyclerViewScrollListener(binding.recyclerView.layoutManager as LinearLayoutManager) {
            override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(view, dx, dy)
                if (dy > 0) {
                    binding.fabAdd.hide()
                } else {
                    binding.fabAdd.show()
                }
            }

            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                fetchSavedTodo(totalItemsCount)
            }
        }

        binding.recyclerView.addOnScrollListener(scrollListener)


        subscribeUI()
        fetchSavedTodo()
        binding.fabAdd.setOnClickListener {
            createOrUpdateTodo(null)
        }
    }

    private fun fetchSavedTodo(offset: Int = 0) {
        viewModel.getAllTodoList(offset)
    }

    private fun subscribeUI() {
        viewModel.liveDataTodoList.observe(this, {
            when (it) {
                is Resource.Loading -> {
                    hideError()
                }
                is Resource.Success -> {
                    val list = it.data
                    val startIndex = todoList.size
                    val itemCount = list?.size ?: 0
                    if (list.isNullOrEmpty() && startIndex == 0) {
                        showError(getString(R.string.error_no_todo))
                    } else {
                        todoList.addAll(list!!)
                        todoAdapter?.notifyItemRangeInserted(startIndex, itemCount)
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

    fun createOrUpdateTodo(todo: TodoModel?) {
        Timber.d("hash ${todo?.hashCode()}")
        val detailIntent = Intent(this, CreateTodoActivity::class.java)
        todo?.let { detailIntent.putExtra(KEY_DATA, it) }
        resultLauncher.launch(detailIntent)
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val todoData: TodoModel? = result.data?.getParcelableExtra(KEY_DATA)
                val isUpdate: Boolean = result.data!!.getBooleanExtra(KEY_IS_UPDATE, false)
                todoData?.let {
                    binding.recyclerView.postDelayed({
                        if (isAlive()) {
                            val message: String
                            if (isUpdate) {
//                                val updatedIndex = todoList.indexOf(it)
                                for (item in todoList) {
                                    if(it.id == item.id) {
                                        val updatedIndex = todoList.indexOf(item)
                                        todoList[updatedIndex] = it
                                        todoAdapter?.notifyItemChanged(updatedIndex)
                                        break
                                    }
                                }
                                message = getString(R.string.success_todo_updated)
                            } else {
                                hideError()
                                todoList.add(0, it)
                                todoAdapter?.notifyItemInserted(0)
                                binding.recyclerView.smoothScrollToPosition(0)
                                message = getString(R.string.success_todo_created)
                            }
                            SnackBarHelper.infoSnackBar(
                                binding.root,
                                message,
                                null,
                                null
                            )
                        }
                    }, 500)
                }
            }
        }

    internal fun alertDelete(todo: TodoModel?) {
        todo ?: return
        val alertBuilder = MaterialAlertDialogBuilder(this)
            .setPositiveButton(getString(R.string.button_yes)) { dialog, _ ->
                val position = todoList.indexOf(todo)
                todoList.removeAt(position)
                todoAdapter?.notifyItemRemoved(position)
                viewModel.deleteTodo(todo)
                if (todoList.isEmpty()) {
                    showError(getString(R.string.error_no_todo))
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.button_no)) { dialog, _ ->
                dialog.dismiss()
            }
            .setMessage(getString(R.string.alert_message_delete_todo))
        alertBuilder.create().show()

    }
}