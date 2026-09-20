package com.example.prjmetrix

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import com.samsung.android.sdk.health.data.HealthDataService
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.permission.AccessType
import com.samsung.android.sdk.health.data.permission.Permission
import com.samsung.android.sdk.health.data.request.DataType
import com.samsung.android.sdk.health.data.request.DataTypes
import com.samsung.android.sdk.health.data.request.LocalDateFilter
import com.samsung.android.sdk.health.data.request.LocalTimeFilter
import java.time.LocalDate
import java.time.LocalDateTime

class SamsungHealthStepsRepository(appContext: Context) {

    private val store: HealthDataStore = HealthDataService.getStore(appContext)

    private val permissions = setOf(
        Permission.of(DataTypes.STEPS, AccessType.READ),
        Permission.of(DataTypes.STEPS_GOAL, AccessType.READ)
    )

    suspend fun hasPermissions(): Boolean =
        store.getGrantedPermissions(permissions).containsAll(permissions)

    // Shows Samsung Health's permission pop-up. Needs an Activity context.
    suspend fun requestPermissions(activity: Activity) {
        store.requestPermissions(permissions, activity)
    }

    suspend fun readTodaySteps(): Long {
        val request = DataType.StepsType.TOTAL.requestBuilder
            .setLocalTimeFilter(
                LocalTimeFilter.of(LocalDate.now().atStartOfDay(), LocalDateTime.now())
            )
            .build()

        var steps = 0L
        store.aggregateData(request).dataList.forEach { data ->
            data.value?.let { steps = it }
        }
        return steps
    }

    suspend fun readStepGoal(): Int {
        val request = DataType.StepsGoalType.LAST.requestBuilder
            .setLocalDateFilter(
                LocalDateFilter.of(LocalDate.now(), LocalDate.now().plusDays(1))
            )
            .build()

        var goal = 0
        store.aggregateData(request).dataList.forEach { data ->
            data.value?.let { goal = it }
        }
        return goal
    }

}

fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}