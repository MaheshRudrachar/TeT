package com.teketys.templetickets.ux.adapters;

/**
 * Created by rudram1 on 8/25/16.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import com.teketys.templetickets.R;
import com.teketys.templetickets.entities.order.CustomerOrder;
import com.teketys.templetickets.entities.order.CustomerOrderDetails;
import com.teketys.templetickets.entities.order.CustomerOrderProduct;
import com.teketys.templetickets.entities.order.CustomerOrderVariation;
import com.teketys.templetickets.entities.order.Order;
import com.teketys.templetickets.utils.Utils;
import com.teketys.templetickets.views.ResizableImageView;

import java.util.List;

import timber.log.Timber;

/**
 * Adapter handling list of order items.
 */
public class OrderRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM_ORDER = 1;

    private LayoutInflater layoutInflater;
    private Context context;
    private CustomerOrderDetails order;

    /**
     * Creates an adapter that handles a list of order items.
     *
     * @param context activity context.
     */
    public OrderRecyclerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_ITEM_ORDER) {
            View view = layoutInflater.inflate(R.layout.list_item_order_product_image, parent, false);
            return new ViewHolderOrderProduct(view);
        } else {
            View view = layoutInflater.inflate(R.layout.list_item_order_header, parent, false);
            return new ViewHolderHeader(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderOrderProduct) {
            ViewHolderOrderProduct viewHolderOrderProduct = (ViewHolderOrderProduct) holder;

            /*Picasso.with(context).load(order.getProducts().get(position - 1).getThumb())
                    .fit().centerInside()
                    .placeholder(R.drawable.placeholder_loading)
                    .error(R.drawable.placeholder_error)
                    .into(viewHolderOrderProduct.productImage);*/

        } else if (holder instanceof ViewHolderHeader) {
            ViewHolderHeader viewHolderHeader = (ViewHolderHeader) holder;

            viewHolderHeader.orderId.setText(String.valueOf(order.getOrder_id()));
            viewHolderHeader.orderName.setText(order.getPayment_firstname() +" " + order.getPayment_lastname());
            viewHolderHeader.orderDateCreated.setText(order.getDate_modified());
            viewHolderHeader.orderTotal.setText(order.getTotal());
            viewHolderHeader.orderPaymentMethod.setText(order.getPayment_method());

            StringBuffer productName = null;
            if(order.getProducts() != null) {
                for (CustomerOrderProduct cop : order.getProducts()) {
                    productName.append(cop.getName() + "/");
                    for (CustomerOrderVariation cov : cop.getOptions()) {
                        productName.append(cov.getName() + "/" + cov.getValue());
                    }
                    productName.append(",");
                }
            }

            viewHolderHeader.orderProductName.setText((productName) != null ? productName.toString().trim() :"");
            //viewHolderHeader.orderProductVariation.setText();

            viewHolderHeader.orderStatus.setText((order.getHistories()) != null ? order.getHistories().get(0).getStatus() : "pending");

        } else {
            Timber.e(new RuntimeException(), "Unknown holder type.");
        }
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        if (order != null) {
            if (order.getProducts() != null) {
                return order.getProducts().size() + 1; // the number of items in the list, +1 for header view.
            } else {
                return 1;
            }
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        else
            return TYPE_ITEM_ORDER;
    }


    /**
     * Add item to list, and notify dataSet changed.
     *
     * @param order item to add.
     */
    public void addOrder(CustomerOrderDetails order) {
        if (order != null) {
            this.order = order;
            notifyDataSetChanged();
        } else {
            Timber.e("Setting null order object.");
        }
    }

    // Provide a reference to the views for each data item
    public static class ViewHolderOrderProduct extends RecyclerView.ViewHolder {
        ResizableImageView productImage;

        public ViewHolderOrderProduct(View itemView) {
            super(itemView);
            productImage = (ResizableImageView) itemView.findViewById(R.id.list_item_product_images_view);
        }
    }

    public static class ViewHolderHeader extends RecyclerView.ViewHolder {

        public TextView orderId;
        public TextView orderName;
        public TextView orderDateCreated;
        public TextView orderTotal;
        public TextView orderPaymentPrice;
        public TextView orderPaymentMethod;
        public TextView orderProductName;
        public TextView orderProductVariation;
        public TextView orderStatus;

        public ViewHolderHeader(View headerView) {
            super(headerView);
            orderId = (TextView) headerView.findViewById(R.id.list_item_order_header_id);
            orderName = (TextView) headerView.findViewById(R.id.list_item_order_header_name);
            orderDateCreated = (TextView) headerView.findViewById(R.id.list_item_order_header_dateCreated);
            orderTotal = (TextView) headerView.findViewById(R.id.list_item_order_header_total);
            orderPaymentMethod = (TextView) headerView.findViewById(R.id.list_item_order_header_payment_method);
            orderPaymentPrice = (TextView) headerView.findViewById(R.id.list_item_order_header_payment_price);
            orderStatus = (TextView) headerView.findViewById(R.id.list_item_order_header_order_status);
            orderProductName = (TextView) headerView.findViewById(R.id.list_item_order_header_product_name);
            orderProductVariation = (TextView) headerView.findViewById(R.id.list_item_order_header_product_variation);
        }
    }
}