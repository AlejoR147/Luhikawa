package com.example.finalproject.model.neurons

import com.example.finalproject.model.layers.InputLayer
import com.example.finalproject.model.layers.processingLayer
import com.example.finalproject.model.layers.exitLayer

class network {
    private val neuronsNetwork = mutableListOf<Neuron>()
    private val inputLayer = InputLayer()
    private val processingLayer = processingLayer()
    private val exitLayer = exitLayer()
    private lateinit var dataInputPackage : List<Double>
    private lateinit var dataPackage : List<Double>

    fun createNeurons() {
        for (i in 1..8) {
            val newNeuron = Neuron()
            neuronsNetwork.add(newNeuron)
        }
    }

    fun neuronsDefaultValue() {
        for(neuron in neuronsNetwork){
            neuron.setDefault()
        }
    }

    fun network (data: List<Any>): List<Double> {
        dataInputPackage = inputLayer.validatedData(data).toList()
        dataPackage = processingLayer.processing(dataInputPackage, neuronsNetwork)
        exitLayer.interpreter(dataPackage)
        return dataPackage
    }

}