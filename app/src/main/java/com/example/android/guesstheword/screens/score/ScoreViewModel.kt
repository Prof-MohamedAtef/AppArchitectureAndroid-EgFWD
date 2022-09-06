package com.example.android.guesstheword.screens.score

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScoreViewModel(finalScore: Int):ViewModel() {

    var _score= MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    var _eventPlayAgain=MutableLiveData<Boolean>()
    val eventPlayAgain:LiveData<Boolean>
        get() = _eventPlayAgain

    init {
        _score.value = finalScore
        Log.i("ScoreViewModel", "Final Score is $finalScore")
    }

    fun onPlayAgain(){
        _eventPlayAgain.value=true
    }

    fun onPlayAgainComplete(){
        _eventPlayAgain.value=false
    }
}