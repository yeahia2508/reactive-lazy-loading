package com.y34h1a.reactivelazyloading;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.y34h1a.reactivelazyloading.fragment.FragmentLazyLoad;
import com.y34h1a.reactivelazyloading.util.RxBus;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private RxBus rxBus = null;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toolbar.setTitle(R.string.toolbar_title);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_fragment, new FragmentLazyLoad(), this.toString())
                    .commit();
        }
    }

    public RxBus getRxBusSingleton() {
        if (rxBus == null) {
            rxBus = new RxBus();
        }

        return rxBus;
    }
}
