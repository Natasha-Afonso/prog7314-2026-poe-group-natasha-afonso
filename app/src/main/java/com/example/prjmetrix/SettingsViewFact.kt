package com.example.prjmetrix

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

//(Simplified Coding, 2021)
class SettingsViewFact(private val context: Context) : ViewModelProvider.Factory {
    //(Saeed, 2019)
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = SettingsRepository(context.applicationContext)
        return SettingsViewModel(repository) as T //new instance for SettingsViewModel
    }
}



/*References
*
* Saeed, M., 2019. ViewModel with ViewModelProvider.Factpry: The creator of ViewModel. (Version 10.0) [source code]. Available at: < https://medium.com/koderlabs/viewmodel-with-viewmodelprovider-factory-the-creator-of-viewmodel-8fabfec1aa4f > [Accessed 19 September 2026]

* */