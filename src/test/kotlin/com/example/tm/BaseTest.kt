package com.example.tm

import org.junit.Before
import org.junit.Test

abstract class BaseTest {
    var classUnderTest: AbstractTaskManager = initClassUnderTest()

    abstract fun initClassUnderTest(): AbstractTaskManager

    @Before
    fun cleanUp() {
        classUnderTest = initClassUnderTest()
    }

    @Test
    fun testSingleProcessIsKilled() {
        // Given
        val p1 = Process(1, Priority.High, classUnderTest)
        classUnderTest.add(p1)
        // When
        classUnderTest.kill(p1)
        // Then
        assert(!p1.isAlive)
        assert(classUnderTest.tasks.isEmpty())
    }

    @Test
    fun testProcessGroupIsKilled() {
        // Given
        val p1 = Process(1, Priority.High, classUnderTest)
        val p2 = Process(2, Priority.High, classUnderTest)
        val p3 = Process(3, Priority.High, classUnderTest)
        val p4 = Process(4, Priority.Medium, classUnderTest)
        val p5 = Process(5, Priority.Low, classUnderTest)

        classUnderTest.add(p1)
        classUnderTest.add(p2)
        classUnderTest.add(p3)
        classUnderTest.add(p4)
        classUnderTest.add(p5)

        // When
        classUnderTest.killGroup(p1.priority)

        // Then
        assert(!p1.isAlive)
        assert(!p2.isAlive)
        assert(!p3.isAlive)
        assert(classUnderTest.tasks.size == 2)
        assert(classUnderTest.tasks.contains(p4))
        assert(classUnderTest.tasks.contains(p5))
    }

    @Test
    fun testAllProcessesAreKilled() {
        // Given
        val p1 = Process(1, Priority.High, classUnderTest)
        val p2 = Process(2, Priority.High, classUnderTest)
        val p3 = Process(3, Priority.High, classUnderTest)
        val p4 = Process(4, Priority.Medium, classUnderTest)
        val p5 = Process(5, Priority.Low, classUnderTest)

        classUnderTest.add(p1)
        classUnderTest.add(p2)
        classUnderTest.add(p3)
        classUnderTest.add(p4)
        classUnderTest.add(p5)

        // When
        classUnderTest.killAll()

        // Then
        assert(!p1.isAlive)
        assert(!p2.isAlive)
        assert(!p3.isAlive)
        assert(!p4.isAlive)
        assert(!p5.isAlive)
        assert(classUnderTest.tasks.isEmpty())
    }

    @Test
    fun testListingByCreationTime() {
        // Given
        val p1 = Process(1, Priority.High, classUnderTest)
        val p2 = Process(2, Priority.High, classUnderTest)
        val p3 = Process(3, Priority.High, classUnderTest)

        classUnderTest.add(p3)
        classUnderTest.add(p2)
        classUnderTest.add(p1)

        // When
        val res = classUnderTest.listOrderedByCreationTime()

        // Then
        val lines = res.split("\n")
        var counter = 3
        for (line in lines) {
            if (line.isNotBlank()) {
                val p = parseProcessFromString(line)
                assert(counter == p?.ID?.toInt())
            }
            counter--
        }
    }

    @Test
    fun testListingById() {
        // Given
        val p1 = Process(1, Priority.High, classUnderTest)
        val p2 = Process(2, Priority.High, classUnderTest)
        val p3 = Process(3, Priority.High, classUnderTest)

        classUnderTest.add(p3)
        classUnderTest.add(p2)
        classUnderTest.add(p1)

        // When
        val res = classUnderTest.listOrderedById()

        // Thens
        val lines = res.split("\n")
        var counter = 1
        for (line in lines) {
            if (line.isNotBlank()) {
                val p = parseProcessFromString(line)
                assert(counter == p?.ID?.toInt())
            }
            counter++
        }
    }


    @Test
    fun testListingByPriority() {
        // Given
        val p1 = Process(1, Priority.Medium, classUnderTest)
        val p2 = Process(2, Priority.High, classUnderTest)
        val p3 = Process(3, Priority.Low, classUnderTest)

        classUnderTest.add(p3)
        classUnderTest.add(p2)
        classUnderTest.add(p1)

        // When
        val res = classUnderTest.listOrderedByPriority()

        // Then
        val lines = res.split("\n")
        val result = ArrayList<Process>()
        for (line in lines) {
            if (line.isNotBlank()) {
                val p = parseProcessFromString(line)
                result.add(p!!)
            }
        }

        assert(result[0].ID == 3.toLong())
        assert(result[1].ID == 1.toLong())
        assert(result[2].ID == 2.toLong())
    }

    @Test
    fun testWhenAProcessIsAlreadyIncludedAddingShouldReturnFalse() {
        // Given
        val p1 = Process(1, Priority.Medium, classUnderTest)

        // Expect
        assert(classUnderTest.add(p1))
        assert(!classUnderTest.add(p1))
    }

    private fun parseProcessFromString(s: String): Process? {
        val processFormatBits = PROCESS_STRING_FORMAT.split(",")
        val idRegx = Regex(processFormatBits[0].replace("%s", "(\\d*),"))
        val idMatch = idRegx.find(s)
        val id = idMatch?.groupValues?.get(1)?.toLong()

        val prioRegx = Regex(processFormatBits[1].replace("%s", "(\\S*),"))
        val prioMatch = prioRegx.find(s)
        val priority = prioMatch?.groupValues?.get(1)?.let { Priority.valueOf(it) }

        return Process(id!!, priority!!, classUnderTest)
    }
}