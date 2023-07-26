package com.example.youtubecal4

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var workingsTV: TextView
    private lateinit var resultsTV: TextView
    private var canAddDecimal: Boolean = true
    private var canAddOperation: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        workingsTV = findViewById(R.id.workingsTV)
        resultsTV = findViewById(R.id.resultsTV)
    }

    fun numberAction(view: View) {
        if (view is Button) {
            if (view.text == ".") {
                if (canAddDecimal)
                    workingsTV.append(view.text)
                canAddDecimal = false
            } else
                workingsTV.append(view.text)

            canAddOperation = true
        }
    }

    fun operationAction(view: View) {
        if (view is Button && canAddOperation) {
            workingsTV.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }

    fun allClearAction(view: View) {
        workingsTV.text = ""
        resultsTV.text = ""
    }

    fun backSpaceAction(view: View) {
        val length = workingsTV.length()
        if (length > 0)
            workingsTV.text = workingsTV.text.subSequence(0, length - 1)
        else
            workingsTV.text = ""
    }

    fun equalsAction(view: View) {
        resultsTV.text = calculateResults()
    }

    private fun calculateResults(): String {
        val digitsOperators = digitsOperators()
        if (digitsOperators.isEmpty()) return ""

        Log.d("ListbeforeCalc", "digitsOperators: $digitsOperators")

        val timesDivision = timesDivisionCalculate(digitsOperators)
        if (timesDivision.isEmpty()) return ""

        Log.d("youtubecal4", "timesDivision: $timesDivision")

        val result = addSubtractCalculate(timesDivision)

        Log.d("youtubecal4", "result: $result")

        return result.toString()
    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float

        for (i in 1 until passedList.size step 2) {
            val operator = passedList[i] as Char
            val nextDigit = (passedList[i + 1] as Float).toFloat()

            if (operator == '+')
                result += nextDigit
            else if (operator == '-')
                result -= nextDigit
        }
        // Log-Ausgabe für den Stand der Liste "passedList"
        Log.d("result", "passedList: $passedList")

        return result
    }


    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList.toMutableList()
        while (list.contains("*") || list.contains("/")) {
            list = calcTimesDiv(list)
        }
        return list
    }

    // Funktion, um alle Multiplikationen und Divisionen in der Liste aufzulösen
    //input: [5,+,3,*,4,-,5,*,3]
    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var i = 0

        while (i < passedList.size) {
            val currentItem = passedList[i]

            if (currentItem == '*') {
                val prevDigit = newList[newList.size - 1] as Float
                val nextDigit = passedList[i + 1] as Float
                val newResult = prevDigit * nextDigit
                newList[newList.size - 1] = newResult
                i += 2
            } else if (currentItem != '/') {
                val prevDigit = newList[newList.size - 1] as Float
                val nextDigit = passedList[i + 1] as Float
                val newResult = prevDigit / nextDigit
                newList[newList.size - 1] = newResult
                i += 2
            } else {
                newList.add(currentItem)
                //println(newList)
                i++
            }
        }
        return newList
    }


    private fun digitsOperators(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        var isLastCharOperator = false

        for (character in workingsTV.text) {
            if (character.isDigit() || character.toString() == getString(R.string.decimalpoint)) {
                currentDigit += character
                isLastCharOperator = false
            } else if (character == '+' || character == '-' || character == '*' || character == '/') {
                if (!isLastCharOperator) {
                    list.add(currentDigit.toFloat())
                    currentDigit = ""
                }
                list.add(character)
                isLastCharOperator = true
            }
        }
        if (currentDigit != "") {
            list.add(currentDigit.toFloat())
        }
        return list
    }

    fun debugAction() {
        val list = mutableListOf<Any>(3, '+', 4, '*', 5)
        val resultList = calcTimesDiv(list)
        resultsTV.text = resultList.toString()
        println(resultList)

    }




}

