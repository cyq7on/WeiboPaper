package com.xihua.weibopaper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
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
import com.xihua.weibopaper.utils.ScreenUtils;
import com.xihua.weibopaper.utils.ToastUtil;
import com.xihua.weibopaper.view.CircleImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private RecyclerView recyclerView;

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
        ViewHolder viewHolder = new ViewHolder(view);
        if (recyclerView == null) {
            recyclerView = (RecyclerView) parent;
        }
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
        String count1 = "";
        String count2 = "";
        String count3 = "";
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
        holder.ivSend.setTag(sc.getIdstr());
        holder.ivComment.setTag(sc.getIdstr());

        //微博配图
        int size = picUrls.size();
        if (size == 0) {
            return;
        }
        int width = ScreenUtils.getScreenWidth(context);// 屏幕宽度
        ImageView image;
        String url;
        if (size == 1) {
            holder.container.removeAllViews();
            image = new ImageView(context);
            image.setMaxHeight(1000);
            image.setMaxWidth(1000);
            image.setMinimumHeight(300);
            image.setMinimumWidth(300);
            image.setImageResource(R.mipmap.ic_launcher);
            url = picUrls.get(0).getThumbnail_pic();
            image.setTag(url);
            ImageView view = (ImageView) recyclerView.findViewWithTag(url);
//            Log.i("vvvvv00000", Boolean.toString(view == null));
//            Log.i("vvvvv11111", (String) image.getTag());
            image.setOnClickListener(new ImageOnClickListener(0));
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            holder.container.addView(image, params);
//            holder.container.setTag(url);
//            LinearLayout ll = (LinearLayout) recyclerView.findViewWithTag(url);
//            Log.i("vvvvv111", Boolean.toString(ll == null));
            ImageUtils.getInstance().displayImage(requestQueue,url,null,image);
        } else if (size == 2) {
            holder.container.removeAllViews();
            LinearLayout horizonLayout = new LinearLayout(context);
            LinearLayout.LayoutParams params;
            float density = ScreenUtils.getDensity(context);
            float imageLayWidth = (width - (10 + 10) * density) * 2 / 3;
            for (int i = 0; i < size; i++) {
                params = new LinearLayout.LayoutParams(
                        (int) (imageLayWidth / 2), (int) (imageLayWidth / 2));
                image = new ImageView(context);
                image.setOnClickListener(new ImageOnClickListener(i));
                image.setScaleType(ImageView.ScaleType.FIT_XY);
                image.setPadding(0, (int) (4 * density), (int) (4 * density),
                        (int) (4 * density));
                image.setImageResource(R.mipmap.ic_launcher);
                url = picUrls.get(i).getThumbnail_pic();
                image.setTag(url);
                horizonLayout.addView(image, params);
                ImageUtils.getInstance().displayImage(requestQueue,url, null,image);
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
                int hangNum = size / 3;
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
                        image.setImageResource(R.mipmap.ic_launcher);
                        url = picUrls.get(i * 3 + j).getThumbnail_pic();
                        image.setTag(url);
                        horizonLayout.addView(image, params);
                        ImageUtils.getInstance().displayImage(requestQueue,url,null,image);
                    }
                    holder.container.addView(horizonLayout);
                }
            } else {
                int hangNum = size / 3 + 1;
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
                            image.setImageResource(R.mipmap.ic_launcher);
                            url = picUrls.get(i * 3 + j).getThumbnail_pic();
                            image.setTag(url);
                            horizonLayout.addView(image, params);
                            ImageUtils.getInstance().displayImage(requestQueue,url,null,image);
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
                            image.setImageResource(R.mipmap.ic_launcher);
                            url = picUrls.get(i * 3 + j).getThumbnail_pic();
                            image.setTag(url);
                            horizonLayout.addView(image, params);
                            ImageUtils.getInstance().displayImage(requestQueue, url, null,image);
                        }
                        holder.container.addView(horizonLayout);
                    }
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
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
        TextView tvLikeNum;
        TextView tvSendNum;
        TextView tvCommentNum;
        private static View.OnClickListener likeListener;
        private static View.OnClickListener sendListener;
        private static View.OnClickListener commentListener;

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
                    RequestQueue requestQueue = Volley.newRequestQueue(MyApplication.getInstance());
                    Map<String, String> params = new HashMap<>();
                    Oauth2AccessToken accessToken = AccessTokenKeeper.
                            readAccessToken(MyApplication.getInstance());
                    GsonRequest<StatusContent> sendRequest = new GsonRequest<StatusContent>(params,
                            Constants.STATUSES_REPOST, StatusContent.class,
                            new Response.Listener<StatusContent>() {
                                @Override
                                public void onResponse(StatusContent response) {
                                    ToastUtil.showShort(MyApplication.getInstance(), "转发成功");
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ToastUtil.showShort(MyApplication.getInstance(), error.getMessage());
                        }
                    });
                    @Override
                    public void onClick(View v) {
                        params.put("source", Constants.APP_KEY);
                        params.put("access_token", accessToken.getToken());
                        params.put("id", (String) v.getTag());
                        requestQueue.add(sendRequest);
                    }
                };
            }
            if (commentListener == null) {
                commentListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtil.showShort(MyApplication.getInstance(),(String)v.getTag());
                    }
                };
            }
            ivLike.setOnClickListener(likeListener);
            ivSend.setOnClickListener(sendListener);
            ivComment.setOnClickListener(commentListener);
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
