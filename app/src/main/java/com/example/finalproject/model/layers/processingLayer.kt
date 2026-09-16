package com.example.finalproject.model.layers

import com.example.finalproject.model.neurons.Neuron
class processingLayer {

    private var dataPackage = mutableListOf<Double>()

    fun processing (dataInputLayer : List<Double>, neuronsNetwork: List<Neuron>):  List<Double>{
        dataPackage.clear()
        var contador = 0
        for (i in neuronsNetwork) {

            val data =  dataInputLayer.get(contador)
            dataPackage.add(i.predict(data))
            contador++
        }
        return dataPackage.toList()
    }

}