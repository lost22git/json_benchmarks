package org.example.jsonbenchmarks;

import com.alibaba.fastjson.JSON;
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
import io.avaje.jsonb.Jsonb;
import io.micronaut.context.ApplicationContext;
import io.quarkus.qson.generator.QsonMapper;
import jakarta.json.bind.JsonbBuilder;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import java.io.IOException;

public class TestMain {

    // --add-opens java.base/java.lang=ALL-UNNAMED
    public static void main(String[] args) throws Exception {

        System.out.println("---------------------- Jackson");
        testJackson();
        System.out.println("---------------------- Jsoniter");
        testJsoniter();
        System.out.println("---------------------- FastJson");
        testFastJson();
        System.out.println("---------------------- Gson");
        testGson();
        System.out.println("---------------------- Moshi");
        testMoshi();
        System.out.println("---------------------- Yasson");
        testYasson();
        System.out.println("---------------------- Micronaut Serialization");
        testMnSerde();
        System.out.println("---------------------- Qson");
        testQson();
        System.out.println("---------------------- Avaje jsonb");
        testAvajeJsonb();

    }


    private static void testJackson() throws JsonProcessingException {
        var user = randomUser();

        var mapper = new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        var json = mapper
            .writer()
            .withDefaultPrettyPrinter()
            .writeValueAsString(user);

        System.out.println("json = \n" + json);

        var deser_user = mapper.readValue(json, User.class);

        System.out.println("deser_user = \n" + deser_user);
    }

    private static void testJsoniter() {
        var user = randomUser();

        // JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
        // var json = JsonStream.serialize(user);

        var json = JsonStream.serialize(
            Config.INSTANCE
                .copyBuilder()
                .encodingMode(EncodingMode.DYNAMIC_MODE)
                .build(),
            user);

        System.out.println("json = \n" + json);

        // JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_STRICTLY);
        // var deser_user = JsonIterator.deserialize(json, User.class);
        var deser_user = JsonIterator.deserialize(
            Config.INSTANCE
                .copyBuilder()
                .decodingMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_STRICTLY)
                .build(),
            json, User.class);

        System.out.println("deser_user = \n" + deser_user);
    }

    private static void testFastJson() {
        var user = randomUser();

        // var json = JSON.toJSONString(user, new SerializeConfig(true));
        var json = JSON.toJSONString(user);

        System.out.println("json = \n" + json);

        // var deser_user = JSON.parseObject(json, User.class, new ParserConfig(true));
        var deser_user = JSON.parseObject(json, User.class);

        System.out.println("deser_user = \n" + deser_user);
    }

    private static void testGson() {

        var user = randomUser();

        var json = new Gson().toJson(user);

        System.out.println("json = \n" + json);

        var deser_user = new Gson().fromJson(json, User.class);

        System.out.println("deser_user = \n" + deser_user);
    }

    private static void testMoshi() throws IOException {
        var user = randomUser();

        var moshi = new Moshi.Builder().build();
        JsonAdapter<User> jsonAdapter = moshi.adapter(User.class);

        var json = jsonAdapter.toJson(user);

        System.out.println("json = \n" + json);

        var deser_user = jsonAdapter.fromJson(json);

        System.out.println("deser_user = \n" + deser_user);

    }

    private static void testYasson() {
        var user = randomUser();

        var jsonb = JsonbBuilder.create();

        var json = jsonb.toJson(user);

        System.out.println("json = \n" + json);

        var deser_user = jsonb.fromJson(json, User.class);

        System.out.println("deser_user = \n" + deser_user);

    }

    private static void testMnSerde() {
        var user = randomUser();

        try (ApplicationContext context = ApplicationContext.run()) {
            var mapper = context.getBean(ObjectMapper.class);
            mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

            var json = mapper
                .writer()
                .withDefaultPrettyPrinter()
                .writeValueAsString(user);

            System.out.println("json = \n" + json);

            var deser_user = mapper.readValue(json, User.class);

            System.out.println("deser_user = \n" + deser_user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void testQson() {

        var user = randomUser();

        var mapper = new QsonMapper();

        var json = mapper.writeString(user);

        System.out.println("json = \n" + json);

        var deser_user = mapper.read(json, User.class);

        System.out.println("deser_user = \n" + deser_user);

    }

    private static void testAvajeJsonb() {

        var user = randomUser();

        var jsonb = Jsonb.builder().build();
        var jsonType = jsonb.type(User.class);

        var json = jsonType.toJson(user);

        System.out.println("json = \n" + json);

        var deser_user = jsonType.fromJson(json);

        System.out.println("deser_user = \n" + deser_user);

    }

    private static User randomUser() {
        var params = new EasyRandomParameters()
            .objectPoolSize(100)
            .randomizationDepth(3)
            .collectionSizeRange(3, 5);
        var easyRandom = new EasyRandom(params);
        var user = easyRandom.nextObject(User.class);
        return user;
    }

}
