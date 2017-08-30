package com.y34h1a.reactivelazyloading.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.y34h1a.reactivelazyloading.MainActivity;
import com.y34h1a.reactivelazyloading.R;
import com.y34h1a.reactivelazyloading.adapter.UserAdapter;
import com.y34h1a.reactivelazyloading.model.User;
import com.y34h1a.reactivelazyloading.util.RxBus;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.PublishProcessor;

public class FragmentLazyLoad extends Fragment {
    @BindView(R.id.rvUserInfo)
    RecyclerView rvUserList;
    private UserAdapter adapter;

    private RxBus bus;
    private CompositeDisposable disposables;
    private PublishProcessor<Integer> lazyLoader;
    private boolean requestUnderWay = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View layout = inflater.inflate(R.layout.fragment_main, container, false);
            ButterKnife.bind(this, layout);
            return layout;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bus = ((MainActivity) getActivity()).getRxBusSingleton();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvUserList.setLayoutManager(layoutManager);

        adapter = new UserAdapter(bus);
        rvUserList.setAdapter(adapter);

        lazyLoader = PublishProcessor.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        
        disposables = new CompositeDisposable();

        Disposable dLazyLoader = lazyLoader
                .onBackpressureDrop()
                .doOnNext(i -> requestUnderWay = true)
                .concatMap(this::getData)
                .observeOn(AndroidSchedulers.mainThread())
                .map(items -> {
                    adapter.addItems(items);
                    adapter.notifyDataSetChanged();

                    return items;
                })
                .doOnNext(dummy -> {
                    requestUnderWay = false;
                    Toast.makeText(getActivity(), "Data Loaded", Toast.LENGTH_SHORT).show();
                })
                .subscribe();


        Disposable dBus = bus.asFlowable()
                .filter(o -> ! requestUnderWay)
                .subscribe(event -> {
                    if (event instanceof UserAdapter.PageEvent) {
                        int nextPage = adapter.getItemCount();
                        lazyLoader.onNext(nextPage);
                    }
                });


        disposables.add(dLazyLoader);
        disposables.add(dBus);

        lazyLoader.onNext(0);
    }


    @Override
    public void onStop() {
        super.onStop();
        disposables.clear();
    }

    private Flowable<List<User>> getData(int pageStart) {

        return Flowable.just(true)
            .observeOn(AndroidSchedulers.mainThread())
                .map(dummy -> {
                    List<User> users = new ArrayList<>();
                    for (int i = 0; i < 6; i++) {
                        User user = new User();
                        user.setName("User " + (i + pageStart));
                        user.setEmail("yeahia.arif@gmail.com");
                        users.add(user);
                    }
                    return users;
            });
    }

}
