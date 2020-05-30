package com.example.githubqueryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;

import utilities.NetworkUtils;


public class MainActivity extends AppCompatActivity {

    // Member variable for the Search Box EditText
   private EditText mSearchBoxEditText;

    // Member variable for the URLDisplay TextView
   private TextView mUrlDisplayTextView;

    // Member variable for the SearchResults TextView
   private TextView mSearchResultsTextView;

    //  Create a variable to store a reference to the error message TextView
    private TextView mErrorMessageDisplay;

    //  Create a ProgressBar variable to store a reference to the ProgressBar
    private ProgressBar mLoadingIndicator;

    /* A constant to save and restore the URL that is being displayed */
    private static final String SEARCH_QUERY_URL_EXTRA = "query";

    /* A constant to save and restore the JSON that is being displayed */
    private static final String SEARCH_RESULTS_RAW_JSON = "results";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  Use findViewById to get a reference to mSearchBoxEditText
        mSearchBoxEditText = (EditText) findViewById(R.id.et_search_box);

        //  Use findViewById to get a reference to mUrlDisplayTextView
        mUrlDisplayTextView = (TextView) findViewById(R.id.tv_url_display);

        // C Use findViewById to get a reference to mSearchResultsTextView
        mSearchResultsTextView = (TextView) findViewById(R.id.tv_github_search_results_json);

        //  If the savedInstanceState bundle is not null, set the text of the URL and
        //  search results TextView respectively

        if (savedInstanceState != null) {
            String queryUrl = savedInstanceState.getString(SEARCH_QUERY_URL_EXTRA);
            String rawJsonSearchResults = savedInstanceState.getString(SEARCH_RESULTS_RAW_JSON);

            mUrlDisplayTextView.setText(queryUrl);
            mSearchResultsTextView.setText(rawJsonSearchResults);
        }

    }


    /**
     * This method retrieves the search text from the EditText, constructs
     * the URL (using {@link NetworkUtils}) for the github repository you'd like to find, displays
     * This method retrieves the search text from the EditText, constructs the
     *  URL (using {@link NetworkUtils}) for the github repository you'd like to find, displays
     * that URL in a TextView, and finally fires off an AsyncTask to perform the GET request using
     * our (not yet created) {@link //GithubQueryTask}
     */
    private void makeGithubSearchQuery(){
        String githubQuery = mSearchBoxEditText.getText().toString();
        URL githubSearchUrl = NetworkUtils.buildUrl(githubQuery);
        mUrlDisplayTextView.setText(githubSearchUrl.toString());

        // Create a new GithubQueryTask and call its execute method, passing in the url to query
        new GithubQueryTask().execute(githubSearchUrl);
    }
    // COMPLETED (14) Create a method called showJsonDataView to show the data and hide the error
    /**
     * This method will make the View for the JSON data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showJsonDataView(){
        // First, make sure the error is invisible
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);

        // Then, make sure the JSON data is visible
        mSearchResultsTextView.setVisibility(View.VISIBLE);
    }

    // COMPLETED (15) Create a method called showErrorMessage to show the error and hide the data
    /**
     * This method will make the error message visible and hide the JSON
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage(){
        // First, hide the currently visible data
        mSearchResultsTextView.setVisibility(View.INVISIBLE);

        // Then, show the error
        mErrorMessageDisplay.setVisibility(View.VISIBLE);

    }

    //Create a class called GithubQueryTask that extends AsyncTask<URL, Void, String>
    public class GithubQueryTask extends AsyncTask<URL, Void, String> {
        //  Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute() {
            mLoadingIndicator.setVisibility(View.VISIBLE);

        }

        //  Override the doInBackground method to perform the query. Return the results.
        //  (Hint: You've already written the code to perform the query)
        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String githubSearchResults = null;
            try {
                githubSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return githubSearchResults;
        }

        //  Override onPostExecute to display the results in the TextView

        @Override
        protected void onPostExecute(String githubSearchResults) {
            //  As soon as the loading is complete, hide the loading indicator
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (githubSearchResults != null && !githubSearchResults.equals("")){
                //  Call showJsonDataView if we have valid, non-null results
                showJsonDataView();

                mSearchResultsTextView.setText(githubSearchResults);
            }else {
                //  Call showErrorMessage if the result is null in onPostExecute
                showErrorMessage();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_search){
            makeGithubSearchQuery();
        }
        return super.onOptionsItemSelected(item);
    }

    // Override onSaveInstanceState to persist data across Activity recreation
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // COMPLETED (4) Make sure super.onSaveInstanceState is called before doing anything else
        super.onSaveInstanceState(outState);

        // COMPLETED (5) Put the contents of the TextView that contains our URL into a variable
        String queryUrl = mUrlDisplayTextView.getText().toString();

        // COMPLETED (6) Using the key for the query URL, put the string in the outState Bundle
        outState.putString(SEARCH_QUERY_URL_EXTRA, queryUrl);


        // COMPLETED (7) Put the contents of the TextView that contains our raw JSON search results
        //  into a variable
        String rawJsonSearchResults = mSearchResultsTextView.getText().toString();

        // COMPLETED (8) Using the key for the raw JSON search results, put the search results into the outState Bundle
        outState.putString(SEARCH_RESULTS_RAW_JSON, rawJsonSearchResults);


    }
}
