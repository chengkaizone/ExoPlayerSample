package com.tony.exoplayersample.model;

import org.json.JSONObject;

/**
 * Created by tony on 2017/11/22.
 */

public class TestModel {

    private int age;
    private String name;

    public TestModel(int age, String name) {

        this.age = age;
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toJSONString() {
        try {
            JSONObject jsonRoot = new JSONObject();

            jsonRoot.put("age", age);
            jsonRoot.put("name", name);

            return jsonRoot.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static TestModel parse(String jsonPref) {

        try {
            JSONObject jsonRoot = new JSONObject(jsonPref);

            int age = jsonRoot.getInt("age");
            String name = jsonRoot.getString("name");

            return new TestModel(age, name);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
