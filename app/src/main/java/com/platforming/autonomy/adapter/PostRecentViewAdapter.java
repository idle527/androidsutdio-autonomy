package com.platforming.autonomy.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.platforming.autonomy.clazz.BulletinBoard;
import com.platforming.autonomy.interfaze.ListenerInterface;
import com.android.autonomy.R;

import java.util.ArrayList;

public class PostRecentViewAdapter extends RecyclerView.Adapter<PostRecentViewAdapter.ViewHolder> {

    private BulletinBoard mData;

    ListenerInterface listenerInterface;

    public void setListenerInterface(ListenerInterface listenerInterface) {
        this.listenerInterface = listenerInterface;
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView type;
        TextView title;
        TextView date;

        ViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조. (hold strong reference)
            type = itemView.findViewById(R.id.tv_recyclerview_post_recent_type);
            title = itemView.findViewById(R.id.tv_recyclerview_post_recent_title);
            date = itemView.findViewById(R.id.tv_recyclerview_post_recent_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        listenerInterface.onSuccess(pos);
                    }
                }
            });
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public PostRecentViewAdapter(BulletinBoard data) {
        mData = data;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public PostRecentViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        View view = inflater.inflate(R.layout.item_recyclerview_post_recent, parent, false) ;
        PostRecentViewAdapter.ViewHolder vh = new PostRecentViewAdapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(PostRecentViewAdapter.ViewHolder holder, int position) {
        BulletinBoard.Post post = mData.getPosts().get(position);
        holder.type.setText(post.getBulletinId());
        holder.title.setText(post.getTitle());
        holder.date.setText(formatTimeString(post.getDate()));
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.getPosts().size() ;
    }

    public static String formatTimeString(long regTime) {
        long curTime = System.currentTimeMillis();
        long diffTime = (curTime - regTime) / 1000;
        String msg = null;
        if (diffTime < 60) {
            msg = "방금 전";
        } else if ((diffTime /= 60) < 60) {
            msg = diffTime + "분 전";
        } else if ((diffTime /= 60) < 24) {
            msg = (diffTime) + "시간 전";
        } else if ((diffTime /= 24) < 30) {
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= 30) < 12) {
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime) + "년 전";
        }
        return msg;
    }
}
