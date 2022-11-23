package com.example.my_culture_tracker.View;


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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler;
import com.example.my_culture_tracker.R;


import org.json.JSONException;


import org.w3c.dom.Document;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import okhttp3.Headers;


public class MovieView extends AppCompatActivity {
    private String movieEan;
    private TextView title;
    private TextView date;
    private TextView author;
    private TextView edition;
    private TextView publishers;
    private TextView codeBar;
    private TextView actors;
    private TextView media;
    private ImageView movieView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_view);
        title = findViewById(R.id.title2Tv);
        date = findViewById(R.id.date2Tv);
        publishers = findViewById(R.id.publishers2Tv);
        author = findViewById(R.id.authors2Tv);
        edition = findViewById(R.id.editionTv);
        codeBar = findViewById(R.id.codeBarTv);
        actors = findViewById(R.id.actorsTv);
        media = findViewById(R.id.mediaTv);
        movieView = findViewById(R.id.movieView);
        progressBar = findViewById(R.id.progressBar2);
        ImageButton backBtn = findViewById(R.id.backBtn2);

        backBtn.setOnClickListener(v -> onBackPressed());

        movieEan = (String) getIntent().getSerializableExtra("movieEan");




        try {
            retrieveData(movieEan);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void retrieveData(CharSequence s) throws IOException {

        String url = "http://www.dvdfr.com/api/search.php?gencode="+s+"&withActors";



        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.get(url, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, String response) {
                Log.d("--------response-----------",response);
                try {
                    setText(response);
                } catch (ParserConfigurationException | IOException | SAXException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, @Nullable Headers headers, String errorResponse, @Nullable Throwable throwable) {
                Log.e("Error",errorResponse);
            }

        });


    }

    private void setText(String response) throws ParserConfigurationException, IOException, SAXException, JSONException {


        Document xml = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new InputSource(new StringReader(response)));


        title.setText(!xml.getElementsByTagName("fr").item(0).getTextContent().equals("")? xml.getElementsByTagName("fr").item(0).getTextContent():"N/A");
        date.setText(!xml.getElementsByTagName("annee").item(0).getTextContent().equals("")?xml.getElementsByTagName("annee").item(0).getTextContent():"N/A");
        publishers.setText(!xml.getElementsByTagName("editeur").item(0).getTextContent().equals("")?xml.getElementsByTagName("editeur").item(0).getTextContent():"N/A");
        author.setText(!xml.getElementsByTagName("star").item(0).getTextContent().equals("")?xml.getElementsByTagName("star").item(0).getTextContent():"N/A");
        edition.setText(!xml.getElementsByTagName("edition").item(0).getTextContent().equals("")?xml.getElementsByTagName("edition").item(0).getTextContent():"N/A");
        codeBar.setText(movieEan);
        actors.setText(xml.getElementsByTagName("star").getLength()>2?getStars(xml.getElementsByTagName("star")):"N/A");
        media.setText(!xml.getElementsByTagName("media").item(0).getTextContent().equals("")?xml.getElementsByTagName("media").item(0).getTextContent():"N/A");


        if(!xml.getElementsByTagName("cover").item(0).getTextContent().equals("")){
            String bookViewUri = xml.getElementsByTagName("cover").item(0).getTextContent();
            new MovieView.DownloadImageTask(movieView, progressBar)
                    .execute(bookViewUri);
        }



    }

    private String getStars(NodeList starList) {
        StringBuilder stars = new StringBuilder();


        for(int i =1; i<starList.getLength(); i++){
            stars.append(starList.item(i).getTextContent()).append("\n");
        }


        return stars.toString();
    }

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