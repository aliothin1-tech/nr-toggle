package com.example.nrtoggle
import java.io.*
object ShellUtils {
    data class Result(val success: Boolean, val output: String)
    fun runRootCommand(cmd: String): Result {
        return try {
            val p = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(p.outputStream)
            os.writeBytes(cmd + "\nexit\n"); os.flush()
            val reader = BufferedReader(InputStreamReader(p.inputStream))
            val sb = StringBuilder(); var line: String? = reader.readLine()
            while (line != null) { sb.append(line).append('\n'); line = reader.readLine() }
            p.waitFor(); Result(p.exitValue() == 0, sb.toString())
        } catch (e: Exception) { Result(false, e.message ?: "") }
    }
}