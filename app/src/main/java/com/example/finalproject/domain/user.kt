package com.example.finalproject.domain

class User (name:String,
            lastName:String,
            age:Int,
            levelEducation:String,
            educationalInstitution:String,
            time:Int) {
    private var name: String = ""
        get() {
            return field
        }
        set(value) {
            field = value
        }

    private var lastName: String = ""
        get() {
            return field
        }
        set(value) {
            field = value
        }

    private var age: Int = 0
        get() {
            return field
        }
        set(value) {
            field = value
        }

    private var levelEducation: String = ""
        get() {
            return field
        }
        set(value) {
            field = value
        }

    private var educationalInstitution: String = ""
        get() {
            return field
        }
        set(value) {
            field = value
        }

    private var availableTime: String = ""
        get() {
            return field
        }
        set(value) {
            field = value
        }

    fun createUser(
        name: String,
        lastName: String,
        age: Int,
        levelEducation: String,
        educationalInstitution: String,
        availableTime: String
    ) {
        this.name = name
        this.lastName = lastName
        this.age = age
        this.levelEducation = levelEducation
        this.educationalInstitution = educationalInstitution
        this.availableTime = availableTime
    }
}