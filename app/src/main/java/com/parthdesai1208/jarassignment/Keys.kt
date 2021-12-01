package com.parthdesai1208.jarassignment

object Keys {
    init {
        System.loadLibrary("native-lib")
    }
    external fun apiKey(): String
}