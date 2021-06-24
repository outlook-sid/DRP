package com.example.drp.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drp.R;

import java.util.List;

public class TransactionInfoAdapter extends RecyclerView.Adapter<TransactionInfoAdapter.TransactionInfoViewHolder> {

    private Context context;
    private List<TransactionInfoModel> transactionModelList;
    private boolean expandedFlag = false;

    public TransactionInfoAdapter(Context context, List<TransactionInfoModel> transactionModelList) {
        this.context = context;
        this.transactionModelList = transactionModelList;
    }

    @NonNull
    @Override
    public TransactionInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_transaction_info, null);
        return new TransactionInfoViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull TransactionInfoViewHolder holder, int position) {
        TransactionInfoModel transactionModel = transactionModelList.get(position);

        holder.tvTransactionDate.setText(UtilHelper.prettyDate(transactionModel.getTransactionDate()));
        holder.tvTransactionSubtotal.setText(transactionModel.getTransactionSubtotal());
        holder.tvTransactionPaymentMethod.setText(transactionModel.getTransactionPaymentMethod());
        holder.tvTransactionBookedDate.setText(UtilHelper.prettyDate(transactionModel.getTransactionBookedDate()));
        holder.tvTransactionBookedTime.setText(transactionModel.getTransactionBookedTime());
        holder.tvTransactionRiceSubtotal.setText(transactionModel.getTransactionRiceSubtotal());
        holder.tvTransactionWheatSubtotal.setText(transactionModel.getTransactionWheatSubtotal());
        holder.tvTransactionSugarSubtotal.setText(transactionModel.getTransactionSugarSubtotal());
        holder.tvTransactionKeroseneSubtotal.setText(transactionModel.getTransactionKeroseneSubtotal());

    }

    @Override
    public int getItemCount() {
        return transactionModelList.size();
    }



    class TransactionInfoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTransactionDate;
        TextView tvTransactionSubtotal;
        TextView tvTransactionPaymentMethod;
        TextView tvTransactionBookedDate;
        TextView tvTransactionBookedTime;
        TextView tvTransactionRiceSubtotal;
        TextView tvTransactionWheatSubtotal;
        TextView tvTransactionSugarSubtotal;
        TextView tvTransactionKeroseneSubtotal;

        public TransactionInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTransactionDate = itemView.findViewById(R.id.tv_transaction_date);
            tvTransactionSubtotal = itemView.findViewById(R.id.tv_transaction_subtotal);
            tvTransactionPaymentMethod = itemView.findViewById(R.id.tv_transaction_info_pay_mode);
            tvTransactionBookedDate = itemView.findViewById(R.id.tv_transaction_info_booked_date);
            tvTransactionBookedTime = itemView.findViewById(R.id.tv_transaction_info_booked_time);
            tvTransactionRiceSubtotal = itemView.findViewById(R.id.tv_transaction_info_rice_subtotal);
            tvTransactionWheatSubtotal = itemView.findViewById(R.id.tv_transaction_info_wheat_subtotal);
            tvTransactionSugarSubtotal = itemView.findViewById(R.id.tv_transaction_info_sugar_subtotal);
            tvTransactionKeroseneSubtotal = itemView.findViewById(R.id.tv_transaction_info_kerosene_subtotal);

            ConstraintLayout constraintLayout = (ConstraintLayout)itemView.findViewById(R.id.cl_transac_info_more) ;
            constraintLayout.setVisibility(View.GONE);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.iv_transac_info_view_more);

            imageView.setOnClickListener(v -> {
                if (expandedFlag) {
                    expandedFlag = false;
                    imageView.setImageDrawable(ResourcesCompat.getDrawable(itemView.getResources(), R.drawable.ic_baseline_arrow_down, null));
                    constraintLayout.setVisibility(View.GONE);
                }
                else {
                    expandedFlag = true;
                    imageView.setImageDrawable(ResourcesCompat.getDrawable(itemView.getResources(), R.drawable.ic_baseline_arrow_up, null));
                    constraintLayout.setVisibility(View.VISIBLE);
                }

            });
        }
    }
}
