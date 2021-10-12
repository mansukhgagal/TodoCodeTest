package com.codetest.todo.ui.create

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.codetest.todo.R
import com.codetest.todo.app.BaseApplication
import com.codetest.todo.network.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private typealias LIST_RESULT = Resource<List<TodoModel>>
private typealias INSERT_RESULT = Resource<TodoModel>

@HiltViewModel
class TodoViewModel @Inject constructor(private val repository: TodoRepository,
                                         application: Application)
    : AndroidViewModel(application) {

    private val _mutableLiveDataTodoList = MutableLiveData<LIST_RESULT>()
    val liveDataTodoList: LiveData<LIST_RESULT>
        get() = _mutableLiveDataTodoList

    private val _mutableLiveDataInsert = MutableLiveData<INSERT_RESULT>()
    val liveDataInsert: LiveData<INSERT_RESULT>
        get() = _mutableLiveDataInsert

    val baseApp = getApplication<BaseApplication>()

    fun getAllTodoList(offset:Int=0) {
        viewModelScope.launch {
            try {
                val list = repository.getAllTodoList(offset)
                _mutableLiveDataTodoList.postValue(Resource.Success(list))
            } catch (e: Exception) {
                e.printStackTrace()
                _mutableLiveDataTodoList.postValue(Resource.Error(e.message ?: baseApp.getString(R.string.error_something_went_wrong),null))
            }
        }
    }

    fun insertTodo(data:TodoModel) {
        viewModelScope.launch {
            try {
                val returnId = repository.insertTodo(data)
                data.id = returnId
                _mutableLiveDataInsert.postValue(Resource.Success(data))
            } catch (e: Exception) {
                e.printStackTrace()
                _mutableLiveDataInsert.postValue(Resource.Error(e.message ?: baseApp.getString(R.string.error_something_went_wrong) ))
            }
        }
    }
}