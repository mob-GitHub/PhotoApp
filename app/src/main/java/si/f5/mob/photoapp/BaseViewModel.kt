package si.f5.mob.photoapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {
    private val _error = MutableLiveData<Throwable?>(null)
    val error: LiveData<Throwable?>
        get() = _error

    fun setError(t: Throwable) {
        _error.postValue(t)
    }

    fun clearError() {
        _error.postValue(null)
    }
}