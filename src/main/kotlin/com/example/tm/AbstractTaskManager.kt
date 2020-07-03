package com.example.tm

import java.lang.StringBuilder
import java.time.Instant
import java.util.LinkedHashSet
import java.util.logging.Logger
import java.util.stream.Collectors

const val PROCESS_LIMIT = 10

abstract class AbstractTaskManager : ProcessListener {
    private val log = Logger.getLogger(PriorityTaskManager::class.toString())

    internal val tasks = LinkedHashSet<Process>()

    abstract fun add(process: Process): Boolean

    fun kill(process: Process) {
        process.kill()
    }

    override fun notifyKilled(process: Process) {
        handleKilledProcess(process)
    }

    fun killGroup(priority: Priority) {
        synchronized(tasks) {
             tasks.stream().filter { p -> p.priority == priority }.collect(Collectors.toSet()).forEach { it.kill() }
        }
    }

    fun killAll() {
        synchronized(tasks) {
            tasks.stream().collect(Collectors.toSet()).forEach { it.kill() }
        }
    }

    fun listOrderedByCreationTime(): String {
        return list(tasks.sortedBy { it.creationDate })
    }

    fun listOrderedByPriority(): String {
        return list(tasks.sortedBy { it.priority })
    }

    fun listOrderedById(): String {
        return list(tasks.sortedBy { it.ID })
    }

    private fun list(list: Collection<Process>): String {
        val sb = StringBuilder()
        list.forEach { sb.append(it.toString()).append("\n") }
        return sb.toString()
    }

    private fun handleKilledProcess(process: Process) {
        synchronized(tasks) {
            val result = tasks.remove(process)
            log.finest("handleKilledProcess, Process:%s removed:%s".format(process.toString(),result))
        }
    }

    protected fun saveProcess(process: Process) {
        tasks.add(process)
        process.creationDate = Instant.now()
    }
    
}