package ru.javacat.justweather.common.util



    fun String.toWindRus(): String{
        return this.replace(Regex("[NSWE]")){
            when (it.value) {
                "N" -> "С"
                "S"-> "Ю"
                "W"->"З"
                "E"->"В"
                else -> it.value
            }
        }
    }