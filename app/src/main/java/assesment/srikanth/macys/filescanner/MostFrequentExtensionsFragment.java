package assesment.srikanth.macys.filescanner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by srikanthsanagapalli on 6/8/16.
 * This fragment shows top 10 large files in the SD Card in descending order.
 */
public class MostFrequentExtensionsFragment extends Fragment {

    FrequentRecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    private static HashMap<String, Integer> fileMap;

    public MostFrequentExtensionsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.file_list_view, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fileMap = (HashMap<String, Integer>)getArguments().getSerializable(Constants.FREQUENT_EXTENSIONS_KEY);
        recyclerView = (RecyclerView) getView().findViewById(R.id.myList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        adapter = new FrequentRecyclerViewAdapter(fileMap);
        recyclerView.setAdapter(adapter);
    }
}

