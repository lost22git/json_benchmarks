# json_benchmarks

Java Json library benchmarks

## Json libraries

- [Jackson](https://github.com/FasterXML/jackson)
- [Gson](https://github.com/google/gson)
- [Moshi](https://github.com/square/moshi)
- [FastJson](https://github.com/alibaba/fastjson)
- [Jsoniter](https://github.com/json-iterator/java)
- [Yasson](https://github.com/eclipse-ee4j/yasson)
- [Micronaut Serialization Jackson](https://github.com/micronaut-projects/micronaut-serialization)

See [Libraries version](https://github.com/lost22git/json_benchmarks/blob/main/gradle/libs.versions.toml)

## Build

Run the command: `.\gradlew clean && .\gradlew jmh`

## Check the jmh result

Open `app/build/results/jmh/index.html` in web browser to check the jmh result

## Github Action

Run [workflow](https://github.com/lost22git/json_benchmarks/actions/workflows/gradle.yml) and download the artifact after the workflow all done
