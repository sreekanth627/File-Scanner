package assesment.srikanth.macys.filescanner;

/**
 * Created by srikanthsanagapalli on 6/8/16.
 * This adapter is used to render data in listview.
 */

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ListItemViewHolder> {

    private List<FileInfo> items;
    private SparseBooleanArray selectedItems;

    public static final String DATE_FORMATTER_24_HOURS = "dd/MM/yyyy:HH:mm:ss";

    RecyclerViewAdapter(List<FileInfo> modelData) {
        if (modelData == null) {
            throw new IllegalArgumentException("modelData must not be null");
        }
        items = modelData;
        selectedItems = new SparseBooleanArray();
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.file_list_item, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder viewHolder, int position) {
        FileInfo info = items.get(position);
        viewHolder.fileName.setText(String.valueOf(info.getFileName()));
        viewHolder.fileSize.setText("size: " + String.valueOf(info.getFileSize()) + " KB");
        viewHolder.fileLocation.setText(String.valueOf(info.getStorageLocation()));
        viewHolder.lastModified.setText(new SimpleDateFormat(DATE_FORMATTER_24_HOURS).format(info.getLastOpened()));
        viewHolder.itemView.setActivated(selectedItems.get(position, false));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        TextView fileSize;
        TextView fileLocation;
        TextView lastModified;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            fileName = (TextView) itemView.findViewById(R.id.fileName);
            fileSize = (TextView) itemView.findViewById(R.id.fileSize);
            fileLocation = (TextView) itemView.findViewById(R.id.fileLocation);
            lastModified = (TextView) itemView.findViewById(R.id.fileModifiedDate);

        }
    }
}
