package com.xihua.weibopaper.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.apkfuns.logutils.LogUtils;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.xihua.weibopaper.activity.CmtOrRelayActivity;
import com.xihua.weibopaper.activity.ImageDetailActivity;
import com.xihua.weibopaper.activity.R;
import com.xihua.weibopaper.bean.PicUrls;
import com.xihua.weibopaper.bean.StatusContent;
import com.xihua.weibopaper.bean.WeiBoUser;
import com.xihua.weibopaper.common.MyApplication;
import com.xihua.weibopaper.utils.DateUtils;
import com.xihua.weibopaper.utils.HighLightUtils;
import com.xihua.weibopaper.utils.ImageUtils;
import com.xihua.weibopaper.utils.ToastUtil;
import com.xihua.weibopaper.view.CircleImageView;
import com.xihua.weibopaper.view.FullyGridLayoutManager;
import com.xihua.weibopaper.view.NoUnderLineClickspan;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import cn.hadcn.keyboard.ChatTextView;

/**
 * @author cyq7on
 * @version V1.0
 * @Package com.xihua.weibopaper.adapter
 * @Description:微博内容的适配器
 * @date 2015/12/2517:12
 */
public class WeiboAdapter extends RecyclerView.Adapter<WeiboAdapter.ViewHolder> {
    private Context context;
    private List<StatusContent> list;
    private OnItemClickListener onItemClickListener;
    private View.OnClickListener onClickListener;
    private RequestQueue requestQueue;
    private XRecyclerView recyclerView;
    private ImageAdapter.OnItemClickListener listener;

    public WeiboAdapter(Context context, List<StatusContent> list, RequestQueue requestQueue) {
        this.context = context;
        this.list = list;
        this.requestQueue = requestQueue;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickLitener(OnItemClickListener mOnItemClickLitener)
    {
        this.onItemClickListener = mOnItemClickLitener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_weibo_content, parent,false);
        ViewHolder viewHolder = new ViewHolder(view,context);
        if (recyclerView == null) {
            recyclerView = (XRecyclerView) parent;
        }
        viewHolder.setIsRecyclable(false);
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
        CharSequence charSequence = Html.fromHtml(source);
        SpannableString ss = new SpannableString(charSequence);
//        ss.setSpan(new ForegroundColorSpan(Color.GRAY),
//                0, charSequence.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //设置前景色
        ss.setSpan(new NoUnderLineClickspan(source),
                0, charSequence.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //去掉下划线
        holder.tvSource.setText(ss);
//        holder.tvSource.setMovementMethod(LinkMovementMethod.getInstance());//点击的时候产生超链接
        final List<PicUrls> picUrls;
        StatusContent reStatus = sc.getRetweeted_status();
        String count1;
        String count2;
        String count3;
        if (reStatus == null) {
            holder.tvUserDo.setVisibility(View.GONE);
            holder.line.setVisibility(View.GONE);
            holder.tvContent.setText(HighLightUtils.highLight(sc.getText(),
                    HighLightUtils.TOPIC,HighLightUtils.USER_NAME,HighLightUtils.URL));
            picUrls = sc.getPic_urls();
            count1 = sc.getAttitudes_count();
            count2 = sc.getReposts_count();
            count3 = sc.getComments_count();
        } else {
            holder.line.setVisibility(View.VISIBLE);
            holder.tvUserDo.setVisibility(View.VISIBLE);
            holder.tvUserDo.setText(HighLightUtils.highLight(sc.getText(),
                    HighLightUtils.TOPIC, HighLightUtils.USER_NAME, HighLightUtils.URL));
            holder.tvContent.setText(HighLightUtils.highLight(reStatus.getText(),
                    HighLightUtils.TOPIC,HighLightUtils.USER_NAME,HighLightUtils.URL));
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
        ImageAdapter imageAdapter = new ImageAdapter(picUrls, context, requestQueue);
//        if (listener == null) {
            listener = new ImageAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int index) {
                    Intent intent = new Intent(context, ImageDetailActivity.class);
                    ArrayList<String> urlList = new ArrayList<>();
                    String url;
                    for (int i = 0;i < picUrls.size();i++) {
                        //换成中等质量图片的url
                        url = picUrls.get(i).getThumbnail_pic().replace("thumbnail","bmiddle");
                        urlList.add(url);
                    }
                    intent.putStringArrayListExtra("urlList",urlList);
                    intent.putExtra("index",index);
                    LogUtils.i(position);
                    LogUtils.i(urlList);
                    context.startActivity(intent);
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            };
//        }
        imageAdapter.setOnItemClickLitener(listener);
        holder.container.setAdapter(imageAdapter);
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
                        Intent intent = new Intent(context, CmtOrRelayActivity.class);
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
                        Intent intent = new Intent(context, CmtOrRelayActivity.class);
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



