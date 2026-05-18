/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.grind.yaml2plus

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import java.io.File
import org.oewntk.grind.yaml2plus.Tracing.progress
import org.oewntk.grind.yaml2plus.Tracing.start
import org.oewntk.model.ModelInfo
import org.oewntk.json.out.ModelConsumer as JsonModuleConsumer
import org.oewntk.ser.`in`.Factory as SerFactory
import org.oewntk.ser.out.ModelConsumer as SerModuleConsumer
import org.oewntk.yaml.`in`.FactoryPlus
import org.oewntk.yaml.`in`.Factory as YamlFactory
import org.oewntk.yaml.out.ModelConsumer as YamlModuleConsumer

/**
 * Main class that generates the OEWN plus database
 *
 * @author Bernard Bou
 * @see "https://sqlunet.sourceforge.net/schema.html"
 */
object Grind {

    /**
     * Main entry point
     *
     * @param args command-line arguments
     * ```
     * yamlDir [outputDir]
     * ```
     */
    @JvmStatic
    fun main(args: Array<String>) {
        val parser = ArgParser("yaml2plus")

        // Options (start with - or --)
        // @formatter:off
        val yaml by parser.argument(        ArgType.String,                                             description = "Input YAML dir")
        val out by parser.argument(         ArgType.String,                                             description = "Output dir or file")
        val yaml2 by parser.option(         ArgType.String,  shortName = "y2", fullName = "YAML2",      description = "Input YAML dir2")    .default("yaml2")
        val operation by parser.option(     ArgType.String,  shortName = "do", fullName = "operation",  description = "Operation")          .default("nothing")
        val inFormat by parser.option(      ArgType.String,  shortName = "if", fullName = "in format",  description = "In format")          .default("yaml")
        val inPlus by parser.option(        ArgType.Boolean, shortName = "p",  fullName = "plus",       description = "Plus input")         .default(false)
        val outFormat by parser.option(     ArgType.String,  shortName = "of", fullName = "out format", description = "Output format")      .default("yaml")
        val outInfo by parser.option(       ArgType.String,  shortName = "i",  fullName = "out info",   description = "Output info")        .default("oewn.info")
        val verbose by parser.option(       ArgType.Boolean, shortName = "v",  fullName = "verbose",    description = "Verbose output")     .default(false)
        // @formatter:on

        parser.parse(args)
        if (verbose) {
            System.err.println("yaml: $yaml")
            System.err.println("yaml2: $yaml2")
            System.err.println("out: $out")
            System.err.println("operation: $operation")
            System.err.println("plus: $inPlus")
            System.err.println("in format: $inFormat")
            System.err.println("out format: $outFormat")
        }
        // Tracing
        val startTime = start()

        // Input
        Tracing.psInfo.println("[Op] $operation")

        // Input
        val input = File(yaml)
        Tracing.psInfo.println("[Input] " + input.absolutePath)

        // Input2
        val input2 = File(yaml2)
        Tracing.psInfo.println("[Input2] " + input2.absolutePath)

        // Output
        val outFile = File(out)
        if (outFile.exists() && !outFile.isDirectory) {
            outFile.delete()
        }
        Tracing.psInfo.println("[Output] " + outFile.absolutePath)

        // Supply model
        progress("before model is supplied,", startTime)
        val model = if (inPlus)
            FactoryPlus(input, input2).get()!!
        else when (inFormat) {
            "ser" -> SerFactory(input).get()!!
            "yaml" -> YamlFactory(input, input2).get()!!
            else -> throw IllegalArgumentException("Unsupported input format")
        }
        //Tracing.psInfo.printf("[Model] %s%n%s%n%n", Arrays.toString(model.getSources()), model.info());
        progress("after model is supplied,", startTime)

        // Consume model
        progress("before model is consumed,", startTime)

        when (outFormat) {
            "ser" -> SerModuleConsumer(outFile).accept(model)
            "json" -> JsonModuleConsumer(outFile).accept(model)
            "yaml" -> YamlModuleConsumer(outFile).accept(model)
            else -> throw IllegalArgumentException("Unsupported output format")
        }
        progress("after model is consumed,", startTime)

        // End
        progress("total,", startTime)

        // info
        val modelInfo = model.info()
        val modelCounts = ModelInfo.counts(model)
        val modelInfo2 = "$modelInfo\n$modelCounts"
        Tracing.psInfo.println(modelInfo2)
        File(outInfo).writeText(modelInfo2)
    }
}
