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

    static Moshi MOSHI;

    static Jsonb YASSON;

    static User USER;

    static String JSON;

    static ObjectMapper MN_SERDE;

    static QsonMapper QSON;

    static io.avaje.jsonb.Jsonb AVAJE_JSONB;

    static {
        JACKSON = new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        GSON = new Gson();

        MOSHI = new Moshi.Builder().build();

        YASSON = JsonbBuilder.create();

        MN_SERDE = ApplicationContext.run().getBean(ObjectMapper.class)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        QSON = new QsonMapper();

        AVAJE_JSONB = io.avaje.jsonb.Jsonb.builder().build();

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
        blackhole.consume(JACKSON.writeValueAsString(USER));
    }

    @Benchmark
    public void jackson_deserialize(Blackhole blackhole) throws JsonProcessingException {
        blackhole.consume(JACKSON.readValue(JSON, User.class));
    }

    // ============== Jsoniter reflect_mode

    static Config JSONITER_REFLECT_MODE_CONFIG = Config.INSTANCE
        .copyBuilder()
        .encodingMode(EncodingMode.REFLECTION_MODE)
        .decodingMode(DecodingMode.REFLECTION_MODE)
        .build();

    @Benchmark
    public void jsoniter_reflect_mode_serialize(Blackhole blackhole) {
        blackhole.consume(JsonStream.serialize(JSONITER_REFLECT_MODE_CONFIG, USER));
    }

    @Benchmark
    public void jsoniter_reflect_mode_deserialize(Blackhole blackhole) {
        blackhole.consume(JsonIterator.deserialize(JSONITER_REFLECT_MODE_CONFIG, JSON, User.class));
    }

    // ============== Jsoniter dynamic_mode

    static Config JSONITER_DYN_MODE_CONFIG = Config.INSTANCE
        .copyBuilder()
        .encodingMode(EncodingMode.DYNAMIC_MODE)
        .decodingMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_STRICTLY)
        .build();

    @Benchmark
    public void jsoniter_dyn_mode_serialize(Blackhole blackhole) {
        blackhole.consume(JsonStream.serialize(JSONITER_DYN_MODE_CONFIG, USER));
    }

    @Benchmark
    public void jsoniter_dyn_mode_deserialize(Blackhole blackhole) {
        blackhole.consume(JsonIterator.deserialize(JSONITER_DYN_MODE_CONFIG, JSON, User.class));
    }

    // ============== FastJson

    static SerializeConfig FASTJSON_SER_CONFIG = new SerializeConfig(true);
    static ParserConfig FAST_DESER_CONFIG = new ParserConfig(true);

    @Benchmark
    public void fastjson_serialize(Blackhole blackhole) {
        // var json = com.alibaba.fastjson.JSON.toJSONString(USER, FASTJSON_SER_CONFIG);
        blackhole.consume(com.alibaba.fastjson.JSON.toJSONString(USER));
    }

    @Benchmark
    public void fastjson_deserialize(Blackhole blackhole) {
        // var user = com.alibaba.fastjson.JSON.parseObject(JSON, User.class,
        // FAST_DESER_CONFIG);
        blackhole.consume(com.alibaba.fastjson.JSON.parseObject(JSON, User.class));
    }

    // ============== Gson

    @Benchmark
    public void gson_serialize(Blackhole blackhole) {
        blackhole.consume(GSON.toJson(USER));
    }

    @Benchmark
    public void gson_deserialize(Blackhole blackhole) {
        blackhole.consume(GSON.fromJson(JSON, User.class));
    }

    // ============== Moshi

    @Benchmark
    public void moshi_serialize(Blackhole blackhole) {
        blackhole.consume(MOSHI.adapter(User.class).toJson(USER));
    }

    @Benchmark
    public void moshi_deserialize(Blackhole blackhole) throws IOException {
        blackhole.consume(MOSHI.adapter(User.class).fromJson(JSON));
    }

    // ============== Yasson

    @Benchmark
    public void yasson_serialize(Blackhole blackhole) {
        blackhole.consume(YASSON.toJson(USER));
    }

    @Benchmark
    public void yasson_deserialize(Blackhole blackhole) throws IOException {
        blackhole.consume(YASSON.fromJson(JSON, User.class));
    }

    // ============== Micronaut serde jackson

    @Benchmark
    public void mn_serialize(Blackhole blackhole) throws JsonProcessingException {
        blackhole.consume(MN_SERDE.writeValueAsString(USER));
    }

    @Benchmark
    public void mn_deserialize(Blackhole blackhole) throws JsonProcessingException {
        blackhole.consume(MN_SERDE.readValue(JSON, User.class));
    }

    // ============== Qson

    @Benchmark
    public void qson_serialize(Blackhole blackhole) {
        blackhole.consume(QSON.writeString(USER));
    }

    @Benchmark
    public void qson_deserialize(Blackhole blackhole) {
        blackhole.consume(QSON.read(JSON, User.class));
    }

    // ============== Avaje jsonb
    @Benchmark
    public void avaje_jsonb_serialize(Blackhole blackhole) {
        blackhole.consume(AVAJE_JSONB.type(User.class).toJson(USER));
    }

    @Benchmark
    public void avaje_jsonb_deserialize(Blackhole blackhole) {
        blackhole.consume(AVAJE_JSONB.type(User.class).fromJson(JSON));
    }

    public static void main(String[] args) throws RunnerException {
        final Options options = new OptionsBuilder().include(Main.class.getName()).build();

        new Runner(options).run();
    }

}
