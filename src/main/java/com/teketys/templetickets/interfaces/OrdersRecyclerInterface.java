package com.teketys.templetickets.interfaces;

/**
 * Created by rudram1 on 8/25/16.
 */

import android.view.View;

import com.teketys.templetickets.entities.order.CustomerOrder;
import com.teketys.templetickets.entities.order.Order;

public interface OrdersRecyclerInterface {

    void onOrderSelected(View v, CustomerOrder order);

}
