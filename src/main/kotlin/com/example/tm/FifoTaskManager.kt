package com.example.tm

class FifoTaskManager : AbstractTaskManager() {
    override fun add(process: Process): Boolean {
        synchronized(tasks) {
            while (tasks.size >= PROCESS_LIMIT) {
                tasks.remove(tasks.first())
            }
            return saveProcess(process)
        }
    }
}