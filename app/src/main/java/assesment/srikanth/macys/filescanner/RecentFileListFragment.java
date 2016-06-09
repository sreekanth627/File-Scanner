package assesment.srikanth.macys.filescanner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by srikanthsanagapalli on 6/8/16.
 * This fragment shows top 5 recent modified files in the SD Card.
 */
public class RecentFileListFragment extends Fragment {

    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    private static List<FileInfo> recentFileList;

    public RecentFileListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.file_list_view, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recentFileList = getArguments().getParcelableArrayList(Constants.RECENT_FILES_KEY);
        recyclerView = (RecyclerView) getView().findViewById(R.id.myList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        adapter = new RecyclerViewAdapter(recentFileList);
        recyclerView.setAdapter(adapter);
    }
}