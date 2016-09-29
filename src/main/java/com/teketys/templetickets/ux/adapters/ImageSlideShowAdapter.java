package com.teketys.templetickets.ux.adapters;

/**
 * Created by rudram1 on 9/2/16.
 */

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.teketys.templetickets.CONST;
import com.teketys.templetickets.MyApplication;
import com.teketys.templetickets.api.EndPoints;
import com.teketys.templetickets.api.GsonRequest;
import com.teketys.templetickets.entities.Banner;
import com.teketys.templetickets.entities.ImageSlideShow;
import com.teketys.templetickets.entities.drawerMenu.DrawerItemCategory;
import com.teketys.templetickets.entities.drawerMenu.DrawerResponse;
import com.teketys.templetickets.utils.MsgUtils;
import com.teketys.templetickets.utils.Utils;
import com.teketys.templetickets.ux.MainActivity;
import com.teketys.templetickets.ux.dialogs.LoginDialogFragment;
import com.teketys.templetickets.ux.dialogs.LoginExpiredDialogFragment;
import com.teketys.templetickets.ux.fragments.HomeFragment;
import com.teketys.templetickets.R;

import timber.log.Timber;

public class ImageSlideShowAdapter extends PagerAdapter {
    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;
    private ImageLoadingListener imageListener;
    FragmentActivity activity;
    List<ImageSlideShow> products;
    HomeFragment homeFragment;
    private ProgressDialog pDialog;

    public ImageSlideShowAdapter(FragmentActivity activity, List<ImageSlideShow> products,
                             HomeFragment homeFragment) {
        this.activity = activity;
        this.homeFragment = homeFragment;
        this.products = products;
        options = new DisplayImageOptions.Builder()
                //.showImageOnFail(R.drawable.ic_error)
                //.showStubImage(R.drawable.ic_launcher)
                //.showImageForEmptyUri(R.drawable.ic_empty).cacheInMemory()
                .cacheOnDisc().build();

        pDialog = Utils.generateProgressDialog(activity, false);
        imageListener = new ImageDisplayListener();
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public View instantiateItem(final ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.vp_image, container, false);

        ImageView mImageView = (ImageView) view
                .findViewById(R.id.image_display);
        mImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle arguments = new Bundle();
                final Fragment fragment = null;
                Log.d("position adapter", "" + position);
                final ImageSlideShow product = (ImageSlideShow) products.get(position);
                arguments.putParcelable("singleProduct", product);

                //Get the categories and filter the selected Temple

                pDialog.show();

                GsonRequest<DrawerResponse> getRequiredCategories = new GsonRequest<>(Request.Method.GET, EndPoints.NAVIGATION_DRAWER, null, DrawerResponse.class,
                        new Response.Listener<DrawerResponse>() {
                            @Override
                            public void onResponse(@NonNull DrawerResponse response) {
                                if(response != null) {
                                    if(response.getStatusCode() != null && response.getStatusText() != null) {
                                        if (response.getStatusCode().toLowerCase().equals(CONST.RESPONSE_CODE) || response.getStatusText().toLowerCase().equals(CONST.RESPONSE_UNAUTHORIZED)) {
                                            LoginDialogFragment.logoutUser(true);
                                            DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                                            loginExpiredDialogFragment.show(fragment.getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                                            if (pDialog != null) pDialog.cancel();                                        }
                                    }
                                    else {
                                        if (response.getNavigation() != null) {
                                            Timber.d("response: %s", response.getNavigation().toString());
                                            redirectToCategory(response, product);
                                        }
                                    }
                                }
                                else
                                    Timber.d("Null response during imageSlider instantiateItem....");

                                if (pDialog != null) pDialog.cancel();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (pDialog != null) pDialog.cancel();
                        MsgUtils.logAndShowErrorMessage(activity, error);
                    }
                }, activity.getSupportFragmentManager(), "");
                getRequiredCategories.setRetryPolicy(MyApplication.getDefaultRetryPolice());
                getRequiredCategories.setShouldCache(false);
                MyApplication.getInstance().addToRequestQueue(getRequiredCategories, CONST.DRAWER_REQUESTS_TAG);

                // Start a new fragment
                /*fragment = new ProductDetailFragment();
                fragment.setArguments(arguments);

                FragmentTransaction transaction = activity
                        .getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, fragment,
                        ProductDetailFragment.ARG_ITEM_ID);
                transaction.addToBackStack(ProductDetailFragment.ARG_ITEM_ID);
                transaction.commit();*/
            }
        });
        imageLoader.displayImage(
                ((ImageSlideShow) products.get(position)).getImage(), mImageView,
                options, imageListener);
        container.addView(view);
        return view;
    }

    private void redirectToCategory(DrawerResponse response, ImageSlideShow product) {

        //Get the selected product category
        boolean isPujaPresent = false;

        for(DrawerItemCategory dic : response.getNavigation()) {
            for(DrawerItemCategory childItemCategory : dic.getChildren()) {
                if(childItemCategory.getName().toLowerCase().startsWith(product.getTitle().toLowerCase())) {

                    isPujaPresent = true;
                    Timber.d("response: %s", childItemCategory.getName() + childItemCategory.getOriginalId());

                    Banner banner = new Banner();
                    banner.setTarget("list:"+childItemCategory.getOriginalId());
                    banner.setName(product.getTitle());

                    if (activity != null && activity instanceof MainActivity)
                        ((MainActivity) activity).onBannerSelected(banner);
                    break;
                }
            }
        }

        if(!isPujaPresent) {
            MsgUtils.showToast(activity, MsgUtils.TOAST_TYPE_MESSAGE, activity.getString(R.string.Pujas_not_found), MsgUtils.ToastLength.SHORT);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private static class ImageDisplayListener extends
            SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}