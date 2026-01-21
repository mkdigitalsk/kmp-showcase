package mk.digital.kmpshowcase.data.repository.dto

import kotlinx.serialization.json.Json

abstract class BaseDTOTest<T>(private val deserializer: (String) -> T) {

    abstract val serverJson: String

    fun run() {
        val parsed = deserializer(serverJson)
        assertClassUnderTestValues(parsed)
    }

    abstract fun assertClassUnderTestValues(classUnderTest: T)
}

abstract class DTOTestRunner {
    companion object {
        inline fun <reified T> defaultDeserializer(): (String) -> T = { json -> Json.decodeFromString<T>(json) }

        inline fun <reified T> create(
            serverJson: String,
            crossinline assertBlock: (T) -> Unit
        ): BaseDTOTest<T> {
            return object : BaseDTOTest<T>(defaultDeserializer()) {
                override val serverJson: String = serverJson
                override fun assertClassUnderTestValues(classUnderTest: T) {
                    assertBlock(classUnderTest)
                }
            }
        }
    }

    protected inline fun <reified T> runTest(
        serverJson: String,
        crossinline assertBlock: (T) -> Unit
    ) = create(serverJson, assertBlock).run()
}
