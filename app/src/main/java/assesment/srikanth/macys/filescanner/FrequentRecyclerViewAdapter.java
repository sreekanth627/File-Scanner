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

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class FrequentRecyclerViewAdapter extends RecyclerView.Adapter<FrequentRecyclerViewAdapter.ListItemViewHolder> {

    private String[] keys = new String[10];
    private Integer[] values = new Integer[10];


    FrequentRecyclerViewAdapter(HashMap<String, Integer> modelData) {
        if (modelData == null) {
            throw new IllegalArgumentException("modelData must not be null");
        }

        Comparator<String> comparator = new ValueComparator(modelData);
        //TreeMap is a map sorted by its keys.
        //The comparator is used to sort the TreeMap by keys.
        TreeMap<String, Integer> extensionsMap = new TreeMap<String, Integer>(comparator);
        extensionsMap.putAll(modelData);
        convertMapToArray(extensionsMap);

    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.frequent_list_item, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder viewHolder, int position) {
        viewHolder.fileExtension.setText(String.valueOf(keys[position]));
        viewHolder.fileFrequency.setText(String.valueOf(values[position]));
    }

    @Override
    public int getItemCount() {
        return keys.length;
    }

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        TextView fileExtension;
        TextView fileFrequency;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            fileExtension = (TextView) itemView.findViewById(R.id.fileExtension);
            fileFrequency = (TextView) itemView.findViewById(R.id.fileFrequency);
        }
    }
   private void convertMapToArray(Map<String, Integer> map) {
       int index = 0;
       for (Map.Entry<String, Integer> mapEntry : map.entrySet()) {
           keys[index] = mapEntry.getKey();
           values[index] = mapEntry.getValue();
           index++;
           if(index == 10) {
               break;
           }
       }
   }

    class ValueComparator implements Comparator<String> {

        HashMap<String, Integer> map = new HashMap<String, Integer>();

        public ValueComparator(HashMap<String, Integer> map){
            this.map.putAll(map);
        }

        @Override
        public int compare(String s1, String s2) {
            if(map.get(s1) >= map.get(s2)){
                return -1;
            }else{
                return 1;
            }
        }
    }
}
