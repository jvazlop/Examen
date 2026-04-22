package org.iesra.procesaalumnos

import org.iesra.procesaalumnos.cli.CommandLineParser

fun main(args: Array<String>) {
    val parser = CommandLineParser()
    val options = parser.parser(args)

    val application = StudentProcessingApplication()
    application.run(options)
}