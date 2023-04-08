package org.example.jsonbenchmarks;

import io.avaje.jsonb.Json;
import io.micronaut.serde.annotation.Serdeable;

import java.util.Map;
import java.util.Set;

@Serdeable
@Json
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private Integer age;
    private Gender gender;
    private Set<User> friends;
    private Map<String, String> attrs;

    public User() {

    }

    public User(Long id,
                String firstName,
                String lastName,
                Integer age,
                Gender gender,
                Set<User> friends,
                Map<String, String> attrs) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
        this.friends = friends;
        this.attrs = attrs;
    }

    public enum Gender {
        MALE, FEMALE, UNKNOWN
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", age=" + age +
               ", gender=" + gender +
               ", friends=" + friends +
               ", attrs=" + attrs +
               '}';
    }

    public Long getId() {
        return id;
    }

    public User setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public User setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public User setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public User setAge(Integer age) {
        this.age = age;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public User setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public Set<User> getFriends() {
        return friends;
    }

    public User setFriends(Set<User> friends) {
        this.friends = friends;
        return this;
    }

    public Map<String, String> getAttrs() {
        return attrs;
    }

    public User setAttrs(Map<String, String> attrs) {
        this.attrs = attrs;
        return this;
    }
}
