package com.example.finalproject.domain

import java.time.LocalDate

class Projects () {
    private var name: String = ""
        get() = field
        set(value) { field = value }

    private var important: Int = 0
        get() = field
        set(value) { field = value }

    private var type: String = ""
        get() = field
        set(value) { field = value }

    private var deadline: LocalDate = TODO()
        get() = field
        set(value) { field = value }

    fun createProject(name: String, important: Int, type: String, deadline: LocalDate) {
        this.name = name
        this.important = important
        this.type = type
        this.deadline = deadline
    }
}