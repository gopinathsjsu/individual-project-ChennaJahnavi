package com.logparser.models;

public record RequestModel(String timestamp, String method, String url, int status, int timeMs) {
}
