package org.iesra.procesaalumnos.cli

import java.nio.file.Path

class CommandLineParser {

    fun parser(args: Array<String>): CliOptions {

        var group: String? = null
        var path: Path? = null

        var i = 0
        while (i < args.size) {
            when (args[i]) {
                "--grupo" -> {
                    group = args.getOrNull(i + 1)
                    i++
                }
                "--path" -> {
                    path = Path.of(args.getOrNull(i + 1))
                    i++
                }
            }
            i++
        }

        if (group == null) {
            throw IllegalArgumentException("Debe indicar --grupo")
        }

        return CliOptions(group, path)
    }
}