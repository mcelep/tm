package com.example.tm

class TaskManager : AbstractTaskManager() {
    override fun add(process: Process): Boolean {
        synchronized(tasks) {
            if (tasks.size >= PROCESS_LIMIT) {
                return false
            }
            return saveProcess(process)
        }
    }
}