package com.example.finalproject.model.activation

import kotlin.math.exp
class Activation {
    fun digmoid (data:Double):Double{
        return 1.0 / (1.0 + exp(-data))
    }

    fun softmax (){

    }
}