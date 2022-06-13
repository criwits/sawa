package top.criwits.sawa.ranking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import top.criwits.sawa.R;

public class RankingAdapter extends ArrayAdapter<RankingEntry> {
    public RankingAdapter(@NonNull Context context, int resource, @NonNull List<RankingEntry> objects) {
        super(context, resource, objects);
    }

    @NonNull @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        RankingEntry entry = getItem(position);
        @SuppressLint("ViewHolder")
        View view = LayoutInflater.from(getContext()).inflate(R.layout.ranking_item, parent, false);

        TextView username = view.findViewById(R.id.usernameView);
        TextView enrollDate = view.findViewById(R.id.enrollDateView);
        TextView score = view.findViewById(R.id.multiRankingScore);
        username.setText(entry.playerName);
        enrollDate.setText(RankingEntry.parseDate(entry.enrollTime));
        score.setText(String.valueOf(entry.score));

        return view;
    }
}