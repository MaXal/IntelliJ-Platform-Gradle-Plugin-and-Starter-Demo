package org.jetbrains.testPlugin

import com.intellij.openapi.components.Service
import com.jetbrains.rd.generator.nova.array

class PluginStorage {
    companion object {
        @JvmStatic
        fun getPluginStorage() = Storage("static method", listOf("static1", "static2"))
    }
}

@Service
class PluginService {
    fun getAnswer(): Int = 42
}

@Service(Service.Level.PROJECT)
class PluginProjectService {
    fun getStrings(): Array<String> = arrayOf("foo","bar")
}

data class Storage(val key: String, val attributes: List<String>)
