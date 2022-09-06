/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0)

class GameViewModel :ViewModel(){

    enum class BuzzType(val pattern: LongArray) {
        CORRECT(CORRECT_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
    }

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>
    private val timer: CountDownTimer

    companion object{
        private const val DONE=0L
        // This is the time when the phone will start buzzing each second
        private const val COUNTDOWN_PANIC_SECONDS = 10L
        private const val ONE_SECOND=1000L
        private const val COUNTDOWN_TIME=60000L
    }

    // The current word
    var _word = MutableLiveData<String>()
    val word:LiveData<String>
        get() = _word

    // The current score
    var _score = MutableLiveData<Int>()
    val score:LiveData<Int>
        get() = _score

    private val _eventGameFinish=MutableLiveData<Boolean>()
    val eventGameFinish:LiveData<Boolean>
        get() = _eventGameFinish

    var _currentTime=MutableLiveData<String>()
    val currentTime:LiveData<String>
        get() = _currentTime

    // Event that triggers the phone to buzz using different patterns, determined by BuzzType
    private val _eventBuzz = MutableLiveData<BuzzType>()
    val eventBuzz: LiveData<BuzzType>
        get() = _eventBuzz


    val currentTimeString = Transformations.map(currentTime, { time->
        DateUtils.formatElapsedTime(time.toLong())
    })

    init {
        _eventGameFinish.value=false
        Log.i("GameViewModel", "GameViewModel created!")
        resetList()
        nextWord()

        _score.value=0
        _word.value=""

        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND){
            override fun onTick(time: Long) {
                _currentTime.value= DateUtils.formatElapsedTime(time/ ONE_SECOND)
                if (time / ONE_SECOND <= COUNTDOWN_PANIC_SECONDS) {
                    _eventBuzz.value = BuzzType.COUNTDOWN_PANIC
                }
            }
            override fun onFinish() {
                _currentTime.value="00:00"
                _eventBuzz.value = BuzzType.GAME_OVER
                _eventGameFinish.value=true
            }
        }
        timer.start()
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
        Log.i("GameViewModel","GameViewModel Destroyed!")
    }

    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
            "queen",
            "hospital",
            "basketball",
            "cat",
            "change",
            "snail",
            "soup",
            "calendar",
            "sad",
            "desk",
            "guitar",
            "home",
            "railway",
            "zebra",
            "jelly",
            "car",
            "crow",
            "trade",
            "bag",
            "roll",
            "bubble"
        )
        wordList.shuffle()
    }



    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
            resetList()
        }
        _word.value = wordList.removeAt(0)

    }

    /** Methods for buttons presses **/

    public fun onSkip() {
        _score.value=(score.value)?.minus(1)
        nextWord()
    }

    public fun onCorrect() {
        _score.value=(score.value)?.plus(1)
        _eventBuzz.value = BuzzType.CORRECT
        nextWord()
    }

    fun onGameFinished(){
        _eventGameFinish.value=false
    }

    fun onBuzzComplete() {
        _eventBuzz.value = BuzzType.NO_BUZZ
    }
}