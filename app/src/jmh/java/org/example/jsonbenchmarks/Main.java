package org.example.jsonbenchmarks;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.jsoniter.JsonIterator;
import com.jsoniter.output.EncodingMode;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Config;
import com.jsoniter.spi.DecodingMode;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import io.micronaut.context.ApplicationContext;
import io.quarkus.qson.generator.QsonMapper;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 3)
@Measurement(iterations = 3, time = 3)
@Fork(1)
@Threads(4)
public class Main {

    static ObjectMapper JACKSON;

    static Gson GSON;

    static JsonAdapter<User> MOSHI;

    static Jsonb YASSON;

    static User USER;

    static String JSON;

    static ObjectMapper MN_SERDE;

    static QsonMapper QSON;

    static {
        JACKSON = new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        GSON = new Gson();

        MOSHI = new Moshi.Builder().build()
            .adapter(User.class);

        YASSON = JsonbBuilder.create();

        MN_SERDE = ApplicationContext.run().getBean(ObjectMapper.class)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        QSON = new QsonMapper();

        var params = new EasyRandomParameters()
            .objectPoolSize(100)
            .randomizationDepth(3)
            .collectionSizeRange(1, 5);
        var easyRandom = new EasyRandom(params);
        USER = easyRandom.nextObject(User.class);

        try {
            JSON = JACKSON.writeValueAsString(USER);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        System.out.println("JSON: \n" + JSON);
    }

    // ============== Jackson

    @Benchmark
    public void jackson_serialize(Blackhole blackhole) throws JsonProcessingException {
        var json = JACKSON.writeValueAsString(USER);
        blackhole.consume(json);
    }

    @Benchmark
    public void jackson_deserialize(Blackhole blackhole) throws JsonProcessingException {
        var user = JACKSON.readValue(JSON, User.class);
        blackhole.consume(user);
    }

    // ============== Jsoniter reflect_mode

    static Config JSONITER_REFLECT_MODE_CONFIG = Config.INSTANCE
        .copyBuilder()
        .encodingMode(EncodingMode.REFLECTION_MODE)
        .decodingMode(DecodingMode.REFLECTION_MODE)
        .build();

    @Benchmark
    public void jsoniter_reflect_mode_serialize(Blackhole blackhole) {
        var json = JsonStream.serialize(JSONITER_REFLECT_MODE_CONFIG, USER);
        blackhole.consume(json);
    }

    @Benchmark
    public void jsoniter_reflect_mode_deserialize(Blackhole blackhole) {
        var user = JsonIterator.deserialize(JSONITER_REFLECT_MODE_CONFIG, JSON, User.class);
        blackhole.consume(user);
    }

    // ============== Jsoniter dynamic_mode

    static Config JSONITER_DYN_MODE_CONFIG = Config.INSTANCE
        .copyBuilder()
        .encodingMode(EncodingMode.DYNAMIC_MODE)
        .decodingMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_STRICTLY)
        .build();

    @Benchmark
    public void jsoniter_dyn_mode_serialize(Blackhole blackhole) {
        var json = JsonStream.serialize(JSONITER_DYN_MODE_CONFIG, USER);
        blackhole.consume(json);
    }

    @Benchmark
    public void jsoniter_dyn_mode_deserialize(Blackhole blackhole) {
        var user = JsonIterator.deserialize(JSONITER_DYN_MODE_CONFIG, JSON, User.class);
        blackhole.consume(user);
    }

    // ============== FastJson

    static SerializeConfig FASTJSON_SER_CONFIG = new SerializeConfig(true);
    static ParserConfig FAST_DESER_CONFIG = new ParserConfig(true);

    @Benchmark
    public void fastjson_serialize(Blackhole blackhole) {
        // var json = com.alibaba.fastjson.JSON.toJSONString(USER, FASTJSON_SER_CONFIG);
        var json = com.alibaba.fastjson.JSON.toJSONString(USER);
        blackhole.consume(json);
    }

    @Benchmark
    public void fastjson_deserialize(Blackhole blackhole) {
        // var user = com.alibaba.fastjson.JSON.parseObject(JSON, User.class,
        // FAST_DESER_CONFIG);
        var user = com.alibaba.fastjson.JSON.parseObject(JSON, User.class);
        blackhole.consume(user);
    }

    // ============== Gson

    @Benchmark
    public void gson_serialize(Blackhole blackhole) {
        var json = GSON.toJson(USER);
        blackhole.consume(json);
    }

    @Benchmark
    public void gson_deserialize(Blackhole blackhole) {
        var user = GSON.fromJson(JSON, User.class);
        blackhole.consume(user);
    }

    // ============== Moshi

    @Benchmark
    public void moshi_serialize(Blackhole blackhole) {
        var json = MOSHI.toJson(USER);
        blackhole.consume(json);
    }

    @Benchmark
    public void moshi_deserialize(Blackhole blackhole) throws IOException {
        var user = MOSHI.fromJson(JSON);
        blackhole.consume(user);
    }

    // ============== Yasson

    @Benchmark
    public void yasson_serialize(Blackhole blackhole) {
        var json = YASSON.toJson(USER);
        blackhole.consume(json);
    }

    @Benchmark
    public void yasson_deserialize(Blackhole blackhole) throws IOException {
        var user = YASSON.fromJson(JSON, User.class);
        blackhole.consume(user);
    }

    // ============== Micronaut serde jackson

    @Benchmark
    public void mn_serialize(Blackhole blackhole) throws JsonProcessingException {
        var json = MN_SERDE.writeValueAsString(USER);
        blackhole.consume(json);
    }

    @Benchmark
    public void mn_deserialize(Blackhole blackhole) throws JsonProcessingException {
        var user = MN_SERDE.readValue(JSON, User.class);
        blackhole.consume(user);
    }

    // ============== Qson

    @Benchmark
    public void qson_serialize(Blackhole blackhole) {
        var json = QSON.writeString(USER);
        blackhole.consume(json);
    }

    @Benchmark
    public void qson_deserialize(Blackhole blackhole) {
        var user = QSON.read(JSON, User.class);
        blackhole.consume(user);
    }


    public static void main(String[] args) throws RunnerException {
        final Options options = new OptionsBuilder().include(Main.class.getName()).build();

        new Runner(options).run();
    }

}
