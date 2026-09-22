package com.example.finalproject.domain
import java.time.LocalDate

class Task () {
    private var area: String = ""
        get() = field
        set(value) { field = value }

    private var difficulty: String = ""
        get() = field
        set(value) { field = value }

    private var deadline: LocalDate = LocalDate.now()
        get() = field
        set(value) { field = value }

    private var assignedOn: LocalDate = LocalDate.now()
        get() = field
        set(value) { field = value }

    private var extense: String = ""
        get() = field
        set(value) { field = value }

    private var important: String = ""
        get() = field
        set(value) { field = value }

    private var type: String = ""
        get() = field
        set(value) { field = value }

    fun crateTask(area:String, difficulty: String, deadline: LocalDate, assignedOn: LocalDate, extense: String, important: String, type:String){
        this.area = area
        this.difficulty = difficulty
        this.deadline = deadline
        this.assignedOn = assignedOn
        this.extense = extense
        this.important = important
        this.type = type
    }
}