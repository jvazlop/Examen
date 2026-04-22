package org.iesra.procesaalumnos.cli

import java.nio.file.Path

data class CliOptions(
    val group: String,
    val path: Path?
)