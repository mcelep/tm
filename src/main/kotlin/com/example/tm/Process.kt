package com.example.tm

import java.time.Instant
import java.util.logging.Logger

const val PROCESS_STRING_FORMAT: String = "Process with ID:%s, Priority:%s, Creation:%s"

class Process(val ID: Long, val priority: Priority, private val processListener: ProcessListener) {
    var creationDate: Instant = Instant.now()
    private val log = Logger.getLogger(PriorityTaskManager::class.toString())


    private var _isAlive: Boolean = true
    val isAlive: Boolean
        get() {
            return _isAlive
        }

    fun kill() {
        synchronized(_isAlive) {
            if (!_isAlive) {
                return
            }
            log.info(info() + " killed.")
            processListener.notifyKilled(this)
            _isAlive = false
        }
    }

    override fun toString(): String {
        return info()
    }

    private fun info(): String {
        return PROCESS_STRING_FORMAT.format(this.ID, this.priority, this.creationDate)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Process

        if (ID != other.ID) return false
        if (priority != other.priority) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ID.hashCode()
        result = 31 * result + priority.hashCode()
        return result
    }
}