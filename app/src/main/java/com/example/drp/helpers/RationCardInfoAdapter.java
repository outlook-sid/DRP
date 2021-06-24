package com.example.drp.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drp.R;

import java.util.List;

public class RationCardInfoAdapter extends RecyclerView.Adapter<RationCardInfoAdapter.RationCardInfoViewHolder> {

    private Context context;
    private List<RationCardModel> rationCardModelList;

    public RationCardInfoAdapter(Context context, List<RationCardModel> rationCardModelList) {
        this.context = context;
        this.rationCardModelList = rationCardModelList;
    }

    @NonNull
    @Override
    public RationCardInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_ration_card_info, null);
        return new RationCardInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RationCardInfoViewHolder holder, int position) {
        RationCardModel rationCardModel = rationCardModelList.get(position);

        holder.tvRationCardID.setText(rationCardModel.getCardNo());
        holder.tvCardHolderName.setText(rationCardModel.getUserName());
        holder.tvPhoneNo.setText(rationCardModel.getUserPhone());
        holder.tvRationDealerID.setText(rationCardModel.getDealerID());
    }

    @Override
    public int getItemCount() {
        return rationCardModelList.size();
    }

    class RationCardInfoViewHolder extends RecyclerView.ViewHolder {

        TextView tvRationCardID;
        TextView tvCardHolderName;
        TextView tvPhoneNo;
        TextView tvRationDealerID;

        public RationCardInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRationCardID = itemView.findViewById(R.id.tv_ration_card_info_card_no);
            tvCardHolderName = itemView.findViewById(R.id.tv_ration_card_info_holder_name);
            tvPhoneNo = itemView.findViewById(R.id.tv_ration_card_info_phn_no);
            tvRationDealerID = itemView.findViewById(R.id.tv_ration_card_info_dealer_id);
        }
    }
}
