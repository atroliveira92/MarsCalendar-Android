package com.arthur.marscalendar.model

data class Day (
    var value: Int,
    var disabled: Boolean,
    var selected: Boolean) {

    constructor(): this (0, false, false)
}