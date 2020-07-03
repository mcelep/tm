package com.example.tm

interface ProcessListener {
    fun notifyKilled(process: Process)
}