package com.xihua.weibopaper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xihua.weibopaper.activity.R;
import com.xihua.weibopaper.bean.PicUrls;
import com.xihua.weibopaper.common.MyApplication;

import java.util.List;

import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * @author cyq7on
 * @version V1.0
 * @Package com.xihua.weibopaper.adapter
 * @Description: RecyclerView显示图片的适配器
 * @date 2016/1/2216:56
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private List<PicUrls> urlList;
    private List<PhotoInfo> photoInfoList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private RequestQueue requestQueue;
    private DisplayImageOptions options;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickLitener(OnItemClickListener mOnItemClickLitener)
    {
        this.onItemClickListener = mOnItemClickLitener;
    }

    public ImageAdapter(List<PicUrls> mData, Context mContext, RequestQueue requestQueue) {
        this.urlList = mData;
        this.context = mContext;
        this.requestQueue = requestQueue;
        options = MyApplication.getOptions();
    }


    public ImageAdapter(List<PhotoInfo> mData, Context mContext) {
        this.photoInfoList = mData;
        this.context = mContext;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .build();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_weibo_image, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemLongClick(holder.itemView, pos);
                    return true;
                }
            });
        }
//        ImageUtils.getInstance().displayImage(requestQueue,
//                urlList.get(position).getThumbnail_pic(),holder.image);
        if (urlList != null) {
            ImageLoader.getInstance().displayImage(urlList.get(position).getThumbnail_pic(),
                    holder.image, options);
        } else {
            ImageLoader.getInstance().displayImage("file:/" +
                    photoInfoList.get(position).getPhotoPath(), holder.image, options);
        }

    }

    @Override
    public int getItemCount() {

        return urlList == null ? photoInfoList.size() : urlList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }


}
