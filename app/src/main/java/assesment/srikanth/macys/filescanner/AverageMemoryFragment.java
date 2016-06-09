package assesment.srikanth.macys.filescanner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by srikanthsanagapalli on 6/8/16.
 * This fragment shows details of Number of scanned files,
 * total memory occupied and average memory for each file.
 */
public class AverageMemoryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_average_memory, container, false);

        int totalFileCount = getArguments().getInt(Constants.TOTAL_FILES_KEY);
        long totalMemory = getArguments().getLong(Constants.TOTAL_MEMORY_KEY);

        TextView fileCountTxt = (TextView)view.findViewById(R.id.totalFileCount);
        TextView totalMemoryTxt = (TextView)view.findViewById(R.id.totalMemorySize);
        TextView averageMemoryTxt = (TextView)view.findViewById(R.id.averageMemory);

        fileCountTxt.setText(Constants.NUMBER_OF_FILES_STRING + totalFileCount);
        totalMemoryTxt.setText(Constants.TOTAL_MEMORY_OCCUPIED + (totalMemory * 1.0)/(1024 * 1024) + " GB");
        averageMemoryTxt.setText(Constants.AVERAGE_MEMORY_OCCUPIED + ((totalMemory * 1.0)/totalFileCount)/1024 + " MB");

        return view;

    }
}
