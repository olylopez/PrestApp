package com.example.prestapp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.hilt.work.WorkerAssistedFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class PrestApp : Application(){

}
