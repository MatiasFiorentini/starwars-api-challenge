package com.starwars.challenge.conexa.client;

public interface SwapiClient {

    <T> T getPaged(String path, String searchParamName, String searchParamValue, int page, int limit, Class<T> responseType);

    <T> T get(String path, Class<T> responseType);
}
