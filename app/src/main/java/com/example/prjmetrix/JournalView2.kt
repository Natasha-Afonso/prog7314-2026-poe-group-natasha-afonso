package com.example.prjmetrix

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

//(Simplified Coding, 2021)
//the view model factory
//View model needs an empty constructor to be created
class JournalView2(private val context: Context) : ViewModelProvider.Factory {
    //(Saeed, 2019)
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = JournalRepository(context.applicationContext) //makes sure data is not lost
        return JournalViewModel(repository) as T //new instance for JournalViewModel
    }
}

/*References
*
* Saeed, M., 2019. ViewModel with ViewModelProvider.Factpry: The creator of ViewModel. (Version 10.0) [source code]. Available at: < https://medium.com/koderlabs/viewmodel-with-viewmodelprovider-factory-the-creator-of-viewmodel-8fabfec1aa4f > [Accessed 19 September 2026]
* Simplified Coding, 2021. #12 Android Login/Signup with MVVM - user Logout [video online] [source code]. Available at: < https://youtu.be/q_5xrPYRoAg?si=gRbHfP1TazLTqbrY > [Accessed 19 September 2026
*
* */