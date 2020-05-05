package com.example.vgvee.arfurniture;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;



import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Files> files;

    public FilesAdapter(Context context, ArrayList<Files> files) {
        this.context = context;
        this.files = files;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.item_row,viewGroup,false);
        MyViewHolder mvh = new MyViewHolder(view);
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Picasso.get().load(files.get(i).its_img).into(myViewHolder.product);
        myViewHolder.product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof MainActivity) {
                    ((MainActivity)context).addObject(files.get(i).its_sfb);
                }
            }
        });

        myViewHolder.name.setText(files.get(i).its_name);
    myViewHolder.provider.setText(files.get(i).its_provider);
    myViewHolder.price.setText("Rs "+files.get(i).its_price);


    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView product;
        TextView name,provider,price;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            product = itemView.findViewById(R.id.iView);
            name = itemView.findViewById(R.id.name);
            provider = itemView.findViewById(R.id.provider);
            price = itemView.findViewById(R.id.price);

        }
    }
}
