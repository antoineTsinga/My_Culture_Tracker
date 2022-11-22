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

                Intent i = new Intent(itemView.getContext(), BookView.class);
                i.setAction(Intent.ACTION_SEND);
                i.putExtra(Intent.EXTRA_TEXT,type);
                i.putExtra("bookIsbn", textViewCode.getText());
                i.setType("text/plain");
                itemView.getContext().startActivity(i);
            });
        }
    }
}