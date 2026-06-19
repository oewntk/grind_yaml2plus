/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.grind.yaml2plus

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.oewntk.grind.yaml2plus.Tracing.progress
import org.oewntk.grind.yaml2plus.Tracing.start
import org.oewntk.model.ModelInfo
import org.oewntk.yaml.`in`.FactoryPlus
import java.io.File
import org.oewntk.json.out.model.ModelConsumer as JsonModelConsumer
import org.oewntk.ser.out.ModelConsumer as SerModelConsumer
import org.oewntk.yaml.out.oewn.CoreModelConsumer as YamlModelConsumer

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
        val in1 by parser.argument(       ArgType.String,                                              description = "Input dir or file")
        val in2 by parser.argument(       ArgType.String,                                              description = "Input YAML dir2")
        val out by parser.argument(       ArgType.String,                                              description = "Output dir or file")
        val operation by parser.option(   ArgType.String,  shortName = "do", fullName = "operation",   description = "Operation")                       .default("nothing")
        val outFormat by parser.option(   ArgType.String,  shortName = "of", fullName = "out_format",  description = "Output format")                   .default("yaml")
        val outInfo by parser.option(     ArgType.String,  shortName = "i",  fullName = "out_info",    description = "Output info")                     .default("oewn.info")
        val outOne by parser.option(      ArgType.Boolean, shortName = "1",  fullName = "out_one",     description = "Output one file")                 .default(false)
        val outMerge by parser.option(    ArgType.Boolean, shortName = "m",  fullName = "merge",       description = "Do not group generated entries")  .default(false)
        val verbose by parser.option(     ArgType.Boolean, shortName = "v",  fullName = "verbose",     description = "Verbose output")                  .default(false)

        val traceTime by parser.option(   ArgType.Boolean, shortName = "tt", fullName = "trace:time",  description = "trace time")                      .default(false)
        val traceHeap by parser.option(   ArgType.Boolean, shortName = "th", fullName = "trace:heap",  description = "trace heap")                      .default(false)
        // @formatter:on
        parser.parse(args)

        if (verbose) {
            System.err.println("in: $in1")
            System.err.println("in2: $in2")
            System.err.println("out: $out")
            System.err.println("operation: $operation")
            System.err.println("out format: $outFormat")
            System.err.println("out merge: $outMerge")
            System.err.println("out one: $outOne")
        }
        // Tracing
        Tracing.traceTime = traceTime
        Tracing.traceHeap = traceHeap

        val startTime = start()

        // Input
        val input = File(in1)
        Tracing.psInfo.println("[Input] ${input.absolutePath}")

        // Input2
        val input2 = File(in2)
        Tracing.psInfo.println("[Input2] " + input2.absolutePath)

        // Processing
        Tracing.psInfo.println("[Plus] true")
        Tracing.psInfo.println("[Op] $operation")

        // Output
        val outFile = File(out)
        if (outFile.exists() && !outFile.isDirectory) {
            outFile.delete()
        }
        Tracing.psInfo.println("[Output] " + outFile.absolutePath)

        // Supply model
        progress("before model is supplied", startTime)
        val model = FactoryPlus(input, input2, verbose = verbose).get()!!
        progress("after model is supplied", startTime)

        // Consume model
        progress("before model is consumed", startTime)

        when (outFormat) {
            "ser" -> SerModelConsumer(outFile).accept(model)
            "json" -> JsonModelConsumer(outFile).accept(model)
            "yaml" -> {
                if (outMerge)
                    File(outFile, "entries-generated.yaml").delete()
                YamlModelConsumer(outFile, split = !outOne, generated = !outMerge, verbose = verbose).accept(model)
            }

            else -> throw IllegalArgumentException("Unsupported output format")
        }
        progress("after model is consumed", startTime)

        // End
        progress("end", startTime)

        // info
        val modelInfo = model.info()
        val modelCounts = ModelInfo.counts(model)
        val modelInfo2 = "$modelInfo\n$modelCounts"
        Tracing.psInfo.println(modelInfo2)
        File(outInfo).writeText(modelInfo2)
    }
}
