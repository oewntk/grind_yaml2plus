/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.grind.yaml2plus

import org.oewntk.grind.yaml2plus.Tracing.progress
import org.oewntk.grind.yaml2plus.Tracing.start
import org.oewntk.model.ModelInfo
import org.oewntk.ser.`in`.Factory
import org.oewntk.yaml.`in`.FactoryPlus
import java.io.File
import org.oewntk.json.out.ModelConsumer as JsonModuleConsumer
import org.oewntk.ser.out.ModelConsumer as SerModuleConsumer
import org.oewntk.yaml.out.ModelConsumer as YamlModuleConsumer

/**
 * Main class that generates the WN database in the SQL format
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
        val iArg = 0

        // Tracing
        val startTime = start()

        // Input
        val outputOp = args[iArg]
        Tracing.psInfo.println("[Op] " + outputOp)

        // Input
        val inDir = File(args[iArg + 1])
        Tracing.psInfo.println("[Input] " + inDir.absolutePath)

        // Input2
        val inDir2 = File(args[iArg + 2])
        Tracing.psInfo.println("[Input2] " + inDir2.absolutePath)

        // Output
        val outFile = File(args[iArg + 3])
        if (!outFile.exists()) {
            outFile.delete()
        }
        Tracing.psInfo.println("[Output] " + outFile.absolutePath)

        // Supply model
        progress("before model is supplied,", startTime)
        val model = Factory(inDir).get()!! // FactoryPlus(inDir, inDir2).get()!!

        //Tracing.psInfo.printf("[Model] %s%n%s%n%n", Arrays.toString(model.getSources()), model.info());
        progress("after model is supplied,", startTime)

        // Consume model
        progress("before model is consumed,", startTime)
        when (outputOp) {
            "ser" -> SerModuleConsumer(outFile).accept(model)
            "json" -> JsonModuleConsumer(outFile).accept(model)
            "yaml" -> YamlModuleConsumer(outFile).accept(model)
        }
        progress("after model is consumed,", startTime)

        // End
        progress("total,", startTime)

        // info
        val modelInfo = model.info()
        val modelCounts = ModelInfo.counts(model)
        val modelInfo2 = "$modelInfo\n$modelCounts"
        Tracing.psInfo.println(modelInfo2)
        File(if (args.size == 5) args[iArg + 4] else "oewn.info").writeText(modelInfo2)
    }
}
