package com.example.supermario.mymovies.util;

import java.util.Map;

public interface AsyncResponse {
    void processFinish(Map<Integer,IdCoverHolder> output);
}