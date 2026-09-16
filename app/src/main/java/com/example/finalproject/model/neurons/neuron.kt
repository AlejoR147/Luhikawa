package com.example.finalproject.model.neurons
import com.example.finalproject.model.activation.Activation

class Neuron() {
    private var weight = 0.5
    private var result: Double = 0.0
    private var breach: Double = 2.0
    private val activation : Activation = Activation()

    fun setDefault (){
        weight = 0.5
        result = 0.0
        breach = 2.0
    }

    fun predict(data: Double):Double{
        result = data * weight
        return activation.digmoid(result)
    }

    fun regression(error:Double) {

    }
}