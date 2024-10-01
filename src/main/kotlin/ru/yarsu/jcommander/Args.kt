package ru.yarsu.jcommander
import com.beust.jcommander.*;

@Parameters(separators = "=")
class Args {
    @Parameter(names = ["--tasks-file"], required = true,
        description = "The csv file")
    var urlFile: String? = null
    @Parameter(names = ["list"], required = true,
        description = "Check if there are any newer versions")
    var list: Boolean? = null
}
