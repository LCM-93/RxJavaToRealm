package com.lcm.rxjavatorealm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lcm.rxjavatorealm.RxRealm.RealmObservable;
import com.lcm.rxjavatorealm.entity.Student;

import java.util.List;
import java.util.Random;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnAdd, btnDel, btnFind;
    private TextView tv;

    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAdd = findViewById(R.id.btn_add);
        btnDel = findViewById(R.id.btn_del);
        btnFind = findViewById(R.id.btn_find);
        tv = findViewById(R.id.tv);

        btnFind.setOnClickListener(this);
        btnDel.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                add();
                break;

            case R.id.btn_del:
                del();
                break;

            case R.id.btn_find:
                find();
                break;
        }
    }

    private void add() {
        Random random = new Random();
        int i = random.nextInt(10000);
        final Student student = new Student();
        student.setId(i);
        student.setNanme("name " + i);
        student.setAge(i);
        RealmObservable
                .createObservable(new Function<Realm, Student>() {
                    @Override
                    public Student apply(Realm realm) throws Exception {
                        return realm.copyToRealm(student);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Student>() {
                    @Override
                    public void accept(Student s) throws Exception {
                        Log.d(TAG, "添加成功");
                        Toast.makeText(MainActivity.this, "添加成功！", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.getMessage());
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                });
    }


    private void del() {
        RealmObservable
                .createObservable(new Function<Realm, Object>() {
                    @Override
                    public Object apply(Realm realm) throws Exception {
                        realm.where(Student.class).findAll().deleteAllFromRealm();
                        return null;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        Log.d(TAG, "删除成功");
                        Toast.makeText(MainActivity.this, "添加成功！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void find() {
        RealmObservable
                .createObservable(new Function<Realm, List<Student>>() {
                    @Override
                    public List<Student> apply(Realm realm) throws Exception {

                        RealmResults<Student> all = realm.where(Student.class).findAll();
                        return realm.copyFromRealm(all);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Student>>() {
                    @Override
                    public void accept(List<Student> students) throws Exception {
                        tv.setText(students.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }
}
