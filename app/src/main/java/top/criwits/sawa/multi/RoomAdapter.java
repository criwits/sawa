package top.criwits.sawa.multi;

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

public class RoomAdapter extends ArrayAdapter<RoomEntry> {
    public RoomAdapter(@NonNull Context context, int resource, @NonNull List<RoomEntry> objects) {
        super(context, resource, objects);
    }
    @NonNull @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        RoomEntry entry = getItem(position);
        @SuppressLint("ViewHolder")
        View view = LayoutInflater.from(getContext()).inflate(R.layout.room_item, parent, false);

        TextView roomID = view.findViewById(R.id.roomID);
        TextView difficultyID = view.findViewById(R.id.difficultyID);

        roomID.setText(String.valueOf(entry.getRoomID()));
        difficultyID.setText(RoomEntry.diffToString(entry.getDifficulty()));

        return view;
    }
}
