package com.example.supermario.mymovies.util;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by supermario on 10/17/2016.
 */

public class MoviesCoversDAO extends AsyncTask<String, Void, SparseArray<IdCoverHolder>> {
    public static final String SEARCH_POPULAR = "SEARCH_POPULAR";
    public static final String SEARCH_TOP_RATED = "TOP_RATED";
    private final String LOG_TAG = MoviesCoversDAO.class.getSimpleName();
    public AsyncResponse asyncResponse;

    @Override
    protected SparseArray<IdCoverHolder> doInBackground(String... params) {
        System.out.println("SEARCH .....................1");
        // If there's no params then set default search I guess.
        if (params.length != 2) {
            return null;
        }
        System.out.println("SEARCH .....................11");
        if (!(params[0].equals(SEARCH_POPULAR) || params[0].equals(SEARCH_TOP_RATED))) {
            return null;
        }
        System.out.println("SEARCH .....................12");
        if (!Utils.isInteger(params[1])) {
            return null;
        }
        System.out.println("SEARCH .....................2");

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesFetchedJsonStr = null;

        String format = "json";

        final String SEARCH_BASE_URL;


        if (params[0].equals(SEARCH_POPULAR)) {
            SEARCH_BASE_URL = "http://api.themoviedb.org/3/movie/popular?";
        } else {
            SEARCH_BASE_URL = "http://api.themoviedb.org/3/movie/top_rated?";
        }
        System.out.println("SEARCH .....................3");
        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast


            final String PAGE_PARAM = "page";
            final String FORMAT_PARAM = "mode";
            final String APIKEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(SEARCH_BASE_URL).buildUpon()
                    .appendQueryParameter(PAGE_PARAM, params[1])
                    .appendQueryParameter(APIKEY_PARAM, "**************************key here")
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .build();


            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG, "Built URI " + builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            System.out.println("SEARCH .....................4");
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            System.out.println("SEARCH .....................5");
            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            System.out.println("SEARCH .....................6");
            moviesFetchedJsonStr = buffer.toString();

            Log.v(LOG_TAG, "Movies string: " + moviesFetchedJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error " + e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        try {
            return getMoviesIDsFromJason(moviesFetchedJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

    @Override
    protected void onPostExecute(SparseArray<IdCoverHolder> result) {
        if (result != null) {
//            mForecastAdapter.clear();
//            for (String dayForecastStr : result) {
//                mForecastAdapter.add(dayForecastStr);
//            }
            // New data is back from the server.  Hooray!

            for (int i = 0; i < result.size(); i++) {
                int key = result.keyAt(i);
                // get the object by the key.
                IdCoverHolder holder = result.get(key);
                System.out.println("ID FOUND : " + holder.id + "   path:" + holder.cover_path);
            }
            System.out.println("FOUND MOVIES : " + result.size());
            asyncResponse.processFinish(result);
        }
    }


    //  getjasoninfo
    private SparseArray getMoviesIDsFromJason(String moviesJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_RESULTS = "results";
        final String OWM_ID = "id";
        final String OWM_POSTER_PATH = "poster_path";


        JSONObject forecastJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = forecastJson.getJSONArray(OWM_RESULTS);

        // OWM returns   IDS which are needed to find movie in database and it's poster jpg path
        SparseArray<IdCoverHolder> idCoverHolders = new SparseArray<IdCoverHolder>(moviesArray.length());
        IdCoverHolder idCoverHolder;
        JSONObject movie;

        for (int i = 0; i < moviesArray.length(); i++) {
            // Get the JSON object representing the movie
            movie = moviesArray.getJSONObject(i);


            idCoverHolder = new IdCoverHolder();
            idCoverHolder.cover_path = movie.getString(OWM_POSTER_PATH);
            idCoverHolder.id = movie.getInt(OWM_ID);
            idCoverHolders.put(idCoverHolder.id, idCoverHolder);

        }
        return idCoverHolders;

    }


}
