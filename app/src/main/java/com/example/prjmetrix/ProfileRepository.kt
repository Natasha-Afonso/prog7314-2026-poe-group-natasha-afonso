package com.example.prjmetrix

import android.content.Context


class ProfileRepository(private val context: Context) {

    val api: ProfileApi by lazy {
        NetworkRepository.create(context, ProfileApi::class.java)
    }



}