package com.example.MobileDevTrio.nightowl;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        holder.placeType.setText(placeList.get(i).getSimplifiedType());
        holder.placeAddress.setText(placeList.get(i).getAddress());
        holder.placeRating.setText(Double.toString(placeList.get(i).getRating()));
        holder.resetRatingStarVisibility();
        holder.setSelectedPlaceRatingStars(Double.toString(placeList.get(i).getRating()));
        holder.closingTime.setText(holder.getClosingTimeString(placeList.get(i)));
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
        TextView closingTime;
        //Ratings stars
        ImageView placeStarEmpty1, placeStarEmpty2, placeStarEmpty3, placeStarEmpty4, placeStarEmpty5,
                placeStarFull1, placeStarFull2, placeStarFull3, placeStarFull4, placeStarFull5,
                placeStarHalf1, placeStarHalf2, placeStarHalf3, placeStarHalf4, placeStarHalf5;

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
            closingTime = itemView.findViewById(R.id.placeClosingTime);

            // Rating Stars
            placeStarFull1 = itemView.findViewById(R.id.placeRatingStarFull1);
            placeStarFull2 = itemView.findViewById(R.id.placeRatingStarFull2);
            placeStarFull3 = itemView.findViewById(R.id.placeRatingStarFull3);
            placeStarFull4 = itemView.findViewById(R.id.placeRatingStarFull4);
            placeStarFull5 = itemView.findViewById(R.id.placeRatingStarFull5);

            placeStarHalf1 = itemView.findViewById(R.id.placeRatingStarHalf1);
            placeStarHalf2 = itemView.findViewById(R.id.placeRatingStarHalf2);
            placeStarHalf3 = itemView.findViewById(R.id.placeRatingStarHalf3);
            placeStarHalf4 = itemView.findViewById(R.id.placeRatingStarHalf4);
            placeStarHalf5 = itemView.findViewById(R.id.placeRatingStarHalf5);

            placeStarEmpty1 = itemView.findViewById(R.id.placeRatingStarEmpty1);
            placeStarEmpty2 = itemView.findViewById(R.id.placeRatingStarEmpty2);
            placeStarEmpty3 = itemView.findViewById(R.id.placeRatingStarEmpty3);
            placeStarEmpty4 = itemView.findViewById(R.id.placeRatingStarEmpty4);
            placeStarEmpty5 = itemView.findViewById(R.id.placeRatingStarEmpty5);
        }


        @Override
        public void onClick(View v) {
            Place place = this.placeList1.get(getAdapterPosition());
            ((MapsActivity) context).cardViewClicked(place);
        }

        private String getClosingTimeString(Place place) {
            String closingTimeString = "";
            String closingTime = place.getClosingHours();
            boolean isOpen247 = place.isOpen247();

            if (isOpen247) {
                closingTimeString = "Open 24 Hours";
            }
            else if (closingTime != null) {
                if (!closingTime.isEmpty()) {
                    String[] hours = closingTime.split(":");
                    String[] minAMPM = hours[1].split(" ");
                    String part1 = hours[0];
                    String part2 = minAMPM[0];
                    String part3 = minAMPM[1];

                    // checks if beginning character is '0' and removes it.
                    if (part1.charAt(0) == '0') {
                        part1 = part1.charAt(1) + "";
                    }

                    //
                    if (part2.charAt(0) == '0' && part2.charAt(1) == '0') {
                        closingTimeString = "Close " + part1 + " " + part3;
                    } else {
                        closingTimeString = "Close " + part1 + ":" + part2 + " " + part3;
                    }


                }
            }

            return closingTimeString;
        }

        private void resetRatingStarVisibility() {
            placeStarFull1.setVisibility(View.INVISIBLE);
            placeStarFull2.setVisibility(View.INVISIBLE);
            placeStarFull3.setVisibility(View.INVISIBLE);
            placeStarFull4.setVisibility(View.INVISIBLE);
            placeStarFull5.setVisibility(View.INVISIBLE);

            placeStarHalf1.setVisibility(View.INVISIBLE);
            placeStarHalf2.setVisibility(View.INVISIBLE);
            placeStarHalf3.setVisibility(View.INVISIBLE);
            placeStarHalf4.setVisibility(View.INVISIBLE);
            placeStarHalf5.setVisibility(View.INVISIBLE);

            placeStarEmpty1.setVisibility(View.INVISIBLE);
            placeStarEmpty2.setVisibility(View.INVISIBLE);
            placeStarEmpty3.setVisibility(View.INVISIBLE);
            placeStarEmpty4.setVisibility(View.INVISIBLE);
            placeStarEmpty5.setVisibility(View.INVISIBLE);
        }

        private void setSelectedPlaceRatingStars(String rating) {
            int part1 = 0, part2 = 0;
            if(!rating.isEmpty()) {
                String[] part = rating.split("\\.");
                part1 = Integer.parseInt(part[0]);
                part2 = Integer.parseInt(part[1]);
            }

            // Set Full Stars
            if (part1 >= 1) {
                placeStarFull1.setVisibility(View.VISIBLE);
            }
            if (part1 >= 2) {
                placeStarFull2.setVisibility(View.VISIBLE);
            }
            if (part1 >= 3) {
                placeStarFull3.setVisibility(View.VISIBLE);
            }
            if (part1 >= 4) {
                placeStarFull4.setVisibility(View.VISIBLE);
            }
            if (part1 == 5) {
                placeStarFull5.setVisibility(View.VISIBLE);
            }


            // Set Initial Empty/Half/Full Stars
            if(part1 == 0) {
                if(part2 < 3) {
                    placeStarEmpty1.setVisibility(View.VISIBLE);       // set first empty star
                } else if (part2 >= 3 && part2 <= 7) {
                    placeStarHalf1.setVisibility(View.VISIBLE);        // set half star
                } else if (part2 > 7) {
                    placeStarFull1.setVisibility(View.VISIBLE);        // set next full star
                }
            }
            if (part1 == 1) {
                placeStarFull1.setVisibility(View.VISIBLE);
                if(part2 < 3) {
                    placeStarEmpty2.setVisibility(View.VISIBLE);       // set first empty star
                } else if (part2 >= 3 && part2 <= 7) {
                    placeStarHalf2.setVisibility(View.VISIBLE);        // set half star
                } else if (part2 > 7) {
                    placeStarFull2.setVisibility(View.VISIBLE);        // set next full star
                }
            }
            if (part1 == 2) {
                placeStarFull2.setVisibility(View.VISIBLE);
                if(part2 < 3) {
                    placeStarEmpty3.setVisibility(View.VISIBLE);       // set first empty star
                } else if (part2 >= 3 && part2 <= 7) {
                    placeStarHalf3.setVisibility(View.VISIBLE);        // set half star
                } else if (part2 > 7) {
                    placeStarFull3.setVisibility(View.VISIBLE);        // set next full star
                }
            }
            if (part1 == 3) {
                placeStarFull3.setVisibility(View.VISIBLE);
                if(part2 < 3) {
                    placeStarEmpty4.setVisibility(View.VISIBLE);       // set first empty star
                } else if (part2 >= 3 && part2 <= 7) {
                    placeStarHalf4.setVisibility(View.VISIBLE);        // set half star
                } else if (part2 > 7) {
                    placeStarFull4.setVisibility(View.VISIBLE);        // set next full star
                }
            }
            if (part1 == 4) {
                placeStarFull4.setVisibility(View.VISIBLE);
                if(part2 < 3) {
                    placeStarEmpty5.setVisibility(View.VISIBLE);       // set first empty star
                } else if (part2 >= 3 && part2 <= 7) {
                    placeStarHalf5.setVisibility(View.VISIBLE);        // set half star
                } else if (part2 > 7) {
                    placeStarFull5.setVisibility(View.VISIBLE);        // set next full star
                }
            }
            if (part1 == 5) {
                placeStarFull5.setVisibility(View.VISIBLE);
            }

            // Set Remaining Empty Stars
            if(part1 <= 3) {
                placeStarEmpty5.setVisibility(View.VISIBLE);
            }
            if(part1 <= 2) {
                placeStarEmpty4.setVisibility(View.VISIBLE);
            }
            if(part1 <= 1) {
                placeStarEmpty3.setVisibility(View.VISIBLE);
            }
            if(part1 == 0) {
                placeStarEmpty2.setVisibility(View.VISIBLE);
            }

        }
    }

}
