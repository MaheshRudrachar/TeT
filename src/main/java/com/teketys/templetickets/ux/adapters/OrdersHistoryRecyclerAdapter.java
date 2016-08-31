package com.teketys.templetickets.ux.adapters;

/**
 * Created by rudram1 on 8/25/16.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.teketys.templetickets.R;
import com.teketys.templetickets.entities.order.CustomerOrder;
import com.teketys.templetickets.entities.order.Order;
import com.teketys.templetickets.interfaces.OrdersRecyclerInterface;
import com.teketys.templetickets.utils.Utils;
import timber.log.Timber;

/**
 * Adapter handling list of orders from history.
 */
public class OrdersHistoryRecyclerAdapter extends RecyclerView.Adapter<OrdersHistoryRecyclerAdapter.ViewHolder> {

    private final OrdersRecyclerInterface ordersRecyclerInterface;
    private LayoutInflater layoutInflater;
    private List<CustomerOrder> orders = new ArrayList<>();

    /**
     * Creates an adapter that handles a list of orders from history
     *
     * @param ordersRecyclerInterface listener indicating events that occurred.
     */
    public OrdersHistoryRecyclerAdapter(OrdersRecyclerInterface ordersRecyclerInterface) {
        this.ordersRecyclerInterface = ordersRecyclerInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.list_item_orders_history, parent, false);
        return new ViewHolder(view, ordersRecyclerInterface);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CustomerOrder order = getOrderItem(position);
        holder.bindContent(order);

        holder.orderIdTv.setText(String.valueOf(order.getOrder_id()));
        holder.orderDateCreatedTv.setText(Utils.parseDate(order.getDate_added()));
        holder.orderTotalPriceTv.setText((order.getTotal()));
    }

    private CustomerOrder getOrderItem(int position) {
        return orders.get(position);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void addOrders(List<CustomerOrder> orderList) {
        if (orderList != null && !orderList.isEmpty()) {
            orders.addAll(orderList);
            notifyDataSetChanged();
        } else {
            Timber.e("Adding empty orders list.");
        }
    }

    /**
     * Clear all data.
     */
    public void clear() {
        orders.clear();
    }

    // Provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView orderIdTv;
        private TextView orderDateCreatedTv;
        private TextView orderTotalPriceTv;
        private CustomerOrder order;

        public ViewHolder(View itemView, final OrdersRecyclerInterface ordersRecyclerInterface) {
            super(itemView);
            orderIdTv = (TextView) itemView.findViewById(R.id.order_history_item_id);
            orderDateCreatedTv = (TextView) itemView.findViewById(R.id.order_history_item_dateCreated);
            orderTotalPriceTv = (TextView) itemView.findViewById(R.id.order_history_item_totalPrice);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ordersRecyclerInterface.onOrderSelected(v, order);
                }
            });
        }

        public void bindContent(CustomerOrder order) {
            this.order = order;
        }
    }
}
