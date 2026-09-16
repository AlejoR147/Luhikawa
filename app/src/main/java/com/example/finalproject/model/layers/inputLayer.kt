package com.example.finalproject.model.layers

class InputLayer {

    fun validatedData(dataInputPackage : List<Any>) : Array<Double> {
        for (data in dataInputPackage){
            when (data) {
                is Int -> {

                }
                is String -> {

                }
                is Double -> {

                }
                else -> {
                    println("...")
                }
            }
        }
        val dataPackage = dataInputPackage.map {it as Double }.toTypedArray()
        return dataPackage
    }

}