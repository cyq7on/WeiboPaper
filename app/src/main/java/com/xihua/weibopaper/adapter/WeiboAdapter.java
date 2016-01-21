package com.xihua.weibopaper.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.xihua.weibopaper.activity.PublishActivity;
import com.xihua.weibopaper.activity.R;
import com.xihua.weibopaper.bean.PicUrls;
import com.xihua.weibopaper.bean.StatusContent;
import com.xihua.weibopaper.bean.WeiBoUser;
import com.xihua.weibopaper.common.Constants;
import com.xihua.weibopaper.common.MyApplication;
import com.xihua.weibopaper.utils.AccessTokenKeeper;
import com.xihua.weibopaper.utils.DateUtils;
import com.xihua.weibopaper.utils.GsonRequest;
import com.xihua.weibopaper.utils.ImageUtils;
import com.xihua.weibopaper.utils.ToastUtil;
import com.xihua.weibopaper.view.CircleImageView;
import com.xihua.weibopaper.view.FullyGridLayoutManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hadcn.keyboard.ChatTextView;

/**
 * @author cyq7on
 * @version V1.0
 * @Package com.xihua.weibopaper.adapter
 * @Description:微博内容的适配器
 * @date 2015/12/2517:12
 */
public class WeiboAdapter extends RecyclerView.Adapter<WeiboAdapter.ViewHolder> {
    Context context;
    private List<StatusContent> list;
    private OnItemClickListener onItemClickListener;
    private View.OnClickListener onClickListener;
    private RequestQueue requestQueue;
    private XRecyclerView recyclerView;

    public WeiboAdapter(Context context, List<StatusContent> list, RequestQueue requestQueue) {
        this.context = context;
        this.list = list;
        this.requestQueue = requestQueue;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_weibo_content, null);
        ViewHolder viewHolder = new ViewHolder(view,context);
        if (recyclerView == null) {
            recyclerView = (XRecyclerView) parent;
        }
//        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
//        if (content.getStatuses().size() == 0) {
//            return;
//        }
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
        StatusContent sc = list.get(position);
        WeiBoUser user = sc.getUser();
//        if (onClickListener == null) {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showShort(context, Integer.toString(position));
            }
        };
        holder.ivMore.setOnClickListener(onClickListener);
//        }
        ImageUtils.getInstance().displayImage(requestQueue, user.getAvatar_large(), null,
                holder.iv);

//        String url = user.getAvatar_large();
//        holder.iv.setTag(url);
//        CircleImageView imageView = (CircleImageView) recyclerView.findViewWithTag(url);
//        Log.i("test",Boolean.toString(imageView == null));

        // 黄V
        if (user.getVerified_type() == 0) {
            holder.ivVerify.setImageResource(R.mipmap.avatar_vip);
        }
        // 200:初级达人 220:高级达人
        else if (user.getVerified_type() == 200 || user.getVerified_type() == 220) {
            holder.ivVerify.setImageResource(R.mipmap.avatar_grassroot);
        }
        // 蓝V
        else if (user.getVerified_type() > 0) {
            holder.ivVerify.setImageResource(R.mipmap.avatar_enterprise_vip);
        }
        if (user.getVerified_type() >= 0) {
            holder.ivVerify.setVisibility(View.VISIBLE);
        } else {
            holder.ivVerify.setVisibility(View.GONE);
        }
        String name = user.getRemark();
        if (name.equals("")) {
            name = user.getScreen_name();
        }
        holder.tvName.setText(name);

        holder.tvCreateTime.setText(DateUtils.formatDate(sc.getCreated_at()));
        String source = sc.getSource();
//        SpannableString msp = new SpannableString(source);
//        msp.setSpan(new ForegroundColorSpan(Color.BLUE),
//                0, source.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //设置前景色
        CharSequence charSequence = Html.fromHtml(source);
        holder.tvSource.setText(charSequence);
//        holder.tvSource.setMovementMethod(LinkMovementMethod.getInstance());//点击的时候产生超链接
        List<PicUrls> picUrls;
        StatusContent reStatus = sc.getRetweeted_status();
        String count1;
        String count2;
        String count3;
        if (reStatus == null) {
            holder.tvUserDo.setVisibility(View.GONE);
            holder.line.setVisibility(View.GONE);
            holder.tvContent.setText(sc.getText());
            picUrls = sc.getPic_urls();
            count1 = sc.getAttitudes_count();
            count2 = sc.getReposts_count();
            count3 = sc.getComments_count();
        } else {
            holder.line.setVisibility(View.VISIBLE);
            holder.tvUserDo.setVisibility(View.VISIBLE);
            holder.tvUserDo.setText(sc.getText());
            holder.tvContent.setText(reStatus.getText());
            picUrls = reStatus.getPic_urls();
            count1 = reStatus.getAttitudes_count();
            count2 = reStatus.getReposts_count();
            count3 = reStatus.getComments_count();
        }

        if (count1.equals("0")) {
            holder.tvLikeNum.setVisibility(View.INVISIBLE);
        }else {
            holder.tvLikeNum.setVisibility(View.VISIBLE);
            holder.tvLikeNum.setText(count1);
        }

        if (count2.equals("0")) {
            holder.tvSendNum.setVisibility(View.INVISIBLE);
        }else {
            holder.tvSendNum.setVisibility(View.VISIBLE);
            holder.tvSendNum.setText(count2);
        }

        if (count3.equals("0")) {
            holder.tvCommentNum.setVisibility(View.INVISIBLE);
        }else {
            holder.tvCommentNum.setVisibility(View.VISIBLE);
            holder.tvCommentNum.setText(count3);
        }

        holder.ivLike.setTag(position);
        holder.ivSend.setTag(sc);
        holder.ivComment.setTag(sc);

        //微博配图
        int size = picUrls.size();
        if (size == 0) {
            return;
        }
        holder.container.setHasFixedSize(true);
        holder.container.setLayoutManager(new FullyGridLayoutManager(context, 3));
        holder.container.setAdapter(new ImageAdapter(picUrls, context, requestQueue));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView iv;
        TextView tvName;
        CircleImageView ivVerify;
        TextView tvCreateTime;
        TextView tvSource;
        ImageView ivMore;
        ChatTextView tvUserDo;
        View line;
        ChatTextView tvContent;
        RecyclerView container;
        ImageView ivLike;
        ImageView ivSend;
        ImageView ivComment;
        TextView tvLikeNum;
        TextView tvSendNum;
        TextView tvCommentNum;
        private static View.OnClickListener likeListener;
        private static View.OnClickListener sendListener;
        private static View.OnClickListener commentListener;

        public ViewHolder(View itemView,final Context context) {
            super(itemView);
            iv = (CircleImageView) itemView.findViewById(R.id.iv);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ivVerify = (CircleImageView) itemView.findViewById(R.id.iv_level);
            tvCreateTime = (TextView) itemView.findViewById(R.id.tv_create_time);
            tvSource = (TextView) itemView.findViewById(R.id.tv_source);
            ivMore = (ImageView) itemView.findViewById(R.id.iv_more);
            tvUserDo = (ChatTextView) itemView.findViewById(R.id.tv_user_do);
            line = itemView.findViewById(R.id.view);
            tvContent = (ChatTextView) itemView.findViewById(R.id.tv_content);
            container = (RecyclerView) itemView.findViewById(R.id.image_container);
            ivLike = (ImageView) itemView.findViewById(R.id.iv_like);
            ivSend = (ImageView) itemView.findViewById(R.id.iv_send);
            ivComment = (ImageView) itemView.findViewById(R.id.iv_comment);
            tvLikeNum = (TextView) itemView.findViewById(R.id.tv_like_num);
            tvSendNum = (TextView) itemView.findViewById(R.id.tv_send_num);
            tvCommentNum = (TextView) itemView.findViewById(R.id.tv_comment_num);

            //添加监听
            if (likeListener == null) {
                likeListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtil.showShort(MyApplication.getInstance(),(int)v.getTag() + "");
                    }
                };
            }
            if (sendListener == null) {
                sendListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PublishActivity.class);
                        intent.putExtra("info",0);
                        intent.putExtra("StatusContent", (StatusContent) v.getTag());
                        context.startActivity(intent);
                    }
                };
            }
            if (commentListener == null) {
                commentListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PublishActivity.class);
                        intent.putExtra("info",1);
                        intent.putExtra("StatusContent",(StatusContent)v.getTag());
                        context.startActivity(intent);
                    }
                };
            }
            ivLike.setOnClickListener(likeListener);
            ivSend.setOnClickListener(sendListener);
            ivComment.setOnClickListener(commentListener);
        }
    }


}

class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private List<PicUrls> urlList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private RequestQueue requestQueue;
    private DisplayImageOptions options;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public ImageAdapter(List<PicUrls> mData, Context mContext,RequestQueue requestQueue) {
        this.urlList = mData;
        this.context = mContext;
        this.requestQueue = requestQueue;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisk(true)
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
        ImageLoader.getInstance().displayImage(urlList.get(position).getThumbnail_pic(),
                holder.image, options);

    }

    @Override
    public int getItemCount() {

        return urlList == null ? 0 : urlList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }


}

