package com.codetest.todo.ui.main

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codetest.todo.R
import com.codetest.todo.databinding.ItemTodoBinding
import com.codetest.todo.ui.create.CreateTodoActivity
import com.codetest.todo.ui.create.TodoModel
import com.codetest.todo.utils.Constants
import com.codetest.todo.utils.Utility
import com.codetest.todo.utils.invisible
import com.codetest.todo.utils.show
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TodoAdapter(val context: Context,val todoList:MutableList<TodoModel>) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    inner class TodoViewHolder(private val binding: ItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        fun bindData(data: TodoModel) {
            binding.apply {

                textTodoTitle.text = data.title

                textTime.text = Utility.getDisplayFormattedTime(data.time)

                data.date?.let {
                    textDate.text = Utility.getFormattedDate(it)
                    textDate.show()
                } ?: kotlin.run {
                    textDate.invisible()
                }

                textTodoDescription.text = data.description

                textType.show()
                when(data.type) {
                    Constants.TYPE_DAILY->textType.setText(context.getString(R.string.type_daily))
                    Constants.TYPE_WEEKLY->textType.setText(context.getString(R.string.type_weekly))
                    else -> textType.invisible()
                }

                card.setOnClickListener(this@TodoViewHolder)
                ibMore.setOnClickListener(this@TodoViewHolder)
            }
        }

        override fun onClick(view: View?) {
            val data = getItem(adapterPosition)
            when(view?.id) {
                R.id.card->{
                    val detailIntent = Intent(context, CreateTodoActivity::class.java)
                    detailIntent.putExtra(Constants.KEY_DATA, data)
                    context.startActivity(detailIntent)
                }
                R.id.ib_more->{
                    (context as? MainActivity)?.alertDelete(data)
                }
            }
        }
    }

    /*val diffCallback = object : DiffUtil.ItemCallback<TodoModel>() {
        override fun areItemsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<TodoModel>) = differ.submitList(list)*/


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            ItemTodoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = todoList[position]
        holder.bindData(todo)
    }

    fun getItem(position: Int): TodoModel? {
        if (position == RecyclerView.NO_POSITION) return null
        return try {
            todoList[position]
        } catch (ex: IndexOutOfBoundsException) {
            null
        }
    }
}














