# MultiK

Multidimensional array library for Kotlin.

## Modules
* multik-api &mdash; contains ndarrays, methods called on them and [math], [stat] and [linalg] interfaces.
* multik-jvm &mdash; implementation including `jvm` and `native` for performance.
* multik-jvm &mdash; implementation of [math], [stat] and [linalg] interfaces on JVM.
* multik-native &mdash; implementation of [math], [stat] and [linalg] interfaces in native code using OpenBLAS.

## Using in your projects
In your Gradle build script:
1. Add the `kotlin-datascience` repository.
2. Add the `org.jetbrains.multik:multik-api:$multik_version` api dependency.
3. Add an implementation dependency: `org.jetbrains.multik:multik-default:$multik_version`,
`org.jetbrains.multik:multik-jvm:$multik_version` or `org.jetbrains.multik:multik-native:$multik_version`.

`build.gradle`:
```groovy
repositories {
    maven { url "https://dl.bintray.com/kotlin/kotlin-datascience" }
}

dependencies {
    implementation "org.jetbrains.multik:multik-api:0.0.1-dev-7"
    implementation "org.jetbrains.multik:multik-default:0.0.1-dev-7"
}
```

`build.gradle.kts`:
```kotlin
repositories {
    maven("https://dl.bintray.com/kotlin/kotlin-datascience")
}

dependencies {
    implementation("org.jetbrains.multik:multik-api:0.0.1-dev-7")
    implementation("org.jetbrains.multik:multik-default:0.0.1-dev-7")
}
```

## Quickstart

#### Creating arrays

```kotlin
val a = mk.ndarray(mk[1, 2, 3])
/* [1, 2, 3] */
val b = mk.ndarray(mk[mk[1.5, 2.1, 3.0], mk[4.0, 5.0, 6.0]])
/*
[[1.5, 2.1, 3.0],
[4.0, 5.0, 6.0]]
*/
val c = mk.ndarray(mk[mk[mk[1.5f, 2f, 3f], mk[4f, 5f, 6f]], mk[mk[3f, 2f, 1f], mk[4f, 5f, 6f]]])
/*
[[[1.5, 2.0, 3.0],
[4.0, 5.0, 6.0]],

[[3.0, 2.0, 1.0],
[4.0, 5.0, 6.0]]]
*/


mk.empty<Double, D2>(3, 4) // create an array of zeros
/*
[[0.0, 0.0, 0.0, 0.0],
[0.0, 0.0, 0.0, 0.0],
[0.0, 0.0, 0.0, 0.0]]
*/
mk.ndarray<Float, D2>(setOf(30f, 2f, 13f, 12f), intArrayOf(2, 2)) // create an array from a collection
/*
[[30.0, 2.0],
[13.0, 12.0]]
*/
val d = mk.ndarray(doubleArrayOf(1.0, 1.3, 3.0, 4.0, 9.5, 5.0), 2, 3) // create an array of shape(2, 3) from a primitive array
/*
[[1.0, 1.3, 3.0],
[4.0, 9.5, 5.0]]
*/
mk.d3array(2, 2, 3) { it * it } // create an array of 3 dimension
/*
[[[0, 1, 4],
[9, 16, 25]],

[[36, 49, 64],
[81, 100, 121]]]
*/
mk.arange<Long>(10, 25, 5) // creare an array with elements in the interval [19, 25) with step 5
/* [10, 15, 20] */
mk.linspace<Double>(0, 2, 9) // create an array of 9 elements in the interval [0, 2]
/* [0.0, 0.25, 0.5, 0.75, 1.0, 1.25, 1.5, 1.75, 2.0] */
val e = mk.identity<Double>(3) // create an identity array of shape (3, 3)
/*
[[1.0, 0.0, 0.0],
[0.0, 1.0, 0.0],
[0.0, 0.0, 1.0]]
*/
```

#### Array properties
```kotlin
a.shape // Array dimensions
a.size // Size of array
a.dim // object Dimension
a.dim.d // number of array dimensions
a.dtype // Data type of array elements
```

#### Arithmetic operations
```kotlin
val f = b - d // subtraction
/*
[[0.5, 0.8, 0.0],
[0.0, -4.5, 1.0]]
*/

d + f // addition
/*
[[1.5, 2.1, 3.0],
[4.0, 5.0, 6.0]]
*/

b / d // division
/*
[[1.5, 1.6153846153846154, 1.0],
[1.0, 0.5263157894736842, 1.2]]
*/

f * d // multiplication
/*
[[0.5, 1.04, 0.0],
[0.0, -42.75, 5.0]]
*/
```

#### Array mathematics
```kotlin
mk.math.sin(a) // element-wise sin 
mk.math.cos(a) // element-wise cos
mk.math.log(b) // element-wise natural logarithm
mk.math.exp(b) // element-wise epx
mk.linalg.dot(d, e) // dot product
```

#### Aggregate functions
```kotlin
mk.math.sum(c) // array-wise sum
mk.math.min(c) // array-wise minimum elements
mk.math.maxD3(c, axis=0) // maximum value of an array along axis 0
mk.math.cumSum(b, axis=1) // cumulative sum of the elements
mk.stat.mean(a) // mean
mk.stat.median(b) // meadian
```

#### Copying arrays
```kotlin
val f = a.clone() // create a copy of the array and its data
val h = b.deepCope() // create a copy of the array and copy the meaningful data
```

#### Operations of Iterable
```kotlin
c.filter { it < 3 } // select all elements less than 3
b.map { (it * it).toInt() } // return squares
c.groupNdarrayBy { it % 2 } // group elemetns by condition
c.sorted() // sort elements
```

#### Indexing/Slicing/Iterating
```kotlin
a[2] // select the element at the 2 index
b[1, 2] // select the element at row 1 column 2
b[1] // select row 1 
b[0.r..2, 1] // select elements at rows 0 and 1 in column 1
b[0..1..1] // select all elements at row 0

for (el in b) {
    print("$el, ") // 1.5, 2.1, 3.0, 4.0, 5.0, 6.0, 
}

// for n-dimensional
val q = b.asDNArray()
for (index in q.multiIndices) {
    print("${q[index]}, ") // 1.5, 2.1, 3.0, 4.0, 5.0, 6.0, 
}
```

#### Inplace

```kotlin
val a = mk.linspace<Float>(0, 1, 10)
/*
a = [0.0, 0.1111111111111111, 0.2222222222222222, 0.3333333333333333, 0.4444444444444444, 0.5555555555555556, 
0.6666666666666666, 0.7777777777777777, 0.8888888888888888, 1.0]
*/
val b = mk.linspace<Float>(8, 9, 10)
/*
b = [8.0, 8.11111111111111, 8.222222222222221, 8.333333333333334, 8.444444444444445, 8.555555555555555,
8.666666666666666, 8.777777777777779, 8.88888888888889, 9.0]
*/

a.inplace { 
    math { 
        (this - b) * b
         abs()
    }
}
// a = [64.0, 64.88888, 65.77778, 66.66666, 67.55556, 68.44444, 69.333336, 70.22222, 71.111115, 72.0]
```

## Building
Multik uses blas for implementing algebraic operations. Therefore, you would need a C ++ compiler.
Run `./gradlew assemble` to build all modules.
* To build api module run `./gradlew multik-api:assemble`.
* To build jvm module run `./gradlew multik-jvm:assemble`.
* To build native module run `./gradlew multik-native:assemble`.
* To build default module run `./gradlew multik-native:assemble` then `./gradlew multik-default:assemble`.

## Testing
`./gradlew test`

## Benchmarks

The benchmark code be found in `src/jmh` folder. To run the benchmarks, run following commands:
```
./gradlew assemble benchmarkJar
java -jar ./build/libs/multik-benchmark.jar
```

## Contributing
There is an opportunity to contribute to the project:
1. Implement [math](multik-api/src/main/kotlin/org/jetbrains/multik/api/Math.kt) and [linalg](multik-api/src/main/kotlin/org/jetbrains/multik/api/LinAlg.kt) interfaces.
2. Create your own engine successor from [Engine](multik-api/src/main/kotlin/org/jetbrains/multik/api/Engine.kt), for example - [JvmEngine](multik-jvm/src/main/kotlin/org/jetbrains/multik/jvm/JvmEngine.kt).
3. Use [mk.addEngine](https://github.com/devcrocod/multik/blob/972b18cfd2952abd811fabf34461d238e55c5587/multik-api/src/main/kotlin/org/jetbrains/multik/api/Multik.kt#L23) and [mk.setEngine](https://github.com/devcrocod/multik/blob/972b18cfd2952abd811fabf34461d238e55c5587/multik-api/src/main/kotlin/org/jetbrains/multik/api/Multik.kt#L27)
to use your implementation.