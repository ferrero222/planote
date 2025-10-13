/*****************************************************************
 *  Package for HILT implementation
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.hilt

/*****************************************************************
 * Imported packages
 ****************************************************************/
import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/*****************************************************************
 * Classes
 ****************************************************************/
/*****************************************************************
 * Main App class with Hilt generation
 ****************************************************************/
@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}