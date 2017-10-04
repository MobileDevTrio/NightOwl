package com.example.khoapham.nightowl;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines Adapter class for the RecyclerView
 */

class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<Venue> venueList;
    private Context context;

    MyAdapter(List<Venue> venueList, Context context) {
        this.venueList = venueList;
        this.context = context;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        return new MyViewHolder(rootView, context, venueList);
    }

    @Override
    public int getItemCount() {
        return venueList.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int i) {
        holder.venueName.setText(venueList.get(i).getName());
        holder.venueType.setText(venueList.get(i).getType());
        holder.venueAddress.setText(venueList.get(i).getAddress());
        holder.venueDescription.setText(venueList.get(i).getDescription());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cv;
        TextView venueName;
        TextView venueType;
        TextView venueAddress;
        TextView venueDescription;
        List<Venue> venueList1 = new ArrayList<>();
        Context context;

        private MyViewHolder(View itemView, Context context, List<Venue> venueList1) {
            super(itemView);
            this.venueList1 = venueList1;
            this.context = context;
            itemView.setOnClickListener(this);
            cv = itemView.findViewById(R.id.cardView);
            venueName = itemView.findViewById(R.id.venueName);
            venueType = itemView.findViewById(R.id.venueType);
            venueAddress = itemView.findViewById(R.id.venueAddress);
            venueDescription = itemView.findViewById(R.id.venueDescription);
        }


        @Override
        public void onClick(View v) {
            Venue venue = this.venueList1.get(getAdapterPosition());
            ((MapsActivity) context).cardViewClicked(venue.getName());
        }
    }

}
