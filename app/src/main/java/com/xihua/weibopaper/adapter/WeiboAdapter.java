package com.xihua.weibopaper.adapter;

import android.content.Context;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.User;
import com.xihua.weibopaper.activity.R;
import com.xihua.weibopaper.bean.PicUrls;
import com.xihua.weibopaper.bean.StatusContent;
import com.xihua.weibopaper.bean.WeiboContent;
import com.xihua.weibopaper.utils.ImageUtils;
import com.xihua.weibopaper.utils.ScreenUtils;
import com.xihua.weibopaper.utils.ToastUtil;
import com.xihua.weibopaper.view.CircleImageView;

/**
 * @author cyq7on
 * @version V1.0
 * @Package com.xihua.weibopaper.adapter
 * @Description:微博内容的适配器
 * @date 2015/12/2517:12
 */
public class WeiboAdapter extends RecyclerView.Adapter<WeiboAdapter.ViewHolder>{
    private Context context;
    private WeiboContent content;
    private OnItemClickListener onItemClickListener;
    private View.OnClickListener onClickListener;
    private RequestQueue requestQueue;
    public WeiboAdapter(Context context,WeiboContent content,RequestQueue requestQueue) {
        this.context = context;
        this.content = content;
        this.requestQueue = requestQueue;
    }

    public interface OnItemClickListener {
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_weibo_content, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (content.getStatuses().size() == 0) {
            return;
        }
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
                    onItemClickListener.onItemLongClick(holder.itemView,pos);
                    return true;
                }
            });
        }
        StatusContent sc = content.getStatuses().get(position);
        User user = sc.getUser();
//        if (onClickListener == null) {
            onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.showShort(context,Integer.toString(position));
                }
            };
            holder.ivMore.setOnClickListener(onClickListener);
//        }
        ImageUtils.getInstance().displayImage(requestQueue, user.avatar_large,null,
                holder.iv);
//        holder.setIsRecyclable(false);
        // 黄V
        if (user.verified_type == 0) {
            holder.ivVerify.setImageResource(R.mipmap.avatar_vip);
        }
        // 200:初级达人 220:高级达人
        else if (user.verified_type == 200 || user.verified_type == 220) {
            holder.ivVerify.setImageResource(R.mipmap.avatar_grassroot);
        }
        // 蓝V
        else if (user.verified_type > 0) {
            holder.ivVerify.setImageResource(R.mipmap.avatar_enterprise_vip);
        }
        if (user.verified_type >= 0) {
            holder.ivVerify.setVisibility(View.VISIBLE);
        }
        else {
            holder.ivVerify.setVisibility(View.GONE);
        }
        String name = user.name;
//        if (name.equals("")) {
//            name = user.screen_name;
//        }
        holder.tvName.setText(name);
        holder.tvCreateTime.setText(user.created_at);
        String source = sc.getSource();
        int len = source.length();
        try {
            holder.tvSource.setText(source.substring(len - 11,len -5));
        }catch (Exception e) {

        }

        PicUrls[] picUrls;
        StatusContent reStatus = sc.getRetweeted_status();
        if (reStatus != null) {
            holder.line.setVisibility(View.VISIBLE);
            holder.tvUserDo.setText(sc.getText());
            holder.tvContent.setText(reStatus.getText());
            picUrls = reStatus.getPic_urls();
        } else {
            holder.tvUserDo.setVisibility(View.GONE);
            holder.tvContent.setText(sc.getText());
            picUrls = sc.getPic_urls();
        }
        //微博配图
        int size = picUrls.length;
        if (size == 0) {
            return;
        }
        int width = (int) ScreenUtils.getScreenWidth(context);// 屏幕宽度
        ImageView image;
        if (size == 1) {
            holder.container.removeAllViews();
            image = new ImageView(context);
            image.setOnClickListener(new ImageOnClickListener(0));
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            holder.container.addView(image, params);
            ImageUtils.getInstance().displayImage(requestQueue,picUrls[0].
                            getThumbnail_pic().replace("thumbnail", "large"), null,image);
        } else if (size == 2) {
            holder.container.removeAllViews();
            LinearLayout horizonLayout = new LinearLayout(context);
            LinearLayout.LayoutParams params;
            float density = ScreenUtils.getDensity(context);
            float imageLayWidth = width - (10 + 10) * density;
            for (int i = 0; i < size; i++) {
                params = new LinearLayout.LayoutParams(
                        (int) (imageLayWidth / 2), (int) (imageLayWidth / 2));
                image = new ImageView(context);
                image.setOnClickListener(new ImageOnClickListener(i));
                image.setScaleType(ImageView.ScaleType.FIT_XY);
                image.setPadding(0, (int) (4 * density), (int) (4 * density),
                        (int) (4 * density));
                horizonLayout.addView(image, params);
                ImageUtils.getInstance().displayImage(requestQueue,picUrls[i].getThumbnail_pic().replace("thumbnail", "bmiddle"),
                        null,image);
            }
            holder.container.addView(horizonLayout);
        } else if (size > 2 && size <= 9) {
            holder.container.removeAllViews();

            LinearLayout.LayoutParams params;
            float density = ScreenUtils.getDensity(context);
            float imageLayWidth = width - (10 + 10) * density;

            int count = size;
            int yuShu = count % 3;
            if (yuShu == 0) {
                int hangNum = (int) (size / 3);
                for (int i = 0; i < hangNum; i++) {
                    LinearLayout horizonLayout = new LinearLayout(context);
                    for (int j = 0; j < 3; j++) {
                        params = new LinearLayout.LayoutParams(
                                (int) (imageLayWidth / 3),
                                (int) (imageLayWidth / 3));
                        image = new ImageView(context);
                        image.setOnClickListener(new ImageOnClickListener(i * 3
                                + j));
                        image.setScaleType(ImageView.ScaleType.FIT_XY);
                        image.setPadding(0, (int) (2 * density),
                                (int) (2 * density), (int) (2 * density));
                        horizonLayout.addView(image, params);
                        ImageUtils.getInstance().displayImage(requestQueue,
                                picUrls[i * 3 + j].getThumbnail_pic().replace("thumbnail", "bmiddle"), null,image);
                    }
                    holder.container.addView(horizonLayout);
                }
            } else {
                int hangNum = (int) (size / 3) + 1;
                for (int i = 0; i <= hangNum - 1; i++) {
                    LinearLayout horizonLayout = new LinearLayout(context);
                    if (i < hangNum - 1) {
                        for (int j = 0; j < 3; j++) {
                            params = new LinearLayout.LayoutParams(
                                    (int) (imageLayWidth / 3),
                                    (int) (imageLayWidth / 3));
                            image = new ImageView(context);
                            image.setOnClickListener(new ImageOnClickListener(i * 3
                                    + j));
                            image.setPadding(0, (int) (2 * density),
                                    (int) (2 * density), (int) (2 * density));
                            image.setScaleType(ImageView.ScaleType.FIT_XY);
                            horizonLayout.addView(image, params);
                            ImageUtils.getInstance().displayImage(requestQueue,
                                    picUrls[i * 3 + j].getThumbnail_pic().replace("thumbnail", "bmiddle"), null,image);
                        }
                        holder.container.addView(horizonLayout);
                    } else if (i == hangNum - 1) {
                        for (int j = 0; j < yuShu; j++) {
                            params = new LinearLayout.LayoutParams(
                                    (int) (imageLayWidth / 3),
                                    (int) (imageLayWidth / 3));
                            image = new ImageView(context);
                            image.setOnClickListener(new ImageOnClickListener(i * 3
                                    + j));
                            image.setScaleType(ImageView.ScaleType.FIT_XY);
                            horizonLayout.addView(image, params);
                            ImageUtils.getInstance().displayImage(requestQueue,
                                    picUrls[i * 3 + j].getThumbnail_pic().replace("thumbnail", "bmiddle"), null,image);
                        }
                        holder.container.addView(horizonLayout);
                    }
                }
            }
        }
        
    }

    @Override
    public int getItemCount() {
        return content.getStatuses().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView iv;
        TextView tvName;
        CircleImageView ivVerify;
        TextView tvCreateTime;
        TextView tvSource;
        ImageView ivMore;
        TextView tvUserDo;
        View line;
        TextView tvContent;
        LinearLayout container;
        ImageView ivLike;
        ImageView ivSend;
        ImageView ivComment;
        public ViewHolder(View itemView) {
            super(itemView);
            iv = (CircleImageView) itemView.findViewById(R.id.iv);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ivVerify = (CircleImageView) itemView.findViewById(R.id.iv_level);
            tvCreateTime = (TextView) itemView.findViewById(R.id.tv_create_time);
            tvSource = (TextView) itemView.findViewById(R.id.tv_source);
            ivMore = (ImageView) itemView.findViewById(R.id.iv_more);
            tvUserDo = (TextView) itemView.findViewById(R.id.tv_user_do);
            line = itemView.findViewById(R.id.view);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            container = (LinearLayout) itemView.findViewById(R.id.image_container);
            ivLike = (ImageView) itemView.findViewById(R.id.iv_like);
            ivSend = (ImageView) itemView.findViewById(R.id.iv_send);
            ivComment = (ImageView) itemView.findViewById(R.id.iv_comment);
        }
    }

    private class ImageOnClickListener implements View.OnClickListener {

        private int index;

        public ImageOnClickListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(context, "第" + index + "张", Toast.LENGTH_LONG)
                    .show();
        }

    }
}
