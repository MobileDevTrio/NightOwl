package com.example.MobileDevTrio.nightowl;
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
    private List<Place> placeList;
    private Context context;

    MyAdapter(List<Place> placeList, Context context) {
        this.placeList = placeList;
        this.context = context;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        return new MyViewHolder(rootView, context, placeList);
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int i) {
        holder.placeName.setText(placeList.get(i).getName());
        holder.placeType.setText(placeList.get(i).getSingleType());
        holder.placeAddress.setText(placeList.get(i).getAddress());
        holder.placeRating.setText(Double.toString(placeList.get(i).getRating()));
        //holder.placeDescription.setText(placeList.get(i).getDescription());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cv;
        TextView placeName;
        TextView placeType;
        TextView placeAddress;
        TextView placeRating;

        //TextView placeDescription;
        List<Place> placeList1 = new ArrayList<>();
        Context context;

        private MyViewHolder(View itemView, Context context, List<Place> placeList1) {
            super(itemView);
            this.placeList1 = placeList1;
            this.context = context;
            itemView.setOnClickListener(this);
            cv = itemView.findViewById(R.id.cardView);
            placeName = itemView.findViewById(R.id.placeName);
            placeType = itemView.findViewById(R.id.placeType);
            placeAddress = itemView.findViewById(R.id.placeAddress);
            placeRating = itemView.findViewById(R.id.placeRating);
            //placeDescription = itemView.findViewById(R.id.placeDescription);
        }


        @Override
        public void onClick(View v) {
            Place place = this.placeList1.get(getAdapterPosition());
            ((MapsActivity) context).cardViewClicked(place);
        }
    }

}
