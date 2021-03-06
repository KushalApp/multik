/*
 * Copyright 2020-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.kotlinx.multik

import org.jetbrains.kotlinx.multik.api.d2array
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.jni.NativeLinAlg
import org.jetbrains.kotlinx.multik.jvm.JvmLinAlg
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit
import kotlin.random.Random


@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@Fork(value = 2, jvmArgsPrepend = ["-Djava.library.path=./build/libs"])
open class DotBenchmark {

    @Param("10", "100", "1000")
    var size: Int = 0
    private lateinit var arg1: D2Array<Double>
    private lateinit var arg2: D2Array<Double>
    private lateinit var result: NDArray<Double, D2>
    private lateinit var ran: Random

    @Setup
    fun generate() {
        ran = Random(1)
        arg1 = mk.d2array(size, size) { ran.nextDouble() }
        arg2 = mk.d2array(size, size) { ran.nextDouble() }
    }

    @Benchmark
    fun dotJniBench(bh: Blackhole) {
        result = NativeLinAlg.dot(arg1, arg2)
        bh.consume(result)
    }

    @Benchmark
    fun dotJvmBench(bh: Blackhole) {
        result = JvmLinAlg.dot(arg1, arg2)
        bh.consume(result)
    }
}
