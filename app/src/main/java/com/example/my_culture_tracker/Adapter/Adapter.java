package com.example.my_culture_tracker.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_culture_tracker.Model.ListItem;
import com.example.my_culture_tracker.R;
import com.example.my_culture_tracker.View.BookView;
import com.example.my_culture_tracker.View.MovieView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.AdapterViewHolder> {

    private List<ListItem> listItemArrayList;
    private Context context;

    public Adapter(List<ListItem> listItemArrayList, Context context){
        this.listItemArrayList = listItemArrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout,parent,false);
        return new  AdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        ListItem listItem = listItemArrayList.get(position);
        holder.textViewCode.setText(listItem.getCode());
        holder.textViewType.setText(listItem.getType());
        Linkify.addLinks(holder.textViewCode,Linkify.ALL);
    }

    @Override
    public int getItemCount() {
        return listItemArrayList.size();
    }

    public class AdapterViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewCode,textViewType;
        private CardView cardView;
        public AdapterViewHolder( View itemView) {
            super(itemView);
            textViewCode = (TextView) itemView.findViewById(R.id.textViewCode);
            textViewType = (TextView) itemView.findViewById(R.id.textViewType);
            cardView = (CardView) itemView.findViewById(R.id.cardView);

            cardView.setOnClickListener(v -> {
                String type = listItemArrayList.get(getAdapterPosition()).getType();



                if(type.equals("Movie")) {

                    Intent movieIntent = new Intent(itemView.getContext(), MovieView.class);
                    movieIntent.setAction(Intent.ACTION_SEND);
                    movieIntent.putExtra(Intent.EXTRA_TEXT,type);
                    movieIntent.putExtra("type", textViewType.getText());
                    movieIntent.putExtra("movieEan", textViewCode.getText());
                    movieIntent.setType("text/plain");
                    itemView.getContext().startActivity(movieIntent);

                }else {

                    Intent bookIntent = new Intent(itemView.getContext(), BookView.class);
                    bookIntent.setAction(Intent.ACTION_SEND);
                    bookIntent.putExtra(Intent.EXTRA_TEXT, type);
                    bookIntent.putExtra("type", textViewType.getText());
                    bookIntent.putExtra("bookIsbn", textViewCode.getText());
                    bookIntent.setType("text/plain");
                    itemView.getContext().startActivity(bookIntent);

                }

            });
        }
    }
}