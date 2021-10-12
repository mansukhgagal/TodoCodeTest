package com.codetest.todo.ui.login

import android.app.Application
import androidx.lifecycle.*
import com.codetest.todo.R
import com.codetest.todo.app.BaseApplication
import com.codetest.todo.data.ApiResponse
import com.codetest.todo.network.Resource
import com.codetest.todo.utils.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private typealias API_RES = Resource<ApiResponse>

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: LoginRepository,application: Application) : AndroidViewModel(application) {

    private val _mutableLiveDataLogin = MutableLiveData<API_RES>()
    val liveDataLogin: LiveData<API_RES>
        get() = _mutableLiveDataLogin

    val baseApp = getApplication<BaseApplication>()

    fun doLogin(userData: UserData) {
        viewModelScope.launch {
            try {
                val response = repository.doLogin(userData)
                _mutableLiveDataLogin.postValue(Resource.Success(response))
            } catch (e: Exception) {
                e.printStackTrace()
                val errorMessage = if(Utility.isInternetAvailable())
                    e.message ?: baseApp.getString(R.string.error_something_went_wrong)
                else
                    baseApp.getString(R.string.error_no_internet)

                _mutableLiveDataLogin.postValue(Resource.Error(errorMessage))
            }
        }
    }
}