package com.example.my_culture_tracker.View;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.my_culture_tracker.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.io.InputStream;


public class BookView extends AppCompatActivity {

    //view binding

    private String bookIsbn;
    private TextView title;
    private TextView date;
    private TextView author;
    private TextView pages;
    private TextView publishers;
    private TextView isbn10;
    private TextView isbn13;
    private TextView weight;
    private TextView description;
    private TextView identifiers;
    private ImageView bookView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_view);

        title = findViewById(R.id.titleTv);
        date = findViewById(R.id.dateTv);
        publishers = findViewById(R.id.publishersTv);
        author = findViewById(R.id.authorsTv);
        pages = findViewById(R.id.pagesTv);
        isbn10 = findViewById(R.id.isbn10Tv);
        isbn13 = findViewById(R.id.isbn13Tv);
        weight = findViewById(R.id.weightTv);
        identifiers = findViewById(R.id.identifiers);
        description = findViewById(R.id.description);
        bookView = findViewById(R.id.bookView);
        progressBar = findViewById(R.id.progressBar);
        ImageButton backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> onBackPressed());

        bookIsbn = (String) getIntent().getSerializableExtra("bookIsbn");




        retrieveData(bookIsbn);
    }

    private void retrieveData(CharSequence s) {

        String url = "http://openlibrary.org/api/books?bibkeys=ISBN:"+s+"&jscmd=details&format=json" ;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {


            try {
                setText(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("Volley response", response.toString());
        },
                error -> {
                    Log.d("Volley response", "An error occurred.");
                    Log.d("error", error.getMessage());
                });
        queue.add(request);
        queue.start();


    }

    private void setText(JSONObject response) throws JSONException {

        JSONObject book = response.getJSONObject("ISBN:"+bookIsbn);
        if (book.equals(null) || book.getJSONObject("details").equals(null)) return;


        JSONObject bookDetails;

            bookDetails = book.getJSONObject("details");



        title.setText(bookDetails.has("title") ? bookDetails.getString("title"): "N/A");
        author.setText(bookDetails.has("authors") ?getAuthors(bookDetails.getJSONArray("authors")): "N/A");
        date.setText(bookDetails.has("publish_date") ?bookDetails.getString("publish_date"): "N/A");
        pages.setText(bookDetails.has("number_of_pages") ?bookDetails.getString("number_of_pages"): "N/A");
        publishers.setText(bookDetails.has("publishers")? getPublishers(bookDetails.getJSONArray("publishers")): "N/A");
        isbn13.setText(bookDetails.has("isbn_13")? bookDetails.getString("isbn_13") : "N/A");
        weight.setText(bookDetails.has("weight")? bookDetails.getString("weight"): "N/A");
        isbn10.setText(bookDetails.has("isbn_10") ?bookDetails.getString("isbn_10"): "N/A");
        description.setText(bookDetails.has("description") ?bookDetails.getString("description"): "The description of the book.");
        identifiers.setText(bookDetails.has("identifiers") ?getIdentifiers(bookDetails.getJSONObject("identifiers")): "The identifiers of the book.");


         if(bookDetails.has("covers")){
                String bookViewUri = "https://covers.openlibrary.org/b/id/"+bookDetails.getJSONArray("covers").remove(0)+"-L.jpg";

             new DownloadImageTask(bookView, progressBar)
                     .execute(bookViewUri);
            }

    }



    private String getAuthors(JSONArray jsonArray) throws JSONException {
        StringBuilder authors = new StringBuilder();
        for(int i=0; i<jsonArray.length(); i++){
            authors.append(jsonArray.getJSONObject(i).getString("name")).append(", ");
        }

        return authors.toString();
    }

    private String getPublishers(JSONArray jsonArray) throws JSONException {

        StringBuilder publishersString = new StringBuilder();
        for(int i=0; i<jsonArray.length(); i++){
            publishersString.append(jsonArray.getString(i)).append(", ");
        }

        return publishersString.toString();
    }

    private String getIdentifiers(JSONObject jsonObject) throws JSONException {


        String identifiers =  "";
        String amazonUri = "https://www.amazon.fr/dp/"+jsonObject.getJSONArray("amazon").remove(0);
        String googleUri = "https://books.google.fr/books/about/?"+jsonObject.getJSONArray("google").remove(0);
        identifiers += amazonUri +"\n"+googleUri;

        return identifiers;
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        ProgressBar loader;

        public DownloadImageTask(ImageView bmImage,ProgressBar loader) {
            this.bmImage = bmImage;
            this.loader = loader;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            loader.setVisibility(View.GONE);
            bmImage.setImageBitmap(result);
        }
    }



}